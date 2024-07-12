package co.yml.ychat.core.network.factories

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.darwin.certificates.CertificatePinner

actual object HttpEngineFactory {

    actual fun getEngine(): HttpClientEngine {
        return Darwin.create {
            // TODO: pin your certificates here
            val builder = CertificatePinner.Builder()
//                .add(
//                    "HOST",
//                    "CERT",
//                    "CERT",
//                    "CERT"
//                )
            handleChallenge(builder.build())
        }
    }
}
