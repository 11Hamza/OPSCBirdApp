package com.test.userlocation.EbirdHotspotFile
import com.google.gson.annotations.SerializedName

data class Hotspot(
    @SerializedName("locId") val locId: String?,
    @SerializedName("locName") val name: String?, // Change to "locName"
    @SerializedName("lat") val latitude: Double?, // Change to "lat"
    @SerializedName("lng") val longitude: Double?, // Change to "lng"
    @SerializedName("countryCode") val countryCode: String?,
    @SerializedName("subnational1Code") val subnational1Code: String?,
    @SerializedName("latestObsDt") val latestObsDt: String?,
    @SerializedName("numSpeciesAllTime") val numSpeciesAllTime: Int?

)

