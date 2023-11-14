package com.test.userlocation.EbirdHotspotFile
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface EbirdApiService {
        @GET("ref/hotspot/{regionCode}")
        suspend fun getHotspotsInRegion(
            @Path("regionCode") regionCode: String,
            @Query("back") back: Int = 30,
            @Query("fmt") format: String = "json",
            @Query("key") apiKey: String
        ): Response<List<Hotspot>> // Change the return type to Response<List<Hotspot>>

    @GET("ref/hotspot/geo")
    fun getNearbyHotspots(
        @Header("x-ebirdapitoken") apiKey: String, // Add API key as a header
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("back") back: Int = 30, // Default to fetching hotspots up to 30 days back
        @Query("dist") distance: Int = 25, // Default search radius of 25 kilometers
        @Query("fmt") format: String = "json" // Default to JSON format
    ): Call<List<Hotspot>>
//    @GET("directions/v5/{profile}/{coordinates}")
//    fun getDirections(
//        @Path("profile") profile: String, // e.g., "walking", "cycling", "driving"
//        @Path("coordinates") coordinates: String, // e.g., "-122.42,37.78;-77.03,38.91"
//        @Query("access_token") accessToken: String // Your Mapbox Access Token
//    ): Call<DirectionsResponse>
}
