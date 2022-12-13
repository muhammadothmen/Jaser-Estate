package com.othman.jaserestate.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.othman.jaserestate.FilterFragment
import com.othman.jaserestate.R
import com.othman.jaserestate.adapters.placeAdapter
import com.othman.jaserestate.database.DatabaseHandler
import com.othman.jaserestate.databinding.ActivityMainBinding
import com.othman.jaserestate.models.HappyPlaceModel
import com.othman.jaserestate.utils.SwipeToDeleteCallback
import com.othman.jaserestate.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var offerOrDemand = OFFER
    private var isDone = NOT_DONE
    private lateinit var placesAdapter: placeAdapter
    private var  getHappyPlacesList : ArrayList<HappyPlaceModel>? = null
    private var defaultWhereClauseQuery = "where offerOrDemand = $offerOrDemand and isDone = $isDone"
    internal var whereClauseQuery = defaultWhereClauseQuery


    private val openAddHappyPlaceActivity: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK ) {
                getHappyPlacesListFromLocalDB()
            }else {
                Log.e("Activity","canceled or back pressed")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //setSupportActionBar(tbMain)

        fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEstateActivity::class.java)
            intent.putExtra(OFFER_OR_DEMAND,offerOrDemand)
            openAddHappyPlaceActivity.launch(intent)
        }

        //set the search view
        tbMain.placesSearch.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon
        ).setColorFilter(Color.WHITE)
        tbMain.placesSearch.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn
        ).setColorFilter(Color.WHITE)
        tbMain.placesSearch.findViewById<TextView>(androidx.appcompat.R.id.search_src_text
        ).setTextColor(Color.WHITE)

        //set the radio button
        rgOfferOrDemand.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.rbOffer){
                offerOrDemand = OFFER
                isDone = NOT_DONE
                resetWhereClauseQuery()
                fabAddHappyPlace.visibility = View.VISIBLE
                getHappyPlacesListFromLocalDB()
            }
            if (checkedId == R.id.rbDemand){
                offerOrDemand = DEMAND
                isDone = NOT_DONE
                resetWhereClauseQuery()
                fabAddHappyPlace.visibility = View.VISIBLE
                getHappyPlacesListFromLocalDB()
            }
            if (checkedId == R.id.rbHistory){
                offerOrDemand = HISTORY
                isDone = DONE
                resetWhereClauseQuery()
                fabAddHappyPlace.visibility = View.GONE
                getHappyPlacesListFromLocalDB()
            }

        }

        tv_filter.setOnClickListener {
            fl_show_data.visibility = View.GONE
            supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(androidx.fragment.R.animator.fragment_open_enter,
                    androidx.fragment.R.animator.fragment_close_enter)
                replace(R.id.fl_filter_fragment, FilterFragment())
                addToBackStack(null)
                commit()
            }
        }
        tv_all_data.setOnClickListener {
            whereClauseQuery = defaultWhereClauseQuery
            getHappyPlacesListFromLocalDB()
        }
        getHappyPlacesListFromLocalDB()
    }




    internal fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        getHappyPlacesList  = dbHandler.getHappyPlacesList(whereClauseQuery)

        if (getHappyPlacesList!!.size > 0) {
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList!!)
        } else {
            rvHappyPlacesList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {


        rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)

        placesAdapter = placeAdapter(this, happyPlacesList)
        rvHappyPlacesList.adapter = placesAdapter

        tbMain.placesSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                placesAdapter.filter.filter(query)
                if (placesAdapter.itemCount == 0){
                    tvNoRecordsAvailable.visibility = View.VISIBLE
                }else{
                    tvNoRecordsAvailable.visibility = View.GONE
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                placesAdapter.filter.filter(newText)
                Handler(Looper.myLooper()!!).postDelayed({
                    if (placesAdapter.itemCount == 0){
                        tvNoRecordsAvailable.visibility = View.VISIBLE
                    }else{
                        tvNoRecordsAvailable.visibility = View.GONE
                    }
                }, 20)

                return false
            }
        })
        tbMain.placesSearch.setOnQueryTextFocusChangeListener { _,IsFocused ->
            if (IsFocused){
                fabAddHappyPlace.visibility = View.GONE
                fl_show_data.visibility = View.VISIBLE
               // cl_filter.visibility = View.GONE
            } else{
                fabAddHappyPlace.visibility = View.VISIBLE
            }
        }

        placesAdapter.setOnClickListener(object : placeAdapter.OnClickListener{
            override fun onClick(position: Int,model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity,EstateDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })

        val editSwipeToEdit = object: SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as placeAdapter
                if (offerOrDemand == HISTORY){
                    adapter.changeDoneSituation(viewHolder.adapterPosition, NOT_DONE)
                    getHappyPlacesListFromLocalDB()
                }else {
                    adapter.notifyEditItem(openAddHappyPlaceActivity, viewHolder.adapterPosition)
                }
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeToEdit)
        editItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)

        val deleteSwipeToDelete = object: SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as placeAdapter
                if (offerOrDemand == HISTORY){
                    val imageListToDelete = getHappyPlacesList?.get(viewHolder.adapterPosition)?.images!!
                    for (image in imageListToDelete){
                        deleteFile(image)
                    }
                    adapter.removeAt(viewHolder.adapterPosition)
                }else {
                    adapter.changeDoneSituation(viewHolder.adapterPosition, DONE)
                    getHappyPlacesListFromLocalDB()
                }
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeToDelete)
        deleteItemTouchHelper.attachToRecyclerView(rvHappyPlacesList)
    }

    fun deleteFile(uri: Uri){
        val deleted = contentResolver.delete(uri, null, null)
        Log.e("Jasser",deleted.toString())
    }

     internal fun resetWhereClauseQuery(){
        if (offerOrDemand != HISTORY) {
            defaultWhereClauseQuery = "where offerOrDemand = $offerOrDemand and isDone = $isDone"
        }else{
            defaultWhereClauseQuery = "where isDone = $isDone"
        }
        whereClauseQuery = defaultWhereClauseQuery
    }





    //object for constants
    companion object {
        internal const val EXTRA_PLACE_DETAILS = "extra_place_details"
        internal const val OFFER_OR_DEMAND = "offerOrDemand"
        internal const val OFFER = 1
        internal const val DEMAND = 2
        internal const val HISTORY = 0
        internal const val DONE = 2
        internal const val NOT_DONE = 1
    }
}