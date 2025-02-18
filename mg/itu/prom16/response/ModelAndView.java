package mg.itu.prom16.response;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.itu.prom16.util.Environment;

import java.io.IOException;
import java.util.Optional;

public class ModelAndView extends ModelMap {
    private String viewName;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
        }

    @Override
    public void processAction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.processAction(request, response);

        String viewFolder = Optional.ofNullable(Environment.getProperty("viewFolder"))
                        .orElse("");
        String suffix = Optional.ofNullable(Environment.getProperty("suffixe"))
                    .orElse("");

        StringBuilder stringBuilder = new StringBuilder(viewFolder)
                                            .append(viewName)
                                            .append(suffix);
 
        RequestDispatcher dispatcher = request.getRequestDispatcher(stringBuilder.toString());
        dispatcher.forward(request, response);
    }
}
