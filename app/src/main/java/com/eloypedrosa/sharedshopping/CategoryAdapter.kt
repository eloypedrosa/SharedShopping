package com.eloypedrosa.sharedshopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val onClick: (Category)->Unit) : RecyclerView.Adapter<CategoryAdapter.VH>() {
    private val items = mutableListOf<Category>()
    inner class VH(v: View): RecyclerView.ViewHolder(v) {
        val tv = v.findViewById<TextView>(R.id.tvCategoryName)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]; holder.tv.text = c.name; holder.itemView.setOnClickListener { onClick(c) }
    }
    override fun getItemCount() = items.size
    fun submitList(ls: List<Category>) { items.clear(); items.addAll(ls); notifyDataSetChanged() }
    fun currentList() = items.toList()
}
