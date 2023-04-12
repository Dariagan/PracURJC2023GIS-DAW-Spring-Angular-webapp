package es.codeurjc.backend.restcontroller;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.TweetService;
import es.codeurjc.backend.service.UserService;
import es.codeurjc.backend.utilities.PageableUtil;
import es.codeurjc.backend.utilities.queriers.LikesQuerier;
import es.codeurjc.backend.utilities.queriers.ReportersQuerier;
import es.codeurjc.backend.utilities.queriers.TweetQuerier;
import es.codeurjc.backend.utilities.queriers.UserQuerier;
import es.codeurjc.backend.utilities.responseentity.GetResponseEntityGenerator;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


@RestController
@RequestMapping("/api/tweets")
public class TweetRestController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private TweetService tweetService;

    @Operation(summary = "Get tweet by id")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the tweet",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Tweet.class)
                )}
        ),
        @ApiResponse(responseCode = "400", description = "Id is not an integer", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content)})
    @JsonView(Tweet.FullView.class)
    @GetMapping("/{id}")
    public ResponseEntity<Tweet> getTweetById(@PathVariable String id) 
    {
        try
        {
            Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);
            
            if (tweetOpt.isPresent()) 
                return new ResponseEntity<>(tweetOpt.get(), HttpStatus.OK);
            else 
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } 
        catch (NumberFormatException e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Post new tweet")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Tweet.class)
            )}),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)})
    @PostMapping("/")
    public ResponseEntity<Tweet> postTweet(@RequestBody Tweet tweet) 
    {
        try {
            tweetService.save(tweet);
            URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(tweet.getId()).toUri();
            return ResponseEntity.created(location).body(tweet);
        }
        catch (EmptyResultDataAccessException e) 
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete tweet")
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", description = "Deleted",
                content = {@Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Tweet.class)
                )}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id given", content = @Content),
            @ApiResponse(responseCode = "404",description = "Not found",content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Tweet> deleteTweet(@RequestParam String id, HttpServletRequest request) 
    {
        Optional <User> userOpt = userService.getUserBy(request);
        try 
        {
            Optional <Tweet> tweetOpt = tweetService.getTweetBy(id);

            if(TweetService.isAllowedToDelete(tweetOpt.get(), userOpt)) 
            {
                if (tweetOpt.isPresent())
                {
                    tweetService.delete(tweetOpt.get());
                    return new ResponseEntity<>(null, HttpStatus.OK);
                }
                else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            } 
            else return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } 
        catch (NumberFormatException e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @Operation(summary = "Get tweets from pageable params")
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
        )
    }) 
    @JsonView(Tweet.FullView.class)
    @GetMapping("")
    public ResponseEntity<List<Tweet>> getAll(
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "date") String sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction) 
    {   
        return new ResponseEntity<>(
            tweetService
            .getPage(PageRequest.of(page, size, PageableUtil.getSort(sortBy, direction)))
            .getContent(), 
            HttpStatus.OK
            );
    }

    @Operation(summary = "Get likers of a tweet by tweet id")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the tweet",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
                )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tweet id is not integer",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "not found",
            content = @Content
        )
    })
    @JsonView(User.UsernameView.class)
    @GetMapping("/{id}/likes")
    public ResponseEntity<Object> getLikesByTweetId(@PathVariable String id,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam Optional<String> user) 
    {
        var reg = new GetResponseEntityGenerator<Tweet, User>(new TweetQuerier(tweetService), new UserQuerier(userService));

        return reg.generateResponseEntity(id, page, size, Optional.empty(), new LikesQuerier(tweetService));
    }

    @Operation(summary = "POST liking user to tweet's likelist")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tweet id is not integer",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403", description = "Forbidden",
            content = @Content
        )
    })
    @PostMapping("/{id}/likes")
    public ResponseEntity<Tweet> likeTweet(@PathVariable String id, @RequestBody User user) 
    {
        try 
        {
            Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);

            if (tweetOpt.isPresent())
            {
                Tweet tweet = tweetOpt.get();
                tweet.getLikes().add(user);
                tweetService.save(tweet);
                URI location = fromCurrentRequest().path("/{id}/likes/{username}")
                    .buildAndExpand(tweet.getId(), user.getUsername()).toUri();

                return ResponseEntity.created(location).body(tweet);
            } 
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (NumberFormatException e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (EmptyResultDataAccessException e) 
        {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "DELETE self user from tweet's likelist")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Deleted",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tweet id is not integer",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content
        )
    })
    @DeleteMapping("/{id}/likes/{username}")
    public ResponseEntity<Tweet> unlikeTweet(
        HttpServletRequest request,
        @PathVariable String id,
        @PathVariable String username) 
    {
        Optional<User> userOpt = userService.getUserBy(request);
        try
        { 
            Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);

            if (tweetOpt.isPresent() && userOpt.isPresent())
            {
                if (UserService.isOwnResource(username, userOpt))
                {
                    Tweet tweet = tweetOpt.get();
                    tweet.getLikes().remove(userOpt.get());
                    tweetService.save(tweet);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } 
            else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (NumberFormatException e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get reporters of a tweet by tweet id")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Found the tweet",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
                )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tweet id is not integer",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "not found",
            content = @Content
        )
    })
    @JsonView(User.UsernameView.class)
    @GetMapping("/{id}/reporters")
    public ResponseEntity<Object> getReportersByTweetId(
        @PathVariable String id,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam Optional<String> user) 
    {
        var greg = new GetResponseEntityGenerator<Tweet, User>(new TweetQuerier(tweetService), new UserQuerier(userService));

        return greg.generateResponseEntity(id, page, size, user, new ReportersQuerier(tweetService));
    }
}