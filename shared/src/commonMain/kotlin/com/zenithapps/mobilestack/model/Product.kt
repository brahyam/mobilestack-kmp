package com.zenithapps.mobilestack.model

sealed interface Product {
    val id: String
    val packageId: String
    val title: String
    val description: String
    val price: String
    val period: Period

    sealed interface Period {
        data object Lifetime : Period
        data class Duration(val value: Int, val unit: PeriodUnit) : Period
    }

    enum class PeriodUnit {
        DAY,
        WEEK,
        MONTH,
        YEAR,
        UNKNOWN,
    }

    // TODO: Replace these with your products
    data class Starter(
        override val id: String,
        override val title: String,
        override val description: String,
        override val price: String,
    ) : Product {
        override val packageId = ID
        override val period = Period.Lifetime

        companion object {
            const val ID = "starter"
        }
    }

    data class AllIn(
        override val id: String,
        override val title: String,
        override val description: String,
        override val price: String
    ) : Product {
        override val packageId = ID
        override val period = Period.Lifetime

        companion object {
            const val ID = "all-in"
        }
    }

    data class Other(
        override val id: String,
        override val packageId: String,
        override val title: String,
        override val description: String,
        override val price: String,
        override val period: Period
    ) : Product
}

