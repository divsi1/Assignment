package com.example.assignment.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.R
import com.example.assignment.model.Facilities
import com.example.assignment.model.Options

var selectedOptions: ArrayList<String?>? = arrayListOf()

class CustomAdapter(private val mContext: Context, private var mList: List<Facilities>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    var exclusionList: ArrayList<HashMap<String?,String?>>? = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_parent_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = mList[position]
        holder.title.text = items.name
        holder.optionsRv.layoutManager = LinearLayoutManager(mContext)
        holder.optionsRv.adapter = FacilityAdapter(mContext, items.options, exclusionList)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setFacilities(list: List<Facilities>) {
        mList = list
        notifyDataSetChanged()
    }

    fun setExclusionListItems(list: ArrayList<HashMap<String?,String?>>? ) {
        exclusionList = list
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val title: TextView = itemView.findViewById(R.id.facilityName)
        val optionsRv: RecyclerView = itemView.findViewById(R.id.rv_facilities)
    }

}

class FacilityAdapter(private val mContext: Context, private val mList: List<Options>, var exclusionList: ArrayList<HashMap<String?,String?>>? ) :
    RecyclerView.Adapter<FacilityAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val items = mList[position]
        val resourceId = items.icon?.let { getImage(it) }
        Glide.with(mContext)
            .load(resourceId?.let { AppCompatResources.getDrawable(mContext, resourceId) })
            .into(holder.imageView)
        holder.title.text = items.name
        if (items.isSelected == true) {
            holder.parent.setBackgroundColor(mContext.getColor(R.color.purple_200))
        } else {
            holder.parent.setBackgroundColor(mContext.getColor(R.color.white))
        }
        holder.parent.setOnClickListener {
            if (selectedOptions?.contains(items.id) == true) {
                items.isSelected = false
                selectedOptions?.remove(items.id)
                Log.d("divya", selectedOptions.toString())
                Toast.makeText(mContext,"Option deselcted successfully",Toast. LENGTH_LONG).show()
                holder.parent.setBackgroundColor(mContext.getColor(R.color.white))
            } else {
                var isAvailable = true
                Log.d("divya", selectedOptions.toString())
                Log.d("divya", exclusionList.toString())

                for(mapData in exclusionList!!) {
                    if(!mapData.containsKey(items.id) && !mapData.containsValue(items.id)){
                        isAvailable = true
                    } else if(mapData.containsKey(items.id)) {
                        if(selectedOptions!!.contains(mapData.get(items.id))) {
                            isAvailable = false
                            break
                        } else {
                            isAvailable = true
                        }
                    } else if(mapData.containsValue(items.id)) {
                        if(selectedOptions!!.contains(getKey(mapData,items.id))) {
                            isAvailable = false
                            break
                        } else{
                            isAvailable = true
                        }
                    }

                }

                if (isAvailable) {
                    items.isSelected = true
                    selectedOptions?.add(items.id)
                    Toast.makeText(mContext,"Option selected",Toast. LENGTH_LONG).show()
                    holder.parent.setBackgroundColor(mContext.getColor(R.color.purple_200))

                } else {
                    Toast.makeText(mContext,"Option not available",Toast. LENGTH_LONG).show()
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun <K, V> getKey(map: Map<K, V>, target: V): K? {
        for ((key, value) in map)
        {
            if (target == value) {
                return key
            }
        }
        return null
    }

    class ItemViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val title: TextView = itemView.findViewById(R.id.title)
        val parent: ConstraintLayout = itemView.findViewById(R.id.ctl_view)
    }

    fun getImage(icon: String): Int {
        return when (icon) {
            "apartment" -> {
                R.drawable.apartment_2x
            }

            "condo" -> {
                R.drawable.condo_2x
            }

            "boat" -> {
                R.drawable.boat_2x
            }

            "land" -> {
                R.drawable.land_2x
            }

            "rooms" -> {
                R.drawable.rooms_2x
            }

            "no-room" -> {
                R.drawable.no_room_2x
            }

            "swimming" -> {
                R.drawable.swimming_2x
            }

            "garden" -> {
                R.drawable.garden_2x
            }

            "garage" -> {
                R.drawable.garage_2x
            }

            else -> {
                0
            }
        }

    }
}