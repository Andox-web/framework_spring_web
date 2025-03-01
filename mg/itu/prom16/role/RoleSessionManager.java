package mg.itu.prom16.role;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.prom16.annotation.RejectRole;
import mg.itu.prom16.annotation.RoleRequired;
import mg.itu.prom16.build.BuildException;
import mg.itu.prom16.environment.Environment;
import mg.itu.prom16.exception.UnauthorizedException;

public class RoleSessionManager {

    private Object sessionObject;
    private String sessionName;
    private RoleMatcher roleMatcher;
    private String unauthorizedRedirectUrl;

    public RoleSessionManager(HttpServletRequest request) throws BuildException {
        this.sessionName = Environment.getProperty("roleSessionName", "roleSession");
        this.sessionObject = request.getSession().getAttribute(this.sessionName);

        String roleMatcherClassName = Environment.getProperty("roleMatcher");
        if (roleMatcherClassName != null && !roleMatcherClassName.isEmpty()) {
            try {
                Class<?> clazz = Class.forName(roleMatcherClassName);
                this.roleMatcher = (RoleMatcher) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new BuildException("Error while creating RoleMatcher instance", e);
            }
        } else {
            this.roleMatcher = new StringRoleMatcher();
        }

        this.unauthorizedRedirectUrl = Environment.getProperty("unauthorizedRedirectUrl");
    }

    public boolean hasRole(String[] roles) {
        if (roles == null || roles.length == 0 || sessionObject == null) {
            return false;
        }
        for (String role : roles) {
            if (roleMatcher.matches(sessionObject, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkAnnotation(Object annotation) throws UnauthorizedException {
        if (annotation instanceof RoleRequired) {
            RoleRequired roleRequired = (RoleRequired) annotation;
            if (sessionObject == null || (roleRequired.value().length != 0 && !hasRole(roleRequired.value()))) {
                throw new UnauthorizedException("You are not authorized to access that page", annotation, sessionObject);
            }
        } else if (annotation instanceof RejectRole) {
            RejectRole rejectRole = (RejectRole) annotation;
            if (hasRole(rejectRole.value())) {
                throw new UnauthorizedException("You are not authorized to access that page", annotation, sessionObject);
            }
        }
        return true;
    }

    public boolean checkClassAndMethodAnnotations(Class<? extends Annotation> annotationClass, Method method) throws UnauthorizedException {
        Class<?> declaringClass = method.getDeclaringClass();

        Object classRoleRequired = declaringClass.getAnnotation(annotationClass);
        if (classRoleRequired != null && !checkAnnotation(classRoleRequired)) {
            return false;
        }

        Object methodRoleRequired = method.getAnnotation(annotationClass);
        if (methodRoleRequired != null && !checkAnnotation(methodRoleRequired)) {
            return false;
        }

        return true;
    }

    public boolean checkRole(Method method) throws UnauthorizedException {
        if (!checkClassAndMethodAnnotations(RoleRequired.class, method)) {
            return false;
        }
        if (!checkClassAndMethodAnnotations(RejectRole.class, method)) {
            return false;
        }
        return true;
    }

    public Object getSessionObject() {
        return sessionObject;
    }

    public void setSessionObject(Object sessionObject) {
        this.sessionObject = sessionObject;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public RoleMatcher getRoleMatcher() {
        return roleMatcher;
    }

    public void setRoleMatcher(RoleMatcher roleMatcher) {
        this.roleMatcher = roleMatcher;
    }

    public String getUnauthorizedRedirectUrl() {
        return unauthorizedRedirectUrl;
    }

    public void setUnauthorizedRedirectUrl(String unauthorizedRedirectUrl) {
        this.unauthorizedRedirectUrl = unauthorizedRedirectUrl;
    }
    
}
