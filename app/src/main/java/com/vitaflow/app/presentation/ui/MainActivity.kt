package com.vitaflow.app.presentation.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vitaflow.app.common.Routes
import com.vitaflow.app.presentation.ui.auth.signin.SignInScreen
import com.vitaflow.app.presentation.ui.auth.signup.SignUpScreen
import com.vitaflow.app.presentation.ui.features.detail.ExerciseDetailScreen
import com.vitaflow.app.presentation.ui.features.home.HomeScreen
import com.vitaflow.app.presentation.ui.features.nutrition.NutritionScreen
import com.vitaflow.app.presentation.ui.theme.VitaFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var showSplashScreen = true
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                showSplashScreen
            }
            setOnExitAnimationListener{ screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.5f,
                    0f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomX.start()
                zoomY.start()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VitaFlowTheme {
                val navController = rememberNavController()
                val currentRoute by navController.currentBackStackEntryAsState()

                // Check if current route should show bottom bar
                val shouldShowBottomBar = currentRoute?.destination?.route?.let { route ->
                    route != Routes.SIGNINSCREEN &&
                            route != Routes.SIGNUPSCREEN &&
                            !route.startsWith(Routes.WORKOUTSCREEN)
                } ?: false

                Scaffold(
                    bottomBar = {
                        if (shouldShowBottomBar) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.SIGNINSCREEN,
                        modifier = if (shouldShowBottomBar) {
                            Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                        } else {
                            Modifier
                        },
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            ) + fadeOut(animationSpec = tween(300))
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            ) + fadeOut(animationSpec = tween(300))
                        }) {
                        composable(Routes.SIGNINSCREEN) {
                            SignInScreen(navController)
                        }
                        composable(Routes.SIGNUPSCREEN) {
                            SignUpScreen(navController)
                        }
                        composable(Routes.HOMESCREEN) {
                            HomeScreen(navController)
                        }
                        composable(Routes.NUTRITIONSCREEN) {
                            NutritionScreen(navController)
                        }
                        composable(Routes.WORKOUTSCREEN + "/{exerciseId}") {
                            ExerciseDetailScreen(navController = navController)
                        }
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            showSplashScreen = false
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp,
        tonalElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEachIndexed { index, item ->
                val isSelected = when (item.title) {
                    "Home" -> currentDestination == Routes.HOMESCREEN
                    "Nutrition" -> currentDestination == Routes.NUTRITIONSCREEN
                    "Workout" -> currentDestination?.startsWith("workout_list") == true
                    "Articles" -> currentDestination?.startsWith("articles") == true
                    "Profile" -> currentDestination?.startsWith("profile") == true
                    else -> false
                }

                NavigationBarItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (isSelected) Color.Black else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp),
                                tint = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        // Handle navigation based on item
                        when (item.title) {
                            "Home" -> {
                                if (currentDestination != Routes.HOMESCREEN) {
                                    navController.navigate(Routes.HOMESCREEN) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }

                            "Nutrition" -> {
                                if (currentDestination != Routes.NUTRITIONSCREEN) {
                                    navController.navigate(Routes.NUTRITIONSCREEN) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }

                            "Workout" -> {
                                // TODO: Navigate to workout list screen
                                // navController.navigate("workout_list")
                            }

                            "Articles" -> {
                                // TODO: Navigate to articles screen
                                // navController.navigate("articles")
                            }

                            "Profile" -> {
                                // TODO: Navigate to profile screen
                                // navController.navigate("profile")
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Transparent,
                        unselectedIconColor = Color.Transparent,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent,
                        disabledIconColor = Color.Gray,
                        disabledTextColor = Color.Gray
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

// Data class for bottom navigation items
data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

// Bottom navigation items with Nutrition instead of Search
val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Outlined.Home),
    BottomNavItem("Nutrition", Icons.Outlined.FavoriteBorder),
    BottomNavItem("Workout", Icons.Default.Star),
    BottomNavItem("Articles", Icons.Default.Menu),
    BottomNavItem("Profile", Icons.Outlined.Person)
)