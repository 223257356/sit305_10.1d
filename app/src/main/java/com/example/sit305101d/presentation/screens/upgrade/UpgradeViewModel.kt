package com.example.sit305101d.presentation.screens.upgrade

import androidx.lifecycle.ViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.koin.android.annotation.KoinViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

@KoinViewModel
class UpgradeViewModel : ContainerHost<UpgradeScreenState, UpgradeScreenEvent>, ViewModel() {
    override val container: Container<UpgradeScreenState, UpgradeScreenEvent> =
        container(UpgradeScreenState())

    fun onEvent(event: UpgradeScreenEvent) = intent {
        when (event) {
            UpgradeScreenEvent.NavigateBack -> postSideEffect(event)
            is UpgradeScreenEvent.OnPayClicked -> postSideEffect(event)
        }
    }
}

data class UpgradeScreenState(
    val packages: PersistentList<SubsPackage> = SubsPackage.mocked()
)

data class SubsPackage(
    val title: String,
    val description: String,
    val price: Float,
    val isHighlighted: Boolean,
    val features: PersistentList<String>,
) {
    companion object {
        fun mocked() = persistentListOf(
            SubsPackage(
                title = "Expert",
                description = "Full premium experience",
                price = 14.99f,
                isHighlighted = true,
                features = persistentListOf(
                    "All Pro features",
                    "Expert-level quizzes",
                    "Personalized learning path",
                    "1-on-1 mentoring session",
                    "Certificate of completion",
                    "24/7 priority support"
                )
            ),
            SubsPackage(
                title = "Pro",
                description = "Advanced features unlocked",
                price = 9.99f,
                isHighlighted = false,
                features = persistentListOf(
                    "All Starter features",
                    "Advanced quiz topics",
                    "Detailed performance analytics",
                    "Priority email support",
                )
            ),
            SubsPackage(
                title = "Starter",
                description = "Improved Quiz generation",
                price = 4.99f,
                isHighlighted = false,
                features = persistentListOf(
                    "Access to basic quiz topics",
                    "Weekly new quizzes",
                    "Basic performance tracking",
                    "Email support"
                )
            ),
        )
    }
}

sealed interface UpgradeScreenEvent {
    data object NavigateBack : UpgradeScreenEvent
    data class OnPayClicked(val sub: SubsPackage) : UpgradeScreenEvent
}
