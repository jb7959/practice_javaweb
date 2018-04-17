# 실습을 위한 개발 환경 세팅
* IntelliJ IDEA
* Maven

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답

스트림혹은 파일은 각자의 Class가 Java에 존재한다. 여기서 각 각체를 읽어주는 Reader가 별도로 존재한다.
각 리더는 버퍼를 사용하면 효율적으로 읽을 수 있는 것으로 보인다.
따라서 InputStream이나 File의 각 Reader(InputStreamReader, FileReader)는 BufferReader를 사용 할 수 있다.

        public class BufferedReader extends Reader {
        ....
        public BufferedReader(Reader in) {
            this(in, defaultCharBufferSize);
        }
    
        /** Checks to make sure that the stream has not been closed */
        private void ensureOpen() throws IOException {
            if (in == null)
                throw new IOException("Stream closed");
        } ....
        
 BufferReader는 Reader의 확장이며, InputStreamReader 또한 Reader의 확장이다.
 FileReader는 InputStreamReader의 확장이다.
 
     public class InputStreamReader extends Reader{... }
     public class FileReader extends InputStreamReader {...}
     
* 참고
* https://stackoverflow.com/a/35446009 [Read/convert an InputStream to a String]
* http://jdm.kr/blog/3 [자바파일입출력]
* https://www.mkyong.com/java/how-to-convert-file-into-an-array-of-bytes/ [NIO를 이용한 파일에서 바이트배열로의 변경]

### 요구사항 2 - get 방식으로 회원가입
* 없음 

### 요구사항 3 - post 방식으로 회원가입
* Chr(13) = \r == 캐리지리턴 : 동일줄 첫번째 자리로 커서 이동
* Chr(10) = \n == 라인피드 : 현재 커서가 위치한 곳에서 아래로 한줄 내림

--> 유닉스 계열의 문서편집기에선 char(10)만 사용해도 줄바꿈이 되지만, 윈도우계열은 두 바이트(\r\n)가 함께해야 줄바꿈이 수행됨

### 요구사항 4 - redirect 방식으로 이동
* 웹 브라우저는 이전 HTTP의 요청사항을 보유하고 있음.
* HTTP 302를 통한 redirect 방식으로 이동은 의도치 않은 data 재전송 및 조작을 방지 할 수 있음

### 요구사항 5 - cookie
* cookie 삽입을 위해서는 HTTP Header에 "SET-Cookie: "CookieName" = "value(쿠키값)";의 방식으로 기입해주어야 함

        private void response302Header(DataOutputStream dos, String location, Map<String,String> cookies) {
            try {
                dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
                dos.writeBytes("LOCATION: " + location + "\r\n");
                if(!cookies.isEmpty()){
                    String setCookie = "SET-Cookie: ";
                    for(String key : cookies.keySet()){
                        setCookie+=(key+"="+cookies.get(key));
                    }
                    dos.writeBytes(setCookie + "\r\n");
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
* Map의 경우 모든 항목의 조회는 Map.keySet()을 활용하여 확인 할 수 있다.

  - Map.keySet을 통해서 모든 맵의 KeySet을 추출한다. 
    - (Map의 ID는 중복이 되면 안되기에, SET의 중복배제사상과 동일하다.)
  - 추출된 식별자 Set은 Foreach()를 통해 참조할 MAP의 전체를 반복조회한다.

        for(String key : cookies.keySet()){
            setCookie+=(key+"="+cookies.get(key));
        }
                        

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 