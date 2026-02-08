package com.fibfilter.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fibfilter.app.R
import com.fibfilter.app.data.model.Stock
import com.fibfilter.app.databinding.ActivityMainBinding
import com.fibfilter.app.databinding.DialogAddStockBinding
import com.fibfilter.app.ui.adapter.StockAdapter
import com.fibfilter.app.ui.viewmodel.StockViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: StockAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        viewModel = ViewModelProvider(this)[StockViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        adapter = StockAdapter(
            onItemClick = { stock ->
                openStockDetail(stock)
            },
            onLongClick = { stock ->
                showStockOptions(stock)
            }
        )
        
        binding.stockRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }
    
    private fun setupObservers() {
        viewModel.allStocks.observe(this) { stocks ->
            adapter.submitList(stocks)
            binding.emptyView.visibility = if (stocks.isEmpty()) View.VISIBLE else View.GONE
            binding.stockRecyclerView.visibility = if (stocks.isEmpty()) View.GONE else View.VISIBLE
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingCard.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
        
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            showAddStockDialog()
        }
    }
    
    private fun showAddStockDialog(existingStock: Stock? = null) {
        val dialogBinding = DialogAddStockBinding.inflate(layoutInflater)
        
        // Pre-fill if editing
        existingStock?.let { stock ->
            dialogBinding.symbolInput.setText(stock.symbol)
            dialogBinding.nameInput.setText(stock.name)
            dialogBinding.highInput.setText(stock.high.toString())
            dialogBinding.lowInput.setText(stock.fibLow?.toString() ?: "")
            dialogBinding.priceInput.setText(stock.currentPrice.toString())
            dialogBinding.symbolInput.isEnabled = false // Don't allow changing symbol
        }
        
        // Calculate Fibonacci levels on button click
        dialogBinding.calculateButton.setOnClickListener {
            val high = dialogBinding.highInput.text.toString().toDoubleOrNull()
            val low = dialogBinding.lowInput.text.toString().toDoubleOrNull()
            
            if (high != null && low != null && high > low) {
                val diff = high - low
                val fib50 = low + (diff * 0.5)
                val fib618 = low + (diff * 0.618)
                val fib78 = low + (diff * 0.786)
                
                dialogBinding.fib50Preview.text = String.format("%.2f", fib50)
                dialogBinding.fib618Preview.text = String.format("%.2f", fib618)
                dialogBinding.fib78Preview.text = String.format("%.2f", fib78)
            } else {
                Toast.makeText(this, "Please enter valid high and low prices", Toast.LENGTH_SHORT).show()
            }
        }
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(if (existingStock == null) "Add Stock" else "Edit Stock")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                saveStock(dialogBinding, existingStock)
            }
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
    }
    
    private fun saveStock(dialogBinding: DialogAddStockBinding, existingStock: Stock?) {
        val symbol = dialogBinding.symbolInput.text.toString().trim().uppercase()
        val name = dialogBinding.nameInput.text.toString().trim()
        val high = dialogBinding.highInput.text.toString().toDoubleOrNull()
        val low = dialogBinding.lowInput.text.toString().toDoubleOrNull()
        val price = dialogBinding.priceInput.text.toString().toDoubleOrNull()
        
        if (symbol.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Please fill in symbol and name", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (high == null || price == null) {
            Toast.makeText(this, "Please fill in high and current price", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Calculate Fibonacci levels
        val fibHigh = high
        val fibLow = low
        val diff = if (low != null) high - low else 0.0
        val fib50 = if (low != null) low + (diff * 0.5) else null
        val fib618 = if (low != null) low + (diff * 0.618) else null
        val fib78 = if (low != null) low + (diff * 0.786) else null
        
        // Determine current level
        val level = when {
            fib618 != null && kotlin.math.abs(price - fib618) / fib618 * 100 <= 2.0 -> "FIB 618"
            fib78 != null && kotlin.math.abs(price - fib78) / fib78 * 100 <= 2.0 -> "FIB 78"
            fib50 != null && kotlin.math.abs(price - fib50) / fib50 * 100 <= 2.0 -> "FIB 50"
            else -> "FALSE"
        }
        
        val stock = Stock(
            symbol = symbol,
            name = name,
            highDate = System.currentTimeMillis(),
            high = high,
            fibHigh = fibHigh,
            fibLow = fibLow,
            fib50 = fib50,
            fib618 = fib618,
            fib78 = fib78,
            currentPrice = price,
            level = level,
            alertEnabled = existingStock?.alertEnabled ?: false,
            targetLevel = existingStock?.targetLevel
        )
        
        if (existingStock == null) {
            viewModel.insertStock(stock)
        } else {
            viewModel.updateStock(stock)
        }
    }
    
    private fun openStockDetail(stock: Stock) {
        val intent = Intent(this, StockDetailActivity::class.java)
        intent.putExtra("stock", stock)
        startActivity(intent)
    }
    
    private fun showStockOptions(stock: Stock) {
        val options = arrayOf("View Details", "Edit", "Refresh Price", "Delete")
        
        MaterialAlertDialogBuilder(this)
            .setTitle(stock.symbol)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openStockDetail(stock)
                    1 -> showAddStockDialog(stock)
                    2 -> viewModel.refreshPrice(stock.symbol)
                    3 -> confirmDelete(stock)
                }
            }
            .show()
    }
    
    private fun confirmDelete(stock: Stock) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Stock")
            .setMessage("Are you sure you want to delete ${stock.symbol}?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteStock(stock)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh_all -> {
                viewModel.refreshAllPrices()
                true
            }
            R.id.action_settings -> {
                // Open settings
                Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
