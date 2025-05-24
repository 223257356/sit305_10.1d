package com.example.sit305101d.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.sit305101d.presentation.navigation.AppNavHost
import com.example.sit305101d.presentation.payment.LocalPaymentSheet
import com.example.sit305101d.presentation.payment.PaymentResultCallback
import com.example.sit305101d.presentation.theme.SIT305101DTheme
import com.stripe.android.paymentsheet.PaymentSheet
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val paymentResultCallback by inject<PaymentResultCallback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val paymentSheet = PaymentSheet(this, paymentResultCallback)
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalPaymentSheet provides paymentSheet) {
                SIT305101DTheme {
                    Surface(
                        modifier = Modifier.Companion.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        AppNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
