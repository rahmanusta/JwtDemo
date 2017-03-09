package com.kodedu.jwt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Created by usta on 03.03.2017.
 */
public class JwtApp {

    private static String SECRET_KEY = "Merhaba KodEdu";

    public static void main(String[] args) throws Exception {

        String JWT_TOKEN = generateJWT();

        boolean isValid = validateJWT(JWT_TOKEN);

        System.out.println("Is valid ? " + isValid);

    }

    private static String generateJWT() throws Exception {

        String HEADER = String.join("\n", Files.readAllLines(Paths.get("./header.json")));
        String PAYLOAD = String.join("\n", Files.readAllLines(Paths.get("./payload.json")));

        String PART1 = doBASE64(HEADER);
        String PART2 = doBASE64(PAYLOAD);

        String PART1_PART2 = PART1 + "." + PART2;

        String PART3 = doBASE64(doHMACSHA256(PART1_PART2, SECRET_KEY));

        String JWT_TOKEN = PART1_PART2 + "." + PART3;

        return JWT_TOKEN;
    }

    private static String doHMACSHA256(String part1AndPart2, String secretKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(), "HmacSHA256"));

        byte[] hashBytes = mac.doFinal(part1AndPart2.getBytes());
        String hash = doBASE64(hashBytes);
        return hash;
    }

    private static String doBASE64(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        String base64 = encoder.encodeToString(bytes);
        return base64;
    }

    private static String doBASE64(String input) {
        byte[] bytes = input.getBytes(Charset.forName("UTF-8"));
        String base64 = doBASE64(bytes);
        return base64;
    }

    private static boolean validateJWT(String jwt) throws Exception {

        String[] parts = jwt.split("\\.");
        String PART1 = parts[0];
        String PART2 = parts[1];
        String PART3 = parts[2];

        String PART1_PART2 = PART1 + "." + PART2;

        String jwtSignature = doBASE64(doHMACSHA256(PART1_PART2, SECRET_KEY));

        return jwtSignature.equals(PART3);

    }
}
