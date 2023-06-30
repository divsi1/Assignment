package com.example.assignment.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.assignment.model.FacilitiesAndExclusions
import com.example.assignment.model.ResponseEntity
import com.example.assignment.model.SingleLiveEvent
import com.example.assignment.model.StorageDatabase
import com.example.assignment.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MainActivityRepository {

    var facilitygetter: SingleLiveEvent<FacilitiesAndExclusions?>? = SingleLiveEvent()

    fun getFacilities(): MutableLiveData<FacilitiesAndExclusions?>? {
        RetrofitClient.apiInterface.getFacilities()
            .enqueue(object : Callback<FacilitiesAndExclusions> {
                override fun onFailure(call: Call<FacilitiesAndExclusions>, t: Throwable) {
                    Log.v("DEBUG : ", t.message.toString())
                }

                override fun onResponse(
                    call: Call<FacilitiesAndExclusions>,
                    response: Response<FacilitiesAndExclusions>
                ) {
                    facilitygetter?.value = response.body()
                }
            })
        return facilitygetter
    }


    var storageDatabase: StorageDatabase? = null
    var responseModel: SingleLiveEvent<ResponseEntity>? = SingleLiveEvent()

    fun initializeDB(context: Context): StorageDatabase {
        return StorageDatabase.getDatabaseClient(context)
    }

    fun insertData(context: Context, entry: String, response: String) {
        storageDatabase = initializeDB(context)
        CoroutineScope(IO).launch {
            val responseDetails = ResponseEntity(entry, response)
            storageDatabase!!.responseDao().addResponse(responseDetails)
        }
    }

    fun getResponseDetails(context: Context, entry: String): LiveData<ResponseEntity>? {
        storageDatabase = initializeDB(context)
        CoroutineScope(IO).launch {
            responseModel?.postValue(storageDatabase!!.responseDao().getLatestResponse(entry))
        }
        return responseModel
    }

    fun updateData(context: Context, entry: String, response: String) {
        storageDatabase = initializeDB(context)
        CoroutineScope(IO).launch {
            storageDatabase!!.responseDao().updateResponse(entry, response)
        }
    }


    fun deleteResponse(context: Context,entry: String) {
        storageDatabase = initializeDB(context)
        CoroutineScope(IO).launch {
            storageDatabase!!.responseDao().deleteAll(entry)
        }
    }


}