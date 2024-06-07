package mg.itu.prom16.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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
import mg.itu.prom16.exception.build.BuildException;
import mg.itu.prom16.exception.request.TypeNotRecognizedException;
import mg.itu.prom16.mapping.Mapping;

public class FrontController extends HttpServlet {

    Map<String,Mapping> controllerList;
    Class<? extends Annotation > annClass=Controller.class;
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            controllerList=PackageScanner.getMapping(getInitParameter("controllerPackage"), annClass);
        } catch (BuildException e) {
            throw new Error(e);
        }
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            String path = request.getServletPath().trim();
            Mapping map = controllerList.get(path);
            response.setContentType("text/html");
            try {
                if (map!=null) {
                    handleResponse(request, response, map);
                }
                else{
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new ServletException(e);
            } catch (TypeNotRecognizedException e) {
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
    private void writeToResponse(HttpServletResponse response, List<String> rows) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            for (String row : rows) {
                out.println(row);
            }
        }
    }
    private void writeModelAndViewToResponse(HttpServletResponse response,HttpServletRequest request, ModelAndView modelAndView) throws IOException, ServletException {
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

    public void handleResponse(HttpServletRequest request, HttpServletResponse response, Mapping map) throws IOException, TypeNotRecognizedException, ServletException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object object = map.execute();
        if (object instanceof String string) {
            List<String> htmlContent = new ArrayList<>();
            htmlContent.add(string);
            writeToResponse(response, htmlContent);
        } else if (object instanceof ModelAndView modelAndView) {
            writeModelAndViewToResponse(response, request, modelAndView);
        } else {
            throw new TypeNotRecognizedException(object.getClass().getTypeName(), map);
        }
    }   
}