package si.fri.rso.simplsrecka.user.services.externalAPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.enterprise.context.ApplicationScoped;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@ApplicationScoped
public class EmailOTPService {

    private String apiKey = System.getenv("EMAIL_OTP_API_KEY");

    public String getOTP(String email) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://email-authentication-system.p.rapidapi.com/?recipient=" + email + "&app=SimplSrecka"))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", "email-authentication-system.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject responseObject = new JSONObject(response.body());
            String authenticationCode = responseObject.getString("authenticationCode");
            return authenticationCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String sendMailWithCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));

        String body = "Vaša OTP koda je: " + code;

        // Construct the request body for the API
        String requestBody = String.format("{\n" +
                "    \"ishtml\": \"false\",\n" +
                "    \"sendto\": \"%s\",\n" +
                "    \"title\": \"SimplSrecka OTP koda\",\n" +
                "    \"body\": \"%s\"\n" +
                "}", email, body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://rapidmail.p.rapidapi.com/"))
                .header("content-type", "application/json")
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", "rapidmail.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            return toMD5(code);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String toMD5(String code) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(code.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
