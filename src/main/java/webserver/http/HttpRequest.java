package webserver.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;
import webserver.RequestHandler;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 안재열 on 2017-06-15.
 * Modified by 안재열 on 2017-09-20.
 */
public class HttpRequest implements IHttpRequest {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private String path = "";
    private Map parameter;
    private String method = "";
    private String httpVersion = "";
    private String body ="";



    public HttpRequest(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String text;
        int lineLength = 0;
        boolean hasBody = false;

        while((text = reader.readLine())!=null){

            if(lineLength==0){
                String [] httpReqFirstLines = text.split("\\s+");
                this.method = httpReqFirstLines[0];
                //get 파라이터가 있을 때, ? 이하 분리
                if(httpReqFirstLines[1].contains("\\?")){
                    String [] pathAndParams = httpReqFirstLines[1].split("\\?");
                    this.path = pathAndParams[0];
                    this.parameter = new HashMap();
                    String [] params = pathAndParams[1].split("&");

                    for (int i = 0; i < params.length; i++){
                        String [] eachParams = params[i].split("=");
                        this.parameter.put(eachParams[0],eachParams[1]);
                    }
                }else{
                    this.path = httpReqFirstLines[1];
                }

                this.httpVersion = httpReqFirstLines[2];
            }else if(!hasBody){
                if(text.equals("\\r\\n\\r\\n")){
                    log.info("HTTP 바디 시작");
                    hasBody = true;
                }else{
                    //예) Connection: keep-alive
                    String[] tempHttpHead = text.split(":");
                   // header.put(tempHttpHead[0],tempHttpHead[1]);
                }
            }
            if(text.equals("")){
                log.info("널읽음HTTP 바디 시작");
                reader.close();
            }
            log.info("읽은 것 : {}{}",text,lineLength);
            lineLength ++;
        }

    }

    public String getPath() {
        return path;
    }

    public Map getParameter() {
        return parameter;
    }

    public String getParameter(String id) {
        return this.parameter.get(id).toString();
    }

    public String getMethod() {
        return this.method;
    }

    private int getContentLength(String line) {
        return Integer.parseInt(getContentsValue(line));
    }

    private String getContentsValue(String line) {
        String[] headerTokens = line.split(":");
        return headerTokens[1].trim();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setParameter(Map parameter) {
        this.parameter = parameter;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private static Map parameterPaser(String param){
        String parameter = URLDecoder.decode(param.substring(param.indexOf("?") + 1));
        Map returnParam = new HashMap<String, String>();
        String[] params = parameter.split("&");
        for (String p : params) {
            String temp[] = p.split("\\=");
            returnParam.put(temp[0], temp[1]);
        }
        return returnParam;
    }

}

