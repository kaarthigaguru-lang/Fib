package com.fibfilter.app.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fibfilter.app.R
import com.fibfilter.app.data.model.Stock
import java.text.SimpleDateFormat
import java.util.*

class StockAdapter(
    private val onItemClick: (Stock) -> Unit,
    private val onLongClick: (Stock) -> Unit
) : ListAdapter<Stock, StockAdapter.StockViewHolder>(StockDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view, onItemClick, onLongClick)
    }
    
    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class StockViewHolder(
        itemView: View,
        private val onItemClick: (Stock) -> Unit,
        private val onLongClick: (Stock) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val symbolText: TextView = itemView.findViewById(R.id.symbolText)
        private val nameText: TextView = itemView.findViewById(R.id.nameText)
        private val priceText: TextView = itemView.findViewById(R.id.priceText)
        private val levelText: TextView = itemView.findViewById(R.id.levelText)
        private val highText: TextView = itemView.findViewById(R.id.highText)
        private val fib50Text: TextView = itemView.findViewById(R.id.fib50Text)
        private val fib618Text: TextView = itemView.findViewById(R.id.fib618Text)
        private val alertIndicator: View = itemView.findViewById(R.id.alertIndicator)
        
        fun bind(stock: Stock) {
            symbolText.text = stock.symbol
            nameText.text = stock.name
            priceText.text = "₹${String.format("%.2f", stock.currentPrice)}"
            levelText.text = stock.level
            
            // Set level color
            when {
                stock.level.contains("618") -> {
                    levelText.setTextColor(Color.parseColor("#4CAF50")) // Green
                }
                stock.level.contains("78") -> {
                    levelText.setTextColor(Color.parseColor("#2196F3")) // Blue
                }
                stock.level.contains("50") -> {
                    levelText.setTextColor(Color.parseColor("#FF9800")) // Orange
                }
                else -> {
                    levelText.setTextColor(Color.parseColor("#757575")) // Gray
                }
            }
            
            highText.text = "High: ₹${String.format("%.2f", stock.high)}"
            
            stock.fib50?.let {
                fib50Text.text = "50%: ₹${String.format("%.2f", it)}"
                fib50Text.visibility = View.VISIBLE
            } ?: run {
                fib50Text.visibility = View.GONE
            }
            
            stock.fib618?.let {
                fib618Text.text = "61.8%: ₹${String.format("%.2f", it)}"
                fib618Text.visibility = View.VISIBLE
            } ?: run {
                fib618Text.visibility = View.GONE
            }
            
            alertIndicator.visibility = if (stock.alertEnabled) View.VISIBLE else View.GONE
            
            cardView.setOnClickListener { onItemClick(stock) }
            cardView.setOnLongClickListener {
                onLongClick(stock)
                true
            }
        }
    }
    
    class StockDiffCallback : DiffUtil.ItemCallback<Stock>() {
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.symbol == newItem.symbol
        }
        
        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem == newItem
        }
    }
}
