package mg.itu.prom16.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingResult {
    private Map<String, FieldError> fieldErrors = new HashMap<>();

    public void addError(String fieldName, String fieldValue, String errorMessage) {
        FieldError fieldError = fieldErrors.getOrDefault(fieldName, new FieldError(fieldName, fieldValue));
        fieldError.addError(errorMessage);
        fieldErrors.put(fieldName, fieldError);
    }

    public boolean hasErrors() {
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : fieldErrors.values()) {
                if (fieldError.hasErrors()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<FieldError> getErrors() {
        return new ArrayList<>(fieldErrors.values());
    }

    public FieldError getErrors(String fieldName) {
        return fieldErrors.get(fieldName);
    }
}
