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
import com.mineinspect.app.ui.screens.evidence.RandomEvidenceScreen
import com.mineinspect.app.ui.screens.gpsgate.GpsGateScreen
import com.mineinspect.app.ui.screens.home.HomeScreen
import com.mineinspect.app.ui.screens.login.LoginScreen
import com.mineinspect.app.ui.screens.map.RouteMapScreen
import com.mineinspect.app.ui.screens.measurement.MeasurementEntryScreen
import com.mineinspect.app.ui.screens.observation.ManualObservationScreen
import com.mineinspect.app.ui.screens.section.SectionChecksMenuScreen
import com.mineinspect.app.ui.screens.section.SectionCompletionScreen
import com.mineinspect.app.ui.screens.section.SectionMonitorScreen
import com.mineinspect.app.ui.screens.section.SectionStartScreen
import com.mineinspect.app.ui.screens.submission.FinalLocationCheckScreen
import com.mineinspect.app.ui.screens.submission.FinalReviewScreen
import com.mineinspect.app.ui.screens.submission.SubmissionCompleteScreen
import com.mineinspect.app.ui.screens.submission.SynchronizationScreen
import com.mineinspect.app.ui.screens.tracking.ActiveTrackingScreen
import com.mineinspect.app.ui.screens.verification.PpeVerificationScreen
import com.mineinspect.app.ui.screens.verification.WorkerVerificationScreen

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
                onStartInspection = { mineId ->
                    navController.navigate(Routes.mineBriefing(mineId))
                },
                onOpenMap = {
                    navController.navigate(Routes.routeMap())
                }
            )
        }
        composable(
            route = Routes.MINE_BRIEFING,
            arguments = listOf(navArgument("mineId") { type = NavType.StringType })
        ) {
            // mineId is read from SavedStateHandle inside MineBriefingViewModel via hiltViewModel().
            MineBriefingScreen(
                onBack = { navController.popBackStack() },
                onStartInspection = { inspectionId -> navController.navigate(Routes.gpsGate(inspectionId)) }
            )
        }
        composable(
            route = Routes.GPS_GATE,
            arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            GpsGateScreen(
                inspectionId = inspectionId,
                onBack = { navController.popBackStack() },
                onStartInspection = { navController.navigate(Routes.activeTracking(inspectionId)) },
                onCancel = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
        composable(
            route = Routes.ACTIVE_TRACKING,
            arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            ActiveTrackingScreen(
                inspectionId = inspectionId,
                onBack = { navController.popBackStack() },
                onViewMap = { navController.navigate(Routes.routeMap(inspectionId)) },
                onViewAreas = { navController.navigate(Routes.areasCoverage(inspectionId)) }
            )
        }
        composable(
            route = Routes.ROUTE_MAP,
            arguments = listOf(navArgument("inspectionId") { type = NavType.StringType; nullable = true; defaultValue = null })
        ) {
            RouteMapScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.AREAS_COVERAGE,
            arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            AreasCoverageScreen(
                inspectionId = inspectionId,
                onBack = { navController.popBackStack() },
                onOpenSectionB = { navController.navigate(Routes.sectionStart(inspectionId, "2")) },
                onOpenSection3 = { navController.navigate(Routes.sectionStart(inspectionId, "3")) },
                onCompleteAudit = { navController.navigate(Routes.finalLocationCheck(inspectionId)) }
            )
        }
        composable(
            route = Routes.SECTION_START,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "2"
            SectionStartScreen(
                inspectionId = inspectionId,
                sectionId = sectionId,
                onBack = { navController.popBackStack() },
                onBegin = { navController.navigate(Routes.sectionMonitoring(inspectionId, sectionId)) }
            )
        }
        composable(
            route = Routes.SECTION_MONITORING,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "2"
            SectionMonitorScreen(
                inspectionId = inspectionId,
                sectionId = sectionId,
                onBack = { navController.popBackStack() },
                onTakePhoto = { navController.navigate(Routes.evidenceCapture(inspectionId, sectionId)) },
                onMoreChecks = { navController.navigate(Routes.sectionChecksMenu(inspectionId, sectionId)) },
                onComplete = { navController.navigate(Routes.sectionCompletion(inspectionId, sectionId)) }
            )
        }
        composable(
            route = Routes.SECTION_COMPLETION,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "2"
            SectionCompletionScreen(
                sectionId = sectionId,
                onBack = { navController.popBackStack() },
                onConfirm = {
                    if (sectionId == "2" || sectionId == "B") {
                        navController.navigate(Routes.sectionStart(inspectionId, "3")) {
                            popUpTo(Routes.AREAS_COVERAGE) { inclusive = false }
                        }
                    } else {
                        navController.popBackStack(Routes.AREAS_COVERAGE, inclusive = false)
                    }
                }
            )
        }
        composable(
            route = Routes.SECTION_CHECKS_MENU,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "2"
            SectionChecksMenuScreen(
                onBack = { navController.popBackStack() },
                onManualObservation = { navController.navigate(Routes.manualObservation(inspectionId, sectionId)) },
                onMeasurementEntry = { navController.navigate(Routes.measurementEntry(inspectionId, sectionId)) },
                onPpeVerification = { navController.navigate(Routes.ppeVerification(inspectionId, sectionId)) },
                onWorkerVerification = { navController.navigate(Routes.workerVerification(inspectionId, sectionId)) },
                onRandomEvidence = { navController.navigate(Routes.randomEvidence(inspectionId, sectionId)) }
            )
        }
        composable(
            route = Routes.MANUAL_OBSERVATION,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) {
            ManualObservationScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack(Routes.SECTION_MONITORING, inclusive = false) }
            )
        }
        composable(
            route = Routes.MEASUREMENT_ENTRY,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) {
            MeasurementEntryScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack(Routes.SECTION_MONITORING, inclusive = false) }
            )
        }
        composable(
            route = Routes.PPE_VERIFICATION,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) {
            PpeVerificationScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack(Routes.SECTION_MONITORING, inclusive = false) }
            )
        }
        composable(
            route = Routes.WORKER_VERIFICATION,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) {
            WorkerVerificationScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack(Routes.SECTION_MONITORING, inclusive = false) }
            )
        }
        composable(
            route = Routes.RANDOM_EVIDENCE,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "2"
            RandomEvidenceScreen(
                sectionId = sectionId,
                onBack = { navController.popBackStack() },
                onCapture = { navController.navigate(Routes.evidenceCapture(inspectionId, sectionId)) }
            )
        }
        composable(
            route = Routes.EVIDENCE_CAPTURE,
            arguments = listOf(
                navArgument("inspectionId") { type = NavType.StringType },
                navArgument("sectionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: "2"
            EvidenceCaptureScreen(
                sectionId = sectionId,
                onBack = { navController.popBackStack() },
                onCaptured = { evidenceId -> navController.navigate(Routes.evidenceDetails(evidenceId)) }
            )
        }
        composable(
            route = Routes.EVIDENCE_DETAILS,
            arguments = listOf(navArgument("evidenceId") { type = NavType.StringType })
        ) {
            EvidenceDetailsScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack(Routes.SECTION_MONITORING, inclusive = false) }
            )
        }
        composable(
            route = Routes.FINAL_LOCATION_CHECK,
            arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            FinalLocationCheckScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.finalReview(inspectionId)) }
            )
        }
        composable(
            route = Routes.FINAL_REVIEW,
            arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val inspectionId = backStackEntry.arguments?.getString("inspectionId") ?: ""
            FinalReviewScreen(
                onBack = { navController.popBackStack() },
                onSubmitted = {
                    navController.navigate(Routes.synchronization(inspectionId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        composable(
            route = Routes.SYNCHRONIZATION,
            arguments = listOf(navArgument("inspectionId") { type = NavType.StringType })
        ) {
            SynchronizationScreen(
                onContinue = {
                    navController.navigate(Routes.SUBMISSION_COMPLETE) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        composable(Routes.SUBMISSION_COMPLETE) {
            SubmissionCompleteScreen(
                onReturnHome = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }
    }
}
