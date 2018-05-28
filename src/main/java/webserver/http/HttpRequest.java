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
    private Map header;
    private String contentType = "text/html";



    public HttpRequest(InputStream in,int receiveBufferSize) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String  req = IOUtils.readData(reader,receiveBufferSize);
        String [] headAndBody = req.split("\\r\\n\\r\\n"); //줄바꿈을 기준으로 분리(한줄공백)
        header = new HashMap();
        this.body = headAndBody[1]; // 바디 세팅
        splitToHead(headAndBody[0]); //헤더 세팅
    }

    private  void splitToHead(String head){
        String [] heads = head.split("\\r\\n");
        for(int i = 0;i<heads.length;i ++){
            if(i==0){
                String [] temp = heads[0].split("\\s+");
                this.method = temp[0];
                this.path = temp[1];
                this.httpVersion = temp[2];
                String mimeTypeOnPath = null;

                //파라미터 추가
                if(this.method.toLowerCase().equals("get")){
                    if(path.contains("?")){
                        String [] splitedPath = path.split("\\?");
                        this.path = splitedPath[0];
                        setParameter(splitedPath[1]);
                    }
                }else {
                    setParameter(this.body);
                }

                if((mimeTypeOnPath = path.split("\\.")[path.split("\\.").length - 1])!=null){ //URI에 .확장자로 미디어타입이 나타난다면..
                    if(mimeTypeOnPath.equals("css")){
                        contentType = "text/css";
                    }else if(mimeTypeOnPath.equals("js")){
                        contentType ="application/javascript";
                    }
                }
            }else {
                String [] temp = heads[i].split(":");
                log.info("{}:{}",temp[0],temp[1].trim());
                this.header.put(temp[0],temp[1].trim());
                if(temp.equals("Content-Type")){
                    this.contentType = temp[1]; //HTTP Request 헤더에 컨텐츠 타입으로 미디어 타입이 나타난다면..
                }
                /******************
                 *예시
                Accept-Encoding : gzip, deflate, br
                Accept-Language : ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
                Cookie : Idea-1c2d522=0a1ffc35-2e45-4ae1-a55d-20dcd816da5f; __utma=111872281.974471363.1523597591.1523597591.1523597591.1; __utmz=111872281.1523597591.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)
                 ******************/
            }
        }
    }

    private void setParameter(String params){
        this.parameter = new HashMap();
        String [] splitedParams = params.split("&");
        for (String eachParam:splitedParams) {
            String [] eachParamSet = eachParam.split("=");
            this.parameter.put(eachParamSet[0],eachParamSet[1].trim());
            log.info("PARAMS {}:{}",eachParamSet[0],eachParamSet[1].trim());
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

