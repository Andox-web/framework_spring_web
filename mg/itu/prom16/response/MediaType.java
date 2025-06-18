package mg.itu.prom16.response;

public enum MediaType {
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_PDF("application/pdf"),	
    TEXT_PLAIN("text/plain");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}