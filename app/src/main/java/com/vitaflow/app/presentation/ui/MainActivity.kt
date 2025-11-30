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
import androidx.compose.foundation.clickable
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
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vitaflow.app.common.Routes
import com.vitaflow.app.presentation.ui.auth.signin.SignInScreen
import com.vitaflow.app.presentation.ui.auth.signup.SignUpScreen
import com.vitaflow.app.presentation.ui.features.barcode.BarcodeScanScreen
import com.vitaflow.app.presentation.ui.features.detail.ExerciseDetailScreen
import com.vitaflow.app.presentation.ui.features.home.HomeScreen
import com.vitaflow.app.presentation.ui.features.nutrition.NutritionScreen
import com.vitaflow.app.presentation.ui.features.onboarding.OnboardingScreen
import com.vitaflow.app.presentation.ui.features.recipes.RecipeScreen
import com.vitaflow.app.presentation.ui.features.search.FoodSearchScreen
import com.vitaflow.app.presentation.ui.features.search.FoodSearchViewModel
import com.vitaflow.app.presentation.ui.features.settings.NutritionSettingsScreen
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
            setKeepOnScreenCondition { showSplashScreen }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_X, 0.5f, 0f)
                val zoomY = ObjectAnimator.ofFloat(screen.iconView, View.SCALE_Y, 0.5f, 0f)
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd { screen.remove() }
                zoomX.start()
                zoomY.start()
            }
        }

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {
            VitaFlowTheme {
                MainContent()
            }
        }
        actionBar?.hide()

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            showSplashScreen = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent() {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    val shouldShowBottomBar = currentRoute?.destination?.route?.let { route ->
        route != Routes.SIGNINSCREEN &&
                route != Routes.FOODSEARCHSCREEN + "/{mealType}" &&
                route != Routes.SIGNUPSCREEN &&
                route != Routes.BARCODE_SCREEN + "/{mealType}" &&
                route != Routes.ONBOARDINGSCREEN &&
                route != Routes.RECIPES_SCREEN &&
                route != Routes.NUTRITIONSCREENSETTINGS &&
                !route.startsWith(Routes.WORKOUTSCREEN)
    } ?: false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.RECIPES_SCREEN,
            modifier = Modifier.fillMaxSize(),
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
            }
        ) {
            composable(Routes.SIGNINSCREEN) {
                SignInScreen(navController)
            }
            composable(Routes.SIGNUPSCREEN) {
                SignUpScreen(navController)
            }
            composable(Routes.HOMESCREEN) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (shouldShowBottomBar) 80.dp else 0.dp)
                ) {
                    HomeScreen(navController)
                }
            }
            composable(Routes.NUTRITIONSCREEN) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (shouldShowBottomBar) 80.dp else 0.dp)
                ) {
                    NutritionScreen(navController)
                }
            }
            composable(Routes.WORKOUTSCREEN + "/{exerciseId}") {
                ExerciseDetailScreen(navController = navController)
            }
            composable(
                route = Routes.FOODSEARCHSCREEN + "/{mealType}",
                arguments = listOf(
                    navArgument("mealType"){
                        type = NavType.StringType
                    }
                )
            ){ backStackEntry ->
                val mealType = backStackEntry.arguments?.getString("mealType") ?: "breakfast"
                FoodSearchScreen(navController = navController, mealType)
            }
            composable(Routes.NUTRITIONSCREENSETTINGS) {
                NutritionSettingsScreen(navController = navController)
            }
            composable(Routes.ONBOARDINGSCREEN) {
                OnboardingScreen(navController = navController)
            }
            composable(Routes.BARCODE_SCREEN + "/{mealType}") { backStackEntry ->
                val mealType = backStackEntry.arguments?.getString("mealType") ?: "breakfast"
                BarcodeScanScreen(navController, mealType)
            }
            composable(Routes.RECIPES_SCREEN) {
                RecipeScreen(navController = navController)
            }
        }

        if (shouldShowBottomBar) {
            ProfessionalBottomBar(
                navController = navController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ProfessionalBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color.White,
        shadowElevation = 16.dp,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = when (item.title) {
                    "Home" -> currentDestination == Routes.HOMESCREEN
                    "Nutrition" -> currentDestination == Routes.NUTRITIONSCREEN
                    "Workout" -> currentDestination?.startsWith("workout_list") == true
                    "Articles" -> currentDestination?.startsWith("articles") == true
                    "Profile" -> currentDestination?.startsWith("profile") == true
                    else -> false
                }

                BottomNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = {
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
                            // TODO: Add other navigation cases
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isSelected) Color.Black else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(20.dp),
                tint = if (isSelected) Color.White else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.title,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Outlined.Home),
    BottomNavItem("Nutrition", Icons.Outlined.FavoriteBorder),
    BottomNavItem("Workout", Icons.Default.Star),
    BottomNavItem("Articles", Icons.Default.Menu),
    BottomNavItem("Profile", Icons.Outlined.Person)
)