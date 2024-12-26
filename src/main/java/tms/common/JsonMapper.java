package tms.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonMapper {

    private final ObjectMapper objectMapper;

    private JsonMapper(){
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    public static JsonMapper getInstance(){
        return Holder.INSTANCE;
    }

    private static class Holder{
        private static final JsonMapper INSTANCE = new JsonMapper();
        private Holder(){
            throw new UnsupportedOperationException("JsonMapper.Holder should not be instantiated");
        }
    }
}