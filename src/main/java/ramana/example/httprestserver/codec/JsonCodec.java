package ramana.example.httprestserver.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonCodec implements Codec {
    @Override
    public Object decode(byte[] data, Class<?> type) throws CodecException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, type);
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte[] encode(Object data) throws CodecException {
        if(data instanceof String) return ((String) data).getBytes();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new CodecException(e);
        }
    }
}
