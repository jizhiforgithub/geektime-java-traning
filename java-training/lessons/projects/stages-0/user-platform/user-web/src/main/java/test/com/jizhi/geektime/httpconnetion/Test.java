package test.com.jizhi.geektime.httpconnetion;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 2021/4/25
 * jizhi7
 **/
public class Test {

    public static void main(String[] args) throws IOException {
        String code = "4c3d36666494de53fe5a5430fe4edd6c47ff57091eb82dc834301622145e75e5";
        String addr = "https://gitee.com/oauth/token?grant_type=authorization_code&code=" + code + "&client_id=63ebd19c2e5b5d1e771d93c4cd8e9403e3da8c8f540a517726104fe836d982b7&redirect_uri=http://localhost:8080/user-web/gitee.jsp&client_secret=55e56b2ad06420637a48b9f846819b498ecbd8ed022c9a19c50c7f2d48304ea1";

        HttpURLConnection connection = (HttpURLConnection) new URL(addr).openConnection();
        connection.setRequestMethod("POST");

        // 设置请求编码
        connection.addRequestProperty("encoding", "UTF-8");
        // 设置允许输入
        connection.setDoInput(true);
        // 设置允许输出
        connection.setDoOutput(true);
        InputStream inputStream = connection.getInputStream();
        String userinfo = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        System.out.println(userinfo);
    }

}
