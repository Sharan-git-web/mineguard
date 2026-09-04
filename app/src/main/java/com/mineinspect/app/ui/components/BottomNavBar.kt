package com.mineinspect.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.mineinspect.app.ui.theme.Primary
import com.mineinspect.app.ui.theme.Secondary

enum class NavTab(val label: String) {
    INSPECTIONS("Inspections"), HAZARDS("Hazards"), MAP("Map"), SYNC("Sync")
}

@Composable
fun AppBottomNavBar(selected: NavTab, onSelect: (NavTab) -> Unit) {
    NavigationBar {
        val items = listOf(
            NavTab.INSPECTIONS to Icons.Filled.Assignment,
            NavTab.HAZARDS to Icons.Filled.Warning,
            NavTab.MAP to Icons.Filled.Map,
            NavTab.SYNC to Icons.Filled.Sync
        )
        items.forEach { (tab, icon) ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = { Icon(icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = Secondary,
                    unselectedTextColor = Secondary
                )
            )
        }
    }
}
