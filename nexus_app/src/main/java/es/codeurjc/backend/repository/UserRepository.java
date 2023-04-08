package es.codeurjc.backend.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;

// Programmed by group 13 A
@Component
public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Set<User> findByFollowing(User user);
    
    // Gets all the users who are following the input user
    Page<User> findByFollowing(User user, Pageable pageable);

    // Gets all the users who the input user is following
    @Query("SELECT u.following FROM UserTable u WHERE u = :user")
    Page<User> findFollowingByUser(User user, Pageable pageable);

    @Query("SELECT u.blocked FROM UserTable u WHERE u = :user")
    Page<User> findBlockedByUser(User user, Pageable pageable);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
