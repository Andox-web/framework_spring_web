package mg.itu.prom16.build;

public class BuildException extends Exception {
    public BuildException(String message) {
        super(message);
        System.err.println(message);  // Imprime le message d'erreur
    }

    public BuildException(String message, Throwable cause) {
        super(message, cause);
        System.err.println(message);  // Imprime le message d'erreur
        cause.printStackTrace(System.err);  // Imprime la trace de la pile de l'exception cause
    }
}
