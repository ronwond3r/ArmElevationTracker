package com.us.armelevationtracker

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.UUID


@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.S)
fun deviceConnect(context: Context, device: BluetoothDevice) {
    // Register onDisconnected callback
    val gattCallback = GattCallback({ onDisconnected(device) }) { /* handle characteristic changed */ }
    gattCallback.setOnDisconnectedListener { onDisconnected(it) }

    // Connect to GATT server
    val gattServer = device.connectGatt(context, false, gattCallback)

    // Discover services
    val services: List<BluetoothGattService> = gattServer?.services ?: emptyList()

    // Find the desired service using the UUID
    val pmdService: BluetoothGattService? = services.find { it.uuid.toString() == PolarMeasurementDataUUIDs.Service }

    // Get characteristics
    val controlCharacteristic: BluetoothGattCharacteristic? =
        pmdService?.getCharacteristic(UUID.fromString(PolarMeasurementDataUUIDs.Characteristics.ControlPoint))
    val dataCharacteristic: BluetoothGattCharacteristic? =
        pmdService?.getCharacteristic(UUID.fromString(PolarMeasurementDataUUIDs.Characteristics.DataMTU))

    // Set up characteristic value changed listeners
    val characteristicListeners = listOf(controlCharacteristic, dataCharacteristic)
    characteristicListeners.forEach { characteristic ->
        characteristic?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        gattServer?.setCharacteristicNotification(characteristic, true)
        gattServer?.readCharacteristic(characteristic)
    }
}

fun onDisconnected(it: BluetoothDevice): BluetoothDevice {

    return it
}


class GattCallback(onDisconnectedListener: () -> BluetoothDevice, onDataChangedListener: () -> Unit) : BluetoothGattCallback() {
    private var disconnectedListener: ((BluetoothDevice) -> Unit)? = null

    fun setOnDisconnectedListener(listener: (BluetoothDevice) -> Unit) {
        disconnectedListener = listener
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            gatt?.device?.let { disconnectedListener?.invoke(it) }
        }
    }

    // Implement other BluetoothGattCallback methods as needed
}