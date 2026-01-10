package com.getmobilestack.kmp.model

data class Product(
    val id: String,
    val packageId: String,
    val title: String,
    val description: String,
    val price: String,
    val period: Period
) {

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

    enum class Type(val packageId: String, val entitlement: String) {
        STARTER("starter", "kmp_template"),
        ALL_IN("all_in", "kmp_template_lifetime"),
        OTHER("other", "other")
    }

    fun toType(): Type {
        return when (packageId) {
            Type.STARTER.packageId -> Type.STARTER
            Type.ALL_IN.packageId -> Type.ALL_IN
            else -> Type.OTHER
        }
    }
}

