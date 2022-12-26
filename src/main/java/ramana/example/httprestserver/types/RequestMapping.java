package ramana.example.httprestserver.types;

public class RequestMapping {
    public final String method;
    public final String path;

    public RequestMapping(String method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public String toString() {
        return "RequestMapping{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return path.hashCode() + method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RequestMapping) {
            RequestMapping tmp = (RequestMapping) obj;
            return method.equals(tmp.method)  &&  path.equals(tmp.path);
        }
        return false;
    }
}
