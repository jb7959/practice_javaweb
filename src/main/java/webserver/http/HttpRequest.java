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

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String text;
        int lineLength = 0;
        boolean hasBody = false;
        while((text = reader.readLine())!=null){
            if(lineLength==0){
                this.header = new HashMap();
                header.put("method",text.split("\\s+")[0]);
                header.put("uri",text.split("\\s+")[1]);
                header.put("httpVersion",text.split("\\s+")[2]);
            }else if(text.equals("\\r\\n\\r\\n")){
                log.info("HTTP 바디 시작");
                hasBody = true;
            }else if(!hasBody){
                //예) Connection: keep-alive
                String[] tempHttpHead = text.split(":");
                header.put(tempHttpHead[0],tempHttpHead[1]);
            }
            log.info("읽은 것 : {}",text);
            lineLength ++;
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

