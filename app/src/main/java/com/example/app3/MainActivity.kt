package com.example.app3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app3.auth.DetailScreen
import com.example.app3.auth.FavouriteScreen
import com.example.app3.auth.LoginScreen
import com.example.app3.auth.MainScreen
import com.example.app3.auth.NotificationScreen
import com.example.app3.auth.ProfileScreen
import com.example.app3.auth.SearchScreen
import com.example.app3.auth.SignUpScreen
import com.example.app3.auth.SuccessScreen
import com.example.app3.main.NotificationMessage
import com.example.app3.ui.theme.App3Theme
import com.facebook.CallbackManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        setContent {
            window.statusBarColor = getColor(R.color.black)
            window.navigationBarColor = getColor(R.color.black)
            App3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthenticationApp(callbackManager)
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}

sealed class DestinationScreen(val route: String) {
    object Main: DestinationScreen("main")
    object SignUp: DestinationScreen("signup")
    object Login: DestinationScreen("login")
    object Home: DestinationScreen("home")
    object Favourite: DestinationScreen("favourite")
    object Profile: DestinationScreen("profile")
    object Detail: DestinationScreen("detail/{seriesId}") {
        fun createRoute(seriesId: String) = "detail/$seriesId"
    }
    object Search: DestinationScreen("search")
    object Notification: DestinationScreen("notification")
}

@Composable
fun AuthenticationApp(callbackManager: CallbackManager) {
    val vm = hiltViewModel<FbViewModel>()
    val navController =  rememberNavController()

    NotificationMessage(vm)

    NavHost(navController = navController, startDestination = DestinationScreen.Login.route) {
        composable(DestinationScreen.Main.route) {
            MainScreen(navController, vm)
        }
        composable(DestinationScreen.SignUp.route) {
            SignUpScreen(navController, vm)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController, vm, callbackManager)
        }
        composable(DestinationScreen.Home.route) {
            SuccessScreen(navController, vm)
        }
        composable(DestinationScreen.Favourite.route) {
            FavouriteScreen(navController, vm)
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController, vm)
        }
        composable(DestinationScreen.Detail.route) { backStackEntry ->
            val seriesId = backStackEntry.arguments?.getString("seriesId")
            seriesId?.let {
                DetailScreen(navController, vm, it)
            }
        }
        composable(DestinationScreen.Search.route) {
            SearchScreen(navController, vm)
        }
        composable(DestinationScreen.Notification.route) {
            NotificationScreen(navController, vm)
        }
    }
}
