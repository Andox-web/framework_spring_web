package mg.itu.prom16.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import mg.itu.prom16.gson.GsonConfiguration;
import mg.itu.prom16.response.MediaType;
import mg.itu.prom16.response.ResponseEntity;

public class ModularApiCaller {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
            
    public static class ApiRequest<T> {
        private final String url;
        private final String method;
        private final Map<String, String> headers;
        private final String body;
        private final MediaType contentType;
        private final Class<T> responseType;

        private ApiRequest(Builder<T> builder) {
            this.url = builder.url;
            this.method = builder.method;
            this.headers = builder.headers;
            this.body = builder.body;
            this.contentType = builder.contentType;
            this.responseType = builder.responseType != null ? builder.responseType : (Class<T>) String.class;
        }

        public static <T> Builder<T> builder() {
            return new Builder<>();
        }

        public ResponseEntity<T> execute() {
            try {
                HttpRequest request = buildHttpRequest();
                HttpResponse<String> httpResponse = HTTP_CLIENT.send(
                    request, 
                    HttpResponse.BodyHandlers.ofString()
                );

                String contentTypeHeader = httpResponse.headers().firstValue("Content-Type").orElse("");
                String responseBodyString = httpResponse.body();
                T responseBody;

                boolean isJson = contentTypeHeader.contains("application/json");

                if (isJson && responseType != String.class) {
                    try {
                        Gson gson = GsonConfiguration.getGson();
                        responseBody = gson.fromJson(responseBodyString, responseType);
                    } catch (JsonSyntaxException e) {
                        throw new RuntimeException("Failed to parse JSON response", e);
                    }
                } else {
                    if (responseType != String.class) {
                        throw new RuntimeException("Cannot deserialize non-JSON response to type " + responseType.getName());
                    }
                    responseBody = (T) responseBodyString;
                }

                ResponseEntity.Builder<T> responseBuilder = ResponseEntity.<T>builder()
                        .body(responseBody)
                        .status(httpResponse.statusCode());

                httpResponse.headers().map().forEach((key, values) -> 
                    responseBuilder.header(key, String.join(", ", values)));

                return responseBuilder.build();

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Request failed", e);
            }
        }

        private HttpRequest buildHttpRequest() {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10));

            headers.forEach(builder::header);

            if (contentType != null) {
                builder.header("Content-Type", contentType.getValue());
            }

            if ("GET".equalsIgnoreCase(method)) {
                builder.GET();
            } else if (body != null) {
                builder.method(method, HttpRequest.BodyPublishers.ofString(body));
            } else {
                builder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            return builder.build();
        }

        public static class Builder<T> {
            private String url;
            private String method = "GET";
            private final Map<String, String> headers = new HashMap<>();
            private String body;
            private MediaType contentType;
            private Class<T> responseType;

            public Builder<T> url(String url) {
                this.url = url;
                return this;
            }

            public Builder<T> method(String method) {
                this.method = method.toUpperCase();
                return this;
            }

            public Builder<T> addHeader(String name, String value) {
                this.headers.put(name, value);
                return this;
            }

            public Builder<T> contentType(MediaType contentType) {
                this.contentType = contentType;
                return this;
            }

            public Builder<T> body(String body) {
                this.body = body;
                return this;
            }

            public Builder<T> responseType(Class<T> responseType) {
                this.responseType = responseType;
                return this;
            }

            public ResponseEntity<T> execute() {
                return build().execute();
            }

            public ApiRequest<T> build() {
                if (url == null || url.isBlank()) {
                    throw new IllegalArgumentException("URL must be provided");
                }
                return new ApiRequest<>(this);
            }
        }
    }
}