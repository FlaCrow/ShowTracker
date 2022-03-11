package com.flacrow.showtracker.api

import com.flacrow.showtracker.utils.Config
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface ShowAPI {
    //    @GET("movies/trending/movie/week")
//    fun getTrendingMovies(@Query("api_key") apiKey: String): List<MoviesJson>
//
//    @GET("movies/trending/tv/week")
//    fun getTrendingShows(@Query("api_key") apiKey: String): List<TvJson>
    @GET("movies/trending/all/week/?api_key=${Config.API_KEY}")
    fun getTrending(@Query("page") page: Int): List<ShowResponse>

}