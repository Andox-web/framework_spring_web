package mg.itu.prom16.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.annotation.controller.Controller;
import mg.itu.prom16.annotation.response.ResponseBody;
import mg.itu.prom16.exception.ErrorPrinter;
import mg.itu.prom16.exception.build.BuildException;
import mg.itu.prom16.exception.request.MappingNotAllowedException;
import mg.itu.prom16.exception.request.TypeNotRecognizedException;
import mg.itu.prom16.interceptor.Interceptor;
import mg.itu.prom16.mapping.ExecutionResult;
import mg.itu.prom16.mapping.Mapper;
import mg.itu.prom16.mapping.Mapping;
import mg.itu.prom16.mapping.VerbMapping;
import mg.itu.prom16.response.ResponseHandler;
import mg.itu.prom16.response.StringResponseHandler;
import mg.itu.prom16.util.BeanFactory;
import mg.itu.prom16.util.Environment;
import mg.itu.prom16.util.InterceptorUtil;
import mg.itu.prom16.util.PackageScanner;

public class FrontController extends HttpServlet {

    private Mapper mapper;
    private Class<? extends Annotation > annClass=Controller.class;
    private final StringResponseHandler stringResponseHandler = new StringResponseHandler();
    private Interceptor[] interceptors ;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            String propertiesFile = getInitParameter("propertiesFile");
            if (propertiesFile == null) {
                propertiesFile = getServletContext().getInitParameter("propertiesFile");
                if (propertiesFile == null) {
                    propertiesFile = "application.properties";
                }
            }
            Environment.loadEnvironmentClass(propertiesFile);
            PackageScanner.scan(Environment.getProperty("projectPackage"));
            BeanFactory.createBeans();
            interceptors = InterceptorUtil.getInterceptors();
            String controllerPackage = Environment.getProperty("controllerPackage");
            if (controllerPackage == null) {
                controllerPackage = "";
            }
            mapper = new Mapper(controllerPackage, annClass);
        } catch (Exception e) {
            throw new ServletException(new BuildException("Failed to initialize FrontController", e));
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String url = request.getServletPath();
        if (isValidResource(url)) {
            processResource(url, response);
            return;
        }
        
        VerbMapping verbMapping= new VerbMapping(request);
        Mapping map = mapper.get(verbMapping);
        response.setContentType("text/html");
        Exception exception = null;
        try {
            
            if (map != null) {
                for (Interceptor interceptor : interceptors) {
                    if (!interceptor.preHandle(request, response, map.getMethod())) {
                        return;
                    }
                }
                ExecutionResult executionResult = map.execute(request, response);
                for (Interceptor interceptor : interceptors) {
                    interceptor.postHandle(request, response, executionResult);
                }
                handleResponse(request, response, executionResult);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
        } catch (TypeNotRecognizedException e) {
            exception=e;
            throw new ServletException(e);
        } catch (NoSuchMethodException e) {
            exception=e;
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IllegalArgumentException e) {
            exception=e;
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (MappingNotAllowedException e) {
            exception=e;
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } catch (Exception e) {
            exception=e;
            if (getInitParameter("errorPage") != null) {
                request.setAttribute("error", e);

                StringBuilder stringBuilder = new StringBuilder(getInitParameter("viewFolder"));
                stringBuilder.append(getInitParameter("errorPage"));
                stringBuilder.append(getInitParameter("suffixe"));

                RequestDispatcher dispatcher = request.getRequestDispatcher(stringBuilder.toString());
                dispatcher.forward(request, response);
            }else ErrorPrinter.printExceptionHtml(response.getWriter(), e);
            e.printStackTrace();
        } finally {
            for (Interceptor interceptor : interceptors) {
                try {
                    interceptor.afterCompletion(request, response, map!=null ? map.getMethod():null, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    public void handleResponse(HttpServletRequest request, HttpServletResponse response, ExecutionResult executionResult) throws Exception {
        Object object = executionResult.getResult();
        
        if (executionResult.getMapping().isRest()||executionResult.getMapping().getMethod().isAnnotationPresent(ResponseBody.class)) {
            if (object instanceof ResponseHandler responseHandler) {
               stringResponseHandler.handleJsonResponse(response, responseHandler.getGsonnableResponse());
                return;
            }
            stringResponseHandler.handleJsonResponse(response, object);
        } else if (object instanceof String string) {
            stringResponseHandler.handleStringResponse(request, response, string);
        } else if (object instanceof ResponseHandler responseHandler) {
            responseHandler.processAction(request, response);
        } else {
            throw new TypeNotRecognizedException(object.getClass().getTypeName(), executionResult.getMapping());
        }
    }

    private boolean isValidResource(String path) {
        return path != null && (path.endsWith(".png") || path.endsWith(".jpg") || 
                path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".html"));
    }
    private void processResource(String path, HttpServletResponse response) throws IOException {
        // Construire le chemin réel du fichier dans WEB-INF
        String realPath = path;

        // Charger la ressource depuis le classpath (WEB-INF)
        try (InputStream resourceStream = getServletContext().getResourceAsStream(realPath)) {
            if (resourceStream == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Déterminer le type MIME du fichier
            String mimeType = getServletContext().getMimeType(realPath);
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // Type par défaut
            }
            response.setContentType(mimeType);

            // Copier le fichier dans la réponse
            ServletOutputStream out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}