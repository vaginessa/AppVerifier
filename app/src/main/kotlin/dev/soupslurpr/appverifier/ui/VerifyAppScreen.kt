package dev.soupslurpr.appverifier.ui

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dev.soupslurpr.appverifier.data.VerificationStatus

@Composable
fun VerifyAppScreen(
    icon: Drawable?,
    name: String,
    packageName: String,
    hash: String,
    verificationStatus: VerificationStatus,
    appNotFound: Boolean,
    onVerifyFromClipboard: (String) -> Unit,
    invalidFormat: Boolean,
) {
    val context = LocalContext.current

    val clipboardManager = LocalClipboardManager.current

    val verticalScroll = rememberScrollState()

    var showMoreInfoAboutVerificationStatusDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(verticalScroll),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (appNotFound) {
            Text("APP NOT INSTALLED")
            Text(
                "The package name provided does not correspond to any installed user app." +
                        "\nPlease note system apps are not included in the search."
            )
        } else if (invalidFormat) {
            Text("INVALID FORMAT")
            Text(
                "The provided text doesn't seem to be in the correct format. Please make sure it is like the " +
                        "following:\n\ncom.example.app\n96:C0:2C:55:75:5C:17:1C:68:13:70:29:3B:37:11:2B:4A:5D:F7:B9:82:C2:C5:58:05:4C:45:51:AD:F5:50:DC"
            )
        } else {
            if (icon != null) {
                Image(
                    rememberDrawablePainter(drawable = icon),
                    null,
                    Modifier.size(150.dp),
                )
            }
            Text(
                text = name,
                style = typography.titleLarge
            )
            Text(text = packageName)
            Text(
                text = hash,
                fontFamily = FontFamily.Monospace
            )
            Button(onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "$packageName\n$hash")
                    type = "plain/text"
                }

                val shareIntent = Intent.createChooser(
                    sendIntent,
                    null,
                )

                startActivity(context, shareIntent, ActivityOptions.makeBasic().toBundle())
            }) {
                Text("Share/Copy Verification Info")
            }
            Button(onClick = {
                if (clipboardManager.hasText()) {
                    onVerifyFromClipboard(clipboardManager.getText()!!.text)
                } else {

                }
            }) {
                Text("Verify from clipboard")
            }
            Text(
                "Verification Status:",
            )
            Row {
                FilledTonalButton(
                    onClick = { showMoreInfoAboutVerificationStatusDialog = true },
                ) {
                    Text(
                        verificationStatus.simpleVerificationStatus.name,
                        style = typography.headlineLarge
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Info,
                        "More info about verification status",
                        tint = verificationStatus.simpleVerificationStatus.color,
                    )
                }
            }
        }
    }

    if (showMoreInfoAboutVerificationStatusDialog) {
        AlertDialog(
            onDismissRequest = { showMoreInfoAboutVerificationStatusDialog = false },
            confirmButton = {
                TextButton(
                    { showMoreInfoAboutVerificationStatusDialog = false }
                ) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        verificationStatus.name,
                        style = typography.headlineSmall,
                        color = verificationStatus.simpleVerificationStatus.color,
                    )
                }
            },
            text = {
                Text(verificationStatus.info)
            }
        )
    }
}