package io.cofix.hedging.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.HashMap;

public class JwtUtil {
    //过期时间15分钟
    private static final long EXPIRE_TIME = 15 * 60 * 1000;

    //生成签名,15分钟后过期
    public static String sign(String username, String password) {
        //过期时间
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        //使用用户名和密码的MD5作为私钥进行加密
        String key = getAlgorithmKey(username, password);
        Algorithm algorithm = Algorithm.HMAC256(key);
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        //附带username生成签名
        return JWT.create().withHeader(header).withClaim("username", username).withExpiresAt(date).sign(algorithm);
    }

    //校验token
    public static boolean verity(String token, String username, String password) {
        try {
            String key  = getAlgorithmKey(username, password);
            Algorithm algorithm = Algorithm.HMAC256(key);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private static String getAlgorithmKey(String username, String password) {
        String  key = username + "-" + password;

        key = DigestUtils.md5Hex(key);

        return key;
    }
}
