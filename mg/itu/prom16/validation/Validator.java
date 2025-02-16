package mg.itu.prom16.validation;

import mg.itu.prom16.annotation.validation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class Validator {

    // Méthode principale de validation qui parcourt tous les champs d'un objet
    public void validate(Object object, BindingResult bindingResult) {
        System.out.println("validate" + object);
        if (object == null) {
            bindingResult.addError("object", null, "L'objet à valider est nul.");
            return;
        }

        // Récupérer toutes les annotations de la classe
        Class<?> objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();

        // Parcours des champs et validation
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                validateField(field, annotation, object, bindingResult);
            }
        }
    }

    // Méthode qui valide chaque champ en fonction des annotations
    private void validateField(Field field, Annotation annotation, Object object, BindingResult bindingResult) {
        field.setAccessible(true); // Permet d'accéder aux champs privés

        try {
            Object fieldValue = field.get(object); // Récupérer la valeur du champ

            if (annotation instanceof NotBlank) {
                validateNotBlank(fieldValue, field, bindingResult);
            } else if (annotation instanceof Email) {
                validateEmail(fieldValue, field, bindingResult);
            } else if (annotation instanceof Size) {
                validateSize(fieldValue, field, (Size) annotation, bindingResult);
            } else if (annotation instanceof Positive) {
                validatePositive(fieldValue, field, bindingResult);
            } else if (annotation instanceof Future) {
                validateFuture(fieldValue, field, bindingResult);
            }
        } catch (IllegalAccessException e) {
            bindingResult.addError(field.getName(), null, "Erreur d'accès au champ : " + field.getName());
        }
    }

    // Validation de @NotBlank
    private void validateNotBlank(Object fieldValue, Field field, BindingResult bindingResult) {
        if (fieldValue == null || fieldValue.toString().trim().isEmpty()) {
            NotBlank annotation = field.getAnnotation(NotBlank.class);
            String message = annotation.message();
            bindingResult.addError(field.getName(), fieldValue != null ? fieldValue.toString() : null, message.isEmpty() ? field.getName() + " ne doit pas être vide." : message);
        }
    }

    // Validation de @Email
    private void validateEmail(Object fieldValue, Field field, BindingResult bindingResult) {
        if (fieldValue != null) {
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);
            if (!pattern.matcher(fieldValue.toString()).matches()) {
                Email annotation = field.getAnnotation(Email.class);
                String message = annotation.message();
                bindingResult.addError(field.getName(), fieldValue.toString(), message.isEmpty() ? field.getName() + " doit être un email valide." : message);
            }
        }
    }

    // Validation de @Size
    private void validateSize(Object fieldValue, Field field, Size sizeAnnotation, BindingResult bindingResult) {
        if (fieldValue != null) {
            int min = sizeAnnotation.min();
            int max = sizeAnnotation.max();
            int length = fieldValue.toString().length();

            if (length < min || length > max) {
                String message = sizeAnnotation.message();
                bindingResult.addError(field.getName(), fieldValue.toString(), message.isEmpty() ? field.getName() + " doit avoir entre " + min + " et " + max + " caractères." : message);
            }
        }
    }

    // Validation de @Positive
    private void validatePositive(Object fieldValue, Field field, BindingResult bindingResult) {
        if (fieldValue instanceof Number) {
            if (((Number) fieldValue).doubleValue() <= 0) {
                Positive annotation = field.getAnnotation(Positive.class);
                String message = annotation.message();
                bindingResult.addError(field.getName(), fieldValue.toString(), message.isEmpty() ? field.getName() + " doit être un nombre positif." : message);
            }
        } else {
            bindingResult.addError(field.getName(), fieldValue != null ? fieldValue.toString() : null, field.getName() + " doit être un nombre.");
        }
    }

    // Validation de @Future
    private void validateFuture(Object fieldValue, Field field, BindingResult bindingResult) {
        if (fieldValue instanceof java.util.Date) {
            java.util.Date currentDate = new java.util.Date();
            if (((java.util.Date) fieldValue).before(currentDate)) {
                Future annotation = field.getAnnotation(Future.class);
                String message = annotation.message();
                bindingResult.addError(field.getName(), fieldValue.toString(), message.isEmpty() ? field.getName() + " doit être une date dans le futur." : message);
            }
        } else {
            bindingResult.addError(field.getName(), fieldValue != null ? fieldValue.toString() : null, field.getName() + " doit être une date.");
        }
    }
}
