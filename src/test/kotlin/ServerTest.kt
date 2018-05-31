import junit.framework.Assert.*
import model.*
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test
import utils.HttpClient
import utils.IntegrationTestBase

class ServerTest : IntegrationTestBase() {
    override val data = listOf(
            Video(id = 1, url = "url"),
            Video(id = 2, url = "url2"),
            Video(id = 3, url = "url2", actor = "some actor"),
            Video(id = 4, url = "url2", description = "some description", keywords = "some"),
            Video(id = 5, url = "url2", keywords = "some keywords"),
            Video(id = 6, url = "url2", title = "some title", description = "some", keywords = "some"),
            Video(id = 7, url = "url2", favourite = true)
    )

    @Test
    fun `should respond with videos`() {
        val videoResult = HttpClient.get<VideoResult>("/videos")

        assertEquals(data, videoResult!!.videos)
    }

    @Test
    fun `should respond paged with videos`() {
        val videoResult = HttpClient.post<VideoResult>("/videos/paged", PagedRequest(0, 1))

        assertEquals(listOf(data.first()), videoResult!!.videos)
    }

    @Test
    fun `should favourite video`() {
        HttpClient.get<VideoResult>("/videos/favourite/1")

        val videoResult = HttpClient.get<VideoResult>("/videos")

        videoResult!!.videos.first().favourite `should be` true
    }

    @Test
    fun `should unfavourite video`() {
        HttpClient.get<VideoResult>("/videos/unfavourite/3")

        val videoResult = HttpClient.get<VideoResult>("/videos")

        videoResult!!.videos[2].favourite `should be` false
    }

    @Test
    fun `should search`() {
        val videoResult = HttpClient.post<VideoResult>("/videos/search", SearchRequest(SearchType.Id, 1))

        videoResult!!.videos `should equal` listOf(data.first())
    }
}

