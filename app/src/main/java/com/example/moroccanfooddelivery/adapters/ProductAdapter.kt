package com.example.moroccanfooddelivery.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moroccanfooddelivery.ProductDetailActivity
import com.example.moroccanfooddelivery.R
import com.example.moroccanfooddelivery.database.entities.Product

class ProductAdapter(
    private val context: Context,
    private val products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.ivProductImage)
        val productName: TextView = view.findViewById(R.id.tvProductName)
        val productChef: TextView = view.findViewById(R.id.tvChefName)
        val productPrice: TextView = view.findViewById(R.id.tvPrice)
        val addToCartButton: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        try {
            holder.productImage.setImageResource(product.imageResourceId)
            holder.productName.text = product.name
            holder.productChef.text = "Chef: ${product.chefName}"
            holder.productPrice.text = "${product.price} DH"

            // Set up item click listener to navigate to details
            holder.itemView.setOnClickListener {
                try {
                    val intent = Intent(context, ProductDetailActivity::class.java)
                    intent.putExtra("PRODUCT_ID", product.id)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    android.util.Log.e("ProductAdapter", "Error navigating to details: ${e.message}")
                }
            }

            // Set up add to cart button
            holder.addToCartButton.setOnClickListener {
                try {
                    val intent = Intent(context, ProductDetailActivity::class.java)
                    intent.putExtra("PRODUCT_ID", product.id)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    android.util.Log.e("ProductAdapter", "Error navigating to details: ${e.message}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("ProductAdapter", "Error binding view: ${e.message}")
        }
    }

    override fun getItemCount() = products.size
}