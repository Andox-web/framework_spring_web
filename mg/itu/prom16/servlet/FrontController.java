package mg.itu.prom16.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.util.PackageScanner;
import mg.itu.prom16.annotation.controller.Controller;
import mg.itu.prom16.exception.build.BuildException;
import mg.itu.prom16.exception.request.ArgumentException;
import mg.itu.prom16.exception.request.MappingNotAllowedException;
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
        }catch (TypeNotRecognizedException e) {
            throw new ServletException(e);
        } catch (NoSuchMethodException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (MappingNotAllowedException e) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } catch (ArgumentException|ReflectiveOperationException|SecurityException e) {
            request.setAttribute("error", e);

            StringBuilder stringBuilder = new StringBuilder(getInitParameter("viewFolder"));
            stringBuilder.append(getInitParameter("errorPage")!=null?getInitParameter("errorPage"):"error");
            stringBuilder.append(getInitParameter("suffixe"));

            RequestDispatcher dispatcher = request.getRequestDispatcher(stringBuilder.toString());
            dispatcher.forward(request, response);
            return;
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

    public void handleResponse(HttpServletRequest request, HttpServletResponse response, Mapping map) throws IOException, TypeNotRecognizedException, ServletException, IllegalArgumentException, MappingNotAllowedException, ArgumentException, ReflectiveOperationException {
        Object object = map.execute(request, response);

        if (map.isRest()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            List<String> htmlContent = new ArrayList<>();
                
            if (object instanceof ModelAndView modelAndView) {
                htmlContent.add(new Gson().toJson(modelAndView.getModel()));
            }else{
                htmlContent.add(new Gson().toJson(object));
            }
            writeToResponse(response, htmlContent);
            
        } else if (object instanceof String string) {
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