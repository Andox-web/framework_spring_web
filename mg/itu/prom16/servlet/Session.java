package mg.itu.prom16.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class Session {

    private HttpSession session;

    // Constructor that initializes the session from the request
    public Session(HttpServletRequest request) {
        this.session = request.getSession(true);
    }

    // Method to set an attribute in the session
    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    // Method to get an attribute from the session
    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    // Method to remove an attribute from the session
    public void removeAttribute(String name) {
        session.removeAttribute(name);
    }

    // Method to invalidate the session
    public void invalidate() {
        session.invalidate();
    }

    // Method to get the session ID
    public String getSessionId() {
        return session.getId();
    }
}
