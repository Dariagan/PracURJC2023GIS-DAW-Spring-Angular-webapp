package es.codeurjc.backend.utilities.queriers;

import java.util.Optional;

import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.UserService;

public class UserQuerier extends IndividualQueriable<User, UserService>
{
    public UserQuerier(UserService service)
    {
        super(service);
    }

    @Override
    public Optional<User> doQuery(String subjectId)
    {
        return getService().getUserBy(subjectId);
    }
}
