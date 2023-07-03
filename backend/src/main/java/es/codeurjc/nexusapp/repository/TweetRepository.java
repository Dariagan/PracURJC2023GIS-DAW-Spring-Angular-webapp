package es.codeurjc.nexusapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;


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
    Page<Tweet> findMostLikedTweets(Pageable pageable);
    
 

    @Query("SELECT t FROM Tweet t WHERE SIZE(t.tags) >= :tagCount AND (:tagCount = 0 OR :tagsSubset MEMBER OF t.tags)")
    Page<Tweet> findTweetsByTags(@Param("tagsSubset") Set<String> tagsSubset, @Param("tagCount") int tagCount, Pageable pageable);
}


