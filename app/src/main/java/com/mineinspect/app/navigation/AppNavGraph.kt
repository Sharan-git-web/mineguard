package com.mineinspect.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mineinspect.app.ui.screens.areas.AreasCoverageScreen
import com.mineinspect.app.ui.screens.briefing.MineBriefingScreen
import com.mineinspect.app.ui.screens.evidence.EvidenceCaptureScreen
import com.mineinspect.app.ui.screens.evidence.EvidenceDetailsScreen
import com.mineinspect.app.ui.screens.gpsgate.GpsGateScreen
import com.mineinspect.app.ui.screens.home.HomeScreen
import com.mineinspect.app.ui.screens.login.LoginScreen
import com.mineinspect.app.ui.screens.map.RouteMapScreen
import com.mineinspect.app.ui.screens.section.SectionMonitorScreen
import com.mineinspect.app.ui.screens.section.SectionStartScreen
import com.mineinspect.app.ui.screens.tracking.ActiveTrackingScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onSignIn = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                onStartInspection = {
                    navController.navigate(Routes.MINE_BRIEFING)
                },
                onOpenMap = {
                    navController.navigate(Routes.ROUTE_MAP)
                }
            )
        }
        composable(Routes.MINE_BRIEFING) {
            MineBriefingScreen(
                onBack = { navController.popBackStack() },
                onStartInspection = { navController.navigate(Routes.GPS_GATE) }
            )
        }
        composable(Routes.GPS_GATE) {
            GpsGateScreen(
                onBack = { navController.popBackStack() },
                onStartInspection = { navController.navigate(Routes.ACTIVE_TRACKING) },
                onCancel = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
        composable(Routes.ACTIVE_TRACKING) {
            ActiveTrackingScreen(
                onBack = { navController.popBackStack() },
                onViewMap = { navController.navigate(Routes.ROUTE_MAP) },
                onViewAreas = { navController.navigate(Routes.AREAS_COVERAGE) }
            )
        }
        composable(Routes.ROUTE_MAP) {
            RouteMapScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.AREAS_COVERAGE) {
            AreasCoverageScreen(
                onBack = { navController.popBackStack() },
                onOpenSectionB = { navController.navigate(Routes.sectionStart("B")) }
            )
        }
        composable(
            route = Routes.SECTION_START,
            arguments = listOf(navArgument("sectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "A"
            SectionStartScreen(
                sectionId = sectionId,
                onBack = { navController.popBackStack() },
                onBegin = { navController.navigate(Routes.sectionMonitoring(sectionId)) }
            )
        }
        composable(
            route = Routes.SECTION_MONITORING,
            arguments = listOf(navArgument("sectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "A"
            SectionMonitorScreen(
                sectionId = sectionId,
                onBack = { navController.popBackStack() },
                onTakePhoto = { navController.navigate(Routes.EVIDENCE_CAPTURE) },
                // Section Completion screen isn't built yet, so return to Areas Coverage for now.
                onComplete = { navController.popBackStack(Routes.AREAS_COVERAGE, inclusive = false) }
            )
        }
        composable(Routes.EVIDENCE_CAPTURE) {
            EvidenceCaptureScreen(
                onBack = { navController.popBackStack() },
                onCaptured = { navController.navigate(Routes.EVIDENCE_DETAILS) }
            )
        }
        composable(Routes.EVIDENCE_DETAILS) {
            EvidenceDetailsScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack(Routes.SECTION_MONITORING, inclusive = false) }
            )
        }
    }
}
