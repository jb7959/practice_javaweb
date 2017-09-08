package webserver.http;

import java.io.*;
import java.net.URLDecoder;

/**
 * Created by 안재열 on 2017-07-12.
 */
public class ReadingFileContent {


    public String read(String filePath) throws UnsupportedEncodingException {
        String path = ReadingFileContent.class.getResource("").getPath();
        path = path.split("/target")[0]+"/webapp"+filePath;
        path = URLDecoder.decode(path, "UTF-8");
        System.out.println(path);
        // File 경로
        File file = new File(path);
        // 버퍼로 읽어들일 임시 변수
        String temp = "";
        // 최종 내용 출력을 위한 변수
        String content = "";

        try (
                FileInputStream fis = new FileInputStream(file);// File Input 스트림 생성
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); // Input 스트림 생성
                BufferedReader br = new BufferedReader(isr); // 버퍼 생성
        ) {
            // 버퍼를 한줄한줄 읽어들여 내용 추출
            while ((temp = br.readLine()) != null) {
                content += temp + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    return content;
    }
}
