package si.fri.rso.simplsrecka.user.services.externalAPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmailValidatorService {

    private String apiKey = System.getenv("EMAIL_VERIFIER_API_KEY");

    public boolean isValidEmail(String email) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://email-checker.p.rapidapi.com/verify/v1?email=" + email))
                    //.header("X-RapidAPI-Key", "d15df1c29bmsh9ff9c640b3dcbcep18a777jsna4a870b2b85e")
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "email-checker.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject jsonResponse = new JSONObject(response.body());
            String status = jsonResponse.getString("status");

            return "valid".equals(status);

        } catch (Exception e) {
            // Log the exception and handle it as per your requirements
            return false;
        }
    }
}