package mg.itu.prom16.validation;

import java.util.ArrayList;
import java.util.List;

public class FieldError {
    private String fieldName;
    private String fieldValue;
    private List<String> errors = new ArrayList<>();

    public FieldError(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public void addError(String errorMessage) {
        errors.add(errorMessage);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getFirstError() {
        return !errors.isEmpty() ? errors.get(0) : null;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
   
}