package com.test.userlocation.MapBoxFile

import com.google.gson.annotations.SerializedName

data class Routes (

    @SerializedName("weight_name" ) var weightName : String?         = null,
    @SerializedName("weight"      ) var weight     : Double?         = null,
    @SerializedName("duration"    ) var duration   : Double?         = null,
    @SerializedName("distance"    ) var distance   : Double?         = null,
    @SerializedName("legs"        ) var legs       : ArrayList<Legs> = arrayListOf(),
    @SerializedName("geometry"    ) var geometry   : Geometry?       = Geometry()

)