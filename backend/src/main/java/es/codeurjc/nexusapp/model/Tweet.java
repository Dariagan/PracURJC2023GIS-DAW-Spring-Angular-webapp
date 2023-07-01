package es.codeurjc.nexusapp.model;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// Programmed by group 13-A
@EqualsAndHashCode
@NoArgsConstructor
@Entity(name = "Tweet")
public class Tweet implements Comparable<Tweet>
{    
    public interface AuthorView extends User.BasicView {}
    public interface TweetIdentifyingView {}
    public interface BasicView extends TweetIdentifyingView{} 
    public interface UserListsView extends User.UsernameView{}

    public interface FullView extends Tweet.AuthorView, BasicView, UserListsView  {
  
    }
    @JsonView(TweetIdentifyingView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter private long id;

    @JsonView(AuthorView.class)
    @ManyToOne
    @Getter private User author;

    @JsonView(BasicView.class)
    @Getter private LocalDateTime date;

    @JsonView(UserListsView.class)
    @ManyToMany
    @Getter private Set<User> reporters = new HashSet<>();

    @JsonView(BasicView.class)
    @Column(columnDefinition = "TEXT")
    @Getter @Setter private String text;

    @Nullable
    @Lob
    @JsonIgnore
    @Getter private Blob media;

    @JsonView(BasicView.class)
    @ElementCollection(fetch = FetchType.EAGER)
    @Getter private Set<String> tags = new HashSet<String>();

    @JsonView(UserListsView.class) // Update view to LikesView
    @Nullable
    @ManyToMany
    @Getter private Set<User> likes = new HashSet<>();

    @JsonView(TweetIdentifyingView.class) // Update view to ReportersView
    @JsonIgnore
    @Nullable
    @OneToMany
    @Getter private List<Tweet> children = new ArrayList<>();

    @JsonView(UserListsView.class) // Update view to SharesView
    @JsonIgnore
    @Nullable
    @OneToMany(cascade = CascadeType.ALL)
    @Getter private Set<Tweet> shares = new HashSet<>();

    public static class Builder
    {
        private User author;
        private String text;
        private Blob media;
        private HashSet<String> tags = new HashSet<String>();

        public Builder setAuthor(User author){
            this.author = author;
            return this;
        }
        public Builder setText(String text){
            this.text = text;
            return this;
        }
        public Builder setMedia(Blob media){
            this.media = media;
            return this;
        }
        public Builder setTags(HashSet<String> tags){
            this.tags = tags;
            return this;
        }
        public Builder clearTags(){
            this.tags.clear();
            return this;
        }
        public Builder addTag(String String){
            //assert(parent == null);
            this.tags.add(String);
            return this;
        }
        public void reset(){
            author = null; text = ""; media = null; tags.clear();
        }
        public Tweet build(){
            assert(author != null && text != null && !text.equals(""));
            return new Tweet(this);
        }
    }
    private Tweet(Tweet.Builder builder)
    {
        this(builder.author, builder.text, builder.media, builder.tags);
    }
    private Tweet(User author, String text, Blob media, HashSet<String> tags)
    {
        this.author = author;
        this.date = LocalDateTime.now();
        this.text = text;
        this.media = media;
        this.tags = tags;
        //this.likes.add(author);
    }

    public String getUserName() {return author.getUsername();}
    public void setNullAuthor() {this.author = null;}
    public boolean authorIsNull() {return this.author == null;}

    public void report(User reporter) {
        reporters.add(reporter);
    }
    
    public void switchLike(User user) {
        assert(user != null);
        if (!likes.contains(user))
            likes.add(user);
        else likes.remove(user);
    }

    public void addTag(String String) {
        //assert(this.parent == null);
        this.tags.add(String);
    }


    /* NOTE this business logic should be deoupled to a controller
    public void reply(Tweet tweet, TweetRepository tweetRepository) {
        assert tweet != this;
        children.add(tweet);
        tweetRepository.save(this);
        tweetRepository.save(tweet);
    }
    */
    public boolean hasMedia() {return media != null;}

    @Override
    public int compareTo(Tweet o) {return date.compareTo(o.date);}
}
