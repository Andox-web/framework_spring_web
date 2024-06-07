package mg.itu.prom16.exception.build;

public class DuplicateUrlException extends BuildException {
    private String url;

    public DuplicateUrlException(String url) {
        super("Erreur de build : L'URL '" + url + "' est dupliquée.");
        this.url=url;
    }

    public DuplicateUrlException(String url, Throwable cause) {
        super("Erreur de build : L'URL '" + url + "' est dupliquée.", cause);
        this.url=url;
    }
}
