package com.othman.jaserestate.activities



import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.othman.jaserestate.R
import com.othman.jaserestate.adapters.HappyPlaceAdapter
import com.othman.jaserestate.database.DatabaseHandler
import com.othman.jaserestate.databinding.ActivityMainBinding
import com.othman.jaserestate.models.HappyPlaceModel
import com.othman.jaserestate.utils.SwipeToDeleteCallback
import com.othman.jaserestate.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var placesAdapter: HappyPlaceAdapter
    private var offerOrDemand = OFFER
    private var isDone = NOT_DONE

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

        setSupportActionBar(tbMain)


        fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEstateActivity::class.java)
            //startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            intent.putExtra(OFFER_OR_DEMAND,offerOrDemand)
            openAddHappyPlaceActivity.launch(intent)
        }




        placesSearch.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon
        ).setColorFilter(Color.WHITE)
        placesSearch.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn
        ).setColorFilter(Color.WHITE)
        placesSearch.findViewById<TextView>(androidx.appcompat.R.id.search_src_text
        ).setTextColor(Color.WHITE)


        rgOfferOrDemand.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.rbOffer){
                offerOrDemand = OFFER
                isDone = NOT_DONE
                fabAddHappyPlace.visibility = View.VISIBLE
                getHappyPlacesListFromLocalDB()
            }
            if (checkedId == R.id.rbDemand){
                offerOrDemand = DEMAND
                isDone = NOT_DONE
                fabAddHappyPlace.visibility = View.VISIBLE
                getHappyPlacesListFromLocalDB()
            }
            if (checkedId == R.id.rbHistory){
                offerOrDemand = HISTORY
                isDone = DONE
                fabAddHappyPlace.visibility = View.GONE
                getHappyPlacesListFromLocalDB()
            }

        }


        getHappyPlacesListFromLocalDB()

    }




    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlacesList : ArrayList<HappyPlaceModel> =
            dbHandler.getHappyPlacesList(offerOrDemand, isDone)


        if (getHappyPlacesList.size > 0) {
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        } else {
            rvHappyPlacesList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }
    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {


        rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)

        placesAdapter = HappyPlaceAdapter(this, happyPlacesList)
        rvHappyPlacesList.adapter = placesAdapter

        placesSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                placesAdapter.filter.filter(query)
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                placesAdapter.filter.filter(newText)
                return false
            }
        })
        placesSearch.setOnQueryTextFocusChangeListener { _,IsFocused ->
            if (IsFocused){
                fabAddHappyPlace.visibility = View.GONE
            } else{
                fabAddHappyPlace.visibility = View.VISIBLE
            }
        }

        placesAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{
            override fun onClick(position: Int,model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity,EstateDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })

        val editSwipeToEdit = object: SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as HappyPlaceAdapter
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
                val adapter = rvHappyPlacesList.adapter as HappyPlaceAdapter
                if (offerOrDemand == HISTORY){
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


    companion object {
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        internal const val EXTRA_PLACE_DETAILS = "extra_place_details"
        internal const val OFFER_OR_DEMAND = "offerOrDemand"
        internal const val OFFER = 1
        internal const val DEMAND = 2
        internal const val HISTORY = 0
        internal const val DONE = 2
        internal const val NOT_DONE = 1

    }
}