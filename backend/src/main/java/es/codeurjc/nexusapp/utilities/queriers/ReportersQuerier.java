package es.codeurjc.nexusapp.utilities.queriers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.TweetService;

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
