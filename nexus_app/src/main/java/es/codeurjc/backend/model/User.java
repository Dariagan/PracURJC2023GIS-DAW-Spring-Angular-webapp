package es.codeurjc.backend.model;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import es.codeurjc.backend.repository.UserRepository;
import es.codeurjc.backend.service.EmailService;
import es.codeurjc.backend.service.UserService;

import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Class initially defined by group 13 B in a basic manner, 
// refactored, reprogrammed, given all functionality by group 13 A
@Entity(name = "UserTable")
public class User implements UserDetails
{
    public interface UsernameView {}
    public interface BasicView extends UsernameView{}
    public interface TweetsView {}
    public interface FollowingView extends UsernameView{}
    public interface FullView extends User.BasicView, User.TweetsView, Tweet.BasicView {}
    
    @JsonView(UsernameView.class)
    @Id
    private String username;

    @JsonView(BasicView.class)
    private String name;

    @JsonView(BasicView.class)
    private String email;

    @JsonView(BasicView.class)
    private String description = "";

    @JsonView(BasicView.class)
    private LocalDateTime signUpDate = LocalDateTime.now();

    @JsonIgnore
    private String encodedPassword;

    @JsonView(BasicView.class)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Nullable
    @Lob
    @JsonIgnore
    private Blob profilePicture;

    @JsonView(TweetsView.class)
    @OneToMany(mappedBy = "author")
    private List<Tweet> tweets = new ArrayList<>();

    @JsonView(FollowingView.class)
    @Nullable
    @ManyToMany
    private Set<User> following = new HashSet<>();

    @JsonIgnore
    @Nullable
    @ManyToMany
    private Set<User> blocked = new HashSet<>();

    public static class Builder {
        private String username, email, encodedPassword, name = "", description = "";
        private boolean admin = false;
        private boolean banned = false;

        public Builder setName(String name){
            this.name = name;
            return this;
        }
        public Builder setUsername(String username){
            this.username = username;
            return this;
        }
        public Builder setEmail(String email){
            this.email = email;
            return this;
        }
        public Builder setEncodedPassword(String encodedPassword){
            this.encodedPassword = encodedPassword;
            return this;
        }
        public Builder setDescription(String description){
            assert description != null;
            this.description = description;
            return this;
        }
        public Builder setBasicUser(){
            this.admin = false;
            return this;
        }
        public Builder setIsAdmin(){
            this.admin = true;
            return this;
        }
        public Builder setBan(){
            this.banned = true;
            return this;
        }
        public void reset(){
            username = null; email = null; encodedPassword = null; 
            name = ""; description = ""; admin = false; banned = false;
        }
        public User build(){
            assert (
                username != null && !username.equals("") && username.length() <= 25 &&
                name != null && name.length() <= 25 &&
                email != null && EmailService.isEmail(email) &&
                encodedPassword != null && encodedPassword.length() >= 10
            );

            return new User(this);
        }
    }
    
    private User(User.Builder builder){
        this(
            builder.username, builder.email, builder.encodedPassword, builder.name,
            builder.description, builder.admin, builder.banned
        );
    }

    private User(
        String username, String email, String encodedPassword, String name,
        String description, boolean admin, boolean banned
    ) {
        this.username = username;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.name = name;
        this.description = description;
        this.profilePicture = null;

        if (admin) this.role = Role.ADMIN;
        else this.role = Role.USER;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name; }

    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getEncodedPassword() {return encodedPassword;}
    public void setEncodedPassword(String encodedPassword) {this.encodedPassword = encodedPassword;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public void ban() { role = Role.BANNED; }

    public void unban() { role = Role.USER; }

    public boolean isBanned() { return role == Role.BANNED; }

    public Blob getProfilePicture() {return profilePicture;}
    public boolean hasProfilePicture() {return profilePicture != null;}
    public void setProfilePicture(Blob profilePicture, UserRepository userRepository) {
        this.profilePicture = profilePicture;
        userRepository.save(this);
    }

    public List<Tweet> getTweets() {return tweets;}
  
    public Role getRole() { return role; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    public void setAdmin() { role = Role.ADMIN; }

    public void removeAdmin() { role = Role.USER; }

    public Set<User> getFollowers(UserService userService)
    {
        return userService.getFollowers(this);
    }

    public Set<User> getFollowing() { return following; }

    // FIXME the next few codeblocks can produce null pointer exceptions.
    public void switchFollow(User user)
    {
        assert user != null && !user.equals(this);
        if (!following.contains(user))
            following.add(user);
        else following.remove(user);
    }

    public Set<User> getBlocked() {return blocked;}

    public Set<User> getBlockedUsers() { return blocked; }

    public void block(User user)
    {
        assert user != this;
        blocked.add(user);
    };

    public void unblock(User user)
    {
        assert user != this;
        blocked.remove(user);
    };

    public LocalDateTime getSignUpDate() { return signUpDate; }

    public String toString() { return username; }

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return encodedPassword;
    }

    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
