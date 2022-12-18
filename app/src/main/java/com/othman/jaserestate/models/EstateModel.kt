package com.othman.jaserestate.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class EstateModel(
    var id: Int,
    val loggingDate: String?,
    val offerOrDemand: Int,
    val type: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double,
    val area: Int,
    val roomNo: String?,
    val roomNoHigh: String?,
    val directions: String?,
    val height: String?,
    val heightHigh: String?,
    val frontOrBack: String?,
    val floorHousesNo: Int,
    val situation: String?,
    val furniture: String?,
    val partialFurniture : String?,
    val furnitureSituation: String?,
    val price: Float,
    val priceType: String?,
    val legal: String?,
    val owner: String?,
    val ownerStandards: String?,
    val loggerName: String?,
    val loggerType: String?,
    val ownerTel: String?,
    val loggerTel: String?,
    val priority: String?,
    val rentDuration: String?,
    val positives: String?,
    val negatives: String?,
    var isDone: Int,
    val doneDate: String?,
    val rentFinishDate: String?,
    val buyerName: String?,
    val buyerTel: String?,
    val images: ArrayList<Uri>?
    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(Uri.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(loggingDate)
        parcel.writeInt(offerOrDemand)
        parcel.writeString(type)
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeInt(area)
        parcel.writeString(roomNo)
        parcel.writeString(roomNoHigh)
        parcel.writeString(directions)
        parcel.writeString(height)
        parcel.writeString(heightHigh)
        parcel.writeString(frontOrBack)
        parcel.writeInt(floorHousesNo)
        parcel.writeString(situation)
        parcel.writeString(furniture)
        parcel.writeString(partialFurniture)
        parcel.writeString(furnitureSituation)
        parcel.writeFloat(price)
        parcel.writeString(priceType)
        parcel.writeString(legal)
        parcel.writeString(owner)
        parcel.writeString(ownerStandards)
        parcel.writeString(loggerName)
        parcel.writeString(loggerType)
        parcel.writeString(ownerTel)
        parcel.writeString(loggerTel)
        parcel.writeString(priority)
        parcel.writeString(rentDuration)
        parcel.writeString(positives)
        parcel.writeString(negatives)
        parcel.writeInt(isDone)
        parcel.writeString(doneDate)
        parcel.writeString(rentFinishDate)
        parcel.writeString(buyerName)
        parcel.writeString(buyerTel)
        parcel.writeTypedList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EstateModel> {
        override fun createFromParcel(parcel: Parcel): EstateModel {
            return EstateModel(parcel)
        }

        override fun newArray(size: Int): Array<EstateModel?> {
            return arrayOfNulls(size)
        }
    }
}