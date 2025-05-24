package com.example.sit305101d.presentation.payment

import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

interface PaymentResultEventBus : SharedFlow<PaymentSheetResult> {
    fun post(event: PaymentSheetResult)
}

@Single
class PaymentResultEventBusImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val eventBus: MutableSharedFlow<PaymentSheetResult> = MutableSharedFlow()
) : PaymentResultEventBus,
    SharedFlow<PaymentSheetResult> by eventBus,
    CoroutineScope by CoroutineScope(dispatcher) {

    override fun post(event: PaymentSheetResult) {
        launch { eventBus.emit(event) }
    }
}
