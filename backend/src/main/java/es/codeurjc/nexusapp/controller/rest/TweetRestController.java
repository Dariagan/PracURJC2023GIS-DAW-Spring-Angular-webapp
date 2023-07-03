package es.codeurjc.nexusapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.TweetService;
import es.codeurjc.nexusapp.service.UserService;
import es.codeurjc.nexusapp.utilities.PageableUtil;
import es.codeurjc.nexusapp.utilities.dtos.JsonString;
import es.codeurjc.nexusapp.utilities.dtos.TweetDto;
import es.codeurjc.nexusapp.utilities.queriers.LikesQuerier;
import es.codeurjc.nexusapp.utilities.queriers.ReportersQuerier;
import es.codeurjc.nexusapp.utilities.queriers.TweetQuerier;
import es.codeurjc.nexusapp.utilities.queriers.UserQuerier;
import es.codeurjc.nexusapp.utilities.responseentity.GetResponseEntityGenerator;

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
    @GetMapping("/{id}")
    public ResponseEntity<TweetDto> getTweetById(@PathVariable long id) 
    {
        try
        {
            Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);
            
            if (tweetOpt.isPresent()) 
                return new ResponseEntity<>(new TweetDto(tweetOpt.get()), HttpStatus.OK);
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
    @PostMapping("")
    @JsonView(Tweet.TweetIdentifyingView.class)
    public ResponseEntity<Tweet> postTweet(String tweetText, MultipartFile image, String[] tags, HttpServletRequest request) {
        var userOpt = userService.getUserBy(request);

        if (userOpt.isPresent()) {
            try {
                Tweet.Builder tweetBuilder = new Tweet.Builder();

                tweetBuilder.setAuthor(userOpt.get())
                    .setText(tweetText);

                if (tags != null)
                    tweetBuilder.setTags(new HashSet<>(List.of(tags)));

                if (image != null)
                    tweetBuilder.setMedia(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));

                Tweet tweet = tweetBuilder.build();
                tweetService.save(tweet);
                URI location = fromCurrentRequest().path("/{id}")
                    .buildAndExpand(tweet.getId()).toUri();
                return ResponseEntity.created(location).body(tweet);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
    public ResponseEntity<Tweet> deleteTweet(@PathVariable long id, HttpServletRequest request) 
    {
        Optional <User> userOpt = userService.getUserBy(request);
        try {
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
            else return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } 
        catch (NumberFormatException e){return ResponseEntity.badRequest().build();}
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
    @GetMapping("")
    public ResponseEntity<List<TweetDto>> getAll(
        @RequestParam(value = "page", defaultValue = "0") int page ,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "sort-by", defaultValue = "date") String sortBy,
        @RequestParam(value = "direction", defaultValue = "desc") String direction,
        @RequestParam(value = "tags") Optional<Set<String>> tags,
        HttpServletRequest request
        )
    {   
        if (sortBy.equalsIgnoreCase("reports"))
            if (UserService.isAdmin(userService.getUserBy(request)))
                return getMostReportedTweets(page, size);
            else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        if (sortBy.equalsIgnoreCase("likes"))
            return getMostLikedTweets(page, size);
        else if (tags.isPresent()){
            return getTweetsByTags(page, size, sortBy, tags.get());
        }
        else {
            List<Tweet> foundTweets = tweetService
                .getAll(PageRequest.of(page, size, PageableUtil.getSort(sortBy, direction)))
                .getContent();

            ArrayList<TweetDto> dtos = new ArrayList<>();
            for (Tweet tweet: foundTweets)    
                dtos.add(new TweetDto(tweet));

            return new ResponseEntity<List<TweetDto>>(dtos, HttpStatus.OK);
        }
    }
    private ResponseEntity<List<TweetDto>> getMostReportedTweets(
        int page,
        int size)
    {       
        List<Tweet> foundTweets = tweetService
            .getMostReportedTweets(PageRequest.of(page, size));

        ArrayList<TweetDto> dtos = new ArrayList<>();
        for (Tweet tweet: foundTweets)    
            dtos.add(new TweetDto(tweet));

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    private ResponseEntity<List<TweetDto>> getMostLikedTweets(
        int page,
        int size)
    {       
        List<Tweet> foundTweets = tweetService
            .getMostLikedTweets(PageRequest.of(page, size))
            .getContent();
        ArrayList<TweetDto> dtos = new ArrayList<>();
        for (Tweet tweet: foundTweets)    
            dtos.add(new TweetDto(tweet));

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    private ResponseEntity<List<TweetDto>> getTweetsByTags(
        int page,
        int size,
        String sortBy,
        Set<String> tags
    ){       
        List<Tweet> foundTweets = tweetService
            .getTweetsByTags(tags, PageRequest.of(page, size))
            .getContent();
        ArrayList<TweetDto> dtos = new ArrayList<>();
        for (Tweet tweet: foundTweets)    
            dtos.add(new TweetDto(tweet));

        return new ResponseEntity<>(dtos, HttpStatus.OK);
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
    public ResponseEntity<Object> getLikesByTweetId(@PathVariable long id,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam Optional<String> user) 
    {
        var reg = new GetResponseEntityGenerator<Tweet, User>(new TweetQuerier(tweetService), new UserQuerier(userService));

        return reg.generateResponseEntity(String.valueOf(id), page, size, Optional.empty(), new LikesQuerier(tweetService));
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
    public ResponseEntity<Tweet> likeTweet(@RequestBody JsonString username, @PathVariable long id,  HttpServletRequest req) 
    {
        try 
        {   
            Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);
            Optional<User> userOpt = userService.getUserBy(username.toString());
            if (tweetOpt.isPresent() && userOpt.isPresent())
            {
                if (UserService.isOwnResource(username.toString(), userService.getUserBy(req)))
                {
                    Tweet tweet = tweetOpt.get();
                    tweet.getLikes().add(userOpt.get());
                    tweetService.save(tweet);
                    URI location = fromCurrentRequest().path("/{id}/likes/{username}")
                        .buildAndExpand(tweet.getId(), username).toUri();

                    return ResponseEntity.created(location).body(tweet);
                }
                else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
        @PathVariable long id,
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
        catch (NumberFormatException e){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
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
    @GetMapping("/{id}/reports")
    public ResponseEntity<Object> getReportersByTweetId(
        @PathVariable long id,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam Optional<String> user) 
    {
        var greg = new GetResponseEntityGenerator<Tweet, User>(new TweetQuerier(tweetService), new UserQuerier(userService));

        return greg.generateResponseEntity(String.valueOf(id), page, size, user, new ReportersQuerier(tweetService));
    }

    @Operation(summary = "POST reporting user to tweet's reportlist")
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
    @PostMapping("/{id}/reports")
    public ResponseEntity<Tweet> reportTweet(@PathVariable long id, HttpServletRequest request) 
    {
        try 
        {
            Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);
            Optional<User> userOpt = userService.getUserBy(request);

            if (userOpt.isPresent())
                if (tweetOpt.isPresent())
                {
                    Tweet tweet = tweetOpt.get();
                    tweet.getReporters().add(userOpt.get());
                    tweetService.save(tweet);
                    URI location = fromCurrentRequest().path("/{id}/reports/{username}")
                        .buildAndExpand(tweet.getId(), userOpt.get()).toUri();

                    return ResponseEntity.created(location).body(tweet);
                
                } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        catch (NumberFormatException e){return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
        catch (EmptyResultDataAccessException e) {return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @GetMapping("/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);

		if (tweetOpt.isPresent() && tweetOpt.get().hasMedia()) 
        {
			Resource file = new InputStreamResource(tweetOpt.get().getMedia().getBinaryStream());

			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(tweetOpt.get().getMedia().length()).body(file);

		} else return ResponseEntity.notFound().build();
	}

    @PostMapping("/{id}/image")
	public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestBody MultipartFile image)
			throws IOException {

		Optional<Tweet> tweetOpt = tweetService.getTweetBy(id);
        if (tweetOpt.isEmpty()) return ResponseEntity.notFound().build();

        URI location = fromCurrentRequest().build().toUri();

		tweetOpt.get().setMedia(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
		tweetService.save(tweetOpt.get());

		return ResponseEntity.created(location).build();
	}
}