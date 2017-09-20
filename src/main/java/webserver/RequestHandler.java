package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;
import webserver.http.ReadingFileContent;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            String contents = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            //readLine() 은 개행문자 직전까지 한줄 읽고 포인터를 다음으로 이동.
            String line = br.readLine();
            log.debug("request line :{}", line);
            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            int contentLength = 0;
            String cookie = "";
            while (!line.equals("")) {
                log.debug("header : {}", line);
                line = br.readLine();
                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                }
                if (line.contains("Cookie")) {
                    cookie = getContentsValue(line);
                }
            }


            String requestLine = line; // HTTP 요청라인
            String httpsMethod = tokens[0];
            String uri = tokens[1];
            String param = null;
            if (uri.contains("?")) {
                param = URLDecoder.decode(uri.substring(uri.indexOf("?")));
                uri = uri.substring(0, uri.indexOf("?"));
            }

//http GET 처리
            if (httpsMethod.equals("GET")) {

                if (param == null) { //http 파라미터가 없는경우
                    if (uri.equals("/user/list")) {
                        if (IsLogined(cookie)) {
                            for (User user : DataBase.findAll()) {
                                log.info("id :{}", user.getUserId());
                                log.info("name :{}", user.getName());
                            }

                        }
                    } else {
                        contents = new ReadingFileContent().read(uri);
                        responseResource(out, uri);
                    }
                } else {
                    // /user/create 처리

                    if (uri.equals("/user/create")) {
                        Map<String, String> params = getParamsFromURI(param);
                        User user = new User(params.get("userID"), params.get("password"), params.get("name"), params.get("email"));
                        log.info("helloWorld");
                        log.info(user.toString());
                    }

                }
            } else if (httpsMethod.equals("POST")) {
                if (uri.equals("/user/create")) {
                    String body = IOUtils.readData(br, contentLength);
                    Map<String, String> params = httpsBodyParser(body);
                    params.get("userID");
                    User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                    log.info(user.toString());
                    DataBase.addUser(user);
                    DataOutputStream dos = new DataOutputStream(out);
                    response302Header(dos, "/index.html");

                } else if (uri.equals("/user/login")) {
                    String body = IOUtils.readData(br, contentLength);
                    Map<String, String> params = httpsBodyParser(body);
                    if ((DataBase.findUserById(params.get("userId")) != null)) {
                        if (DataBase.findUserById(params.get("userId")).getPassword().equals(params.get("password"))) {
                            DataOutputStream dos = new DataOutputStream(out);
                            response302LoginedHeader(dos, "/index.html");
                        }
                    } else {
                        responseResource(out, "/user/login_failed.html");
                    }
                } else {
                    responseResource(out, uri);
                }
            } else {
                log.info("*****************************" + br.readLine());
                responseResource(out, uri);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String uri) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + uri).toPath());
        String contentType = "html";
        if(uri.contains("css")){contentType = "css";}
        response200Header(dos, body.length, contentType);
        responseBody(dos, body);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");

            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            if (contentType.equals("css")) {
                dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            } else {
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            }
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String uri) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + uri + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginedHeader(DataOutputStream dos, String uri) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: " + uri + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private Map<String, String> getParamsFromURI(String param) {
        Map devidedParams = new HashMap<String, String>();
        String[] params = param.split("&");
        for (String p : params) {
            log.info(p);
            String temp[] = p.split("\\=");
            devidedParams.put(temp[0], temp[1]);
        }
        return devidedParams;
    }

    private int getContentLength(String line) {
        return Integer.parseInt(getContentsValue(line));
    }

    private String getContentsValue(String line) {
        String[] headerTokens = line.split(":");
        return headerTokens[1].trim();
    }

    private Map httpsBodyParser(String body) throws UnsupportedEncodingException {
        String[] devidedString = body.split("&");
        Map returnMap = new HashMap<String, String>();
        for (String e : devidedString) {
            String[] temp = e.split("=");
            returnMap.put(temp[0], URLDecoder.decode(temp[1], "UTF-8"));
        }
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Here");
        return returnMap;
    }

    private Boolean IsLogined(String cookie) {
        Boolean result = false;
        if (cookie.contains("logined=true")) {
            log.info("로그인 됨");
            result = true;
        } else if (cookie.contains("logined=false")) {
            log.info("로그아웃 상태");
        } else {
            log.info("로그인 및 로그아웃 기록 쿠키 부재");
        }

        return result;
    }

}


