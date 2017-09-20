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
    private Map header;
    private Map parameter;
    private String method = "";
    private String body ="";
/*
        assertEquals("POST", request.getMethod());
        assertEquals("/user/create", request.getPath());
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("javajigi", request.getParameter("userId"));*/


    public HttpRequest(InputStream in) throws IOException {
        String contents = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String line = br.readLine();
        log.debug("request line :{}", line);
        String[] tokens = line.split(" ");
        int contentLength = 0;
        String cookie = "";

        //Method, Parameter, path 초기화
        String requestLine = line; // HTTP 요청라인
        this.method = tokens[0];
        this.path = tokens[1];
        if (path.contains("?")) {
            String parameter = URLDecoder.decode(path.substring(path.indexOf("?") + 1));
            //path 초기화
            this.path = path.substring(0, path.indexOf("?"));
            //parameter 초기화
            this.parameter = parameterPaser(parameter);
        }

        //header 초기화 부분
        this.header = new HashMap<String, String>();
        while (!line.equals("")) {
            line = br.readLine();
            try {
                if(!line.isEmpty()){
                String[] splitedHeader = line.split(" ");
                this.header.put(splitedHeader[0].replace(":"," ").trim(), splitedHeader[1].replace(":"," ").trim());
                }
            }catch (ArrayIndexOutOfBoundsException e){
                log.error(e.toString());
            }
        }
        //body 생성 (post)
        if(this.method.toLowerCase().equals("post")){
        this.body = IOUtils.readData(br, Integer.parseInt(this.header.get("Content-Length").toString()));
        this.parameter = parameterPaser(body);
        }
    }

    public String getPath() {
        return path;
    }

    public Map getHeader() {
        return header;
    }

    public String getHeader(String id) {
        String rv ="";
        try {
            log.info(this.header.get(id).toString());
            rv = this.header.get(id).toString();
        }catch (NullPointerException e){
            log.error("요청하신 값이 존재하지 않습니다.");
        }
        return rv;
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

