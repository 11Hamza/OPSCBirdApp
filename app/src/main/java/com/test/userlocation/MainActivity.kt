package com.test.userlocation

import CustomDirectionsResponse
import CustomRoute
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.test.userlocation.EbirdHotspotFile.EbirdApiService
import com.test.userlocation.EbirdHotspotFile.Hotspot
import com.test.userlocation.LocationFile.LocationPermissionHelper
import com.test.userlocation.MapBoxFile.MapBoxService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import android.graphics.Color
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.test.userlocation.SettingsFile.SettingsActivity
import kotlin.math.*
import android.widget.LinearLayout



class MainActivity : AppCompatActivity() {
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var mapView: MapView
    private lateinit var popupContainer: FrameLayout

    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0


    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
        rotateMap(90.0)
    }



    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener { point ->
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(point).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(point)
        rotateMap(90.0)

        // Update user's location
        userLatitude = point.latitude()
        userLongitude = point.longitude()
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    // Move the API base URL to resources or a separate config file
    private val baseUrl = "https://api.ebird.org/v2/"
    private val apiKey = "7jg12hq274nf"

    private val mapBoxBaseURL = "https://api.mapbox.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val ebirdApiService = retrofit.create(EbirdApiService::class.java)
    private val mapbBoxService = retrofit.create(MapBoxService::class.java)
    private var radius: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val Home = findViewById<Button>(R.id.btn_BackHome)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        Home.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
        // radius = getRadiusValue().toInt()

        mapView = findViewById(R.id.mapView)
        popupContainer = findViewById(R.id.customPopupContainer)

        val accessToken = getString(R.string.YOUR_MAPBOX_ACCESS_TOKEN)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            onMapReady()

        }

        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }

    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            val imageBitmap =
                AppCompatResources.getDrawable(this@MainActivity, R.drawable.img)?.toBitmap()
            if (imageBitmap != null) {
                style.addImage("img", imageBitmap)
            }
            initLocationComponent()
            setupGesturesListener()
            CoroutineScope(Dispatchers.Main).launch {
                val hotspots = fetchHotspotsInRegion(
                    radius ?: 10
                )  // Use 10 as default value if radius is null
                val pointAnnotationManager =
                    mapView.annotations.createPointAnnotationManager(mapView)
                for (hotspot in hotspots) {
                    val markerLongitude = hotspot.longitude ?: 0.0
                    val markerLatitude = hotspot.latitude ?: 0.0
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(markerLongitude, markerLatitude))
                        .withIconImage("img")
                        .withIconSize(0.3)
                    //    .withTextField(hotspot.name ?: "")   Display Hotspot name on map. uncode if u want that. it displays in popup
                    pointAnnotationManager.create(pointAnnotationOptions)
                }
                pointAnnotationManager.addClickListener { clickedAnnotation ->
                    val hotspot = hotspots.find { hotspot ->
                        val point =
                            Point.fromLngLat(hotspot.longitude ?: 0.0, hotspot.latitude ?: 0.0)
                        point == clickedAnnotation.point
                    }
                    if (hotspot != null) {
                        showPopup(hotspot, style)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateMapWithNewRadius()

    }

    private fun updateMapWithNewRadius() {
        radius = getRadiusValue().toInt()
        CoroutineScope(Dispatchers.Main).launch {
            val hotspots =
                fetchHotspotsInRegion(radius ?: 10)  // Use 10 as default value if radius is null
            updateMapWithHotspots(hotspots)
        }
    }

    fun updateMapWithHotspots(hotspots: List<Hotspot>) {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            val pointAnnotationManager = mapView.annotations.createPointAnnotationManager(mapView)
            pointAnnotationManager.deleteAll()
            for (hotspot in hotspots) {
                val markerLongitude = hotspot.longitude ?: 0.0
                val markerLatitude = hotspot.latitude ?: 0.0
                val pointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(markerLongitude, markerLatitude))
                    .withIconImage("img")
                    .withIconSize(0.3)
                pointAnnotationManager.create(pointAnnotationOptions)
            }
            pointAnnotationManager.addClickListener { clickedAnnotation ->
                val hotspot = hotspots.find { hotspot ->
                    val point =
                        Point.fromLngLat(hotspot.longitude ?: 0.0, hotspot.latitude ?: 0.0)
                    point == clickedAnnotation.point
                }
                if (hotspot != null) {
                    updateObservationsScrollView(hotspot.locId ?: "")

                    showPopup(hotspot, style)

                    true
                } else {
                    false
                }
            }
        }
    }

    // In MainActivity
    private fun getUserPreferredUnit(): String {
        val sharedPreferences = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("unit_key", "km")
            ?: "km"  // default to km if no preference is saved
    }


    private fun showPopup(hotspot: Hotspot, style: Style) {


        val navigationOptions = NavigationOptions.Builder(this)
            .accessToken(getString(R.string.YOUR_MAPBOX_ACCESS_TOKEN))
            .build()
        val mapboxNavigation = MapboxNavigationProvider.create(navigationOptions)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        mapboxNavigation.startTripSession()

        mapboxNavigation.registerRouteProgressObserver(object : RouteProgressObserver {
            override fun onRouteProgressChanged(routeProgress: RouteProgress) {
                // Distance to the next turn
                val distanceToNextTurn = routeProgress.currentLegProgress?.currentStepProgress?.distanceRemaining?.toInt()
                val distanceToNextTurnText = "${distanceToNextTurn ?: 0} meters"
                findViewById<TextView>(R.id.textViewDistanceToTurn).text = distanceToNextTurnText

                // Name of the street to turn onto
                val nextStepInstruction = routeProgress.currentLegProgress?.currentStepProgress?.step?.maneuver()?.instruction()
                findViewById<TextView>(R.id.textViewNextStreetName).text = nextStepInstruction ?: "Unknown"

                // Estimated time of arrival
                val eta = routeProgress.durationRemaining.toInt() // Duration remaining in seconds
                val etaText = formatSecondsToTime(eta)
                findViewById<TextView>(R.id.textViewEstimatedArrivalTime).text = etaText
                updateNavigationUI(routeProgress)
            }
            private fun updateNavigationUI(routeProgress: RouteProgress) {
                // Distance to the next turn
                val distanceToNextTurn = routeProgress.currentLegProgress?.currentStepProgress?.distanceRemaining?.toInt()
                val distanceToNextTurnText = "${distanceToNextTurn ?: 0} meters"
                findViewById<TextView>(R.id.textViewDistanceToTurn).text = distanceToNextTurnText

                // Name of the street to turn onto
                val nextStepInstruction = routeProgress.currentLegProgress?.currentStepProgress?.step?.maneuver()?.instruction()
                findViewById<TextView>(R.id.textViewNextStreetName).text = nextStepInstruction ?: "Unknown"

                // Estimated time of arrival
                val eta = routeProgress.durationRemaining.toInt() // Duration remaining in seconds
                val etaText = formatSecondsToTime(eta)
                findViewById<TextView>(R.id.textViewEstimatedArrivalTime).text = etaText
            }

            private fun formatSecondsToTime(seconds: Int): String {
                val hours = seconds / 3600
                val minutes = (seconds % 3600) / 60
                return when {
                    hours > 0 -> String.format("%d hr %02d min", hours, minutes)
                    else -> String.format("%02d min", minutes)
                }
            }

        })


        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.custom_popup_layout, null)



        val mapboxMap = mapView.getMapboxMap()

        val mapBoxBaseURL = "https://api.mapbox.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(mapBoxBaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mapbBoxService = retrofit.create(MapBoxService::class.java)

        val PRECISION_6 = 6
        updateObservationsScrollView(hotspot.locId.toString())

        popupView.findViewById<TextView>(R.id.hotspot_name).text = hotspot.name ?: "N/A"
        popupView.findViewById<TextView>(R.id.latestObsDt).text =
            "Latest Observation: ${hotspot.latestObsDt ?: "N/A"}"
        popupView.findViewById<TextView>(R.id.numSpeciesAllTime).text =
            "Species Count: ${hotspot.numSpeciesAllTime?.toString() ?: "N/A"}"


        popupView.findViewById<Button>(R.id.close_button).setOnClickListener {
            popupContainer.visibility = View.GONE
        }

        popupView.findViewById<Button>(R.id.directions_button).setOnClickListener {
            val origin = Point.fromLngLat(userLongitude, userLatitude)
            val destination = Point.fromLngLat(hotspot.longitude!!, hotspot.latitude!!)
            mapboxNavigation.setRoutes(emptyList())
//            findViewById<TextView>(R.id.textViewDistanceToTurn).clearComposingText()
//            findViewById<TextView>(R.id.textViewNextStreetName).clearComposingText()
//            findViewById<TextView>(R.id.textViewEstimatedArrivalTime).clearComposingText()


            val routeOptions = RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .coordinatesList(listOf(origin, destination))
                .build()
            mapboxNavigation.setRoutes(emptyList())
            findViewById<TextView>(R.id.textViewDistanceToTurn).text = "Calculating..."
            findViewById<TextView>(R.id.textViewNextStreetName).text = "Calculating..."
            findViewById<TextView>(R.id.textViewEstimatedArrivalTime).text = "Calculating..."



            mapboxNavigation.requestRoutes(
                routeOptions,
                object : RouterCallback {
                    override fun onRoutesReady(
                        routes: List<DirectionsRoute>,
                        routerOrigin: RouterOrigin
                    ) {
                        val route = routes.firstOrNull()
                        if (route != null) {
                            // Convert route geometry to a LineString
                            val routeGeometry =
                                LineString.fromPolyline(route.geometry()!!, PRECISION_6)
                            val featureCollection = FeatureCollection.fromFeatures(
                                arrayOf(Feature.fromGeometry(routeGeometry))
                            )

                            val source = style.getSourceAs<GeoJsonSource>("route-source")
                            if (source != null) {
                                source.featureCollection(featureCollection)
                            } else {
                                val sourceBuilder = GeoJsonSource.Builder("route-source")
                                    .featureCollection(featureCollection)
                                style.addSource(sourceBuilder.build())
                                style.addLayer(lineLayer("route-layer", "route-source") {
                                    lineCap(LineCap.ROUND)
                                    lineJoin(LineJoin.ROUND)
                                    lineColor(Color.parseColor("#ff0077"))
                                    lineWidth(5.0)
                                    mapboxNavigation.setRoutes(routes)

                                })
                            }
                        }
                    }

                    override fun onFailure(
                        reasons: List<RouterFailure>,
                        routeOptions: RouteOptions
                    ) {
                        // Handle the failure
                    }

                    override fun onCanceled(
                        routeOptions: RouteOptions,
                        routerOrigin: RouterOrigin
                    ) {
                        // Handle the cancellation
                    }
                }
            )
        }

        // Add Observation Button setup
        val addObservationButton = popupView.findViewById<Button>(R.id.addObservationButton)
        addObservationButton.setOnClickListener {
            val observationPopupView = LayoutInflater.from(this).inflate(R.layout.observation_popup_layout, null)
            val observationDialog = AlertDialog.Builder(this)
                .setView(observationPopupView)
                .create()

            val submitObservationButton = observationPopupView.findViewById<Button>(R.id.submitObservationButton)
            submitObservationButton.setOnClickListener {
                val birdName = observationPopupView.findViewById<EditText>(R.id.birdNameEditText).text.toString()
                // Capture other observation details similarly

                // Logic to save these details to Firebase
                saveObservationToFirebase(birdName, hotspot.locId?:"")
                updateObservationsScrollView(hotspot.locId ?: "")
                observationDialog.dismiss()
            }

            observationDialog.show()
        }

        // Update Observations ScrollView
        updateObservationsScrollView(hotspot.locId.toString())

        // Rest of the code for showPopup method...
        popupContainer.removeAllViews()
        popupContainer.addView(popupView)
        popupContainer.visibility = View.VISIBLE
    }


    private fun saveObservationToFirebase(birdName: String, hotspotId: String) {
        val observation = mapOf(
            "birdName" to birdName,
            // Add other observation details here
        )

        val databaseReference = FirebaseDatabase.getInstance().getReference("Observations")
        databaseReference.child(hotspotId).push().setValue(observation)
            .addOnSuccessListener {
                Toast.makeText(this, "Observation saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save observation", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateObservationsScrollView(hotspotId: String) {
        val observationsLayout = findViewById<LinearLayout>(R.id.observationsLinearLayout)
        if (observationsLayout == null) {
            Log.e("MainActivity", "LinearLayout not found")
            return
        }
        val databaseReference = FirebaseDatabase.getInstance().getReference("Observations").child(hotspotId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //    observationsLayout.removeAllViews()
                for (observationSnapshot in snapshot.children) {
                    val observation = observationSnapshot.getValue(Observation::class.java)
                    val observationView = LayoutInflater.from(this@MainActivity).inflate(R.layout.observation_item_layout, null)

                    observationView.findViewById<TextView>(R.id.birdName).text = observation?.birdName
                    // Set other observation details

                    observationsLayout.addView(observationView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load observations", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.img
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MainActivity,
                    R.drawable.img
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun rotateMap(angle: Double) {
        val cameraOptions = CameraOptions.Builder()
            .bearing(angle)
            .build()
        mapView.getMapboxMap().setCamera(cameraOptions)
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String = "km"): Double {
        val r = 6371 // radius of the Earth in km
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distanceInKm = r * c  // distance in kilometers
        return if (unit == "mi") distanceInKm * 0.621371 else distanceInKm  // convert to miles if necessary
    }

    private fun getRadiusValue(): Float {
        val sharedPreferences = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("radius_key", 10f)
    }

    private suspend fun fetchHotspotsInRegion(radius: Int): List<Hotspot> {
        val preferredUnit = getUserPreferredUnit()
        // Convert radius to kilometers if user's preference is miles
        val radiusInKm = if (preferredUnit == "mi") radius * 1.60934 else radius.toDouble()

        val regionCode = "ZA"
        val back = 30
        val fmt = "json"
        val response = withContext(Dispatchers.IO) {
            ebirdApiService.getHotspotsInRegion(regionCode, back, fmt, apiKey)
        }
        if (response.isSuccessful) {
            val hotspots = response.body()
            if (hotspots != null) {
                // Filter the hotspots based on the distance from the user's location
                val filteredHotspots = hotspots.filter { hotspot ->
                    val distance = haversine(
                        userLatitude, userLongitude,
                        hotspot.latitude ?: 0.0, hotspot.longitude ?: 0.0
                    )
                    distance <= radius // keep hotspots within 10 km
                }

                if (filteredHotspots.isEmpty()) {
                    Log.d("API Response", "No hotspots found within 10 km")
                } else {
                    Log.d("API Response", "Filtered Hotspots: $filteredHotspots")
                    return filteredHotspots
                }
            } else {
                Log.e("API Error", "Response body is null")
            }
        } else {
            Log.e("API Error", "API request failed with status code: ${response.code()}")
        }
        return emptyList()

    }
}
