package com.othman.jaserestate.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.othman.jaserestate.models.EstateModel

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "EstateDatabase" // Database name
        private const val TABLE_HAPPY_PLACE = "EstateTable" // Table Name
        private const val ENCODING_SETTING = "PRAGMA encoding ='windows-1256'"

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_LOGGING_DATE = "loggingDate"
        private const val KEY_OFFER_OR_DEMAND = "offerOrDemand"
        private const val KEY_TYPE = "type"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_AREA = "area"
        private const val KEY_ROOM_NO = "roomNo"
        private const val KEY_ROOM_NO_HIGH = "roomNoHigh"
        private const val KEY_DIRECTIONS = "directions"
        private const val KEY_HEIGHT = "height"
        private const val KEY_HEIGHT_HIGH = "heightHigh"
        private const val KEY_FRONT_OR_BACK = "frontOrBack"
        private const val KEY_FLOOR_HOUSES_NO = "floorHousesNo"
        private const val KEY_SITUATION = "situation"
        private const val KEY_FURNITURE = "furniture"
        private const val KEY_FURNITURE_SITUATIONS = "furnitureSituation"
        private const val KEY_PARTIAL_FURNITURE= "partialFurniture"
        private const val KEY_PRICE = "price"
        private const val KEY_PRICE_TYPE = "priceType"
        private const val KEY_LEGAL = "legal"
        private const val KEY_OWNER = "owner"
        private const val KEY_OWNER_STANDERS = "ownerStandards"
        private const val KEY_LOGGER_NAME = "LoggerName"
        private const val KEY_LOGGER_TYPE = "LoggerType"
        private const val KEY_OWNER_TEL = "OwnerTel"
        private const val KEY_LOGGER_TEL = "LoggerTel"
        private const val KEY_PRIORITY = "priority"
        private const val KEY_RENT_DURATION = "rentDuration"
        private const val KEY_POSITIVES = "positives"
        private const val KEY_NEGATIVES = "negatives"
        private const val KEY_IS_DONE = "isDone"
        private const val KEY_DONE_DATE = "doneDate"
        private const val KEY_RENT_FINISH_DATE = "rentFinishDate"
        private const val KEY_BUYER_NAME = "buyerName"
        private const val KEY_BUYER_TEL = "buyerTel"
        private const val KEY_IMAGES_NO = "imagesNo"
        private const val KEY_IMAGE_1 = "image_1"
        private const val KEY_IMAGE_2 = "image_2"
        private const val KEY_IMAGE_3 = "image_3"
        private const val KEY_IMAGE_4 = "image_4"
        private const val KEY_IMAGE_5 = "image_5"
        private const val KEY_IMAGE_6 = "image_6"
        private const val KEY_IMAGE_7 = "image_7"
        private const val KEY_IMAGE_8 = "image_8"
        private const val KEY_IMAGE_9 = "image_9"
        private const val KEY_IMAGE_10 = "image_10"

        private val imageListKey = arrayListOf(KEY_IMAGE_1, KEY_IMAGE_2, KEY_IMAGE_3, KEY_IMAGE_4,
        KEY_IMAGE_5, KEY_IMAGE_6, KEY_IMAGE_7, KEY_IMAGE_8, KEY_IMAGE_9, KEY_IMAGE_10)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_HAPPY_PLACE_TABLE = "CREATE TABLE IF NOT EXISTS  ${TABLE_HAPPY_PLACE} (" +
                "${KEY_ID}  INTEGER PRIMARY KEY,"+
                "${KEY_LOGGING_DATE}  TEXT,"+
                "${KEY_OFFER_OR_DEMAND}  TEXT,"+
                "${KEY_TYPE}  TEXT,"+
                "${KEY_LOCATION}  TEXT,"+
                "${KEY_LATITUDE}  TEXT,"+
                "${KEY_LONGITUDE}  TEXT,"+
                "${KEY_AREA}  INTEGER,"+
                "${KEY_ROOM_NO}  TEXT,"+
                "${KEY_ROOM_NO_HIGH}  TEXT,"+
                "${KEY_DIRECTIONS}  TEXT,"+
                "${KEY_HEIGHT}  TEXT,"+
                "${KEY_HEIGHT_HIGH}  TEXT,"+
                "${KEY_FRONT_OR_BACK}  TEXT,"+
                "${KEY_FLOOR_HOUSES_NO}  TEXT,"+
                "${KEY_SITUATION}  TEXT,"+
                "${KEY_FURNITURE}  TEXT,"+
                "${KEY_PARTIAL_FURNITURE}  TEXT,"+
                "${KEY_FURNITURE_SITUATIONS}  TEXT,"+
                "${KEY_PRICE}  TEXT,"+
                "${KEY_PRICE_TYPE}  TEXT,"+
                "${KEY_LEGAL}  TEXT,"+
                "${KEY_OWNER}  TEXT,"+
                "${KEY_OWNER_STANDERS}  TEXT,"+
                "${KEY_LOGGER_NAME}  TEXT,"+
                "${KEY_LOGGER_TYPE}  TEXT,"+
                "${KEY_OWNER_TEL}  TEXT,"+
                "${KEY_LOGGER_TEL}  TEXT,"+
                "${KEY_PRIORITY}  TEXT,"+
                "${KEY_RENT_DURATION}  TEXT,"+
                "${KEY_POSITIVES}  TEXT,"+
                "${KEY_NEGATIVES}  TEXT,"+
                "${KEY_IS_DONE}  TEXT,"+
                "${KEY_DONE_DATE}  TEXT,"+
                "${KEY_RENT_FINISH_DATE}  TEXT,"+
                "${KEY_BUYER_NAME}  TEXT,"+
                "${KEY_BUYER_TEL}  TEXT,"+
                "${KEY_IMAGES_NO}  TEXT,"+
                "${KEY_IMAGE_1}  TEXT,"+
                "${KEY_IMAGE_2}  TEXT,"+
                "${KEY_IMAGE_3}  TEXT,"+
                "${KEY_IMAGE_4}  TEXT,"+
                "${KEY_IMAGE_5}  TEXT,"+
                "${KEY_IMAGE_6}  TEXT,"+
                "${KEY_IMAGE_7}  TEXT,"+
                "${KEY_IMAGE_8}  TEXT,"+
                "${KEY_IMAGE_9}  TEXT,"+
                "${KEY_IMAGE_10}  TEXT)"

                db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        //db?.execSQL("ALTER TABLE $TABLE_HAPPY_PLACE  ADD COLUMN test TEXT ");
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL(ENCODING_SETTING)
    }

    /**
     * Function to insert a Happy Place details to SQLite Database.
     */
    fun addPlace(place: EstateModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_LOGGING_DATE, place.loggingDate)
        contentValues.put(KEY_OFFER_OR_DEMAND, place.offerOrDemand)
        contentValues.put(KEY_TYPE, place.type)
        contentValues.put(KEY_LOCATION, place.location)
        contentValues.put(KEY_LATITUDE, place.latitude)
        contentValues.put(KEY_LONGITUDE, place.longitude)
        contentValues.put(KEY_AREA, place.area)
        contentValues.put(KEY_ROOM_NO, place.roomNo)
        contentValues.put(KEY_ROOM_NO_HIGH, place.roomNoHigh)
        contentValues.put(KEY_DIRECTIONS, place.directions)
        contentValues.put(KEY_HEIGHT, place.height)
        contentValues.put(KEY_HEIGHT_HIGH, place.heightHigh)
        contentValues.put(KEY_FRONT_OR_BACK, place.frontOrBack)
        contentValues.put(KEY_FLOOR_HOUSES_NO, place.floorHousesNo)
        contentValues.put(KEY_SITUATION, place.situation)
        contentValues.put(KEY_FURNITURE, place.furniture)
        contentValues.put(KEY_PARTIAL_FURNITURE, place.partialFurniture)
        contentValues.put(KEY_FURNITURE_SITUATIONS, place.furnitureSituation)
        contentValues.put(KEY_PRICE, place.price)
        contentValues.put(KEY_PRICE_TYPE, place.priceType)
        contentValues.put(KEY_LEGAL, place.legal)
        contentValues.put(KEY_OWNER, place.owner)
        contentValues.put(KEY_OWNER_STANDERS, place.ownerStandards)
        contentValues.put(KEY_LOGGER_NAME, place.loggerName)
        contentValues.put(KEY_LOGGER_TYPE, place.loggerType)
        contentValues.put(KEY_OWNER_TEL, place.ownerTel)
        contentValues.put(KEY_LOGGER_TEL, place.loggerTel)
        contentValues.put(KEY_PRIORITY, place.priority)
        contentValues.put(KEY_RENT_DURATION, place.rentDuration)
        contentValues.put(KEY_POSITIVES, place.positives)
        contentValues.put(KEY_NEGATIVES, place.negatives)
        contentValues.put(KEY_IS_DONE, place.isDone)
        contentValues.put(KEY_DONE_DATE, place.doneDate)
        contentValues.put(KEY_RENT_FINISH_DATE, place.rentFinishDate)
        contentValues.put(KEY_BUYER_NAME, place.buyerName)
        contentValues.put(KEY_BUYER_TEL, place.buyerTel)
        contentValues.put(KEY_IMAGES_NO, place.images!!.size)
        for (image  in 0 until place.images.size){
            contentValues.put(imageListKey[image], place.images[image].toString())
        }







        // Inserting Row
        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)
        //2nd argument is String containing nullColumnHack
        //db.close() // Closing database connection
        return result
    }

    /**
     * Function to read all the list of Happy Places data which are inserted.
     */
    @SuppressLint("Range")
    fun getPlacesList(mQuery: String): ArrayList<EstateModel> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val placeList: ArrayList<EstateModel> = ArrayList()

        //var selectQuery = "select  * from $TABLE_HAPPY_PLACE" + " where $KEY_AREA * 10 between (10 * 10) and (580 * 10) " + "and $KEY_PRICE * 10 between (10 * 10) and (580 * 10)"
        val selectQuery = "select  * from $TABLE_HAPPY_PLACE $mQuery"
        val db = this.readableDatabase
        try {
            val cursor: Cursor = db.rawQuery(selectQuery,null)
            if (cursor.moveToFirst()) {
                do {
                    val imagesNo = cursor.getInt(cursor.getColumnIndex(KEY_IMAGES_NO))
                    val imagesList = ArrayList<Uri>()
                    for (image in 0 until imagesNo){
                        imagesList.add( Uri.parse(cursor.getString(cursor.getColumnIndex(imageListKey[image]))))
                    }

                    val place = EstateModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOGGING_DATE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_OFFER_OR_DEMAND)),
                        cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_AREA)),
                        cursor.getString(cursor.getColumnIndex(KEY_ROOM_NO)),
                        cursor.getString(cursor.getColumnIndex(KEY_ROOM_NO_HIGH)),
                        cursor.getString(cursor.getColumnIndex(KEY_DIRECTIONS)),
                        cursor.getString(cursor.getColumnIndex(KEY_HEIGHT)),
                        cursor.getString(cursor.getColumnIndex(KEY_HEIGHT_HIGH)),
                        cursor.getString(cursor.getColumnIndex(KEY_FRONT_OR_BACK)),
                        cursor.getInt(cursor.getColumnIndex(KEY_FLOOR_HOUSES_NO)),
                        cursor.getString(cursor.getColumnIndex(KEY_SITUATION)),
                        cursor.getString(cursor.getColumnIndex(KEY_FURNITURE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PARTIAL_FURNITURE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FURNITURE_SITUATIONS)),
                        cursor.getFloat(cursor.getColumnIndex(KEY_PRICE)),
                        cursor.getString(cursor.getColumnIndex(KEY_PRICE_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LEGAL)),
                        cursor.getString(cursor.getColumnIndex(KEY_OWNER)),
                        cursor.getString(cursor.getColumnIndex(KEY_OWNER_STANDERS)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOGGER_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOGGER_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_OWNER_TEL)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOGGER_TEL)),
                        cursor.getString(cursor.getColumnIndex(KEY_PRIORITY)),
                        cursor.getString(cursor.getColumnIndex(KEY_RENT_DURATION)),
                        cursor.getString(cursor.getColumnIndex(KEY_POSITIVES)),
                        cursor.getString(cursor.getColumnIndex(KEY_NEGATIVES)),
                        cursor.getInt(cursor.getColumnIndex(KEY_IS_DONE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DONE_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_RENT_FINISH_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_BUYER_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_BUYER_TEL)),
                        imagesList)

                    placeList.add(place)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return placeList
    }



    /**
     * Function to update record
     */
    fun updateEstate(estate: EstateModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_LOGGING_DATE, estate.loggingDate)
        contentValues.put(KEY_OFFER_OR_DEMAND, estate.offerOrDemand)
        contentValues.put(KEY_TYPE, estate.type)
        contentValues.put(KEY_LOCATION, estate.location)
        contentValues.put(KEY_LATITUDE, estate.latitude)
        contentValues.put(KEY_LONGITUDE, estate.longitude)
        contentValues.put(KEY_AREA, estate.area)
        contentValues.put(KEY_ROOM_NO, estate.roomNo)
        contentValues.put(KEY_ROOM_NO_HIGH, estate.roomNoHigh)
        contentValues.put(KEY_DIRECTIONS, estate.directions)
        contentValues.put(KEY_HEIGHT, estate.height)
        contentValues.put(KEY_HEIGHT_HIGH, estate.heightHigh)
        contentValues.put(KEY_FRONT_OR_BACK, estate.frontOrBack)
        contentValues.put(KEY_FLOOR_HOUSES_NO, estate.floorHousesNo)
        contentValues.put(KEY_SITUATION, estate.situation)
        contentValues.put(KEY_FURNITURE, estate.furniture)
        contentValues.put(KEY_PARTIAL_FURNITURE, estate.partialFurniture)
        contentValues.put(KEY_FURNITURE_SITUATIONS, estate.furnitureSituation)
        contentValues.put(KEY_PRICE, estate.price)
        contentValues.put(KEY_PRICE_TYPE, estate.priceType)
        contentValues.put(KEY_LEGAL, estate.legal)
        contentValues.put(KEY_OWNER, estate.owner)
        contentValues.put(KEY_OWNER_STANDERS, estate.ownerStandards)
        contentValues.put(KEY_LOGGER_NAME, estate.loggerName)
        contentValues.put(KEY_LOGGER_TYPE, estate.loggerType)
        contentValues.put(KEY_OWNER_TEL, estate.ownerTel)
        contentValues.put(KEY_LOGGER_TEL, estate.loggerTel)
        contentValues.put(KEY_PRIORITY, estate.priority)
        contentValues.put(KEY_RENT_DURATION, estate.rentDuration)
        contentValues.put(KEY_POSITIVES, estate.positives)
        contentValues.put(KEY_NEGATIVES, estate.negatives)
        contentValues.put(KEY_IS_DONE, estate.isDone)
        contentValues.put(KEY_DONE_DATE, estate.doneDate)
        contentValues.put(KEY_RENT_FINISH_DATE, estate.rentFinishDate)
        contentValues.put(KEY_BUYER_NAME, estate.buyerName)
        contentValues.put(KEY_BUYER_TEL, estate.buyerTel)
        contentValues.put(KEY_IMAGES_NO, estate.images!!.size)
        for (image  in 0 until estate.images.size){
            contentValues.put(imageListKey[image], estate.images[image].toString())
        }



        // Updating Row
        val success =
            db.update(TABLE_HAPPY_PLACE, contentValues, KEY_ID + "=" + estate.id, null)
        //2nd argument is String containing nullColumnHack

        //db.close() // Closing database connection
        return success
    }

    /**
     * Function to delete happy place details.
     */
    fun deleteEstate(happyPlace: EstateModel): Int {
        val db = this.writableDatabase
        // Deleting Row
        val success = db.delete(TABLE_HAPPY_PLACE, KEY_ID + "=" + happyPlace.id, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}