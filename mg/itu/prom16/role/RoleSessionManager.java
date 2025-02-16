package mg.itu.prom16.role;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.RejectRole;
import mg.itu.prom16.annotation.RoleRequired;
import mg.itu.prom16.exception.build.BuildException;

public class RoleSessionManager {

    private Object sessionObject;
    private String sessionName;
    private RoleMatcher roleMatcher;

    public RoleSessionManager(HttpServletRequest request) throws BuildException {
        this.sessionName = request.getServletContext().getInitParameter("sessionName");
        if (this.sessionName == null || this.sessionName.isEmpty()) {
            this.sessionName = "roleSession";
        }
        this.sessionObject = request.getSession().getAttribute(this.sessionName);

        String roleMatcherClassName = request.getServletContext().getInitParameter("roleMatcher");
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

    public boolean checkRole(Method method, HttpServletResponse response) throws Exception {
        RoleRequired roleRequired = method.getAnnotation(RoleRequired.class);
        if (roleRequired != null) {
            if(sessionObject==null || (roleRequired.value().length!=0 && !hasRole(roleRequired.value()))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }

        RejectRole rejectRole = method.getAnnotation(RejectRole.class);
        if (rejectRole != null && hasRole(rejectRole.value())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
