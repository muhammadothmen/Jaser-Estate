package com.othman.jaserestate.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.othman.jaserestate.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_images.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagesAdapter(private val context: Context, private val imageList: List<Uri>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_images,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder ){
            holder.itemView.iv_place_image.setImageURI(imageList[position])
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}