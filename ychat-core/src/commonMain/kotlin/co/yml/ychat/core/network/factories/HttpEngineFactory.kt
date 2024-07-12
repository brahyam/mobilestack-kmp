package co.yml.ychat.core.network.factories

import io.ktor.client.engine.HttpClientEngine

expect object HttpEngineFactory {
    fun getEngine(): HttpClientEngine
}
