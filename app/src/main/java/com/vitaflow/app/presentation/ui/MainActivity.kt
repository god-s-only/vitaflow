package com.vitaflow.app.presentation.ui

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vitaflow.app.common.Routes
import com.vitaflow.app.data.local.VitaFlowSession
import com.vitaflow.app.presentation.ui.auth.signin.SignInScreen
import com.vitaflow.app.presentation.ui.auth.signin.SignInViewModel
import com.vitaflow.app.presentation.ui.auth.signup.SignUpScreen
import com.vitaflow.app.presentation.ui.features.barcode.BarcodeScanScreen
import com.vitaflow.app.presentation.ui.features.capture.PhotoCaptureScreen
import com.vitaflow.app.presentation.ui.features.detail.ExerciseDetailScreen
import com.vitaflow.app.presentation.ui.features.exercisebodypart.ExercisesScreen
import com.vitaflow.app.presentation.ui.features.home.HomeScreen
import com.vitaflow.app.presentation.ui.features.nutrition.NutritionScreen
import com.vitaflow.app.presentation.ui.features.onboarding.OnboardingScreen
import com.vitaflow.app.presentation.ui.features.recipedetail.RecipeDetailScreen
import com.vitaflow.app.presentation.ui.features.recipestartcooking.RecipeStartCookingScreen
import com.vitaflow.app.presentation.ui.features.recipes.RecipeScreen
import com.vitaflow.app.presentation.ui.features.search.FoodSearchScreen
import com.vitaflow.app.presentation.ui.features.settings.NutritionSettingsScreen
import com.vitaflow.app.presentation.ui.features.steps.StepsSettingsScreen
import com.vitaflow.app.presentation.ui.features.steps.StepsTrackerScreenContainer
import com.vitaflow.app.presentation.ui.features.workout.WorkoutBodyPartsScreen
import com.vitaflow.app.presentation.ui.theme.VitaFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var showSplashScreen = true

    @Inject
    lateinit var vitaFlowSession: VitaFlowSession

    @RequiresApi(Build.VERSION_CODES.O)
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
                MainContent(vitaFlowSession)
            }
        }
        actionBar?.hide()

        lifecycleScope.launch {
            delay(2000)
            showSplashScreen = false
        }
    }
}

private val ROUTES_WITH_BOTTOM_BAR = setOf(
    Routes.HOMESCREEN,
    Routes.NUTRITIONSCREEN,
    Routes.WORKOUT_BODY_PARTS_SCREEN,
    Routes.STEPS_CONTAINER_SCREEN,
    Routes.PROFILESCREEN
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MainContent(vitaFlowSession: VitaFlowSession) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    val token by vitaFlowSession.getTokenFlow().collectAsState(initial = null)
    val startDestination = remember(token) {
        if (!token.isNullOrEmpty()) Routes.HOMESCREEN else Routes.SIGNINSCREEN
    }

    val shouldShowBottomBar = remember(currentRoute?.destination?.route) {
        currentRoute?.destination?.route in ROUTES_WITH_BOTTOM_BAR
    }

    LaunchedEffect(startDestination) {
        if (!token.isNullOrEmpty() && currentRoute?.destination?.route == Routes.SIGNINSCREEN) {
            navController.navigate(Routes.HOMESCREEN) {
                popUpTo(Routes.SIGNINSCREEN) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
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
            composable(
                route = Routes.FOODSEARCHSCREEN + "/{mealType}",
                arguments = listOf(navArgument("mealType") { type = NavType.StringType })
            ) { backStackEntry ->
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
            composable(Routes.RECIPES_DETAIL_SCREEN + "/{recipeId}") {
                RecipeDetailScreen(navController = navController)
            }
            composable(Routes.RECIPE_START_COOKING + "/{recipeId}") {
                RecipeStartCookingScreen(navController = navController)
            }
            composable(Routes.STEPS_CONTAINER_SCREEN) {
                StepsTrackerScreenContainer(navController = navController)
            }
            composable(Routes.WORKOUT_BODY_PARTS_SCREEN) {
                WorkoutBodyPartsScreen(navController = navController)
            }
            composable(
                Routes.WORKOUT_BODY_PARTS_SELECTED_SCREEN + "/{bodyPart}",
                arguments = listOf(navArgument("bodyPart") { type = NavType.StringType })
            ) {
                val bodyPart = it.arguments?.getString("bodyPart") ?: ""
                ExercisesScreen(bodyPart)
            }
            composable(Routes.STEPS_SETTINGS_SCREEN) {
                StepsSettingsScreen(navController = navController)
            }
            composable(Routes.PHOTO_CAPTURE) {
                PhotoCaptureScreen(navController = navController)
            }

        }

        AnimatedBottomBar(
            visible = shouldShowBottomBar,
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AnimatedBottomBar(
    visible: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 100.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bottom_bar_offset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "bottom_bar_alpha"
    )

    if (visible || offsetY < 100.dp) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .graphicsLayer { this.alpha = alpha }
                .windowInsetsPadding(WindowInsets.navigationBars),
            color = Color.White,
            shadowElevation = 16.dp,
            tonalElevation = 8.dp
        ) {
            ProfessionalBottomBarContent(navController)
        }
    }
}

@Composable
private fun ProfessionalBottomBarContent(navController: NavController) {
    val currentRoute by navController.currentBackStackEntryAsState()
    val currentDestination = currentRoute?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = when (item.route) {
                Routes.HOMESCREEN -> currentDestination == Routes.HOMESCREEN
                Routes.NUTRITIONSCREEN -> currentDestination == Routes.NUTRITIONSCREEN
                Routes.WORKOUT_BODY_PARTS_SCREEN -> currentDestination == Routes.WORKOUT_BODY_PARTS_SCREEN
                Routes.STEPS_CONTAINER_SCREEN -> currentDestination == Routes.STEPS_CONTAINER_SCREEN
                Routes.PROFILESCREEN -> currentDestination == Routes.PROFILESCREEN
                else -> false
            }

            BottomNavItem(
                item = item,
                isSelected = isSelected,
                onClick = {
                    if (currentDestination != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
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
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00C853) else Color.Transparent,
        animationSpec = tween(durationMillis = 300),
        label = "nav_bg_color"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Gray,
        animationSpec = tween(durationMillis = 300),
        label = "nav_icon_tint"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00C853) else Color.Gray,
        animationSpec = tween(durationMillis = 300),
        label = "nav_text_color"
    )

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Container
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(22.dp),
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Outlined.Home, Routes.HOMESCREEN),
    BottomNavItem("Nutrition", Icons.Outlined.FavoriteBorder, Routes.NUTRITIONSCREEN),
    BottomNavItem("Workout", Icons.Default.Star, Routes.WORKOUT_BODY_PARTS_SCREEN),
    BottomNavItem("Steps", Icons.Default.Menu, Routes.STEPS_CONTAINER_SCREEN),
)