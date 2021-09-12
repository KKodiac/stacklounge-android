package com.example.stacklounge.tool

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stacklounge.R
import kotlinx.android.synthetic.main.fragment_main_favorite.*
import kotlinx.android.synthetic.main.frame_tool.view.*

class ToolListAdapter(
    private val toolList: MutableList<ToolData>
) : RecyclerView.Adapter<ToolListAdapter.ViewHolder>() {

    inner class ViewHolder(toolView: View) :
        RecyclerView.ViewHolder(toolView) {
//            val id: String
            val name: TextView? = toolView.findViewById(R.id.toolName)
//            val slug: String?
//            val title: String?
//            val description: String?
            val imageUrl: ImageView = toolView.findViewById(R.id.toolImage)
            var cardView: CardView = toolView.findViewById(R.id.toolCardView)
            var activity: Context = toolView.context
//            val ossRepo: String?
//            val canonicalUrl: String?
//            val websiteUrl: String?
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.frame_tool, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recyclerData: ToolData = toolList[position]
        holder.name?.text = recyclerData.name
        Glide.with(holder.itemView).load(recyclerData.imageUrl).into(holder.imageUrl)
        holder.cardView.setOnClickListener {
            Log.d("CLCKED", "clicked")
            var intent = Intent(holder.activity, ToolSubActivity::class.java)
            intent.putExtra("imageUrl", recyclerData.imageUrl)
            intent.putExtra("name", recyclerData.name)
            intent.putExtra("description", recyclerData.description)
            intent.putExtra("title", recyclerData.title)
            holder.activity.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return toolList.size
    }
}
