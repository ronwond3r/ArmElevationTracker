package com.us.armelevationtracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.us.armelevationtracker.ui.theme.ArmElevationTrackerTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArmElevationTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewmodel = SensorViewModel()

                    // Set up navigation host
                    NavHost(navController = navController, startDestination = "mainScreen") {
                        composable("mainScreen") {
                            MainScreen(navController)
                        }
                        composable("bluetoothConnectionScreen") {
                            BluetoothConnectionScreen(viewmodel)
                        }
                        composable("measurementScreen") {
                            ArmMovementTracker<Any>()
                        }
                        composable("dataExportScreen") {
                            DataExportScreen()
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Arm Sensor")
        Spacer(Modifier .height(50.dp))
        Button(
            onClick = { navController.navigate("bluetoothConnectionScreen") }
        ) {
            Text(text = "Start")
        }
        Button(
            onClick = { navController.navigate("measurementScreen") }
        ) {
            Text(text ="Arm Track")
        }
    }
}












@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ArmElevationTrackerTheme {
        //MainScreen(navController = NavHostController())
    }
}


