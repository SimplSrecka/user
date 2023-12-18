package si.fri.rso.simplsrecka.user.services.externalAPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.enterprise.context.ApplicationScoped;
import org.json.JSONObject;

@ApplicationScoped
public class EmailOTPService {

    private String apiKey = System.getenv("EMAIL_OTP_API_KEY");

    public String getOTP(String email) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://email-authentication-system.p.rapidapi.com/?recipient=" + email + "&app=SimplSrecka"))
                .header("X-RapidAPI-Key", "d15df1c29bmsh9ff9c640b3dcbcep18a777jsna4a870b2b85e")
                //.header("X-RapidAPI-Key", apiKey)
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
}
