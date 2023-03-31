package es.codeurjc.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import es.codeurjc.backend.model.User;

//Programmed by group 13 A
@Component
public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Set<User> findByFollowing(User user);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
