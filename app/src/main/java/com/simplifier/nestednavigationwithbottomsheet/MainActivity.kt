package com.simplifier.nestednavigationwithbottomsheet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.simplifier.nestednavigationwithbottomsheet.ui.theme.NestedNavigationWithBottomSheetTheme
import kotlinx.coroutines.launch


val dashboardItems = listOf("settings_screen", "user_screen", "camera_screen")

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NestedNavigationWithBottomSheetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val sheetState = rememberStandardBottomSheetState(
                        initialValue = SheetValue.Hidden,
                        skipHiddenState = false
                    )

                    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)
                    val scope = rememberCoroutineScope()

                    BottomSheetScaffold(
                        scaffoldState = scaffoldState,
                        sheetContent = { BottomSheetContent() },
                        sheetSwipeEnabled = false
                    ) {
                        NavHost(navController = navController, startDestination = "login_route") {
                            navigation(startDestination = "login_screen", route = "login_route") {
                                composable(route = "login_screen") {
                                    scope.launch {
                                        sheetState.hide()
                                    }

                                    ExampleScreen(title = "Login Screen") {
                                        navController.navigate("dashboard_route") {
                                            popUpTo("login_route") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            }

                            navigation(
                                startDestination = "dashboard_screen",
                                route = "dashboard_route"
                            ) {
                                composable(route = "dashboard_screen") {
                                    scope.launch {
                                        sheetState.expand()
                                    }
                                    Dashboard {
                                        if (it == "login_route") {
                                            navController.navigate(it) {
                                                popUpTo("dashboard_route") {
                                                    inclusive = true
                                                }
                                            }
                                        } else {
                                            navController.navigate(it)
                                        }

                                    }
                                }

                                dashboardItems.forEach { items ->
                                    composable(route = items) {
                                        scope.launch {
                                            sheetState.expand()
                                        }
                                        ExampleScreen(title = items) {
                                            navController.navigate("dashboard_screen")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExampleScreen(title: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title)
        Button(onClick = { onClick() }) {
            Text(text = "Next")
        }
    }
}

@Composable
fun Dashboard(onClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn {
            items(dashboardItems) {
                Button(onClick = { onClick(it) }) {
                    Text(text = "Navigate to $it")
                }
            }

            item {
                Button(onClick = { onClick("login_route") }) {
                    Text(text = "Logout")
                }
            }
        }
    }
}

@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val tempArray = listOf("Value 1", "Value 2", "Value 3", "Value 4")

        LazyColumn {
            items(tempArray) {
                Text(text = it)
            }
        }

    }
}
