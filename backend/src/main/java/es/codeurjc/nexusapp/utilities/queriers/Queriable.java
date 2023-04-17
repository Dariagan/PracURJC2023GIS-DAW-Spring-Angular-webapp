package es.codeurjc.nexusapp.utilities.queriers;

import es.codeurjc.nexusapp.service.EntityService;

public abstract class Queriable<T, S extends EntityService<?>>
{    
    private S service;

    public Queriable(S service)
    {
        this.service = service;
    }

    S getService() {return service;}
}
