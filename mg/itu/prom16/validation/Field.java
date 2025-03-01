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
        return fieldValue==null?"":fieldValue;
    }
    public String getFieldValueAsString() {
        return fieldValue==null?"":fieldValue.toString();
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

    @Override
    public String toString() {
        return "Field [fieldName=" + fieldName + ", fieldValue=" + fieldValue + ", errors=" + errors + "]";
    }
   
}