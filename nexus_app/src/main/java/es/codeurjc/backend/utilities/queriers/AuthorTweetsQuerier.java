package es.codeurjc.backend.utilities.queriers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.TweetService;

public class AuthorTweetsQuerier extends CollectionQueriable<User, Tweet, TweetService>
{
    public AuthorTweetsQuerier(TweetService service) 
    {
        super(service);
    }

    @Override
    public Collection<Tweet> doQuery(Optional<User> pathVariableSubject, Pageable pageable)
    {
        assert pathVariableSubject.isPresent();
        return getService().getTweetsByUser(pathVariableSubject.get());
    }
    
}
