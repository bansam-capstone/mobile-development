package com.bangkit.capstone.domain.model

import com.google.android.gms.maps.model.LatLng

data class LocationInfo(
    val name: String,
    val latLng: LatLng,
    val identifier: String
)