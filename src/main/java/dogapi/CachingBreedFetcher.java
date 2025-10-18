package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {

        if (breed == null || breed.isBlank()) {
            throw new IllegalArgumentException("Breed must not be null or blank");
        }

        String key = breed.toLowerCase(Locale.ROOT);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        callsMade++;

        try {
            List<String> subBreeds = fetcher.getSubBreeds(breed);

            cache.put(key, new ArrayList<>(subBreeds));

            return new ArrayList<>(subBreeds);
        }
        catch (BreedNotFoundException e) {
            throw new BreedNotFoundException(breed);
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}