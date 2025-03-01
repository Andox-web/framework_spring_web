package mg.itu.prom16.gson;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BigDecimalAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonElement json, Type type, JsonDeserializationContext context) 
        throws JsonParseException {
        return new BigDecimal(json.getAsString());
    }

    @Override
    public JsonElement serialize(BigDecimal src, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(src.toPlainString());
    }
}