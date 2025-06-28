package com.example.moroccanfooddelivery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.Random

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var orderId: Int = -1
    private val handler = Handler(Looper.getMainLooper())
    private val random = Random()

    // For demo purposes - starting point in Morocco (Casablanca)
    private var currentDeliveryLocation = LatLng(33.5731, -7.5898)
    // Destination - can be user's address, but using a fixed point for demo
    private val destinationLocation = LatLng(33.5950, -7.6187)

    private lateinit var tvOrderId: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvOrderItems: TextView
    private lateinit var tvEstimatedTime: TextView
    private lateinit var btnBack: ImageButton

    private val locationUpdateRunnable = object : Runnable {
        override fun run() {
            updateDeliveryLocation()
            handler.postDelayed(this, 5000) // Update every 5 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d("TrackingActivity", "Starting activity creation")
            setContentView(R.layout.activity_tracking)
            Log.d("TrackingActivity", "Content view set successfully")

            // Initialize views
            tvOrderId = findViewById(R.id.tvOrderId)
            tvOrderStatus = findViewById(R.id.tvOrderStatus)
            tvOrderItems = findViewById(R.id.tvOrderItems)
            tvEstimatedTime = findViewById(R.id.tvEstimatedTime)
            btnBack = findViewById(R.id.btnBack)

            // Get order ID from intent
            orderId = intent.getIntExtra("ORDER_ID", -1)
            if (orderId == -1) {
                Log.e("TrackingActivity", "No order ID provided")
                Toast.makeText(this, "Order ID not found", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            Log.d("TrackingActivity", "Order ID received: $orderId")

            // Setup map
            try {
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as? SupportMapFragment

                if (mapFragment == null) {
                    Log.e("TrackingActivity", "Map fragment not found")
                    Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show()
                } else {
                    mapFragment.getMapAsync(this)
                    Log.d("TrackingActivity", "Map fragment initialized")
                }
            } catch (e: Exception) {
                Log.e("TrackingActivity", "Error setting up map: ${e.message}", e)
                Toast.makeText(this, "Map initialization failed", Toast.LENGTH_SHORT).show()
            }

            // Load order details
            loadOrderDetails()

            // Setup back button
            btnBack.setOnClickListener {
                Log.d("TrackingActivity", "Back button clicked")
                finish()
            }

            Log.d("TrackingActivity", "Activity setup complete")
        } catch (e: Exception) {
            Log.e("TrackingActivity", "Fatal error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error tracking your order", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadOrderDetails() {
        try {
            // Simulated order details - replace with database calls if needed
            tvOrderId.text = "Order #$orderId"
            tvOrderStatus.text = "On the way"
            tvOrderStatus.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))

            // Simulated order items - replace with database calls if needed
            val orderItems = listOf(
                "Tagine (2)",
                "Couscous (1)",
                "Moroccan Tea (2)"
            )
            val itemSummary = orderItems.joinToString(", ")
            tvOrderItems.text = itemSummary

            // Simulated estimated delivery time
            val estimatedMinutes = 15 + random.nextInt(30)
            tvEstimatedTime.text = "$estimatedMinutes min"

            Log.d("TrackingActivity", "Order details loaded successfully")
        } catch (e: Exception) {
            Log.e("TrackingActivity", "Error loading order details: ${e.message}", e)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        try {
            googleMap = map
            Log.d("TrackingActivity", "Map ready")

            // Check for location permission with explicit try-catch
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    googleMap.isMyLocationEnabled = true
                    Log.d("TrackingActivity", "Location permission granted")
                } catch (securityException: SecurityException) {
                    Log.e("TrackingActivity", "Security exception: ${securityException.message}", securityException)
                    Toast.makeText(this, "Location permission issue detected", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("TrackingActivity", "Requesting location permission")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }

            // Continue with map setup regardless of permission state
            // Show delivery person marker
            addDeliveryMarker()
            drawRouteLine()

            // Move camera to show both markers
            val cameraPosition = CameraUpdateFactory.newLatLngZoom(currentDeliveryLocation, 14f)
            googleMap.moveCamera(cameraPosition)

            // Start location updates
            handler.post(locationUpdateRunnable)
            Log.d("TrackingActivity", "Location updates started")

        } catch (e: Exception) {
            Log.e("TrackingActivity", "Error in onMapReady: ${e.message}", e)
            Toast.makeText(this, "Error loading map", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDeliveryMarker() {
        try {
            // Clear previous marker
            googleMap.clear()

            // Add delivery person marker - fallback to default marker if custom icon not available
            try {
                googleMap.addMarker(
                    MarkerOptions()
                        .position(currentDeliveryLocation)
                        .title("Delivery Person")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )
            } catch (e: Exception) {
                Log.e("TrackingActivity", "Error adding custom delivery marker: ${e.message}", e)
                // Fallback to default marker
                googleMap.addMarker(
                    MarkerOptions()
                        .position(currentDeliveryLocation)
                        .title("Delivery Person")
                )
            }

            // Add destination marker
            googleMap.addMarker(
                MarkerOptions()
                    .position(destinationLocation)
                    .title("Delivery Destination")
            )

            Log.d("TrackingActivity", "Markers added successfully")
        } catch (e: Exception) {
            Log.e("TrackingActivity", "Error adding markers: ${e.message}", e)
        }
    }

    private fun drawRouteLine() {
        try {
            // Simple direct line between delivery person and destination
            // For a real app, you might want to use the Directions API to get an actual route
            val polylineOptions = PolylineOptions()
                .add(currentDeliveryLocation)
                .add(destinationLocation)
                .width(5f)
                .color(ContextCompat.getColor(this, android.R.color.holo_blue_dark))

            googleMap.addPolyline(polylineOptions)
        } catch (e: Exception) {
            Log.e("TrackingActivity", "Error drawing route: ${e.message}", e)
        }
    }

    private fun updateDeliveryLocation() {
        try {
            // Simulate delivery person movement
            val latDiff = (destinationLocation.latitude - currentDeliveryLocation.latitude) / 10
            val lngDiff = (destinationLocation.longitude - currentDeliveryLocation.longitude) / 10

            currentDeliveryLocation = LatLng(
                currentDeliveryLocation.latitude + latDiff + (random.nextDouble() - 0.5) * 0.001,
                currentDeliveryLocation.longitude + lngDiff + (random.nextDouble() - 0.5) * 0.001
            )

            // Update marker position
            addDeliveryMarker()
            drawRouteLine()

            // Update order status
            val distance = calculateDistance(currentDeliveryLocation, destinationLocation)
            if (distance < 0.05) { // Less than 50 meters
                tvOrderStatus.text = "Arrived"
                tvOrderStatus.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                handler.removeCallbacks(locationUpdateRunnable)
                Log.d("TrackingActivity", "Delivery arrived at destination")
            }

            // Update the estimated time
            val estimatedMinutes = Math.max(1, (distance * 60).toInt()) // Rough estimate: 1 km = 60 min
            tvEstimatedTime.text = "$estimatedMinutes min"
        } catch (e: Exception) {
            Log.e("TrackingActivity", "Error updating location: ${e.message}", e)
        }
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        // Simple distance calculation (Haversine formula would be more accurate)
        val lat1 = start.latitude
        val lon1 = start.longitude
        val lat2 = end.latitude
        val lon2 = end.longitude

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        // Distance in kilometers
        return 6371 * c
    }

    private fun checkAndEnableMyLocation() {
        try {
            if (::googleMap.isInitialized &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            }
        } catch (securityException: SecurityException) {
            Log.e("TrackingActivity", "Security exception in checkAndEnableMyLocation: ${securityException.message}", securityException)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location on map
                if (::googleMap.isInitialized) {
                    try {
                        googleMap.isMyLocationEnabled = true
                        Log.d("TrackingActivity", "Location permission granted in callback")
                    } catch (securityException: SecurityException) {
                        Log.e("TrackingActivity", "Security exception after permission granted: ${securityException.message}", securityException)
                    }
                }
            } else {
                // Permission denied, show a message
                Toast.makeText(
                    this,
                    "Location permission denied. Your position won't be shown on the map.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Make sure map functionality continues regardless of permission
            if (::googleMap.isInitialized) {
                addDeliveryMarker()
                drawRouteLine()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check permission when activity resumes
        if (::googleMap.isInitialized) {
            checkAndEnableMyLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(locationUpdateRunnable)
        Log.d("TrackingActivity", "Activity destroyed, callbacks removed")
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}