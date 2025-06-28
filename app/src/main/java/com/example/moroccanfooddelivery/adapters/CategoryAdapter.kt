package com.example.moroccanfooddelivery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moroccanfooddelivery.Category
import com.example.moroccanfooddelivery.R

class CategoryAdapter(
    private val context: Context,
    private val categories: List<Category>,
    private val listener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface OnCategoryClickListener {
        fun onCategoryClick(categoryType: String)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCategory: CardView = itemView.findViewById(R.id.cvCategory)
        val ivCategoryImage: ImageView = itemView.findViewById(R.id.ivCategoryImage)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.ivCategoryImage.setImageResource(category.imageResourceId)
        holder.tvCategoryName.text = category.name

        holder.cvCategory.setOnClickListener {
            listener.onCategoryClick(category.type)
        }
    }

    override fun getItemCount(): Int = categories.size
}