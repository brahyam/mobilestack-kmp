package co.yml.ychat.core.network.factories

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

actual object HttpEngineFactory {
    actual fun getEngine(): HttpClientEngine {
        return OkHttp.create {
            preconfigured = getOkHttpClient()
        }
    }
}

private fun getOkHttpClient(): OkHttpClient {
    val certificatePinner = CertificatePinner.Builder()
        // TODO: Pin your certificates here
//        .add(
//            pattern = "HOST",
//            pins = arrayOf(
//                "CERT",
//                "CERT",
//                "CERT"
//            )
//        )
        .build()
    return OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .build()
}
