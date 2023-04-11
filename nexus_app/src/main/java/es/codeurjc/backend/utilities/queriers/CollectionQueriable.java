package es.codeurjc.backend.utilities.queriers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import es.codeurjc.backend.service.EntityService;

public abstract class CollectionQueriable<T, U, S extends EntityService<?>> extends Queriable <T, S>
{
    public CollectionQueriable(S service) 
    {
        super(service);
    }

    public abstract Collection<U> doQuery(Optional<T> pathVariableSubject, Pageable pageable);
}
