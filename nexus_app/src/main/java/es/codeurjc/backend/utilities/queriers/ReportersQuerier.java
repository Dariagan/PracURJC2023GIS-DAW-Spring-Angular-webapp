package es.codeurjc.backend.utilities.queriers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.TweetService;

public class ReportersQuerier extends CollectionQueriable<Tweet, User, TweetService>
{
    public ReportersQuerier(TweetService service)
    {
        super(service);
    }

    @Override
    public Collection<User> doQuery(Optional<Tweet> pathVariableSubject, Pageable pageable) 
    {
        return getService().getReportingUsers(pathVariableSubject, pageable);
    }
}
