package es.codeurjc.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;


@Component
public interface TweetRepository extends JpaRepository<Tweet, Long>{

    Optional<Tweet> findById(long id);
    List<Tweet> findFirst10ByAuthor(User author);
    
    // query made by group 13-A
    @Query("SELECT t FROM UserTable u JOIN u.following f JOIN f.tweets t WHERE u = :user AND t.author.banned = false ORDER BY t.date DESC")
    Page<Tweet> findFollowedUsersTweets(User user, Pageable pageable);

    @Query("SELECT t FROM Tweet t WHERE t.author.banned = false")
    Page<Tweet> findAll(Pageable pageable);

    List<Tweet> findAllByAuthor(User author);

    // 13-A
    @Query("SELECT t FROM Tweet t WHERE t.author.username = :username AND t.author.banned = false")
    List<Tweet> findAllByUsername(String username);

    // query made by group 13-A
    @Query("SELECT t FROM Tweet t JOIN t.reporters r GROUP BY t.id ORDER BY COUNT(r) DESC")
    List<Tweet> findMostReportedTweets(Pageable pageable);

    // query made by group 13-A
    @Query("SELECT t FROM Tweet t JOIN t.likes l WHERE t.author.banned = false GROUP BY t.id ORDER BY COUNT(l) DESC")
    List<Tweet> findMostLikedTweets(Pageable pageable);
    
    // query made by group 13-A
    @Query("SELECT t FROM Tweet t WHERE EXISTS (SELECT tag FROM t.tags tag WHERE tag IN :tags) AND t.author.banned = false")
    List<Tweet> findTweetsByTags(Set<String> tags, Pageable pageable);
}
