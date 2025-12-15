package com.eloypedrosa.sharedshopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private var products: MutableList<Product>,
    private val onToggleCompleted: (Product, Boolean)->Unit,
    private val onLongClick: (Product)->Unit
): RecyclerView.Adapter<ProductAdapter.VH>() {

    inner class VH(v: View): RecyclerView.ViewHolder(v) {
        val tvName = v.findViewById<TextView>(R.id.tvProductName)
        val tvQty = v.findViewById<TextView>(R.id.tvProductQty)
        val cbDone = v.findViewById<CheckBox>(R.id.cbDone)
        val ivIcon = v.findViewById<ImageView>(R.id.ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = products[position]
        holder.tvName.text = p.name
        holder.tvQty.text = p.quantity ?: ""

        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = p.completed
        holder.cbDone.setOnCheckedChangeListener { _, isChecked -> onToggleCompleted(p, isChecked) }

        holder.ivIcon.setImageResource(iconResId(holder.itemView.context, p.icon))

        // Aquí usamos el onLongClick
        holder.itemView.setOnLongClickListener {
            onLongClick(p)
            true
        }
    }

    override fun getItemCount() = products.size

    fun updateList(newList: List<Product>) {
        products.clear()
        products.addAll(newList.sortedBy { it.completed })
        notifyDataSetChanged()
    }

    private fun iconResId(ctx: android.content.Context, key: String?): Int {
        if (key == null) return android.R.drawable.ic_menu_help
        return when(key) {
            "milk" -> R.drawable.ic_milk
            "yogurt" -> R.drawable.ic_yogurt
            "bread" -> R.drawable.ic_bread
            "egg" -> R.drawable.ic_egg
            else -> android.R.drawable.ic_menu_help
        }
    }
}