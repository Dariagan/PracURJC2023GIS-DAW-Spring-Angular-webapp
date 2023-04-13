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
    
    // 13-A
    @Query("SELECT t FROM UserTable u " +
    "JOIN u.following f JOIN f.tweets t WHERE u = :user " +
    "AND t.author.role != 'BANNED' " +
    "AND t.author NOT IN (SELECT bu FROM UserTable u2 JOIN u2.blocked bu WHERE u2 = :user) " +
    "ORDER BY t.date DESC")
    Page<Tweet> findFollowedUsersTweets(User user, Pageable pageable);

    // 13-A
    @Query("SELECT t FROM Tweet t WHERE t.author.role != 'BANNED' " +
    "AND t.author NOT IN (SELECT bu FROM UserTable u2 JOIN u2.blocked bu WHERE u = :user) ")
    Page<Tweet> findAll(User user, Pageable pageable);

    // 13-A
    @Query("SELECT t FROM Tweet t WHERE t.author.role != 'BANNED'")
    Page<Tweet> findAll(Pageable pageable);


    Page<Tweet> findAllByAuthor(User author, Pageable pageable);

    // 13-A
    @Query("SELECT t FROM Tweet t WHERE t.author.username = :username AND t.author.role != 'BANNED'")
    List<Tweet> findAllByUsername(String username);

    // 13-A
    @Query("SELECT t FROM Tweet t JOIN t.reporters r GROUP BY t.id ORDER BY COUNT(r) DESC")
    List<Tweet> findMostReportedTweets(Pageable pageable);

    @Query("SELECT t.likes FROM Tweet t WHERE t = :tweet")
    Page<User> findUsersWhoLikedTweet(Tweet tweet, Pageable pageable);

    @Query("SELECT t.reporters FROM Tweet t WHERE t = :tweet")
    Page<User> findUsersWhoReportedTweet(Tweet tweet, Pageable pageable);

    // 13-A
    @Query("SELECT t FROM Tweet t JOIN t.likes l " +
    "WHERE t.author.role != 'BANNED' GROUP BY t.id ORDER BY COUNT(l) DESC")
    List<Tweet> findMostLikedTweets(Pageable pageable);
    
    // 13-A
    @Query("SELECT t FROM Tweet t " +
    "WHERE EXISTS (SELECT tag FROM t.tags tag WHERE tag IN :tags) " +
    "AND t.author.role != 'BANNED'")
    List<Tweet> findTweetsByTags(Set<String> tags, Pageable pageable);
}
