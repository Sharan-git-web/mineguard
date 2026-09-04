package com.mineinspect.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mineinspect.app.navigation.AppNavGraph
import com.mineinspect.app.ui.theme.MineInspectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MineInspectTheme {
                AppNavGraph()
            }
        }
    }
}
