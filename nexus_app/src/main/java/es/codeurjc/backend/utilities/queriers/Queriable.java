package es.codeurjc.backend.utilities.queriers;

import es.codeurjc.backend.service.EntityService;

public abstract class Queriable<T, S extends EntityService<?>>
{    
    private S service;

    public Queriable(S service)
    {
        this.service = service;
    }

    S getService() {return service;}
}
