package webserver.http;

/**
 * Created by 안재열 on 2017-06-15.
 */
public interface IHttpRequest {
        String httpMethod = "";
        String uri = "";
        String version= "";
}
