package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.util.PackageScanner;
import mg.itu.prom16.annotation.Controller;

public class FrontController extends HttpServlet {

    boolean isInstanced;
    List<Class<?>> controllerList;
    Class<? extends Annotation> annClass=Controller.class;
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            controllerList=PackageScanner.getClassesFromPackage(getInitParameter("controllerPackage"), annClass);
            isInstanced=true;
        } catch (Exception e) {
            isInstanced=false;
        }
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isInstanced) {
            try {
                controllerList=PackageScanner.getClassesFromPackage(getInitParameter("controllerPackage"), annClass);
                isInstanced=true;
            } catch (Exception e) {
                isInstanced=false;
            }   
        }

        response.setContentType("text/html");

        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            out.println("<h1>Servlet Path: " + request.getServletPath() + "</h1>");
            out.println("<ul>");
            for(Class<?> class1:controllerList){
                out.println("<li>"+class1.getName()+"</li>");
            }
            out.println("</ul>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
}
