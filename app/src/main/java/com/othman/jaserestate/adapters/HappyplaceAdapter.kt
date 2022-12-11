package com.othman.jaserestate.adapters

import android.content.Context
import android.content.Intent
import android.content.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.othman.jaserestate.R
import com.othman.jaserestate.activities.AddEstateActivity
import com.othman.jaserestate.activities.MainActivity
import com.othman.jaserestate.database.DatabaseHandler
import com.othman.jaserestate.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_add_estate.*
import kotlinx.android.synthetic.main.item_happy_place.view.*
import java.util.*

open class placeAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var onClickListener: OnClickListener? = null
    var placesFilterList = ArrayList<HappyPlaceModel>()
    init {
        placesFilterList = list
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)





    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_place,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = placesFilterList[position]

        if (holder is MyViewHolder) {
            //if (model.images!![0]!= "") {}
                holder.itemView.iv_place_image.setImageURI(model.images!![0])

            holder.itemView.tvTitle.text = model.location
            val roomNo = context.resources.getString(R.string.edit_text_hint_roomNo)
            holder.itemView.tvDescription.text = "$roomNo: ${ model.roomNo }"

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return placesFilterList.size
    }

    /**
     * A function to edit the added happy place detail and pass the existing details through intent.
     */
    fun notifyEditItem(activityLauncher: ActivityResultLauncher<Intent>, position: Int) {
        val intent = Intent(context, AddEstateActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, placesFilterList[position])
        //activity.startActivityForResult(intent, requestCod) // Activity is started with requestCode
        activityLauncher.launch(intent)
        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }

    /**
     * A function to delete the added happy place detail from the local storage.
     */
    fun removeAt(position: Int) {

        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deleteHappyPlace(placesFilterList[position])

        if (isDeleted > 0) {
            val model = placesFilterList[position]
            placesFilterList.remove(model)
            if (list.size != placesFilterList.size) {
                list.remove(model)
            }
            notifyItemRemoved(position)
        }
    }
    fun changeDoneSituation(position: Int ,situation: Int){
        val model = placesFilterList[position]
        model.isDone = situation
        val dbHandler = DatabaseHandler(context)
        val updateHappyPlace = dbHandler.updateHappyPlace(model)

    }

    /**
     * A function to bind the onclickListener.
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    placesFilterList = list
                } else {
                    val resultList = ArrayList<HappyPlaceModel>()
                    for (row in list){
                        if (row.location?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.height?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.directions?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.area.toString().lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))
                            || row.frontOrBack?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.floorHousesNo.toString().lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))
                            || row.furniture?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.furnitureSituation?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.situation?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.legal?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.positives?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.owner?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.loggerName?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.loggerType?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.priority?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.rentDuration?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                            || row.ownerStandards?.lowercase(Locale.ROOT)!!.contains(charSearch.lowercase(Locale.ROOT))
                        ){
                            resultList.add(row)
                        }
                    }
                    placesFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = placesFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                placesFilterList = results?.values as ArrayList<HappyPlaceModel>
                Log.e("mph","puplish")
                notifyDataSetChanged()
            }

        }
    }
}