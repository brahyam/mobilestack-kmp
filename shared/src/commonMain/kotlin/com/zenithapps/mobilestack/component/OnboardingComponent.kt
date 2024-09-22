package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.OnboardingComponent.Model
import com.zenithapps.mobilestack.component.OnboardingComponent.Output
import com.zenithapps.mobilestack.provider.AnalyticsProvider
import com.zenithapps.mobilestack.provider.BillingProvider
import com.zenithapps.mobilestack.provider.KeyValueStorageProvider
import com.zenithapps.mobilestack.util.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

interface OnboardingComponent {

    val model: Value<Model>

    data class Model(
        val currentStep: Int = 0,
        val steps: List<Step> = Step.entries,
        val socialProofInteractions: Int = 0,
        val shouldShowPaywall: Boolean
    )

    enum class Step {
        WELCOME,
        STEP_1,
        STEP_2,
        STEP_3,
        STEP_4
    }

    fun onPageSelected(position: Int)
    fun onSkipTap()
    fun onNextTap()
    fun onBackTap()

    sealed interface Output {
        data object Finished : Output
    }
}

private const val SCREEN_NAME = "onboarding"

class DefaultOnboardingComponent(
    componentContext: ComponentContext,
    private val keyValueStorageProvider: KeyValueStorageProvider,
    private val analyticsProvider: AnalyticsProvider,
    private val billingProvider: BillingProvider,
    private val onOutput: (Output) -> Unit
) : OnboardingComponent, ComponentContext by componentContext {

    override val model = MutableValue(Model(shouldShowPaywall = billingProvider.isConfigured))

    init {
        val startDate = LocalDate(2024, 7, 1)
        val currentDate = now()
        val daysBetween = startDate.daysUntil(currentDate.date)
        model.value = model.value.copy(socialProofInteractions = daysBetween)
    }

    override fun onPageSelected(position: Int) {
        model.value = model.value.copy(currentStep = position)
    }

    override fun onSkipTap() {
        keyValueStorageProvider.setBoolean("onboarding_completed", true)
        analyticsProvider.logEvent(
            eventName = "onboarding_skipped",
            screenName = SCREEN_NAME,
            params = emptyMap()
        )
        onOutput(Output.Finished)
    }

    override fun onNextTap() {
        if (model.value.currentStep == OnboardingComponent.Step.STEP_4.ordinal) {
            keyValueStorageProvider.setBoolean("onboarding_completed", true)
            analyticsProvider.logEvent(
                eventName = "onboarding_completed",
                screenName = SCREEN_NAME,
                params = emptyMap()
            )
            onOutput(Output.Finished)
        } else {
            model.value = model.value.copy(currentStep = model.value.currentStep + 1)
        }
    }

    override fun onBackTap() {
        if (model.value.currentStep > 0) {
            model.value = model.value.copy(currentStep = model.value.currentStep - 1)
        }
    }
}