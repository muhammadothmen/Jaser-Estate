package com.othman.jaserestate.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.othman.jaserestate.R
import com.othman.jaserestate.adapters.PlaceAdapter
import com.othman.jaserestate.database.DatabaseHandler
import com.othman.jaserestate.databinding.ActivityMainBinding
import com.othman.jaserestate.models.EstateModel
import com.othman.jaserestate.utils.FilterFragment
import com.othman.jaserestate.utils.SwipeToDeleteCallback
import com.othman.jaserestate.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.launch
import java.io.File
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var offerOrDemand = OFFER
    private var isDone = NOT_DONE
    private lateinit var placesAdapter: PlaceAdapter
    private var  getEstateList : ArrayList<EstateModel>? = null
    private var defaultWhereClauseQuery = "where offerOrDemand = $offerOrDemand and isDone = $isDone"
    internal var whereClauseQuery = defaultWhereClauseQuery
    private val readStoragePermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val writeStoragePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE


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

        if (isEditFilesPermissionGranted()){
            try {
                restoreDatabase()
                restoreImages()
            }catch (e: Exception){
                Log.e("Jasser", e.toString())
            }
        }else{
            requestStoragePermission()
        }

    }

    private fun isEditFilesPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        }else{
            (ContextCompat.checkSelfPermission(
                this,
                readStoragePermission
            ) != PackageManager.PERMISSION_GRANTED
                    )
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                /*ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION),
                    1
                )*/
                try {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    finish()
                }catch (e: Exception){
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    readStoragePermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(writeStoragePermission, readStoragePermission),
                    1
                )
                finish()
            }
        }
    }


    internal fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        getEstateList  = dbHandler.getPlacesList(whereClauseQuery)

        if (getEstateList!!.size > 0) {
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE
            setupEstatesRecyclerView(getEstateList!!)
        } else {
            rvHappyPlacesList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    private fun setupEstatesRecyclerView(happyPlacesList: ArrayList<EstateModel>) {


        rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)

        placesAdapter = PlaceAdapter(this, happyPlacesList)
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

        placesAdapter.setOnClickListener(object : PlaceAdapter.OnClickListener{
            override fun onClick(position: Int,model: EstateModel) {
                val intent = Intent(this@MainActivity,EstateDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })

        val editSwipeToEdit = object: SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlacesList.adapter as PlaceAdapter
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
                val adapter = rvHappyPlacesList.adapter as PlaceAdapter
                if (offerOrDemand == HISTORY){
                    val imageListToDelete = getEstateList?.get(viewHolder.adapterPosition)?.images!!
                    for (image in imageListToDelete){
                        try {
                            mDeleteFile(image)
                        }catch (e: Exception){
                            Log.e("Jasser", e.toString())
                        }
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

    fun mDeleteFile(uri: Uri){
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

    override fun onResume() {
        super.onResume()
        getHappyPlacesListFromLocalDB()
    }


    private fun backupDatabase(){
        val appDataPath = "." + applicationContext.applicationInfo.dataDir.toString().removePrefix("/") + File.separator + ".databases"
        Log.e("Jasser", appDataPath)
        val dbName = "EstateDatabase"
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val documentFolder = storageDirectory + File.separator + appDataPath
        val db = getDatabasePath(dbName).absolutePath
        val backupFile = File(documentFolder, ".$dbName.db")
        File(db).copyTo(backupFile, true)
    }

    private fun restoreDatabase(){
        val appDataPath = "." + applicationContext.applicationInfo.dataDir.toString().removePrefix("/") + File.separator + ".databases"
        val dbName = "EstateDatabase"
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val backupDatabaseFolder = storageDirectory + File.separator + appDataPath
        val db = getDatabasePath(dbName).absolutePath
        val dbExternal = backupDatabaseFolder + File.separator + "." + dbName +".db"
        if(File(dbExternal).exists()){
            File(dbExternal).copyTo(File(db), true)
        }else{
            Toast.makeText(this@MainActivity,"No backup file exists",Toast.LENGTH_SHORT).show()
        }
    }

    private fun backupImages(){
        val appDataPath = "." + applicationContext.applicationInfo.dataDir.toString().removePrefix("/") + File.separator + ".images"
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val backupImageFolder = storageDirectory + File.separator + appDataPath
        val appImageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val appImages = File(appImageFolder).listFiles()
        val backupImages = File(backupImageFolder).listFiles()
        backupImages?.let {
            for (backupImage in backupImages){
                val backupImageName = backupImage.name
                if(!File(appImageFolder, backupImageName).exists()){
                    File(backupImageFolder,backupImageName).delete().let {
                        Log.e("Jasser",it.toString())
                    }
                }
            }
        }
        appImages?.let {
            for(appImage in appImages) {
                val appImageName = appImage.name
                if (!File(backupImageFolder, appImageName).exists()){
                    File(appImage.absolutePath).copyTo(File(backupImageFolder, appImageName),true).let {
                        Log.e("Jasser", it.absolutePath)
                    }
                }
            }
        }

    }

    private fun restoreImages(){
        val appDataPath = "." + applicationContext.applicationInfo.dataDir.toString().removePrefix("/") + File.separator + ".images"
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()
        val backupImageFolder = storageDirectory + File.separator + appDataPath
        val appImageFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val appImages = File(appImageFolder).listFiles()
        val backupImages = File(backupImageFolder).listFiles()

        /*appImages?.let {
            for(appImage in appImages) {
                val appImageName = appImage.name
                if (!File(backupImageFolder, appImageName).exists()){
                    File(appImage.absolutePath).delete().let {
                        Log.e("Jasser",it.toString())
                    }
                }
            }
        }*/

        backupImages?.let {
            for (backupImage in backupImages){
                val backupImageName = backupImage.name
                if(!File(appImageFolder, backupImageName).exists()){
                    File(backupImage.absolutePath).copyTo(File(appImageFolder, backupImageName),true).let {
                        Log.e("Jasser", it.absolutePath)
                    }
                }
            }
        }



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

    override fun onPause() {
        lifecycleScope.launch{
            if (isEditFilesPermissionGranted()) {
                try {
                    backupDatabase()
                    backupImages()
                } catch (e: Exception) {
                    Log.e("Jasser", e.toString())
                }
            }
        }
        super.onPause()
    }





}