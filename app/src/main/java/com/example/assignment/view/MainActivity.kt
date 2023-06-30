package com.example.assignment.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.model.FacilitiesAndExclusions
import com.example.assignment.viewmodel.MainActivityViewModel
import com.google.gson.Gson


class MainActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var mainActivityViewModel: MainActivityViewModel
    var adapter: CustomAdapter? = null
    var exclusionList: ArrayList<HashMap<String?, String?>>? = arrayListOf()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this@MainActivity
        adapter = CustomAdapter(mContext = context, arrayListOf())
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        refreshDB()
        setRecyclerView()
    }

    private fun refreshDB() {
        val currentTime = System.currentTimeMillis()

        val pref = applicationContext.getSharedPreferences("SharedPreferences", 0)
        val lastTimeStamp = pref.getLong("last_time_stamp", currentTime); // getting String

        if (lastTimeStamp == currentTime) {
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putLong("last_time_stamp", currentTime); // Storing string
            editor.apply()
        }

        if((currentTime - lastTimeStamp) >=  86400) {  //86400
            getData()
        } else {
            getDataFromDB()
        }

    }

    private fun getData() {
        mainActivityViewModel.getFacilities()?.observe(
            this,
            Observer { facilitiesAndExclusions ->
                if(facilitiesAndExclusions != null) {
                    setFacilitesDataToViews(facilitiesAndExclusions)
                    Log.d("divya", "insert data to db with response from network call")
                    mainActivityViewModel.removeData(context, "latest")
                    val facilitiesAndExclusionsJsonString = Gson().toJson(facilitiesAndExclusions)
                    mainActivityViewModel.insertData(
                        context,
                        "latest",
                        facilitiesAndExclusionsJsonString
                    )

                    val pref = applicationContext.getSharedPreferences("SharedPreferences", 0)
                    val editor: SharedPreferences.Editor = pref.edit()
                    editor.putLong("last_time_stamp", System.currentTimeMillis()); // Storing data
                    editor.apply(); // commit changes
                }
            })
    }

    private fun getDataFromDB() {

        mainActivityViewModel.getResponseDetails(context, entry = "latest")
            ?.observe(this, Observer {
                if ((it == null || it.response == "null")) {
                    getData()
                } else {
                    val facilitiesAndExclusions =
                        Gson().fromJson(it.response, FacilitiesAndExclusions::class.java)
                    facilitiesAndExclusions?.facilities?.let { adapter?.setFacilities(it) }
                    for (exclusion in facilitiesAndExclusions?.exclusions ?: arrayListOf()) {
                        val map: HashMap<String?, String?> = hashMapOf()
                        map.put((exclusion[0].optionsId), (exclusion[1].optionsId))
                        exclusionList?.add(map)
                    }
                    adapter?.setExclusionListItems(exclusionList)
                }
            })
    }

    private fun setRecyclerView() {
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        val textView = findViewById<TextView>(R.id.tv_instuction)
        textView.setOnClickListener {
            refreshDB()
        }
    }

    private fun setFacilitesDataToViews(facilitiesAndExclusions: FacilitiesAndExclusions?) {
        facilitiesAndExclusions?.facilities?.let { adapter?.setFacilities(it) }
        for (exclusion in facilitiesAndExclusions?.exclusions ?: arrayListOf()) {
            val map: HashMap<String?, String?> = hashMapOf()
            map.put((exclusion[0].optionsId), (exclusion[1].optionsId))
            exclusionList?.add(map)
        }
        adapter?.setExclusionListItems(exclusionList)
    }
}

