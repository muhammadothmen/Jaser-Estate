package com.othman.jaserestate.adapters

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.othman.jaserestate.R
import kotlinx.android.synthetic.main.item_images.view.*


class ImagesAdapter(private val context: Context, private val imageList: List<Uri>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_images,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder ){
            holder.itemView.iv_place_image.setImageURI(imageList[position])
        }
        holder.itemView.setOnClickListener {
            try {
                val openImageIntent = Intent(Intent.ACTION_VIEW)
                openImageIntent.flags = FLAG_GRANT_READ_URI_PERMISSION
                openImageIntent.setDataAndType(imageList[position],"image/*")
                startActivity(context,openImageIntent,null)
            }catch (e:Exception){
                Log.e("Jasser",e.toString())
            }

        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}