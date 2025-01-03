package cn.gyyx.im.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    /**
     * 加密串的组成元素
     */
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    private MD5(){

    }

    /**
     * 对象明文进行MD5编码32位
     * @param s 待编码的字符串
     * @return 摘要后的字符串
     */
    public static String encode(String s) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10)
                    hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * @Title: byteToString
     * @Description: 字节转成字符创
     * @return
     * String
     * @throws
     */
    private static String byteToString(byte[] md){
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = HEX_DIGITS[byte0 >>> 4 & 0xf];
            str[k++] = HEX_DIGITS[byte0 & 0xf];
        }
        return new String(str);
    }

    /**
     * 对字符串进行md5编码，返回字节格式的Md5值
     * @param s 文本
     * @return 编码后的字节
     */
    public static byte[] encodeFromStr(String s) {
        byte[] strTemp = s.getBytes();
        return encodeFromByte(strTemp);
    }

    /**
     * 对字节码进行md5,返回字节格式的md5
     * @param bytes 待编码的字节
     * @return 编码后字节
     */
    public static byte[] encodeFromByte(byte[] bytes){
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(bytes);
            return mdTemp.digest();
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    /**
     * 进行hmac编码
     * @param content 待编码字符
     * @param privateKey 私钥
     * @return 编码后结果
     */
    public static String hmacEncode(String content, String privateKey) {
        return byteToString(hmacEncode(content.getBytes(), privateKey.getBytes()));
    }


    /**
     * 进行hmac编码
     * @param content 待编码字符
     * @param privateKey 私钥字节
     * @return 编码后字节
     */
    public static byte[] hmacEncode(byte[] content, byte[] privateKey) {
        /*
         * HmacMd5 calculation formula: H(K XOR opad, H(K XOR ipad, text))
         * HmacMd5 计算公式：H(K XOR opad, H(K XOR ipad, text))
         * H代表hash算法，本类中使用MD5算法，K代表密钥，text代表要加密的数据 ipad为0x36，opad为0x5C。
         */
        int length = 64;
        byte[] ipad = new byte[length];
        byte[] opad = new byte[length];
        for (int i = 0; i < 64; i++) {
            ipad[i] = 0x36;
            opad[i] = 0x5C;
        }

        byte[] actualKey = privateKey; // Actual key.
        byte[] keyArr = new byte[length]; // Key bytes of 64 bytes length
        /*
         * If key's length is longer than 64,then use hash to digest it and use
         * the result as actual key. 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
         */
        if (privateKey.length > length) {
            actualKey = encodeFromByte(privateKey);
        }

        for (int i = 0; i < actualKey.length; i++) {
            keyArr[i] = actualKey[i];
        }

        /*
         * append zeros to K 如果密钥长度不足64字节，就使用0x00补齐到64字节。
         */
        if (actualKey.length < length) {
            for (int i = actualKey.length; i < keyArr.length; i++)
                keyArr[i] = 0x00;
        }

        /*
         * calc K XOR ipad 使用密钥和ipad进行异或运算。
         */
        byte[] kIpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
        }

        /*
         * append "text" to the end of "K XOR ipad" 将待加密数据追加到K XOR ipad计算结果后面。
         */
        byte[] firstAppendResult = new byte[kIpadXorResult.length + content.length];
        for (int i = 0; i < kIpadXorResult.length; i++) {
            firstAppendResult[i] = kIpadXorResult[i];
        }

        for (int i = 0; i < content.length; i++) {
            firstAppendResult[i + keyArr.length] = content[i];
        }

        /*
         * calc H(K XOR ipad, text) 使用哈希算法计算上面结果的摘要。
         */
        byte[] firstHashResult = encodeFromByte(firstAppendResult);

        /*
         * calc K XOR opad 使用密钥和opad进行异或运算。
         */
        byte[] kOpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
        }

        /*
         * append "H(K XOR ipad, text)" to the end of "K XOR opad" 将H(K XOR
         * ipad, text)结果追加到K XOR opad结果后面
         */
        byte[] secondAppendResult = new byte[kOpadXorResult.length + firstHashResult.length];

        for (int i = 0; i < kOpadXorResult.length; i++) {
            secondAppendResult[i] = kOpadXorResult[i];
        }

        for (int i = 0; i < firstHashResult.length; i++) {
            secondAppendResult[i + keyArr.length] = firstHashResult[i];
        }

        /*
         * H(K XOR opad, H(K XOR ipad, text)) 对上面的数据进行哈希运算。
         */
        return encodeFromByte(secondAppendResult);
    }

}
