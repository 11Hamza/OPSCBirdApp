package com.test.userlocation.MapBoxFile

import com.google.gson.annotations.SerializedName

data class Waypoints (

    @SerializedName("distance" ) var distance : Double?           = null,
    @SerializedName("name"     ) var name     : String?           = null,
    @SerializedName("location" ) var location : ArrayList<Double> = arrayListOf()

)
