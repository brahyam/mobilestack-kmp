package com.getmobilestack.kmp.component

import com.arkivanov.decompose.ComponentContext

interface LoadingComponent

class DefaultLoadingComponent(
    componentContext: ComponentContext
) : LoadingComponent, ComponentContext by componentContext