package com.example.assignment.model

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.concurrent.atomic.AtomicBoolean

@Serializable
data class FacilitiesAndExclusions(
    @SerializedName("facilities") var facilities: ArrayList<Facilities> = arrayListOf(),
    @SerializedName("exclusions") var exclusions: ArrayList<ArrayList<Exclusions>> = arrayListOf()
)

@Serializable
data class Exclusions (
    @SerializedName("facility_id") var facilityId : String? = null,
    @SerializedName("options_id") var optionsId  : String? = null
)

@Serializable
data class Facilities(
    @SerializedName("facility_id") var facilityId: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("options") var options: ArrayList<Options> = arrayListOf()
)

@Serializable
data class Options (
    @SerializedName("name") var name : String? = null,
    @SerializedName("icon") var icon : String? = null,
    @SerializedName("id") var id   : String? = null,
    var isSelected   : Boolean? = false
)

@Entity(tableName = "facilitiesAndExclusions")
data class ResponseEntity(
    @ColumnInfo(name = "entry")
    var entry: String?,

    @ColumnInfo(name = "responseString")
    var response: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = 0
}

@Dao
interface ResponseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     suspend fun addResponse(response: ResponseEntity?)

    @Query("SELECT * FROM facilitiesAndExclusions where entry=:latest ")
     suspend fun getLatestResponse(latest: String): ResponseEntity

    @Query("UPDATE facilitiesAndExclusions SET responseString = :response WHERE entry = :latest")
     suspend fun updateResponse(response: String?, latest: String)

    @Query("DELETE from facilitiesAndExclusions where entry=:latest")
     suspend fun deleteAll(latest: String)
}


@Database(entities = [ResponseEntity::class], version = 1, exportSchema = false)
abstract class StorageDatabase : RoomDatabase() {
    abstract fun responseDao(): ResponseDao

    companion object {
        @Volatile
        private var INSTANCE: StorageDatabase? = null
        fun getDatabaseClient(context: Context): StorageDatabase {

            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                INSTANCE =
                    Room.databaseBuilder(context, StorageDatabase::class.java, "STORAGE_DATABASE")
                        .fallbackToDestructiveMigration()
                        .build()

                return INSTANCE!!
            }
        }
    }

}

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }
        // Observe the internal MutableLiveData
        super.observe(owner, object : Observer<T> {
            override fun onChanged(t: T?) {
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            }
        })
    }
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        setValue(null)
    }
    companion object {
        private val TAG = "SingleLiveEvent"
    }
}