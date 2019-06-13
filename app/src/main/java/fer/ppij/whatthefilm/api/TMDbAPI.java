package fer.ppij.whatthefilm.api;

import fer.ppij.whatthefilm.model.Movie;
import fer.ppij.whatthefilm.model.ResultsPage;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbAPI {

    @GET("movie/{movie_id}")
    Single<Movie> getMovie(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/popular?language=en-US")
    Single<ResultsPage> getPopularMovies(@Query("page") int page, @Query("api_key") String apiKey);
}
