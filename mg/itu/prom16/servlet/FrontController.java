package mg.itu.prom16.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.util.PackageScanner;
import mg.itu.prom16.annotation.Controller;
import mg.itu.prom16.mapping.Mapping;

public class FrontController extends HttpServlet {

    Map<String,Mapping> controllerList;
    List<String> htmlContent;
    Class<? extends Annotation > annClass=Controller.class;
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            controllerList=PackageScanner.getMapping(getInitParameter("controllerPackage"), annClass);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        htmlContent=new ArrayList<>();
        try {
            String path = request.getServletPath().trim();
            Mapping map = controllerList.get(path);
            response.setContentType("text/html");
            if (map!=null) {
                Object object = map.execute();
                if (object instanceof String string) {
                    htmlContent.add(string);
                }
                else if (object instanceof ModelAndView modelAndView) {
                    writeModelAndViewToResponse(response, request, modelAndView);
                }
                else{
                    htmlContent.add("<h1>Error</h1>");
                    htmlContent.add("<p>Type non reconnu</p>");
                    writeTableToResponse(response, htmlContent);    
                }
            }
            else{
                htmlContent.add("<h1>Error 404</h1>");
                htmlContent.add("<p>lien tsy misy Exception</p>");
                writeTableToResponse(response, htmlContent);
            }
            
        } catch (Exception e) {
            throw new ServletException(e);    
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
    public void writeTableToResponse(HttpServletResponse response, List<String> rows) throws IOException {
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html><body>");
            for (String row : rows) {
                out.println(row);
            }
            out.println("</body></html>");
        }
    }
    public void writeModelAndViewToResponse(HttpServletResponse response,HttpServletRequest request, ModelAndView modelAndView) throws IOException, ServletException {
        String viewName = modelAndView.getViewName();
        StringBuilder stringBuilder = new StringBuilder(getInitParameter("viewFolder"));
        stringBuilder.append(viewName);
        stringBuilder.append(getInitParameter("suffixe"));
        for (Entry<String,Object> keyValueEntry : modelAndView.getModel().entrySet()) {
            request.setAttribute(keyValueEntry.getKey(), keyValueEntry.getValue());
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(stringBuilder.toString());
        dispatcher.forward(request, response);
    }
    
}