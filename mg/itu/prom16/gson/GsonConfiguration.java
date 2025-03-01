package mg.itu.prom16.gson;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonConfiguration {
    public static Gson getGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
            .create();
    }
}
