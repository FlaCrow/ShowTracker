package com.flacrow.showtracker.data.api

import com.flacrow.core.utils.Config
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowAPI {
    //    @GET("movies/trending/movie/week")
//    fun getTrendingMovies(@Query("api_key") apiKey: String): List<MoviesJson>
//
//    @GET("movies/trending/tv/week")
//    fun getTrendingShows(@Query("api_key") apiKey: String): List<TvJson>
    @GET("3/trending/all/week?api_key=${Config.API_KEY}")
    suspend fun getTrending(@Query("page") page: Int): ShowResponse

    @GET("3/search/movie?api_key=${Config.API_KEY}")
    suspend fun searchMovieByQuery(
        @Query("query") query: String,
        @Query("page") page: Int
    ): ShowResponse

    @GET("3/search/tv?api_key=${Config.API_KEY}")
    suspend fun searchTvByQuery(
        @Query("query") query: String,
        @Query("page") page: Int
    ): ShowResponse


    @GET("3/movie/{movie_id}?api_key=${Config.API_KEY}")
    suspend fun searchMovieById(@Path("movie_id") id: Int): MovieDetailedResponse

    @GET("3/tv/{tv_id}?api_key=${Config.API_KEY}")
    suspend fun searchTvById(@Path("tv_id") id: Int): TvDetailedResponse

    @GET("3/movie/{movie_id}/credits?api_key=${Config.API_KEY}")
    suspend fun getMovieCredits(@Path("movie_id") id: Int): CreditsListsResponse

    @GET("3/tv/{tv_id}/aggregate_credits?api_key=${Config.API_KEY}")
    suspend fun getTvCredits(@Path("tv_id") id: Int): CreditsListsResponse

    @GET("3/tv/{tv_id}/season/{season_num}?api_key=${Config.API_KEY}")
    suspend fun getSeasonEpisodes(
        @Path("tv_id") id: Int,
        @Path("season_num") seasonNumber: Int
    ): SeasonEpisodesResponse
}