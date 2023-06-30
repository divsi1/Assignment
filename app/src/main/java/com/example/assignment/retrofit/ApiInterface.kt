package com.example.assignment.retrofit

import com.example.assignment.model.FacilitiesAndExclusions
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("ad-assignment/db/")
    fun getFacilities() : Call<FacilitiesAndExclusions>
}