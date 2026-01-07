package com.xybi.springbootinit.utils;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;

/**
 * Ed25519 JWT 工具类
 */
public class Ed25519JWTUtil {
    
    private static final int DEFAULT_EXPIRE_SECONDS = 900; // 15分钟
    private static final int CLOCK_SKEW_SECONDS = 30; // 时钟偏移补偿30秒
    
    /**
     * 生成 Ed25519 签名的 JWT
     *
     * @param privateKeyBase64 Base64编码的私钥（PKCS#8格式）
     * @param subject         JWT主题
     * @param kid             密钥ID
     * @param expireSeconds   过期时间（秒），如果为null则使用默认值
     * @return JWT字符串
     * @throws Exception 签名异常
     */
    public static String generateJWT(String privateKeyBase64, String subject, String kid, Integer expireSeconds) throws Exception {
        // 解析私钥
        String privateKeyString = privateKeyBase64.trim()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .trim();
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        byte[] seed = parsePkcs8Ed25519Key(privateKeyBytes);
        
        EdDSANamedCurveSpec edSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        EdDSAPrivateKeySpec privateKeySpec = new EdDSAPrivateKeySpec(seed, edSpec);
        EdDSAPrivateKey privateKey = new EdDSAPrivateKey(privateKeySpec);
        
        // 计算过期时间
        int expireTime = expireSeconds != null ? expireSeconds : DEFAULT_EXPIRE_SECONDS;
        
        // 创建JWT头部
        String headerJson = String.format("{\"alg\": \"EdDSA\", \"kid\": \"%s\"}", kid);
        
        // 创建JWT载荷
        long iat = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() - CLOCK_SKEW_SECONDS;
        long exp = iat + expireTime;
        String payloadJson = String.format("{\"sub\": \"%s\", \"iat\": %d, \"exp\": %d}", subject, iat, exp);
        
        // 编码头部和载荷
        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes());
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes());
        String headerAndPayload = encodedHeader + "." + encodedPayload;
        
        // 生成签名
        EdDSAEngine eddsa = new EdDSAEngine();
        eddsa.initSign(privateKey);
        eddsa.update(headerAndPayload.getBytes());
        byte[] signature = eddsa.sign();
        String encodedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        
        // 组装完整的JWT
        return headerAndPayload + "." + encodedSignature;
    }
    
    /**
     * 生成默认过期时间的JWT
     */
    public static String generateJWT(String privateKeyBase64, String subject, String kid) throws Exception {
        return generateJWT(privateKeyBase64, subject, kid, null);
    }
    
    /**
     * 验证 JWT 签名
     *
     * @param jwt              JWT字符串
     * @param publicKeyBase64  Base64编码的公钥
     * @return 验证结果
     * @throws Exception 验证异常
     */
    public static boolean verifyJWT(String jwt, String publicKeyBase64) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        
        String headerAndPayload = parts[0] + "." + parts[1];
        byte[] signature = Base64.getUrlDecoder().decode(parts[2]);
        
        // 解析公钥
        String publicKeyString = publicKeyBase64.trim()
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .trim();
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        
        EdDSANamedCurveSpec edSpec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        EdDSAPublicKeySpec publicKeySpec = new EdDSAPublicKeySpec(publicKeyBytes, edSpec);
        EdDSAPublicKey publicKey = new EdDSAPublicKey(publicKeySpec);
        
        // 验证签名
        EdDSAEngine eddsa = new EdDSAEngine();
        eddsa.initVerify(publicKey);
        eddsa.update(headerAndPayload.getBytes());
        return eddsa.verify(signature);
    }
    
    /**
     * 解析 JWT 并检查是否过期
     *
     * @param jwt JWT字符串
     * @return 是否有效（未过期）
     */
    public static boolean isNotExpired(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) {
                return false;
            }
            
            // 解码载荷
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(payloadBytes);
            
            // 提取exp字段
            int expStart = payloadJson.indexOf("\"exp\":");
            if (expStart == -1) {
                return false;
            }
            
            expStart += 6; // 跳过 "\"exp\":"
            int expEnd = payloadJson.indexOf(',', expStart);
            if (expEnd == -1) {
                expEnd = payloadJson.indexOf('}', expStart);
            }
            
            long exp = Long.parseLong(payloadJson.substring(expStart, expEnd).trim());
            long currentTime = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
            
            return currentTime < exp;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 解析 JWT 主题
     *
     * @param jwt JWT字符串
     * @return 主题字符串，解析失败返回null
     */
    public static String getSubject(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            
            // 解码载荷
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(payloadBytes);
            
            // 提取sub字段
            int subStart = payloadJson.indexOf("\"sub\":");
            if (subStart == -1) {
                return null;
            }
            
            subStart += 6; // 跳过 "\"sub\":"
            int subEnd = payloadJson.indexOf(',', subStart);
            if (subEnd == -1) {
                subEnd = payloadJson.indexOf('}', subStart);
            }
            
            String subValue = payloadJson.substring(subStart, subEnd).trim();
            // 移除引号
            if (subValue.startsWith("\"") && subValue.endsWith("\"")) {
                subValue = subValue.substring(1, subValue.length() - 1);
            }
            
            return subValue;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 解析 PKCS#8 格式的 Ed25519 私钥
     */
    private static byte[] parsePkcs8Ed25519Key(byte[] pkcs8Key) throws Exception {
        if (pkcs8Key.length < 48) {
            throw new IllegalArgumentException("Invalid Ed25519 private key format");
        }
        
        // 查找 Ed25519 私钥的 32 字节种子
        int seedIndex = pkcs8Key.length - 32;
        if (pkcs8Key[seedIndex - 2] == 0x04 && pkcs8Key[seedIndex - 1] == 0x20) {
            // 找到了 OCTET STRING 标记 (0x04 0x20)，后面是 32 字节的种子
            byte[] seed = new byte[32];
            System.arraycopy(pkcs8Key, seedIndex, seed, 0, 32);
            return seed;
        } else {
            // 尝试其他可能的位置
            for (int i = 0; i < pkcs8Key.length - 34; i++) {
                if (pkcs8Key[i] == 0x04 && pkcs8Key[i + 1] == 0x20) { // OCTET STRING with 32-byte length
                    byte[] seed = new byte[32];
                    System.arraycopy(pkcs8Key, i + 2, seed, 0, 32);
                    return seed;
                }
            }
        }
        
        throw new IllegalArgumentException("Cannot find Ed25519 seed in PKCS#8 key");
    }
}
