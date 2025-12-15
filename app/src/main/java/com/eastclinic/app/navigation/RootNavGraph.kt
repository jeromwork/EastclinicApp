package com.eastclinic.app.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eastclinic.app.ui.RootScreen
import com.eastclinic.appointments.presentation.AppointmentsScreen
import com.eastclinic.auth.presentation.navigation.AuthRoutes
import com.eastclinic.auth.presentation.navigation.authGraph
import com.eastclinic.chat.presentation.ChatScreen
import com.eastclinic.clinics.presentation.ClinicsScreen
import com.eastclinic.doctors.presentation.DoctorsScreen
import com.eastclinic.home.presentation.HomeScreen

object RootRoutes {
    const val ROOT = "root"
    const val HOME = "home"
    const val CLINICS = "clinics"
    const val DOCTORS = "doctors"
    const val APPOINTMENTS = "appointments"
    const val CHAT = "chat"
}

@androidx.compose.runtime.Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: String = RootRoutes.ROOT
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(RootRoutes.ROOT) {
            RootScreen()
        }
        
        authGraph(
            onNavigateToHome = { route ->
                navController.navigate(route) {
                    popUpTo(RootRoutes.ROOT) { inclusive = true }
                }
            }
        )
        
        composable(RootRoutes.HOME) {
            HomeScreen()
        }
        
        composable(RootRoutes.CLINICS) {
            ClinicsScreen()
        }
        
        composable(RootRoutes.DOCTORS) {
            DoctorsScreen()
        }
        
        composable(RootRoutes.APPOINTMENTS) {
            AppointmentsScreen()
        }
        
        composable(RootRoutes.CHAT) {
            ChatScreen()
        }
    }
}
