package com.us.armelevationtracker

import androidx.lifecycle.ViewModel

class SensorViewModel : ViewModel() {
    // Your properties and methods here

    private var isBluetoothConnected: Boolean = false
    private var isMeasurementRunning: Boolean = false
    private var isBlueToothDisconnected: Boolean = false

    fun startMeasurement() {
        // Your logic to start the measurement
        isMeasurementRunning = true
    }

    fun stopMeasurement() {
        // Your logic to stop the measurement
        isMeasurementRunning = false
    }

    fun isBluetoothConnected(): Boolean {
        // Your logic to check Bluetooth connection status

        return isBluetoothConnected
    }

    fun exportData() {
        // Your logic to export data
    }

    fun connectToDevice(deviceName: String) {
        // Your logic to connect to the Bluetooth device

        isBluetoothConnected = true
    }

    fun onDisconnected(deviceName: String){

        isBlueToothDisconnected = true
    }
}

// In your activity or composable, you can create an instance of SensorViewModel lazily
val viewModel: SensorViewModel by lazy { SensorViewModel() }
