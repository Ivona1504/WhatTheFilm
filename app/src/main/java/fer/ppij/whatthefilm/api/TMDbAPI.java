package fer.ppij.whatthefilm.api;

import fer.ppij.whatthefilm.model.Movie;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbAPI {

    @GET("movie/{movie_id}")
    Single<Movie> getMovie(@Path("movie_id") int movieId, @Query("api_key") String apiKey);
}
