package com.neos.simulator.util;

import com.neos.simulator.controllers.CreateDeviceSimulationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {

    private static final Logger LOGGER = LogManager.getLogger(Utils.class);

    public static String stripQuotes(String s) {
        return s.replaceAll("'", "").replaceAll("\"", "").trim();
    }

    public static Thread findThreadById(long threadId) {
        return Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getId() == threadId)
                .findFirst()
                .orElse(null);
    }

    public static Map<String, Object> parseQuery(String query) throws UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }
        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], "UTF-8");
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";
            params.put(key, value);
        }
        return params;
    }

    public static UUID generateUUID(String input) throws NoSuchAlgorithmException {
        // Hash the input to ensure a consistent length
        byte[] hash = java.security.MessageDigest.getInstance("SHA-256")
                .digest(input.getBytes(StandardCharsets.UTF_8));

        // Use the first 16 bytes to create a UUID
        long mostSignificantBits = 0;
        long leastSignificantBits = 0;

        for (int i = 0; i < 8; i++) {
            mostSignificantBits = (mostSignificantBits << 8) | (hash[i] & 0xff);
        }

        for (int i = 8; i < 16; i++) {
            leastSignificantBits = (leastSignificantBits << 8) | (hash[i] & 0xff);
        }

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}

