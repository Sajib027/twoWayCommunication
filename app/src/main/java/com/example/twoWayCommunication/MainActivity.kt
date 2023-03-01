package com.example.towWayCommunication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.towWayCommunication.ui.theme.Pamiatv2Theme

class MainActivity : ComponentActivity() {
    val textFieldState = TextFieldValue()
    val mUrl = "file:///android_asset/webSample.html"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pamiatv2Theme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MyContent()
                }
            }
        }

    }

    class JSBridge(val context: Context) {
        @JavascriptInterface
        fun showMessageInNative(toast:String){
               Toast.makeText(context, "Toast from android", Toast.LENGTH_SHORT).show()
//            Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show()
//            textFieldState.text = toast
//            textFieldState.text = TextFieldValue(toast).toString()
        }
    }

    /**
     * Send data to webview through function updateFromNative.
     */
    private fun sendDataToWebView(webView: WebView?){
        webView?.evaluateJavascript(
            "javascript: " +"updateFromNative('${textFieldState.text}')",
            null)
    }


    @Composable
    fun MyContent() {
        val webViewState = remember { mutableStateOf<WebView?>(null) }
        var webView: WebView? = null
        // Declare a string that contains a url
        val mUrl = "file:///android_asset/webSample.html"
        Column {

            TextField(
                value = textFieldState.text ,
                onValueChange = { textFieldState.text  = it },
                label = { Text("Enter text") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    sendDataToWebView(webView)

                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save and use")
            }
        }

        // Adding a WebView inside AndroidView
        // with layout as full screen
        AndroidView(factory = {
             WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
//            addJavascriptInterface(JSBridge(this, textFieldState), "Android")
                 addJavascriptInterface(JSBridge(context), "Android")
            loadUrl(mUrl)
            }
        }, update = { webView ->
            // Update the WebView instance if needed
            if (webViewState.value != webView) {
                webViewState.value = webView
            }
        })

        // Get the WebView instance from the mutable state variable
        webView = webViewState.value ?: return
    }

    class TextFieldValue(initialValue: String = "") {
        var text by mutableStateOf(initialValue)
    }
}


