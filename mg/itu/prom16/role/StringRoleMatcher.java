package mg.itu.prom16.role;

public class StringRoleMatcher implements RoleMatcher {
    @Override
    public boolean matches(Object object, String role) {
        if (object instanceof String) {
            return object.equals(role);
        }
        return false;
    }
}
