package mg.itu.prom16.response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public class ResponseEntity<T> {
    private final T body;
    private final int statusCode;
    private final Map<String, String> headers;

    private ResponseEntity(Builder<T> builder) {
        this.body = builder.body;
        this.statusCode = builder.statusCode;
        this.headers = Collections.unmodifiableMap(builder.headers);
    }

    // Getters
    public T getBody() {
        return body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    // Builder pattern
    public static class Builder<T> {
        private T body;
        private int statusCode = 200;
        private final Map<String, String> headers = new HashMap<>();

        public Builder<T> body(T body) {
            this.body = body;
            return this;
        }

        public Builder<T> status(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder<T> header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public ResponseEntity<T> build() {
            return new ResponseEntity<>(this);
        }
    }

    // Méthodes factory communes
    public static <T> ResponseEntity<T> ok(T body) {
        return new Builder<T>()
            .status(200)
            .body(body)
            .build();
    }

    public static <T> ResponseEntity<T> created(T body, String location) {
        return new Builder<T>()
            .status(201)
            .body(body)
            .header("Location", location)
            .build();
    }

    public static ResponseEntity<Void> noContent() {
        return new Builder<Void>()
            .status(204)
            .build();
    }

    public static <T> ResponseEntity<T> notFound() {
        return new Builder<T>()
            .status(404)
            .build();
    }

    public static <T> ResponseEntity<T> badRequest() {
        return new Builder<T>()
            .status(400)
            .build();
    }

    public static <T> ResponseEntity<T> error(int statusCode, String errorMessage) {
        return new Builder<T>()
            .status(statusCode)
            .body((T) Map.of("error", errorMessage))
            .build();
    }
    public T applyResponse(HttpServletResponse response) throws IOException {
        // Status code
        response.setStatus(getStatusCode());

        // Headers
        getHeaders().forEach(response::addHeader);

        // Body
        return getBody();
    }
}