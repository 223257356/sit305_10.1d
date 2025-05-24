package com.example.sit305101d.presentation.screens.upgrade

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sit305101d.presentation.payment.LocalPaymentSheet
import com.example.sit305101d.presentation.payment.StripeUiEvent
import com.example.sit305101d.presentation.payment.StripeViewModel
import com.example.sit305101d.presentation.theme.SIT305101DTheme
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.koin.androidx.compose.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun UpgradeScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: UpgradeViewModel = koinViewModel(),
    stripeViewModel: StripeViewModel = koinViewModel()
) {
    val state = viewModel.collectAsState().value
    viewModel.collectSideEffect {
        when (it) {
            UpgradeScreenEvent.NavigateBack -> onNavigateBack()
            is UpgradeScreenEvent.OnPayClicked -> stripeViewModel.onEvent(
                StripeUiEvent.CheckOut(it.sub.price, "usd")
            )
        }
    }
    val context = LocalContext.current
    val paymentSheet = LocalPaymentSheet.current

    val stripeState = stripeViewModel.collectAsState().value
    stripeViewModel.collectSideEffect {
        when (it) {
            is StripeUiEvent.OnPaymentPresented -> {
                paymentSheet.presentWithPaymentIntent(
                    it.secret,
                    it.configuration
                )
            }

            is StripeUiEvent.OnResultCallback -> {
                when (val result = it.paymentSheetResult) {
                    PaymentSheetResult.Canceled -> Toast.makeText(
                        context,
                        "Payment Canceled",
                        Toast.LENGTH_SHORT
                    ).show()

                    PaymentSheetResult.Completed -> Toast.makeText(
                        context,
                        "Payment Success",
                        Toast.LENGTH_SHORT
                    ).show()

                    is PaymentSheetResult.Failed -> Toast.makeText(
                        context,
                        "Payment Failed: ${result.error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            else -> Unit
        }
    }
    UpgradeScreen(
        state = state,
        modifier = modifier,
        onEvent = viewModel::onEvent
    )
    AnimatedVisibility(stripeState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpgradeScreen(
    state: UpgradeScreenState,
    modifier: Modifier = Modifier,
    onEvent: (UpgradeScreenEvent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Upgrade your experience",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(UpgradeScreenEvent.NavigateBack)
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.packages.forEach {
                SubscriptionPackCard(it, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun SubscriptionPackCard(
    data: SubsPackage,
    modifier: Modifier = Modifier,
    onEvent: (UpgradeScreenEvent) -> Unit = {},
) {
    val goldColor = Color(0xFFFFD700)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (data.isHighlighted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (data.isHighlighted) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (data.isHighlighted) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Text(
                text = data.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (data.isHighlighted) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Text(
                text = "$${data.price}",
                style = MaterialTheme.typography.headlineMedium,
                color = goldColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            data.features.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (data.isHighlighted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (data.isHighlighted) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onEvent(UpgradeScreenEvent.OnPayClicked(data))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (data.isHighlighted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
            ) {
                Text(
                    "Pay with Stripe",
                    color = if (data.isHighlighted) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSecondary
                    }
                )
            }
        }
    }
}

@Composable
@Preview
private fun UpgradeScreenPreview() {
    SIT305101DTheme {
        UpgradeScreen(
            state = UpgradeScreenState(
                packages = SubsPackage.mocked()
            )
        )
    }
}
