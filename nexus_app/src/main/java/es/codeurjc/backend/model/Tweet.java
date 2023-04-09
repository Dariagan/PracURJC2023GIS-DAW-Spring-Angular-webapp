package es.codeurjc.backend.model;

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


// Programmed by group 13-A
@Entity(name = "Tweet")
public class Tweet implements Comparable<Tweet>
{    
    public interface AuthorView extends User.BasicView{}
    public interface BasicView {}
    public interface LikesView extends User.BasicView{}
    public interface ReportersView extends User.UsernameView{}
    public interface FullView extends Tweet.AuthorView, Tweet.BasicView, Tweet.LikesView, Tweet.ReportersView {}

    @JsonView(BasicView.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonView(AuthorView.class)
    @ManyToOne
    private User author;

    @JsonView(BasicView.class)
    private LocalDateTime date;

    @JsonView(ReportersView.class)
    @ManyToMany
    private Set<User> reporters = new HashSet<>();

    @JsonView(BasicView.class)
    @Column(columnDefinition = "TEXT")
    private String text;

    @Nullable
    @Lob
    @JsonIgnore
    private Blob media;

    @JsonView(BasicView.class)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tags = new HashSet<String>();

    @JsonView(LikesView.class)
    @Nullable
    @ManyToMany
    private Set<User> likes  = new HashSet<>();

    @JsonIgnore
    @Nullable
    @OneToMany
    private List<Tweet> children = new ArrayList<>();

    @JsonIgnore
    @Nullable
    @OneToMany(cascade = CascadeType.ALL)  
    private Set<Tweet> shares = new HashSet<>();

    public static class Builder {
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

    private Tweet(Tweet.Builder builder) {
        this(builder.author, builder.text, builder.media, builder.tags);
    }
    private Tweet(User author, String text, Blob media, HashSet<String> tags) {
        this.author = author;
        this.date = LocalDateTime.now();
        this.text = text;
        this.media = media;
        this.tags = tags;
        //this.likes.add(author);
    }

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public User getAuthor() {return author;}
    public String getUserName() {return author.getUsername();}
    public void setNullAuthor() {this.author = null;}
    public boolean authorIsNull() {return this.author == null;}

    public LocalDateTime getDate() {return date;}

    public Set<User> getReporters() {
        return reporters;
    }
    public void report(User reporter) {
        reporters.add(reporter);
    }

    public String getText() {return text;}
    public void setText(String text) {this.text = text;}
    
    public Set<User> getLikes() {return likes;}
    public void switchLike(User user) {
        assert(user != null);
        if (!likes.contains(user))
            likes.add(user);
        else likes.remove(user);
    }

    public Set<Tweet> getShares() {return shares;}
    
    public Set<String> getTags() {return tags;}
    public void addTag(String String) {
        //assert(this.parent == null);
        this.tags.add(String);
    }

    public List<Tweet> getChildren() {return children;}

    /* FIXME
    public void reply(Tweet tweet, TweetRepository tweetRepository) {
        assert tweet != this;
        children.add(tweet);
        tweetRepository.save(this);
        tweetRepository.save(tweet);
    }
    */

    public Blob getMedia() {return media;}
    public boolean hasMedia() {return media != null;}

    //DON'T USE, ONLY FOR DATABASE
    public Tweet() {}
    @Override
    public int compareTo(Tweet o) {
        
       return this.date.compareTo(o.date);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Objects.hash(id, author, date);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Tweet other = (Tweet) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(author, other.author)
                && Objects.equals(date, other.date);
    }

	
}
