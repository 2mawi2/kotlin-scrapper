package persistence

import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import app.VIDEOS_DB
import app.VIDEOS_TEST_DB

interface IDbProvider {
    fun get(): Nitrite
}

class DbProvider(private val isDevelopment: Boolean) : IDbProvider {
    override fun get(): Nitrite {
        return nitrite {
            path = if (isDevelopment) VIDEOS_TEST_DB else VIDEOS_DB
            autoCommitBufferSize = 2048
            compress = true
            autoCompact = false
        }
    }
}