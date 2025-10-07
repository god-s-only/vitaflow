package com.vitaflow.app.presentation.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black
                ),
                title = {
                    Text(
                        text = "Vita Flow",
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Greeting Section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Hello,",
                    fontSize = 24.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text =  "User",
                    fontSize = 32.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            // Date and Refresh Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "3 days ago",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { /* Handle refresh */ }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Health Metrics Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HealthMetricCard(
                        title = "Heart Rate",
                        value = "78",
                        unit = "bpm",
                        backgroundColor = Color(0xFFFCE4EC),
                        iconColor = Color(0xFFE91E63),
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricCard(
                        title = "Exercise",
                        value = "24",
                        unit = "min",
                        backgroundColor = Color(0xFFE8EAF6),
                        iconColor = Color(0xFF3F51B5),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bottom Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HealthMetricCard(
                        title = "Walking",
                        value = "10",
                        unit = "km",
                        backgroundColor = Color(0xFFE0F2F1),
                        iconColor = Color(0xFF00897B),
                        modifier = Modifier.weight(1f)
                    )
                    HealthMetricCard(
                        title = "Sleep",
                        value = "8",
                        unit = "hrs",
                        backgroundColor = Color(0xFFE3F2FD),
                        iconColor = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom navigation
        }
    }
}

@Composable
fun HealthMetricCard(
    title: String,
    value: String,
    unit: String,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(iconColor)
                )
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // Value and Unit
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 32.sp,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = unit,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray,
        modifier = Modifier.height(80.dp)
    ) {
        // Home
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            selected = true,
            onClick = { /* Handle home click */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1A1A1A),
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // History
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "History",
                    modifier = Modifier.size(24.dp)
                )
            },
            selected = false,
            onClick = { /* Handle history click */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1A1A1A),
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Notifications
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(24.dp)
                )
            },
            selected = false,
            onClick = { /* Handle notifications click */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1A1A1A),
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Profile
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp)
                )
            },
            selected = false,
            onClick = { /* Handle profile click */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF1A1A1A),
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}