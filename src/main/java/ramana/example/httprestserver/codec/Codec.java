package ramana.example.httprestserver.codec;

public interface Codec {
    Object decode(byte[] data, Class<?> type) throws CodecException;
    byte[] encode(Object data) throws CodecException;

    class CodecException extends Exception {
        public CodecException(Throwable cause) {
            super(cause);
        }
    }
}
