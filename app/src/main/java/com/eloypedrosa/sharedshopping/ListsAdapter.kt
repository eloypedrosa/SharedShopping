package com.eloypedrosa.sharedshopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 1. Añadimos el parámetro onLongClick al constructor
class ListsAdapter(
    private val onClick: (ShoppingList) -> Unit,
    private val onLongClick: (ShoppingList) -> Unit
) : RecyclerView.Adapter<ListsAdapter.VH>() {

    private val items = mutableListOf<ShoppingList>()

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tv = v.findViewById<TextView>(R.id.tvListName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val l = items[position]
        holder.tv.text = l.name

        // Click normal para entrar
        holder.itemView.setOnClickListener { onClick(l) }

        // 2. Long Click para borrar
        holder.itemView.setOnLongClickListener {
            onLongClick(l)
            true // Retornamos true para indicar que consumimos el evento
        }
    }

    override fun getItemCount() = items.size
    fun submitList(ls: List<ShoppingList>) { items.clear(); items.addAll(ls); notifyDataSetChanged() }
}