package utils

import scrapper.main
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import model.Video
import org.dizitart.kno2.getRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import persistence.DbProvider

abstract class IntegrationTestBase {
    abstract val data: List<Video>

    @Before
    internal fun setUp() {
        DbProvider(isDevelopment = true).get().use { db ->
            db.getRepository<Video>().remove(ObjectFilters.ALL)
            data.forEach { db.getRepository<Video>().insert(it) }
        }
    }

    companion object {
        lateinit var server: Job

        @BeforeClass
        @JvmStatic
        fun setupAll() {
            server = launch { main(arrayOf("development")) }
        }

        @AfterClass
        @JvmStatic
        fun tearDownAll() {
            server.cancel()
        }
    }
}