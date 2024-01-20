package com.example.java_everyday.sign;


import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AppUtils {

    /**
     * key:appId、value:appSecret
     */
    static Map<String, String> appMap = new ConcurrentHashMap<>();

    /**
     * 分别保存生成的公私钥对
     * key:appId，value:公私钥对
     */
    static Map<String, Map<String, String>> appKeyPair = new ConcurrentHashMap<>();


    static Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .initialCapacity(100) // 初始的缓存空间大小
            .maximumSize(1000) // 缓存的最大条数
            .removalListener((key, val, removalCause) -> System.out.println("key: " + key + "被移除"))
            .evictionListener((key, val, evictionCause) -> System.out.println("key: " + key + "被回收"))
            .recordStats() // 记录命中
            .build();

    private static String initAppInfo() {
        // appId、appSecret生成规则，依据之前介绍过的方式，保证全局唯一即可
        String appId = "123456";
        String appSecret = "654321";
        appMap.put(appId, appSecret);
        return appId;
    }

    /**
     * 生成公私钥对
     *
     * @throws Exception
     */
    public static void initKeyPair(String appId) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("publicKey", new String(Base64.getEncoder().encode(publicKey.getEncoded())));
        keyMap.put("privateKey", new String(Base64.getEncoder().encode(privateKey.getEncoded())));
        appKeyPair.put(appId, keyMap);
    }

    private static void serverVerify(String requestParam) throws Exception {
        APIRequestEntity apiRequestEntity = JSONObject.parseObject(requestParam, APIRequestEntity.class);
        Header header = apiRequestEntity.getHeader();
        String userEntity = JSONObject.toJSONString(apiRequestEntity.getBody());

        // 首先，拿到参数后同样进行签名
        String sign = getSHA256Str(userEntity);
        if (!sign.equals(header.getSign())) {
            throw new Exception("数据签名错误！");
        }

        // 从header中获取相关信息，其中appSecret需要自己根据传过来的appId来获取
        String appId = header.getAppId();
        String appSecret = getAppSecret(appId);
        String nonce = header.getNonce();
        String timestamp = header.getTimestamp();

        // 请求时间有效期校验
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if ((now - Long.parseLong(timestamp)) / 1000 / 60 >= 5) {
            throw new Exception("请求过期！");
        }

        // nonce有效性判断
        String str = cache.getIfPresent(appId + "_" + nonce);
        if (Objects.nonNull(str)) {
            throw new Exception("重复请求！");
        }
        cache.put(appId + "_" + nonce, "1");

        // 按照同样的方式生成appSign，然后使用公钥进行验签
        StringBuilder sb = getStringBuilder(appId, nonce, sign, timestamp, appSecret);

        if (!rsaVerifySignature(sb.toString(), appKeyPair.get(appId).get("publicKey"), header.getAppSign())) {
            throw new Exception("公钥验签错误！");
        }

        System.out.println("【提供方】验证通过！");
    }

    public static String clientCall() {
        // 假设接口请求方与接口提供方，已经通过其他渠道，确认了双方交互的appId、appSecret
        String appId = "123456";
        String appSecret = "654321";
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 应该为随机数，演示随便写一个
        String nonce = "1234";

        // 业务请求参数
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId("1");
        userEntity.setPhone("13912345678");

        // 使用sha256的方式生成签名
        String sign = getSHA256Str(JSONObject.toJSONString(userEntity));

        StringBuilder sb = getStringBuilder(appId, nonce, sign, timestamp, appSecret);
        System.out.println("【请求方】拼接后的参数：" + sb);

        // 使用sha256withRSA的方式对header中的内容加签
        String appSign = sha256withRSASignature(appKeyPair.get(appId).get("privateKey"), sb.toString());
        System.out.println("【请求方】appSign：" + appSign);

        // 请求参数组装
        Header header = Header.builder()
                .appId(appId)
                .nonce(nonce)
                .sign(sign)
                .timestamp(timestamp)
                .appSign(appSign)
                .build();
        APIRequestEntity apiRequestEntity = new APIRequestEntity();
        apiRequestEntity.setHeader(header);
        apiRequestEntity.setBody(userEntity);

        String requestParam = JSONObject.toJSONString(apiRequestEntity);
        System.out.println("【请求方】接口请求参数: " + requestParam);

        return requestParam;
    }

    private static StringBuilder getStringBuilder(String appId, String nonce, String sign, String timestamp, String appSecret) {
        Map<String, String> data = new HashMap<>();
        data.put("appId", appId);
        data.put("nonce", nonce);
        data.put("sign", sign);
        data.put("timestamp", timestamp);
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (!data.get(k).trim().isEmpty()) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("appSecret=").append(appSecret);
        return sb;
    }

    /**
     * 私钥签名
     *
     * @param privateKeyStr
     * @param dataStr
     * @return
     */
    public static String sha256withRSASignature(String privateKeyStr, String dataStr) {
        try {
            byte[] key = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(dataStr.getBytes());
            return new String(Base64.getEncoder().encode(signature.sign()));
        } catch (Exception e) {
            throw new RuntimeException("签名计算出现异常", e);
        }
    }

    /**
     * 公钥验签
     *
     * @param dataStr
     * @param publicKeyStr
     * @param signStr
     * @return
     * @throws Exception
     */
    public static boolean rsaVerifySignature(String dataStr, String publicKeyStr, String signStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr));
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataStr.getBytes());
        return signature.verify(Base64.getDecoder().decode(signStr));
    }


    private static String getAppSecret(String appId) {
        return String.valueOf(appMap.get(appId));
    }

    @SneakyThrows
    public static String getSHA256Str(String str) {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(hash);
    }

    public static void main(String[] args) throws Exception {
        try {
            String appId = initAppInfo(); // 模拟生成appId、appSecret
            initKeyPair(appId); // 根据appId生成公私钥对
            String requestParam = clientCall(); // 模拟请求方
            serverVerify(requestParam); // 模拟提供方验证
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
