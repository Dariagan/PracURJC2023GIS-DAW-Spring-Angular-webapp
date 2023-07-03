package es.codeurjc.nexusapp.service;

import io.vavr.control.Try;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.repository.TweetRepository;
import es.codeurjc.nexusapp.utilities.OptPair;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

// Programmed entirely by group 13-A
@Service
public final class TweetService implements EntityService<Tweet>
{
    final static String MOD_ENDPOINT = "https://mod-microservice.vercel.app/postShouldGetDeleted/";

    @Autowired
    private TweetRepository tweetRepository;

    public static boolean isOwnTweet(Optional<Tweet> tweet, HttpServletRequest req)
    {
        return tweet.isPresent() && isOwnTweet(tweet, req);
    }

    public static boolean isOwnTweet(Tweet tweet, HttpServletRequest req)
    {
        return tweet.getAuthor().getUsername().equals(req.getUserPrincipal().getName());
    }

    public static boolean isAllowedToDelete(Tweet tweet, Optional<User> userOpt)
    {
        return userOpt.isPresent() && 
        (tweet.getAuthor().equals(userOpt.get()) || userOpt.get().isAdmin());
    }

    public static boolean isAllowedToDelete(Tweet tweet, User user)
    {
        return tweet.getAuthor().equals(user) || user.isAdmin();
    }

    public Optional<Tweet> getTweetBy(String id) throws NumberFormatException
    {
        long numericalId = Long.parseLong(id);
        return getTweetBy(numericalId);
    }

    public Optional<Tweet> getTweetBy(long id)
    {
        return tweetRepository.findById(id);
    }

    public Page<Tweet> getTweetsByTags(Set<String> tags, Pageable pageable)
    {
        return tweetRepository.findTweetsByTags(tags, tags.size(), pageable);
    }

    public Page<Tweet> getAll(Pageable pageable)
    {
        return tweetRepository.findAll(pageable);
    }

    public static boolean readIfPostShouldGetDeleted(long id)
    {
        RestTemplate api = new RestTemplate();
        String jsonStr = api.getForEntity(MOD_ENDPOINT + id, String.class).getBody();
        return Try
            .of(() -> new ObjectMapper().readTree(jsonStr))
            .map(json -> json.get("response").asBoolean())
            .getOrElse(false);
    }

    public void delete(Optional<Tweet> tweet) 
    {
        if (tweet.isPresent() && readIfPostShouldGetDeleted(tweet.get().getId())){
            tweetRepository.delete(tweet.get());
            tweetRepository.flush();
        }
    }

    public Page<Tweet> getFollowedUsersTweets(Optional<User> userOpt, Pageable pageable)
    {
        assert userOpt.isPresent();
        return tweetRepository.findFollowedUsersTweets(userOpt.get(), pageable);
    }

    public List<Tweet> getMostReportedTweets(Pageable pageable)
    {
        return tweetRepository.findMostReportedTweets(pageable);
    }

    public List<User> getLikingUsers(Optional<Tweet> tweetOpt, Pageable pageable)
    {
        assert tweetOpt.isPresent();
        return tweetRepository.findUsersWhoLikedTweet(tweetOpt.get(), pageable).getContent();
    }

    public Page<Tweet> getMostLikedTweets(Pageable pageable)
    {
        return tweetRepository.findMostLikedTweets(pageable);
    }

    public List<User> getReportingUsers(Optional<Tweet> tweetOpt, Pageable pageable)
    {
        assert tweetOpt.isPresent();
        return tweetRepository.findUsersWhoReportedTweet(tweetOpt.get(), pageable).getContent();
    }

    public Page<Tweet> getTweetsByUser(User user, Pageable pageable)
    {
        return tweetRepository.findAllByAuthor(user, pageable);
    }

    public void switchLike(OptPair<Tweet, User> tweetAndUser)
    {
        tweetAndUser.ifIsFull(this::switchLike);
    }

    private void switchLike(Tweet tweet, User user)
    {
        tweet.switchLike(user);
        tweetRepository.save(tweet);
    }

    public void report(OptPair<Tweet, User> tweetAndUser)
    {
        tweetAndUser.ifIsFull(this::report);
    }

    private void report(Tweet tweet, User reportingUser) 
    {
        tweet.report(reportingUser);
        tweetRepository.save(tweet);
    }

    @Override
	public TweetService save(Tweet tweet) {
		tweetRepository.save(tweet);
        return this;
	}

    @Override
    public TweetService delete(Tweet tweet)
    {
        tweetRepository.delete(tweet);
        tweetRepository.flush();
        return this;
    }	
   
}
