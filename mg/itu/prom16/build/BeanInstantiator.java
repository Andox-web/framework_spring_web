package mg.itu.prom16.build;

public interface BeanInstantiator<T> {
    T instantiate() throws Exception;
}
