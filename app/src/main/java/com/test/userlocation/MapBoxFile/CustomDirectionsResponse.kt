import com.google.gson.annotations.SerializedName
import com.test.userlocation.MapBoxFile.Geometry

data class CustomDirectionsResponse(
    @SerializedName("routes") val routes: List<CustomRoute>
)

data class CustomRoute(
    @SerializedName("geometry") val geometry:CustomGeometry,
    @SerializedName("distance") val distance: Double  // assuming the distance is provided in meters


)


data class CustomGeometry(
    @SerializedName("coordinates") val coordinates: List<List<Double>>,
    @SerializedName("type") val type: String
)
