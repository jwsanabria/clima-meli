package co.com.meli.clima.restclimatico.infrastructure.repository;

import java.util.Collection;
public interface CommonRepository<T> {
    public T save(T domain);
    public Iterable<T> save(Collection<T> domains);
    public void delete(T domain);
    public T findById(Integer id);
    public Iterable<T> findAll();

}