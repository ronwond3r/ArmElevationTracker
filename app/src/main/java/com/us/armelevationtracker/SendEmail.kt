package com.us.armelevationtracker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
@SuppressLint("QueryPermissionsNeeded")
   fun SendEmail(subject: String, body: String) {
        val context = LocalContext.current
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Handle case where no email app is available
            Toast.makeText(context, "No app Found", Toast.LENGTH_SHORT).show()
        }
    }

