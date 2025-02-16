package mg.itu.prom16.response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ResponseHandler {
    Object getGsonnableResponse();
    void processAction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
    void handleAction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
