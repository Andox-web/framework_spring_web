package mg.itu.prom16.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.build.BeanFactory;
import mg.itu.prom16.environment.Environment;
import mg.itu.prom16.gson.GsonConfiguration;

public class StringResponseHandler {

    private static final String[] SPECIAL_PREFIXES = {"redirect:"};

    public static boolean startsWithAny(String string) {

        for (String prefix : SPECIAL_PREFIXES) {
            if (string.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public void handleStringResponse(HttpServletRequest request, HttpServletResponse response, String string) throws IOException, ServletException {
        if (startsWithAny(string)) {
            handleSpecialPrefix(request, response, string);
        } else {
            String viewFolder = Optional.ofNullable(Environment.getProperty("viewFolder"))
                        .orElse("");
            String suffix = Optional.ofNullable(Environment.getProperty("suffixe"))
                        .orElse("");

            StringBuilder stringBuilder = new StringBuilder(viewFolder)
                                                .append(string)
                                                .append(suffix);
    
            RequestDispatcher dispatcher = request.getRequestDispatcher(stringBuilder.toString());
            dispatcher.forward(request, response);
        }
    }

    private void handleSpecialPrefix(HttpServletRequest request, HttpServletResponse response, String string) throws IOException, ServletException {
        if (string.startsWith("redirect:")) {
            String redirectUrl = string.substring("redirect:".length());
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + redirectUrl);
        }
        // Add more prefix handling logic here if needed
    }

    public void handleJsonResponse(HttpServletResponse response, Object object) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        List<String> jsonResponse = new ArrayList<>();
        jsonResponse.add(GsonConfiguration.getGson().toJson(object));
        writeToResponse(response, jsonResponse);
    }

    public void writeToResponse(HttpServletResponse response, List<String> rows) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            for (String row : rows) {
                out.println(row);
            }
        }
    }
}
