package mg.itu.prom16.request;

public class MappingNotAllowedException extends Exception {

    public MappingNotAllowedException() {
        super();
    }

    public MappingNotAllowedException(String message) {
        super(message);
    }

    public MappingNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingNotAllowedException(Throwable cause) {
        super(cause);
    }
}
