package com.test.userlocation.MapBoxFile

import com.google.gson.annotations.SerializedName

data class Geometry (

    @SerializedName("coordinates" ) var coordinates : ArrayList<ArrayList<Double>> = arrayListOf(),
    @SerializedName("type"        ) var type        : String?                      = null

)
