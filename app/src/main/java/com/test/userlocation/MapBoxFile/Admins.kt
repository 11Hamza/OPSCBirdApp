package com.test.userlocation.MapBoxFile

import com.google.gson.annotations.SerializedName


data class Admins (

    @SerializedName("iso_3166_1_alpha3" ) var iso31661Alpha3 : String? = null,
    @SerializedName("iso_3166_1"        ) var iso31661       : String? = null

)
