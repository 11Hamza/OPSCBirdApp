package com.test.userlocation.MapBoxFile

import com.google.gson.annotations.SerializedName


data class Steps (

    @SerializedName("intersections" ) var intersections : ArrayList<Intersections> = arrayListOf(),
    @SerializedName("maneuver"      ) var maneuver      : Maneuver?                = Maneuver(),
    @SerializedName("name"          ) var name          : String?                  = null,
    @SerializedName("duration"      ) var duration      : Double?                  = null,
    @SerializedName("distance"      ) var distance      : Double?                  = null,
    @SerializedName("driving_side"  ) var drivingSide   : String?                  = null,
    @SerializedName("weight"        ) var weight        : Double?                  = null,
    @SerializedName("mode"          ) var mode          : String?                  = null,
    @SerializedName("geometry"      ) var geometry      : Geometry?                = Geometry()

)
