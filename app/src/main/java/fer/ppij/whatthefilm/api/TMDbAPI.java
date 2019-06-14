package fer.ppij.whatthefilm.api;

import fer.ppij.whatthefilm.model.Movie;
import fer.ppij.whatthefilm.model.ResultsPage;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDbAPI {

    @GET("movie/{movie_id}?language=en-US")
    Single<Movie> getMovie(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/popular?language=en-US")
    Single<ResultsPage> getPopularMovies(@Query("page") int page, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/similar?language=en-US")
    Single<ResultsPage> getSimilarMovies(@Path("movie_id") int movieId, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("search/movie?language=en-US")
    Single<ResultsPage> searchMovies(@Query("query") String query, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("discover/movie?language=en-US")
    Single<ResultsPage> genreDiscover(@Query("with_genres") String genres, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("search/movie?language=en-US")
    Single<ResultsPage> getGenres(@Query("with_genres") String genres, @Query("page") int page, @Query("api_key") String apiKey);
}
