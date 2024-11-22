package mg.itu.prom16.validation;

import mg.itu.prom16.annotation.validation.Valid;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ValidationScanner {

    private final Validator validator;

    public ValidationScanner() {
        this.validator = new Validator();
    }

    public void scanAndValidate(Method method, Object[] args) {
        if (method == null || args == null) {
            throw new IllegalArgumentException("La méthode ou les arguments ne peuvent pas être null.");
        }

        Parameter[] parameters = method.getParameters();
        System.out.println(parameters.length+"  params");
        BindingResult bindingResult = null;

        // Étape 1 : Rechercher BindingResult dans les arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof BindingResult) {
                bindingResult = (BindingResult) args[i];
                break;
            }
        }

        if (bindingResult == null) {
            return;
        }

        // Étape 2 : Valider les champs des arguments annotés avec @Valid
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Valid.class)) {
                Object argument = args[i];
                if (argument == bindingResult) {
                    continue;
                }
                validator.validate(argument, bindingResult);
            }
        }

        // Étape 3 : Afficher les résultats de validation
        if (bindingResult.hasErrors()) {
            System.out.println("Erreurs détectées : " + bindingResult.getErrors());
        } else {
            System.out.println("Validation réussie !");
        }
    }

   
}
