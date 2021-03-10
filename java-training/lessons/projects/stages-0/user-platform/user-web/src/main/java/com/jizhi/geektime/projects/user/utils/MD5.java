package com.jizhi.geektime.projects.user.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MD5计算工具类
 *
 * @author jizhi7
 * @since 1.0
 **/
public class MD5 {

    private static Logger logger = Logger.getLogger(MD5.class.getName());

    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return byteToHex(md5.digest());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "file{} MD5 fail", e);
            return null;
        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "close fileInputStream fail", e);
                return null;
            }
        }
    }

    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }

}
