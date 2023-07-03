package es.codeurjc.nexusapp.utilities.dtos;

import java.util.ArrayList;


import com.fasterxml.jackson.annotation.JsonIgnore;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import lombok.Getter;

@Getter
public class UserDto {
    
    private final String username, description, role;
    private final boolean banned, hasImage;
    private final ArrayList<String> 
        following = new ArrayList<>(), 
        followers  = new ArrayList<>(), 
        blocked = new ArrayList<>();
    private final ArrayList<Long> tweets = new ArrayList<>();

    

    @JsonIgnore
    public UserDto(User user){
        username = user.getUsername();
        description = user.getDescription();
        role = user.getRole().toString();
        banned = user.isBanned();
        hasImage = user.hasImage();
        for (Tweet tweet: user.getTweets())
            tweets.add(tweet.getId());
        for (User followed: user.getFollowing())
            following.add(followed.getUsername());
        for (User follower: user.getFollowers())
            followers.add(follower.getUsername());
        for (User blocked: user.getBlocked())
            this.blocked.add(blocked.getUsername());
    }

}
