package com.test.userlocation.MapBoxFile

import CustomDirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapBoxService {
    @GET("directions/v5/{profile}/{coordinates}")
    fun getDirections(
        @Path("profile", encoded = true) profile: String,
        @Path(value = "coordinates", encoded = true) coordinates: String,
        @Query("alternatives") alternatives: Boolean,
        @Query("geometries") geometries: String,
        @Query("language") language: String,
        @Query("overview") overview: String,
        @Query("steps") steps: Boolean,
        @Query("access_token") accessToken: String
    ): Call<CustomDirectionsResponse>
}
