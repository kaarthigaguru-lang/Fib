package com.fibfilter.app.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fibfilter.app.R
import com.fibfilter.app.data.model.Stock
import com.fibfilter.app.databinding.ActivityStockDetailBinding
import com.fibfilter.app.ui.viewmodel.StockViewModel
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class StockDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStockDetailBinding
    private lateinit var viewModel: StockViewModel
    private lateinit var stock: Stock
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        viewModel = ViewModelProvider(this)[StockViewModel::class.java]
        
        stock = intent.getParcelableExtra("stock") ?: run {
            finish()
            return
        }
        
        setupUI()
        setupChart()
        setupAlertSettings()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupUI() {
        supportActionBar?.title = stock.symbol
        
        binding.stockSymbol.text = stock.symbol
        binding.stockName.text = stock.name
        binding.currentPrice.text = "₹${String.format("%.2f", stock.currentPrice)}"
        binding.levelBadge.text = stock.level
        
        // Set level badge color
        val badgeColor = when {
            stock.level.contains("618") -> Color.parseColor("#4CAF50")
            stock.level.contains("78") -> Color.parseColor("#2196F3")
            stock.level.contains("50") -> Color.parseColor("#FF9800")
            else -> Color.parseColor("#757575")
        }
        binding.levelBadge.setBackgroundColor(badgeColor)
        
        binding.fibHighValue.text = "₹${String.format("%.2f", stock.fibHigh ?: stock.high)}"
        binding.fib78Value.text = stock.fib78?.let { "₹${String.format("%.2f", it)}" } ?: "--"
        binding.fib618Value.text = stock.fib618?.let { "₹${String.format("%.2f", it)}" } ?: "--"
        binding.fib50Value.text = stock.fib50?.let { "₹${String.format("%.2f", it)}" } ?: "--"
        binding.fibLowValue.text = stock.fibLow?.let { "₹${String.format("%.2f", it)}" } ?: "--"
    }
    
    private fun setupChart() {
        val chart = binding.fibChart
        
        // Create price line data
        val entries = mutableListOf<Entry>()
        entries.add(Entry(0f, stock.currentPrice.toFloat()))
        
        val dataSet = LineDataSet(entries, "Current Price").apply {
            color = Color.parseColor("#FF5722")
            lineWidth = 3f
            setCircleColor(Color.parseColor("#FF5722"))
            circleRadius = 6f
            setDrawValues(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        }
        
        val lineData = LineData(dataSet)
        chart.data = lineData
        
        // Add Fibonacci level lines
        stock.fibHigh?.let { high ->
            val highLine = LimitLine(high.toFloat(), "High").apply {
                lineColor = Color.parseColor("#F44336")
                lineWidth = 2f
                textColor = Color.BLACK
                textSize = 10f
            }
            chart.axisLeft.addLimitLine(highLine)
        }
        
        stock.fib78?.let { fib78 ->
            val fib78Line = LimitLine(fib78.toFloat(), "78.6%").apply {
                lineColor = Color.parseColor("#2196F3")
                lineWidth = 2f
                textColor = Color.BLACK
                textSize = 10f
            }
            chart.axisLeft.addLimitLine(fib78Line)
        }
        
        stock.fib618?.let { fib618 ->
            val fib618Line = LimitLine(fib618.toFloat(), "61.8%").apply {
                lineColor = Color.parseColor("#4CAF50")
                lineWidth = 2f
                textColor = Color.BLACK
                textSize = 10f
            }
            chart.axisLeft.addLimitLine(fib618Line)
        }
        
        stock.fib50?.let { fib50 ->
            val fib50Line = LimitLine(fib50.toFloat(), "50%").apply {
                lineColor = Color.parseColor("#FF9800")
                lineWidth = 2f
                textColor = Color.BLACK
                textSize = 10f
            }
            chart.axisLeft.addLimitLine(fib50Line)
        }
        
        stock.fibLow?.let { low ->
            val lowLine = LimitLine(low.toFloat(), "Low").apply {
                lineColor = Color.parseColor("#9C27B0")
                lineWidth = 2f
                textColor = Color.BLACK
                textSize = 10f
            }
            chart.axisLeft.addLimitLine(lowLine)
        }
        
        // Chart styling
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            xAxis.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.LTGRAY
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            invalidate()
        }
    }
    
    private fun setupAlertSettings() {
        binding.alertSwitch.isChecked = stock.alertEnabled
        
        val levels = listOf("FIB HIGH", "FIB 78", "FIB 618", "FIB 50", "FIB LOW")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, levels)
        binding.targetLevelInput.setAdapter(adapter)
        
        stock.targetLevel?.let {
            binding.targetLevelInput.setText(it, false)
        }
        
        binding.targetLevelLayout.isEnabled = stock.alertEnabled
        binding.alertSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.targetLevelLayout.isEnabled = isChecked
        }
    }
    
    private fun setupClickListeners() {
        binding.refreshButton.setOnClickListener {
            viewModel.refreshPrice(stock.symbol)
        }
        
        binding.saveAlertButton.setOnClickListener {
            val enabled = binding.alertSwitch.isChecked
            val targetLevel = binding.targetLevelInput.text.toString()
            
            if (enabled && targetLevel.isEmpty()) {
                Toast.makeText(this, "Please select a target level", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            viewModel.updateAlert(
                stock.symbol,
                enabled,
                if (enabled) targetLevel else null
            )
        }
    }
    
    private fun observeViewModel() {
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
                
                // Refresh stock data
                viewModel.getStock(stock.symbol) { updatedStock ->
                    updatedStock?.let {
                        stock = it
                        runOnUiThread {
                            setupUI()
                            setupChart()
                        }
                    }
                }
            }
        }
        
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
