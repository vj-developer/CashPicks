package com.tt.cashpicks

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.tt.cashpicks.ui.theme.CashPicksTheme
import kotlinx.coroutines.launch
import java.lang.Math.abs
import kotlin.math.roundToInt

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CashPicksTheme {
                // A surface container using the 'background' color from the theme
                NestedBottomSheetPoc()
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun NestedBottomSheetPoc() {
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()

    Box() {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = { OutSideSheet() },
            sheetPeekHeight = 0.dp
        )
        {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column() {
                    Button(onClick = {
                        scope.launch {
                            if(sheetState.isCollapsed) { sheetState.expand() }
                            else { sheetState.collapse()}
                        }
                    }) {
                        Text(text = "Toggle sheet")
                    }

                    //Text(text = "Bottom sheet fraction :${sheetState.progress.fraction}")
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun OutSideSheet() {
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return Offset.Zero
            }
        }
    }

    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .nestedScroll(nestedScrollConnection)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ){
        //Text("Outside Bottom sheet")
        //WebViewPage(url = "https://developer.android.com/jetpack/compose")
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = { InsideBottomSheet() },
            sheetPeekHeight = 0.dp
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Green),
                contentAlignment = Alignment.Center,

            ) {
                Column() {
                    Button(onClick = {
                        scope.launch {
                            if(sheetState.isCollapsed) { sheetState.expand() }
                            else { sheetState.collapse()}
                        }
                    }) {
                        Text(text = "Toggle Inside bottom sheet")
                    }

                    //Text(text = "Bottom sheet fraction :${sheetState.progress.fraction}")
                }
            }
        }
    }
}

@Composable
fun InsideBottomSheet() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ){
        WebViewPage(url = "https://developer.android.com/jetpack/compose")
    }
}

@Composable
fun WebViewPage(url: String){
    WebViewCompose(url = url)
}

private class MyWebView(context: Context) : WebView(context) {
    val verticalScrollRange: Int get() = computeVerticalScrollRange() - height
}

@Composable
fun WebViewCompose(url: String, modifier: Modifier = Modifier, onCreated: (WebView) -> Unit = {}) {
    val context = LocalContext.current
    val webView: MyWebView = remember(context) {
        MyWebView(context).also(onCreated)
    }
    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }
    val scrollabeState = rememberScrollableState { delta ->
        val scrollY = webView.scrollY
        val consume = (scrollY - delta).coerceIn(0f, webView.verticalScrollRange.toFloat())
        webView.scrollTo(0, consume.roundToInt())
        (scrollY - webView.scrollY).toFloat()
    }
    AndroidView(
        factory = { webView },
        modifier = modifier
            .scrollable(scrollabeState, Orientation.Vertical)
    ) { webView2 ->
        webView2.loadUrl(url)
    }
}


@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CashPicksTheme {
        NestedBottomSheetPoc()
    }
}