package com.example.assignment.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment.model.FacilitiesAndExclusions
import com.example.assignment.model.ResponseEntity
import com.example.assignment.repository.MainActivityRepository


class MainActivityViewModel : ViewModel() {

    fun getFacilities(): MutableLiveData<FacilitiesAndExclusions?>? {
        return MainActivityRepository.getFacilities()
    }

    fun insertData(context: Context, entry: String, response: String) {
        MainActivityRepository.insertData(context, entry, response)
    }

    fun getResponseDetails(context: Context, entry: String): LiveData<ResponseEntity>? {
            return MainActivityRepository.getResponseDetails(context, entry)
    }

    fun removeData(context: Context, entry: String) {
        MainActivityRepository.deleteResponse(context, entry)
    }

}