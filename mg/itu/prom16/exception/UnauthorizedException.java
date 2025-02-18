package mg.itu.prom16.exception;

import mg.itu.prom16.annotation.RejectRole;
import mg.itu.prom16.annotation.RoleRequired;

public class UnauthorizedException extends Exception {
    private final Object annotation;
    private final Object sessionObject;

    public UnauthorizedException(String message, Object annotation, Object sessionObject) {
        super(message);
        this.annotation = annotation;
        this.sessionObject = sessionObject;
    }

    public Object getAnnotation() {
        return annotation;
    }

    public Object getSessionObject() {
        return sessionObject;
    }

    public String getAnnotationUnauthorizedRedirectUrl() {
        if (annotation instanceof RoleRequired) {
            return ((RoleRequired) annotation).unauthorizedRedirectUrl();
        } else if (annotation instanceof RejectRole) {
            return ((RejectRole) annotation).unauthorizedRedirectUrl();
        }
        return null;
    }
}
