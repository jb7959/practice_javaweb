package webserver.http;

import java.net.URLDecoder;

import static com.sun.org.apache.xalan.internal.lib.ExsltStrings.split;

/**
 * Created by 안재열 on 2017-06-15.
 */
public class HttpRequest implements IHttpRequest {
    private String httpMethod = "";
    private String uri = "";
    private String version = "";
    private String param = "";

    public HttpRequest(String inputStream) {
        String[] httpRequests = inputStream.split(" ");
        this.httpMethod = httpRequests[0];
        //Splitting HTTP Get method.
        if (httpRequests[1].contains("?")) {
           this.param = URLDecoder.decode(httpRequests[1].substring(httpRequests[1].indexOf("?")));
           this.uri = httpRequests[1].substring(0,httpRequests[1].indexOf("?"));
        } else {
            this.uri = httpRequests[1];
        }
        this.version = httpRequests[2];
    }

    public HttpRequest(String httpMethod, String uri, String version) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.version = version;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpMethod='" + httpMethod + '\'' +
                ", uri='" + uri + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}

