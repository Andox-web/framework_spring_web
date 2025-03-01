package mg.itu.prom16.build;

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
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
