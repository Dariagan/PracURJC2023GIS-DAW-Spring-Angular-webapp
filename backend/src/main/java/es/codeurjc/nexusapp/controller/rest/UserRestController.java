package es.codeurjc.nexusapp.controller.rest;

import java.net.URI;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.model.User.UsernameView;
import es.codeurjc.nexusapp.service.TweetService;
import es.codeurjc.nexusapp.service.UserService;
import es.codeurjc.nexusapp.utilities.PageableUtil;
import es.codeurjc.nexusapp.utilities.dtos.TweetDto;
import es.codeurjc.nexusapp.utilities.dtos.UserDto;
import es.codeurjc.nexusapp.utilities.queriers.BlockQuerier;
import es.codeurjc.nexusapp.utilities.queriers.FollowedUsersTweetsQuerier;
import es.codeurjc.nexusapp.utilities.queriers.FollowingQuerier;
import es.codeurjc.nexusapp.utilities.queriers.TweetQuerier;
import es.codeurjc.nexusapp.utilities.queriers.UserQuerier;
import es.codeurjc.nexusapp.utilities.responseentity.GetResponseEntityGenerator;
import es.codeurjc.nexusapp.utilities.responseentity.ResourcesBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private TweetService tweetService;

    @Operation(summary = "Get user by username")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Found the user",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
                )}
        ),
        @ApiResponse(
            responseCode = "404", description = "Not found", content = @Content)})
    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userService.getUserBy(username);
        if (userOpt.isPresent()) 
        {
            return new ResponseEntity<>(new UserDto(userOpt.get()), HttpStatus.OK);
        } 
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "POST new user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", description = "Created",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )}
        ),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createUser(HttpServletRequest request, @RequestBody User createdUser) 
    {
        if (userService.isAdmin(request)) 
        {
            userService.save(createdUser);
            URI location = fromCurrentRequest().path("/{username}")
                .buildAndExpand(createdUser.getUsername()).toUri();
            return ResponseEntity.created(location).body(createdUser);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Get tweets by username")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Tweet.class)
            )}
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content
        )})
    @GetMapping("/{username}/tweets")
    public ResponseEntity<ArrayList<TweetDto>> getTweetsByUsername(
        @PathVariable String username,
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "date") String sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction
    ){
        var userOpt = userService.getUserBy(username);
        if(userOpt.isPresent())
        {
            List<Tweet> foundTweets = tweetService
                    .getTweetsByUser(userOpt.get(), PageRequest.of(page, size, PageableUtil.getSort(sortBy, direction)))
                    .getContent();

            ArrayList<TweetDto> dtos = new ArrayList<>();
            for (Tweet foundTweet: foundTweets)
                dtos.add(new TweetDto(foundTweet));
            
                return new ResponseEntity<>(
                    dtos, 
                    HttpStatus.OK
                    );      
        } else return ResponseEntity.notFound().build();
    }

    @Operation(summary = "GET users followed by pathvariable username")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Found users followed by user",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
                )}
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content)})
    @JsonView(User.UsernameView.class)
    @GetMapping("/{username}/following")
    public ResponseEntity<Object> getUserFollowing(
        @PathVariable String username,
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "username") Optional<String> sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction,
        @RequestParam Optional<String> user) 
    {
        var greg = new GetResponseEntityGenerator<User, User>(new UserQuerier(userService), new UserQuerier(userService));

        return greg.generateGetResponseEntity(username, page, size, sortBy, direction, user, new FollowingQuerier(userService));
    }

    @Operation(summary = "POST user in followlist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = {@Content(
                mediaType = "application/json", schema = @Schema(implementation = User.class))}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @JsonView(User.UsernameView.class)
    @PostMapping("/{follower}/following")
    public ResponseEntity<User> followUser(
        HttpServletRequest request, 
        @PathVariable String follower,
        @RequestBody String followed) 
    {
        Optional<User> loggedUserOpt = userService.getUserBy(request);

        if (UserService.isOwnResource(follower, loggedUserOpt) && !follower.equals(followed)) 
        {   
            var followedUserOpt = userService.getUserBy(followed);
            if (followedUserOpt.isPresent()){
                loggedUserOpt.get().toggleFollow(followedUserOpt.get());
                userService.save(loggedUserOpt.get());
                URI location = fromCurrentRequest().path("/{followed}")
                    .buildAndExpand(followed).toUri();
                return ResponseEntity.created(location).body(followedUserOpt.get());    
            }
            else return ResponseEntity.notFound().build();
        } 
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "Toggle user's ban status")
    @ApiResponse(responseCode = "200", description = "User ban status toggled successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Forbidden - Not admin")
    @PatchMapping("/{username}")
    public ResponseEntity<User> toggleBanOnUser(
        HttpServletRequest request, 
        @PathVariable String username,
        @RequestParam(name = "banned", required = true) boolean banned
        ) 
    {
        var reqUserOpt = userService.getUserBy(request);
        if (UserService.isAdmin(reqUserOpt)){
            var targetUserOpt = userService.getUserBy(username);
            if (targetUserOpt.isPresent()){
                if (banned)
                    targetUserOpt.get().ban();
                else
                    targetUserOpt.get().unban();
                userService.save(targetUserOpt.get());
                return ResponseEntity.ok().build();
            }else return ResponseEntity.notFound().build();
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "Delete user from followlist")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Deleted",
            content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content)})
    @DeleteMapping("/{unfollower}/following/{unfollowed}")
    public ResponseEntity<User> unfollowUser(
        HttpServletRequest request,
        @PathVariable String unfollower, 
        @PathVariable String unfollowed) 
    {
        Optional<User> loggedUserOpt = userService.getUserBy(request);
        Optional<User> unfollowedUserOpt = userService.getUserBy(unfollowed);

        if (UserService.isOwnResource(unfollower, loggedUserOpt) && !unfollower.equals(unfollowed)) 
        {   
            if (unfollowedUserOpt.isPresent() && 
            loggedUserOpt.get().getFollowing().remove(unfollowedUserOpt.get())) 
            {
                userService.save(loggedUserOpt.get());
                return new ResponseEntity<>(HttpStatus.OK);
            } 
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "GET block list of pathvariable username")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Found users blocked by user",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
                )}),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @JsonView(User.UsernameView.class)
    @GetMapping("/{username}/blocks")
    public ResponseEntity<Object> getUserBlocks(
        @PathVariable String username,
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "username") Optional<String> sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction,
        @RequestParam Optional<String> user,//optional param user to query for (check if it is in the list)
        HttpServletRequest request
        )
    {
        if (UserService.isOwnResource(username, request)){
            var greg = new GetResponseEntityGenerator<User, User>
            (new UserQuerier(userService), new UserQuerier(userService));

            return greg.generateGetResponseEntity
            (username, page, size, sortBy, direction, user, new BlockQuerier(userService));
        }
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "POST user in block list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class))}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PostMapping("/{username}/blocks")
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(User.UsernameView.class)
    public ResponseEntity<User> blockUser(
        HttpServletRequest request, 
        @PathVariable String username,
        @RequestBody String blocked) 
    {
        Optional<User> loggedUserOpt = userService.getUserBy(request);

        if (UserService.isOwnResource(username, loggedUserOpt) && !username.equals(blocked)) 
        {
            var blockedUserOpt = userService.getUserBy(blocked);
            if(blockedUserOpt.isPresent()){
                loggedUserOpt.get().block(blockedUserOpt.get());
                userService.save(loggedUserOpt.get());
                URI location = fromCurrentRequest().path("/{blocked}")
                    .buildAndExpand(blocked).toUri();
                return ResponseEntity.created(location).body(blockedUserOpt.get());
            }
            else return ResponseEntity.notFound().build();
        } 
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "DELETE user from blocklist")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deleted",
            content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = User.class))}),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content)})
    @DeleteMapping("/{username}/blocks/{unblocked}")
    public ResponseEntity<User> unblockUser(
        HttpServletRequest request, 
        @PathVariable String username,
        @PathVariable String unblocked) 
    {
        Optional<User> loggedUserOpt = userService.getUserBy(request);
        Optional<User> unblockedUserOpt = userService.getUserBy(unblocked);

        if (loggedUserOpt.isPresent() && unblockedUserOpt.isPresent())
            
            if (UserService.isOwnResource(username, loggedUserOpt)) 
            {
                loggedUserOpt.get().getBlocked().remove(unblockedUserOpt.get());
                userService.save(loggedUserOpt.get());
                return new ResponseEntity<>(HttpStatus.OK);
            } 
            else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @Operation(summary = "GET paged users from Pageable parameters")
    @ApiResponses(value = { @ApiResponse(
            responseCode = "200",  description = "Found users",
            content = {@Content(
                mediaType = "application/json", schema = @Schema(implementation = User.class))})
    }) 
    @JsonView(User.FullView.class)
    @GetMapping("")
    public ResponseEntity<List<User>> getUsers(
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "username") String sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction) 
    {   
        var users = userService.findAll(PageableUtil.getPageable(page, size, sortBy, direction));
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "GET followed users' tweets")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Found",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Tweet.class)
            )}
        ),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @JsonView(Tweet.FullView.class)
    @GetMapping("/{username}/following/tweets")
    public ResponseEntity<Object> getFollowedUserTweets(@PathVariable String username,
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by") Optional<String> sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction,
        @RequestParam Optional<String> tweet) 
    {
        var greg = new GetResponseEntityGenerator<User, Tweet>
        (new UserQuerier(userService), new TweetQuerier(tweetService));

        return greg.generateGetResponseEntity
        (username, page, size, sortBy, direction, tweet, new FollowedUsersTweetsQuerier(tweetService));
    }

    @Operation(summary = "GET authenticated user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "User found",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )}
        ),
        @ApiResponse(
            responseCode = "404", description = "User not found", content = @Content)})
    @GetMapping("/me")
    public ResponseEntity<UserDto> getLoggedUser(HttpServletRequest request) 
    {
        Optional<User> userOpt = userService.getUserBy(request);        
        
        if (userOpt.isPresent())
            return new ResponseEntity<>(new UserDto(userOpt.get()), HttpStatus.OK); 
        else
            return ResponseEntity.notFound().build();
    }

    @PostMapping("/{username}/image")
    @Operation(summary = "Upload an image for a user")
    @ApiResponse(responseCode = "201", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "204", description = "No image provided")
    @ApiResponse(responseCode = "403", description = "Can't change another user's image")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Object> uploadImage(
            @Parameter(description = "Username of the user") @PathVariable String username,
            @Parameter(description = "Image file to upload") @RequestBody MultipartFile imageFile,
            HttpServletRequest request)
    {
        Optional<User> user = userService.getUserBy(username);
        if (user.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(UserService.isOwnResource(username, user))
        {
            URI location = fromCurrentRequest().build().toUri();

            if (!imageFile.isEmpty())
            {
                try {
                    user.get().setImage(BlobProxy.generateProxy(imageFile.getInputStream(), imageFile.getSize()));
                    userService.save(user.get());

                    return ResponseEntity.created(location).build();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<Object>(location, null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<Object>(HttpStatus.FORBIDDEN);
    }


    @GetMapping("/{username}/image")
    @Operation(summary = "Download the user's image")
    @ApiResponse(responseCode = "200", description = "Image retrieved successfully", content = @Content(mediaType = "image/*"))
    @ApiResponse(responseCode = "204", description = "User has no image")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Resource> downloadImage(
            @Parameter(description = "Username of the user") @PathVariable String username)
    {
        Optional<User> user = userService.getUserBy(username);
        if (user.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Blob> image = Optional.ofNullable(user.get().getImage());
        if (image.isEmpty()) return ResponseEntity.noContent().build();

        return ResourcesBuilder
                .tryBuildImgResponse(image)
                .getOrElse(ResponseEntity.internalServerError().build());
    }

}