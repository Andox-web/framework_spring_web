package mg.itu.prom16.exception.request;

import mg.itu.prom16.mapping.Mapping;

public class TypeNotRecognizedException extends Exception {

    public TypeNotRecognizedException(String type,Mapping mapping) {
        super("Type not recognized: " + type+" dans la methode "+mapping.getMethod().getName()+" de la Classe "+mapping.getClass().getSimpleName());
    }

    public TypeNotRecognizedException(String type,Mapping mapping, Throwable cause) {
        super("Type not recognized: " + type+" dans la methode "+mapping.getMethod().getName()+" de la Classe "+mapping.getClass().getSimpleName(), cause);
    }
}
