package lk.watupa.identity.utils;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class JwtKeyGenerator  {
    public static void main(String[] args) {
        var key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }
}
