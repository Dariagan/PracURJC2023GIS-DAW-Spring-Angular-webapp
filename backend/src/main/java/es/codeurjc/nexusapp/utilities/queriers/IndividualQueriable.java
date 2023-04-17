package es.codeurjc.nexusapp.utilities.queriers;

import java.util.Optional;

import es.codeurjc.nexusapp.service.EntityService;

public abstract class IndividualQueriable<T, S extends EntityService<T>> extends Queriable<T, S>
{
    public IndividualQueriable(S service)
    {
       super(service);
    }

    public abstract Optional<T> doQuery(String subjectId);
}
