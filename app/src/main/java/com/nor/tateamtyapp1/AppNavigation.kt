package com.nor.tateamtyapp1

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val SPLASH_SCREEN = "splash_screen"
    const val WELCOME_SCREEN = "welcome_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val DASHBOARD_SCREEN = "dashboard_screen"
    const val PARENT_LOGIN_SCREEN = "parent_login_screen"
    const val PARENT_DASHBOARD_SCREEN = "parent_dashboard_screen"
    const val SETTINGS_SCREEN = "settings_screen"
    const val REGISTER_CHILD_SCREEN = "register_child_screen"
    const val VACCINATION_MANAGEMENT_SCREEN = "vaccination_management_screen"
    const val VACCINATION_SCHEDULE_SCREEN = "vaccination_schedule_screen"
    const val VACCINATION_RECORDS_SCREEN = "vaccination_records_screen"
    const val CHILD_VACCINATION_DETAILS_SCREEN = "child_vaccination_details_screen"
    const val VIEW_CHILD_RECORDS_SCREEN = "view_child_records_screen"
    const val SEARCH_CHILD_SCREEN = "search_child_screen"
    const val CERTIFICATE_ISSUING_SCREEN = "certificate_issuing_screen"
    const val REPORTS_SCREEN = "reports_screen"
    const val VACCINATIONS_OVER_TIME_SCREEN = "vaccinations_over_time_screen"
    const val COMPLETION_REPORT_SCREEN = "completion_report_screen"
    const val SEARCH_REGISTERED_CHILD_SCREEN = "search_registered_child_screen"
    const val PROJECT_GUIDE_SCREEN = "project_guide_screen"
    const val VACCINE_INFO_SCREEN = "vaccine_info_screen"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH_SCREEN,
        // تبسيط الحركات لتكون "تلاشي" فقط لسرعة الأداء
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) },
        popEnterTransition = { fadeIn(animationSpec = tween(400)) },
        popExitTransition = { fadeOut(animationSpec = tween(400)) }
    ) {
        composable(Routes.SPLASH_SCREEN) {
            SplashScreen(onAnimationFinished = {
                navController.navigate(Routes.WELCOME_SCREEN) {
                    popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
                }
            })
        }

        composable(Routes.WELCOME_SCREEN) {
            WelcomeScreen(
                onStaffLoginClicked = { navController.navigate(Routes.LOGIN_SCREEN) },
                onParentPortalClicked = { navController.navigate(Routes.PARENT_LOGIN_SCREEN) }
            )
        }

        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD_SCREEN) {
                        popUpTo(Routes.WELCOME_SCREEN) { inclusive = true }
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.PARENT_LOGIN_SCREEN) {
            ParentLoginScreen(
                onLoginSuccess = { phone ->
                    navController.navigate("${Routes.PARENT_DASHBOARD_SCREEN}/$phone") {
                        popUpTo(Routes.WELCOME_SCREEN) { inclusive = true }
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD_SCREEN) {
            DashboardScreen(
                onLogout = {
                    navController.navigate(Routes.WELCOME_SCREEN) {
                        popUpTo(Routes.WELCOME_SCREEN) { inclusive = true }
                    }
                },
                onRegisterChildClicked = { navController.navigate(Routes.REGISTER_CHILD_SCREEN) },
                onVaccinationManagementClicked = { navController.navigate(Routes.VACCINATION_MANAGEMENT_SCREEN) },
                onIssueCertificateClicked = { navController.navigate(Routes.SEARCH_CHILD_SCREEN) },
                onNavigateToReports = { navController.navigate(Routes.REPORTS_SCREEN) },
                onSearchChildClicked = { navController.navigate(Routes.SEARCH_REGISTERED_CHILD_SCREEN) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS_SCREEN) },
                onNavigateToUserManual = { navController.navigate(Routes.PROJECT_GUIDE_SCREEN) },
                onChildDueTodayClicked = { childId ->
                    navController.navigate("${Routes.CHILD_VACCINATION_DETAILS_SCREEN}/$childId")
                }
            )
        }

        composable(Routes.PROJECT_GUIDE_SCREEN) {
            ProjectGuideScreen(onBackPressed = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS_SCREEN) {
            SettingsScreen(onBackPressed = { navController.popBackStack() })
        }

        composable(
            route = "${Routes.PARENT_DASHBOARD_SCREEN}/{parentPhone}",
            arguments = listOf(navArgument("parentPhone") { type = NavType.StringType })
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("parentPhone") ?: ""
            ParentDashboardScreen(
                parentPhone = phone,
                onLogout = {
                    navController.navigate(Routes.WELCOME_SCREEN) {
                        popUpTo(Routes.WELCOME_SCREEN) { inclusive = true }
                    }
                },
                onViewChildRecords = { childId ->
                    navController.navigate("${Routes.VIEW_CHILD_RECORDS_SCREEN}/$childId")
                },
                onNavigateToVaccineInfo = {
                    navController.navigate(Routes.VACCINE_INFO_SCREEN)
                }
            )
        }

        composable(Routes.VACCINE_INFO_SCREEN) {
            VaccineInfoScreen(onBackPressed = { navController.popBackStack() })
        }

        composable(Routes.REGISTER_CHILD_SCREEN) {
            RegisterChildScreen(onCancel = { navController.popBackStack() })
        }

        composable(Routes.VACCINATION_MANAGEMENT_SCREEN) {
            VaccinationManagementScreen(
                onBackPressed = { navController.popBackStack() },
                onNavigateToSchedule = { navController.navigate(Routes.VACCINATION_SCHEDULE_SCREEN) },
                onNavigateToRecords = { navController.navigate(Routes.VACCINATION_RECORDS_SCREEN) }
            )
        }

        composable(Routes.VACCINATION_SCHEDULE_SCREEN) {
            VaccinationScheduleScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.VACCINATION_RECORDS_SCREEN) {
            VaccinationRecordsScreen(
                onBackPressed = { navController.popBackStack() },
                onChildClicked = { childId ->
                    navController.navigate("${Routes.VIEW_CHILD_RECORDS_SCREEN}/$childId")
                }
            )
        }

        composable(
            route = "${Routes.CHILD_VACCINATION_DETAILS_SCREEN}/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.StringType })
        ) {
            ChildVaccinationDetailsScreen(onBackPressed = { navController.popBackStack() }, isReadOnly = false)
        }

        composable(
            route = "${Routes.VIEW_CHILD_RECORDS_SCREEN}/{childId}",
            arguments = listOf(navArgument("childId") { type = NavType.StringType })
        ) {
            ChildVaccinationDetailsScreen(onBackPressed = { navController.popBackStack() }, isReadOnly = true)
        }

        composable(Routes.REPORTS_SCREEN) {
            ReportsScreen(
                onBackPressed = { navController.popBackStack() },
                onNavigateToVaccinationsOverTime = { navController.navigate(Routes.VACCINATIONS_OVER_TIME_SCREEN) },
                onNavigateToCompletionReport = { navController.navigate(Routes.COMPLETION_REPORT_SCREEN) }
            )
        }

        composable(Routes.VACCINATIONS_OVER_TIME_SCREEN) {
            VaccinationsOverTimeScreen(onBackPressed = { navController.popBackStack() })
        }

        composable(Routes.COMPLETION_REPORT_SCREEN) {
            CompletionReportScreen(onBackPressed = { navController.popBackStack() })
        }

        composable(Routes.SEARCH_CHILD_SCREEN) {
            SearchChildScreen(
                onBackPressed = { navController.popBackStack() },
                onSearchClicked = { nationalId ->
                    navController.navigate("${Routes.CERTIFICATE_ISSUING_SCREEN}/$nationalId")
                }
            )
        }

        composable(
            route = "${Routes.CERTIFICATE_ISSUING_SCREEN}/{nationalId}",
            arguments = listOf(navArgument("nationalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val nationalId = backStackEntry.arguments?.getString("nationalId") ?: ""
            CertificateIssuingScreen(
                nationalId = nationalId,
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.SEARCH_REGISTERED_CHILD_SCREEN) {
            SearchRegisteredChildScreen(
                onBackPressed = { navController.popBackStack() },
                onViewRecordsClicked = { childId ->
                    navController.navigate("${Routes.CHILD_VACCINATION_DETAILS_SCREEN}/$childId")
                }
            )
        }
    }
}
