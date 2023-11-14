package com.test.userlocation

import java.util.Date

data class Observation(
    val userId: String = "", // Default value
    val hotspotId: String = "", // Default value
    val birdName: String = "", // Default value
    val observedAt: Date = Date() // Default current date
)


