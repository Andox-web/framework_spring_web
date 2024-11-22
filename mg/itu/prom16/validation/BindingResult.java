package mg.itu.prom16.validation;

import java.util.ArrayList;
import java.util.List;

public class BindingResult {
    private List<String> errors = new ArrayList<>();

    // Ajoute une erreur à la liste des erreurs
    public void addError(String errorMessage) {
        errors.add(errorMessage);
    }

    // Vérifie si des erreurs existent
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // Récupère toutes les erreurs
    public List<String> getErrors() {
        return errors;
    }
}
