package mg.itu.prom16.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;

public class ServletUtils {
    public static String getParameter(HttpServletRequest request, String paramName) throws IOException {
        String contentType = request.getContentType();

        // Traitement pour le cas JSON
        if (contentType != null && contentType.contains("application/json")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String jsonString = sb.toString();
            if (jsonString != null && !jsonString.isEmpty()) {
                try {
                    JsonObject jsonObj = JsonParser.parseString(jsonString).getAsJsonObject();
                    if (jsonObj.has(paramName)) {
                        JsonElement element = jsonObj.get(paramName);
                        // Si c'est une valeur primitive, on utilise getAsString(), sinon on retourne la représentation JSON de l'objet/array
                        if (element.isJsonPrimitive()) {
                            return element.getAsString();
                        } else {
                            return element.toString();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Vous pouvez gérer l'erreur selon vos besoins
                }
            }
        } 
        // Traitement pour le cas form-urlencoded
        else if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
            String paramValue = request.getParameter(paramName);
            if (paramValue == null) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = request.getReader()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                String body = sb.toString();
                String[] params = body.split("&");
                for (String param : params) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && pair[0].equals(paramName)) {
                        paramValue = URLDecoder.decode(pair[1], "UTF-8");
                        break;
                    }
                }
            }
            return paramValue;
        } 
        // Fallback
        else {
            return request.getParameter(paramName);
        }

        return null;
    }
}
