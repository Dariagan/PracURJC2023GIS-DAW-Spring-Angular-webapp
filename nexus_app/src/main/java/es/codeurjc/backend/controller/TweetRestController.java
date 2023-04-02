package es.codeurjc.backend.controller;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;


import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.TweetRepository;
import es.codeurjc.backend.service.TweetService;
import es.codeurjc.backend.service.UserService;
import es.codeurjc.backend.utilities.Sorter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;


@RestController
@RequestMapping("/api/tweets")
public class TweetRestController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private TweetRepository tweetRepository;

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
        @ApiResponse(
            responseCode = "400",
            description = "Invalid id supplied",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "not found",
            content = @Content
        )
    })
    @JsonView(Tweet.FullView.class)
    @GetMapping("/{id}")
    public ResponseEntity<Tweet> getTweetById(@PathVariable long id) {
        Optional<Tweet> tweetOpt = tweetRepository.findById(id);
        if (tweetOpt.isPresent()) 
            return new ResponseEntity<>(tweetOpt.get(), HttpStatus.OK);
        else 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        )
    })
    @GetMapping("/{username}/tweets")
    public ResponseEntity<List<Tweet>> getTweetsByUsername(@PathVariable String username) {
        Optional<User> userOpt = userService.getUserBy(username);
        if (userOpt.isPresent()) {
            List<Tweet> tweets = userOpt.get().getTweets();
            return new ResponseEntity<>(tweets, HttpStatus.OK);
        } else 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get tweets of the current user")
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
        )
    })
    @JsonView(User.BasicView.class)
    @GetMapping("/me")
    public ResponseEntity<List<Tweet>> getTweetsOfCurrentUser(HttpServletRequest request) {
        
        Optional<User> userOpt = userService.getUserBy(request);

        if (userOpt.isPresent())
            tweetRepository.findFirst10ByAuthor(userOpt.get());

        List<Tweet> tweets = userOpt.get().getTweets();
        return new ResponseEntity<>(tweets, HttpStatus.OK);
    }
    
    @Operation(summary = "Get paged tweets from a Pageable")
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
    public Page<Tweet> getAll(
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "date") String sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction
        ) {   
        return tweetRepository.findAll(PageRequest.of(page, size, Sorter.getCustomSort(sortBy, direction)));
    }

    @Operation(summary = "Get likes of a tweet by id")
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
            description = "Invalid id supplied",
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
    public ResponseEntity<Set<User>> getLikesByTweetId(@PathVariable long id) 
    {
        Optional<Tweet> tweetOpt = tweetRepository.findById(id);
        if (tweetOpt.isPresent()) 
        {
            return new ResponseEntity<>(tweetOpt.get().getLikes(), HttpStatus.OK);
        } 
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //POST new tweet
    @Operation(summary = "Post new tweet")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content = {@Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Tweet.class)
            )}
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content
        )
    })
    @PostMapping("/")
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Tweet> postTweet(@RequestBody Tweet tweet) 
    {
        try {
            tweetRepository.save(tweet);
            URI location = fromCurrentRequest().path("/{id}")
                .buildAndExpand(tweet.getId()).toUri();
            return ResponseEntity.created(location).body(tweet);
        }
        catch (EmptyResultDataAccessException e) 
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete tweet")
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "Deleted",
                content = {@Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Tweet.class)
                )}
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid id supplied",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = @Content
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Tweet> deleteTweet(@RequestParam long id, HttpServletRequest request) 
    {
        Optional <User> userOpt = userService.getUserBy(request);
        Optional <Tweet> tweetOpt = tweetRepository.findById(id);

        if(userOpt.isPresent() && TweetService.isAllowedToDelete(tweetOpt.get(), userOpt.get())) 
        {
            if (tweetOpt.isPresent())
            {
                tweetRepository.delete(tweetOpt.get());
                return new ResponseEntity<>(null,HttpStatus.OK);
            }
            else return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        } 
        else return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
    }
}