package es.codeurjc.nexusapp.utilities.queriers;

import java.util.Optional;

import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.UserService;

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
