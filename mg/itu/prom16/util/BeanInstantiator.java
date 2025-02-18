package mg.itu.prom16.util;

public interface BeanInstantiator<T> {
    T instantiate() throws Exception;
}
