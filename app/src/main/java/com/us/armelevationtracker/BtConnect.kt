package com.us.armelevationtracker

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.io.IOException
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothConnectionScreen(viewModel: SensorViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bluetooth Connection")

        Spacer(modifier = Modifier.height(16.dp))

        // Check Bluetooth permissions
        if (BluetoothHelper.hasBluetoothPermissions(context)) {
            // Bluetooth permissions are granted
            BluetoothDeviceList(viewModel = viewModel)
        } else {
            // Request Bluetooth permissions
            Button(
                onClick = {
                    BluetoothHelper.requestBluetoothPermissions(context as Activity)
                }
            ) {
                Text("Request Bluetooth Permissions")
            }
        }
    }
}

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BluetoothDeviceList(viewModel: SensorViewModel) {
    // Use BluetoothAdapter to get available devices
    val context = LocalContext.current
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val availableDevices: List<BluetoothDevice>? = bluetoothAdapter?.bondedDevices?.toList()

    // If there are no available devices, display a message
    if (availableDevices.isNullOrEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No available Bluetooth devices found.")
        }
    } else {
        // Display the list of available devices
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Available Bluetooth Devices")

            Spacer(modifier = Modifier.height(16.dp))

            availableDevices.forEach { device ->
                Button(
                    onClick = {
                        // Check Bluetooth permissions
                        if (BluetoothHelper.hasBluetoothPermissions(context)) {
                            // Connect to the selected device
                            try {
                                // Create a BluetoothSocket for the device
                                val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                                val bluetoothSocket: BluetoothSocket? =
                                    device.createRfcommSocketToServiceRecord(uuid)

                                // Connect to the device
                                bluetoothSocket?.connect()

                                // Successfully connected
                                showToast(context, "Connected to ${device.name}")

                                // TODO: Perform any additional setup or communication with the device
                                    deviceConnect(context, device)

                            } catch (e: IOException) {
                                // Handle connection error
                                showToast(context, "Failed to connect to the device.")
                                e.printStackTrace()
                            }
                        }
                    }
                ) {
                    Text(device.name ?: "Unnamed Device")
                }
            }
        }
    }
}

// ... rest of the code

// Simulated showToast function
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun PreviewBluetoothConnectionScreen() {
    BluetoothConnectionScreen(viewModel = SensorViewModel())
}

