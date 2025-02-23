package mg.itu.prom16.validation;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private String fieldName;
    private Object fieldValue;
    private List<String> errors = new ArrayList<>();

    public Field(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public void addError(String errorMessage) {
        errors.add(errorMessage);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getFirstError() {
        return !errors.isEmpty() ? errors.get(0) : "";
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
   
}