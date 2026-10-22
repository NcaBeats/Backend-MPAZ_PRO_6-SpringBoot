package com.example.mpaz_pro_6springboot.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementacion minima de JWT (HS256) con la libreria estandar de Java.
 * El payload solo contiene los claims emitidos por este backend, por lo que
 * la extraccion se hace una vez validada la firma.
 */
@Component
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_DECODER = Base64.getUrlDecoder();

    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(
            @Value("${mpaz.auth.jwt.secret}") String secret,
            @Value("${mpaz.auth.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(CustomUserDetails user) {
        long issuedAt = Instant.now().getEpochSecond();
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("{\"sub\":\"" + escape(user.getUsername())
                + "\",\"uid\":" + user.getUsuario().getId()
                + ",\"rol\":\"" + user.getUsuario().getRol() + "\""
                + ",\"iat\":" + issuedAt
                + ",\"exp\":" + (issuedAt + expirationSeconds) + "}");
        String content = header + "." + payload;
        return content + "." + sign(content);
    }

    public Optional<JwtClaims> parse(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }

        String content = parts[0] + "." + parts[1];
        String expectedSignature = sign(content);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8))) {
            return Optional.empty();
        }

        String payload;
        try {
            payload = new String(BASE64_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        String username = stringClaim(payload, "sub");
        Long usuarioId = numberClaim(payload, "uid");
        long expiration = numberClaimOr(payload, "exp", -1);
        if (username == null || username.isBlank() || usuarioId == null
                || expiration <= Instant.now().getEpochSecond()) {
            return Optional.empty();
        }

        return Optional.of(new JwtClaims(username, usuarioId, stringClaim(payload, "rol"), expiration));
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return BASE64_ENCODER.encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token JWT", ex);
        }
    }

    private static String encode(String value) {
        return BASE64_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String stringClaim(String payload, String name) {
        Matcher matcher = Pattern.compile(
                "\"" + Pattern.quote(name) + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(payload);
        return matcher.find() ? unescape(matcher.group(1)) : null;
    }

    private static Long numberClaim(String payload, String name) {
        Matcher matcher = Pattern.compile(
                "\"" + Pattern.quote(name) + "\"\\s*:\\s*(-?\\d+)").matcher(payload);
        if (!matcher.find()) {
            return null;
        }
        try {
            return Long.parseLong(matcher.group(1));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static long numberClaimOr(String payload, String name, long fallback) {
        Long value = numberClaim(payload, name);
        return value != null ? value : fallback;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String value) {
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
