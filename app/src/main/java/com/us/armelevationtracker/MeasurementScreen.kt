package com.us.armelevationtracker
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.net.Uri.parse
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun <Activity> ArmMovementTracker() {
    val sensorManager = LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val context = LocalContext.current

    var isTracking by remember { mutableStateOf(false) }
    var abductionAngleStart by remember { mutableFloatStateOf(0f) }
    var abductionAngleEnd by remember { mutableFloatStateOf(0f) }
    var abductionInit by remember{ mutableFloatStateOf(0f) }
    var startTime by remember { mutableLongStateOf(0L) }

    val alpha = 0.2f
    val ewmaFilter = EwmaFilter(alpha)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Arm Abduction Tracker")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Initial Abduction Angle: $abductionAngleStart degrees")
        Text("Start Abduction Angle: $abductionInit degrees")
        Text("Final Abduction Angle: $abductionAngleEnd degrees")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Email the start figures
            val subject = "Start Figures"
            val body = "Initial Abduction Angle: $abductionAngleStart degrees\nStart Abduction Angle: $abductionInit degrees"

            val intent = Intent(Intent.ACTION_SEND).apply {
                data = parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("you@gmail.com")) // Set recipient email if needed
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                type = "message/rfc822" // Use this MIME type for emails
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(Intent.createChooser(intent, "Send Email"))
            } else {
                // Handle case where no email app is available
                Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Send Email")
        }

        Spacer(modifier = Modifier .height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    // Start tracking
                    isTracking = true

                    // Reset initial and final angles
                    abductionAngleStart = 0f
                    abductionAngleEnd = 0f
                    abductionInit = 0f

                    // Record the start time when tracking begins
                    startTime = System.currentTimeMillis()
                },
                enabled = !isTracking
            ) {
                Text("Start Tracking (Slow)", modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Stop tracking
                    isTracking = false

                    // Capture the final angle
                    abductionAngleEnd = abductionAngleStart
                },
                enabled = isTracking
            ) {
                Text("Stop Tracking", modifier = Modifier.padding(8.dp))
            }
        }
    }

    DisposableEffect(sensorManager, isTracking) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isTracking) {
                    // Extract gyroscope values for x, y, and z axes
                    val angularSpeedX = event.values[0]
                    val angularSpeedY = event.values[1]
                    val angularSpeedZ = event.values[2]

                    // Apply EWMA filter to smooth the angular speed (angle)
                    val smoothedAngularSpeedY = ewmaFilter.filter(angularSpeedY)

                    // Integrate angular speeds to get the angle of abduction
                    val elapsedTime = (System.currentTimeMillis() - startTime) / 1000f
                    abductionAngleStart = smoothedAngularSpeedY * elapsedTime

                    // Update the initial angle only once
                    if (abductionInit == 0f) {
                        abductionInit = abductionAngleStart
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Handle accuracy changes if needed
                sensor.toString()
            }
        }

        if (isTracking) {
            // Register the gyroscope listener when tracking is enabled
            sensorManager.registerListener(sensorListener, gyroscope, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
        }

        onDispose {
            // Unregister the listener when the composable is disposed
            sensorManager.unregisterListener(sensorListener)
        }
    }

    // Request necessary permissions
    DisposableEffect(context as Activity) {
        onDispose {
            BluetoothHelper.requestBluetoothPermissions(context)
        }
    }
}



@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ArmMovementTrackerApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArmMovementTracker<Any>()
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun PreviewArmMovementTracker() {
    ArmMovementTrackerApp()
}

