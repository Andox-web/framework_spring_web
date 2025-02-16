package mg.itu.prom16.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingResult {
    private Map<String, Field> fieldErrors = new HashMap<>();

    public void addError(String fieldName, Object fieldValue, String errorMessage) {
        Field fieldError = fieldErrors.getOrDefault(fieldName, new Field(fieldName, fieldValue));
        fieldError.addError(errorMessage);
        fieldErrors.put(fieldName, fieldError);
    }

    public void addField(String fieldName, Object fieldValue){
        Field fieldError = fieldErrors.getOrDefault(fieldName, new Field(fieldName, fieldValue));
        fieldErrors.put(fieldName, fieldError);
    }

    public boolean hasErrors() {
        if (!fieldErrors.isEmpty()) {
            for (Field fieldError : fieldErrors.values()) {
                if (fieldError.hasErrors()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Field> getErrors() {
        return new ArrayList<>(fieldErrors.values());
    }

    public Field getErrors(String fieldName) {
        return fieldErrors.get(fieldName);
    }
}
