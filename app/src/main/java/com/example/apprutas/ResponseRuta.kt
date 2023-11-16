package com.example.apprutas

import com.google.gson.annotations.SerializedName

data class ResponseRuta(@SerializedName("features") val features: List<Features>)

data class Features (@SerializedName("geometry") val geometry: Geometry)

data class Geometry (@SerializedName("coordinates") val coordinates: List<List<Double>>)