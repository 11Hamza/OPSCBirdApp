package com.test.userlocation.MapBoxFile

import com.google.gson.annotations.SerializedName

data class MainRoute (
    @SerializedName("routes"    ) var routes    : ArrayList<Routes>    = arrayListOf(),
    @SerializedName("waypoints" ) var waypoints : ArrayList<Waypoints> = arrayListOf(),
    @SerializedName("code"      ) var code      : String?              = null,
    @SerializedName("uuid"      ) var uuid      : String?              = null

    )