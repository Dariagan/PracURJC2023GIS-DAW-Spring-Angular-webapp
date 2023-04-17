package es.codeurjc.nexusapp.utilities.queriers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.UserService;

public class FollowersQuerier extends CollectionQueriable<User, User, UserService>
{
    public FollowersQuerier(UserService service)
    {
        super(service);
    }

    @Override
    public Collection<User> doQuery(Optional<User> subject, Pageable pageable) 
    {
        return getService().getFollowers(subject, pageable);
    }
}
