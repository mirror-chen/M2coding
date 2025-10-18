package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // return statement included so that the starter code can compile and run.
        // Defining some constants
        String normalized = breed.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
        String url = "https://dog.ceo/api/breed/" + normalized + "/list";

        try {
            Request request = new Request.Builder().url(url).get().build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new BreedNotFoundException("Request failed with HTTP code: " + response.code());
                }

                String responseBody = response.body().string();

                JSONObject json = new JSONObject(responseBody);
                if (!"success".equalsIgnoreCase(json.optString("status"))) {
                    throw new BreedNotFoundException("Breed not found: " + breed);
                }

                JSONArray subBreedsJson = json.getJSONArray("message");
                List<String> subBreeds = new ArrayList<>();
                for (int i = 0; i < subBreedsJson.length(); i++) {
                    subBreeds.add(subBreedsJson.getString(i));
                }

                return subBreeds;
            }

        } catch (IOException | JSONException e) {
            throw new BreedNotFoundException("Failed to fetch sub-breeds for " + breed);
        }
    }
}