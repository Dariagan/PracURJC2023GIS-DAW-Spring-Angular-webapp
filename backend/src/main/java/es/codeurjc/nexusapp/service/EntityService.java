package es.codeurjc.nexusapp.service;

public interface EntityService <T>
{
    public EntityService <T> delete (T t);

    public EntityService <T> save (T t);
}
