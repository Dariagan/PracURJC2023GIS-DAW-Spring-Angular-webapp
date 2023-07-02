package es.codeurjc.nexusapp.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.repository.UserRepository;
import es.codeurjc.nexusapp.utilities.OptTwo;
import es.codeurjc.nexusapp.utilities.PageableUtil;

// Whole service programmed by group 13-A
@Service
public final class UserService implements EntityService<User>
{
    @Autowired
    private UserRepository userRepository;

    public User tryToGetUsernameBy(String username)
    {
        return userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public OptTwo<User> getUserBy(String user1, String user2) {
        return OptTwo.of(getUserBy(user1), getUserBy(user2));
    }

    public List<User> findAll(Pageable pageable)
    {
        return userRepository.findAll(pageable).getContent();
    }

    public OptTwo<User> getUserBy(String username, HttpServletRequest request)
    {
        return OptTwo.of(getUserBy(username), getUserBy(request));
    }

    public Optional<User> getUserBy(HttpServletRequest request)
    {
        return getUserBy(
            Try.of(() -> request.getUserPrincipal().getName()).getOrElse("")
        );
    }

    public Optional<User> getUserBy(String usernameOrEmail)
    {
        if (EmailService.isEmail(usernameOrEmail))
            return userRepository.findByEmail(usernameOrEmail);
        else
            return userRepository.findByUsername(usernameOrEmail);
    }

    public Set<User> getFollowers(User user, Pageable pageable)
    {
        return userRepository.findByFollowing(user);
    }

    public Optional<UserDetails> getUserDetailsBy(String username) {
        return getUserBy(username).map(UserDetails.class::cast);
    }

    public List<User> getFollowers(Optional<User> userOpt, Pageable pageable)
    {
        return userRepository.findByFollowing(userOpt.get(), pageable).getContent();
    }

    public List<User> getFollowedBy(User user, Pageable pageable) {
        return userRepository.findFollowingByUser(user, pageable).getContent();
    }

    public List<User> getFollowedBy(Optional<User> userOpt, Pageable pageable) 
    {
        assert userOpt.isPresent();
        return userRepository.findFollowingByUser(userOpt.get(), pageable).getContent();
    }

    public List<User> getBlockedBy(Optional<User> userOpt, Pageable pageable) 
    {
        assert userOpt.isPresent();
        return userRepository.findBlockedByUser(userOpt.get(), pageable).getContent();
    }

    public boolean isUsernameTaken(String username)
    {
        return userRepository.existsByUsername(username);
    }

    public UserService save(User user)
    {
        userRepository.save(user);
        return this;
    }

    public UserService flush()
    {
        userRepository.flush();
        return this;
    }


    public UserService save(OptTwo<User> users){
        users.forEach(userRepository::save);
        return this;
    }

    public UserService delete(User user)
    {
        userRepository.delete(user);
        userRepository.flush();
        return this;
    }

    public static boolean isVisitorAuthenticated(OptTwo<User> users)
    {
        return users.isRight();
    }

    public static boolean urlUserExists(OptTwo<User> users)
    {
        return users.isLeft();
    }

    public static boolean isSelfAction(OptTwo<User> users){
        if (users.isEmpty()) return false;
        return users.getRight().equals(users.getLeft());
    }

    public static boolean isVisitorAdmin(OptTwo<User> users)
    {
        return users.getRight().isAdmin();
    }

    public static boolean isAdmin(Optional<User> user)
    {
        return user.isPresent() && user.get().isAdmin();
    }

    public boolean isAdmin(HttpServletRequest request)
    {
        Optional<User> userOpt = this.getUserBy(request);
        return userOpt.isPresent() && userOpt.get().isAdmin();
    }

    public static boolean isOwnResource(String ownerUsername, Optional<User> loggedUser)
    {
        return loggedUser.isPresent() && loggedUser.get().getUsername().equals(ownerUsername);
    }

    public static String redirectToReferer(HttpServletRequest req)
    {
        return "redirect:" + req.getHeader("Referer");
    }

    public User buildHelper(String username, String email, String encodedPassword)
    {
        User.Builder builder = new User.Builder();

        builder
            .setUsername(username)
            .setEmail(email)
            .setEncodedPassword(encodedPassword);

        return builder.build();
    }


}