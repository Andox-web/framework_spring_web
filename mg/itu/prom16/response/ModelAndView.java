package mg.itu.prom16.response;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
        
        StringBuilder stringBuilder = new StringBuilder(request.getServletContext().getInitParameter("viewFolder"));
        stringBuilder.append(viewName);
        stringBuilder.append(request.getServletContext().getInitParameter("suffixe"));
        
        RequestDispatcher dispatcher = request.getRequestDispatcher(stringBuilder.toString());
        dispatcher.forward(request, response);
    }

}
