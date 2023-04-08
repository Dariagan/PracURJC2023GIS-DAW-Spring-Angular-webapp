package es.codeurjc.backend.service;

public interface EntityService <T>
{
    public EntityService <T> delete (T t);

    public EntityService <T> save (T t);
}
