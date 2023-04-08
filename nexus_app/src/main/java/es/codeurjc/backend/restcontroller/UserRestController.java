package es.codeurjc.backend.restcontroller;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import ch.qos.logback.core.joran.conditional.ElseAction;
import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.TweetService;
import es.codeurjc.backend.service.UserService;
import es.codeurjc.backend.utilities.PageableUtil;
import es.codeurjc.backend.utilities.queriers.AuthorTweetsQuerier;
import es.codeurjc.backend.utilities.queriers.BlockQuerier;
import es.codeurjc.backend.utilities.queriers.FollowedUsersTweetsQuerier;
import es.codeurjc.backend.utilities.queriers.FollowingQuerier;
import es.codeurjc.backend.utilities.queriers.TweetQuerier;
import es.codeurjc.backend.utilities.queriers.UserQuerier;
import es.codeurjc.backend.utilities.responseentity.GetResponseEntityGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    @JsonView(User.FullView.class)
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userService.getUserBy(username);
        if (userOpt.isPresent()) 
        {
            return new ResponseEntity<>(userOpt.get(), HttpStatus.OK);
        } 
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    @JsonView(Tweet.FullView.class)
    @GetMapping("/{username}/tweets")
    public ResponseEntity<Object> getTweetsByUsername(
        @PathVariable String username,
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "username") Optional<String> sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction,
        @RequestParam Optional<String> tweet) 
    {
        var reg = new GetResponseEntityGenerator<User, Tweet>(new UserQuerier(userService), new TweetQuerier(tweetService));

        return reg.generateGetResponseEntity(username, page, size, sortBy, direction, tweet, new AuthorTweetsQuerier(tweetService));
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
    @JsonView(User.FullView.class)
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
    @PostMapping("/{username}/following")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> followUser(
        HttpServletRequest request, 
        @PathVariable String username,
        @RequestBody User followed) 
    {
        Optional<User> loggedUser = userService.getUserBy(request);

        if (UserService.isOwnResource(username, loggedUser)) 
        {
            userService.save(loggedUser.get()).save(followed);
            URI location = fromCurrentRequest().path("/{followeduser}")
                .buildAndExpand(followed.getUsername()).toUri();
            return ResponseEntity.created(location).body(followed);
        } 
        else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
    @DeleteMapping("/{username}/blocked/{unfollowed}")
    public ResponseEntity<User> unfollowUser(
        HttpServletRequest request, 
        @PathVariable String username,
        @PathVariable String unblocked) 
    {
        Optional<User> loggedUserOpt = userService.getUserBy(request);
        Optional<User> unfollowedUserOpt = userService.getUserBy(unblocked);

        if (loggedUserOpt.isPresent() && unfollowedUserOpt.isPresent())
            
            if (UserService.isOwnResource(username, loggedUserOpt)) 
            {
                loggedUserOpt.get().getFollowing().remove(unfollowedUserOpt.get());
                userService.save(loggedUserOpt.get());
                return new ResponseEntity<>(null, HttpStatus.OK);
            } 
            else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "GET blocklist of pathvariable username")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", description = "Found users blocked by user",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
                )}),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @JsonView(User.FullView.class)
    @GetMapping("/{username}/blocked")
    public ResponseEntity<Object> getUserBlocked(
        @PathVariable String username,
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "username") Optional<String> sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction,
        @RequestParam Optional<String> user) 
    {
        var greg = new GetResponseEntityGenerator<User, User>
        (new UserQuerier(userService), new UserQuerier(userService));

        return greg.generateGetResponseEntity
        (username, page, size, sortBy, direction, user, new BlockQuerier(userService));
    }

    @Operation(summary = "Post user in blocklist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class))}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PostMapping("/{username}/blocked")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> blockUser(
        HttpServletRequest request, 
        @PathVariable String username,
        @RequestBody User blocked) 
    {
        Optional<User> loggedUser = userService.getUserBy(request);

        if (UserService.isOwnResource(username, loggedUser)) 
        {
            userService.save(loggedUser.get()).save(blocked);
            URI location = fromCurrentRequest().path("/{blockeduser}")
                .buildAndExpand(blocked.getUsername()).toUri();
            return ResponseEntity.created(location).body(blocked);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
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
    @DeleteMapping("/{username}/blocked/{unblocked}")
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
                return new ResponseEntity<>(null, HttpStatus.OK);
            } 
            else return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @Operation(summary = "Get paged users from Pageable parameters")
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

    @Operation(summary = "Get followed users' tweets")
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

    @Operation(summary = "Post new user")
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

    @Operation(summary = "Get authenticated user")
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
    @JsonView(User.FullView.class)
    @GetMapping("/me")
    public ResponseEntity<User> getLoggedUser(HttpServletRequest request) 
    {
        Optional<User> userOpt = userService.getUserBy(request);
        return userOpt
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

}