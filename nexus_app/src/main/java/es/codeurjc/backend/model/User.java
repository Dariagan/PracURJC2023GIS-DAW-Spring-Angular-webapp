package es.codeurjc.backend.model;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Base64;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import es.codeurjc.backend.repository.UserRepository;
import es.codeurjc.backend.service.UserService;

import org.springframework.lang.Nullable;

// Class initially defined by group 13 B in a basic manner, 
// refactored, reprogrammed, given all functionality by group 13 A
@Entity(name = "UserTable")
public class User {
    
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
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();
    
    @JsonView(BasicView.class)
    private boolean banned = false;

    @Nullable
    @Lob
    @JsonIgnore
    private Blob profilePicture = null;

    @JsonView(TweetsView.class)
    @OneToMany(mappedBy = "author")
    private List<Tweet> tweets = new ArrayList<Tweet>();

    @JsonView(FollowingView.class)
    @Nullable
    @ManyToMany
    private Set<User> following = new HashSet<User>();

        @JsonIgnore
        @Nullable
        @ManyToMany
        private Set<User> blockedUsers = new HashSet<>();  

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
        public Builder setAdmin(){
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
            assert username != null && !username.equals("") && username.length() <= 25

            && name != null && name.length() <= 25

            && email != null && UserService.isEmail(email)
            
            && encodedPassword != null && encodedPassword.length() >= 10;

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
        this.roles.add("USER");
        if (admin) this.roles.add("ADMIN");
        this.profilePicture = null;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name; }

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getEncodedPassword() {return encodedPassword;}
    public void setEncodedPassword(String encodedPassword) {this.encodedPassword = encodedPassword;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public void ban() {
        this.banned = true;
        roles.remove("USER");
    }
    public void unban() {
        this.banned = false;
        roles.add("USER");
    }
    public boolean isBanned() {return banned;}

    public Blob getProfilePicture() {return profilePicture;}
    public boolean hasProfilePicture() {return profilePicture != null;}
    public void setProfilePicture(Blob profilePicture, UserRepository userRepository) {
        this.profilePicture = profilePicture;
        userRepository.save(this);
    }

    public List<Tweet> getTweets() {return tweets;}
  
    public Set<String> getRoles() {return this.roles;}

    public boolean isAdmin() {return roles.contains("ADMIN");}
    public void setAdmin() {assert(!isAdmin()); this.roles.add("ADMIN");}
    public void removeAdmin(){this.roles.remove("ADMIN");}

    public Set<User> getFollowers(UserService userService) {
        return userService.getFollowers(this);
    }
    public Set<User> getFollowing() {return following;}
    public void switchFollow(User user) {
        assert user != null && !user.equals(this);
        if (!following.contains(user))
            following.add(user);
        else following.remove(user);
    }

    public Set<User> getBlockedUsers() {return blockedUsers;}
    public void block(User user) {
        assert user != this;
        blockedUsers.add(user);
    };
    public void unblock(User user){
        assert user != this;
        blockedUsers.remove(user);
    };

    public LocalDateTime getSignUpDate() {return signUpDate;}

    public String toString() {return username;}

    //DON'T USE, ONLY FOR DATABASE 
    public User() {}
}
