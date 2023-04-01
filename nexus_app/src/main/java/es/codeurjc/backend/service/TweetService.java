package es.codeurjc.backend.service;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.TweetRepository;

import io.vavr.control.Try;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;

// Programmed by group 13 A
@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    public Optional<Tweet> getTweetById(String id) {
        return getTweetById(Long.parseLong(id));
    }
    public Optional<Tweet> getTweetById(Long id) {
        return tweetRepository.findById(id);
    }

    public static boolean isOwnTweet(Tweet tweet, HttpServletRequest req) {
        return tweet.getAuthor().getUsername().equals(req.getUserPrincipal().getName());
    }

    public TweetService save(Tweet tweet) {
        tweetRepository.save(tweet);
        return this;
    }
    public TweetService saveAndFlush(Tweet tweet) {
        tweetRepository.saveAndFlush(tweet);
        return this;
    }
    public TweetService delete(Tweet tweet) {
        tweetRepository.delete(tweet);
        return this;
    }
    
    public Page<Tweet> findPage(Pageable page){
        return tweetRepository.findAll(page);
    }

    public Page<Tweet> getPageOfTweets(int page){
        return tweetRepository.findAll(PageRequest.of(page,5));
    }

    public List<Tweet> findAll(){
        return tweetRepository.findAll();
    }

    public static boolean readIfPostShouldGetdeleted(Long id) 
    {
        final String modEndpoint = "https://mod-microservice.vercel.app/postShouldGetDeleted/";

        RestTemplate api = new RestTemplate();
        String jsonStr = api.getForEntity(modEndpoint + id.toString(), String.class).getBody();
        return Try
        .of(() -> new ObjectMapper().readTree(jsonStr))
        .map(json -> json.get("response").asBoolean())
        .getOrElse(false);
    }

    public List<Tweet> queryTweetsToModerate() {
        return tweetRepository.findMostReportedTweets();
    }


    public List<Tweet> getTweetsByUser(User user){
        return tweetRepository.findAllByAuthor(user);
    }
    
}
