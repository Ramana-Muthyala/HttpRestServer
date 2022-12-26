package ramana.example.httprestserver.codec;

import java.util.HashMap;

public class CodecRegistry {
    public static final String APPLICATION_BY_JSON = "application/json";
    private static final HashMap<String, Codec> registry = new HashMap<>();
    static {
        registry.put(APPLICATION_BY_JSON, new JsonCodec());
    }
    public static Codec get(String mediaType) {
        return registry.get(mediaType);
    }
}
