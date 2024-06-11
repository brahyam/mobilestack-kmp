@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package co.yml.ychat.core.model

actual typealias FileBytes = ByteArray

actual fun FileBytes.toByteArray(): ByteArray {
    return this
}
