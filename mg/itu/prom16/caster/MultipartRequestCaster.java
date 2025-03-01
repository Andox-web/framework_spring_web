package mg.itu.prom16.caster;

import java.util.Collection;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.itu.prom16.annotation.Component;
import mg.itu.prom16.request.ArgumentException;
import mg.itu.prom16.servlet.MultipartFile;

@Component
public class MultipartRequestCaster extends RequestTypeCaster<MultipartFile>{

    @Override
    public MultipartFile[] resolveArray(Class<?> type, String paramName, HttpServletRequest request)
            throws ArgumentException {
        try {
            Collection<Part> parts = request.getParts();
            String racine = request.getServletContext().getResource("/").getPath();	
            return parts.stream()
                    .filter(part -> part.getName().equals(paramName))
                    .map(part -> new MultipartFile(part, racine))
                    .toArray(MultipartFile[]::new);
        } catch (Exception e) {
            throw new ArgumentException("Failed to resolve MultipartFile array", e);
        }
    }

    @Override
    public Collection<MultipartFile> resolveList(Class<?> type, String paramName, HttpServletRequest request)
            throws ArgumentException {
        try {
            Collection<Part> parts = request.getParts();
            String racine = request.getServletContext().getResource("/").getPath();
            return parts.stream()
                    .filter(part -> part.getName().equals(paramName))
                    .map(part -> new MultipartFile(part, racine))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ArgumentException("Failed to resolve MultipartFile list", e);
        }
    }

    @Override
    public MultipartFile resolveObject(Class<?> type, String paramName, HttpServletRequest request)
            throws ArgumentException {
        try {
            Part part = request.getPart(paramName);
            if (part != null) {
                return new MultipartFile(part,request.getServletContext().getResource("/").getPath());
            } 
            return null;
        } catch (Exception e) {
            throw new ArgumentException("Failed to resolve MultipartFile object", e);
        }
    }

}