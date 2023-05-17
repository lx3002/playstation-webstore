package com.example.webstore

import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.R
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.painterResource

import com.example.webstore.Seller.VendorDashboard
import com.example.webstore.Seller.ViewInventory
import kotlinx.coroutines.delay

class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "splash_screen" ){
//                composables in the nav graph
                composable("splash_screen"){
                    SplashScreen(navController = navController)
                }

                //select screen
                composable("select_screen"){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(), contentAlignment = Alignment.Center )
                    {
                        
                        Column {
                            Text(text = "Select the user type", color = Color.Black, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(5.dp))
                            Button(onClick = { startTheVendorActivity()}) {
                                Text(text = "Vendor", color = Color.Black)

                            }



                            Button(onClick = { startTheClientActivity()}) {
                                Text(text = "Shopper")

                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SplashScreen(navController: NavController){
        val scale  = remember {
            androidx.compose.animation.core.Animatable(0f)
        }
        //animation effect
        LaunchedEffect(key1 = true){
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = {
                        OvershootInterpolator(4f).getInterpolation(it)
                    }
                )
            )
            delay(3000L)
            // after time elapses
            navController.navigate("select_screen")
        }
    }

    private fun startTheClientActivity() {
        val intent = Intent(this,ViewInventory::class.java)
        startActivity(intent)


    }

    private fun startTheVendorActivity() {
        val intent = Intent(this,MainActivity::class.java)
       startActivity(intent)

    }

    @Composable
    fun Splashscreen(navController: NavController) {
        val scale = remember {
            androidx.compose.animation.core.Animatable(0f)
        }
        //animation effect
        LaunchedEffect(key1 = true) {
            scale.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = {
                        OvershootInterpolator(4f).getInterpolation(it)
                    }
                )
            )
            delay(3000L)
            // after time elapses
            navController.navigate("select_screen")
        }
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black),
            contentAlignment = Alignment.Center
        ) {

        }
    }
}
    
    
    
    
    
    
    
    
    
    




