import scrapper.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import junit.framework.Assert.*
import org.amshove.kluent.`should be greater than`
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should equal`
import org.junit.Test

class BlogAppKtTest : IntegrationTestBase() {
    override val data = listOf(
            Video(id = 1, url = "url"),
            Video(id = 2, url = "url2"),
            Video(id = 3, url = "url2", actor = "some actor"),
            Video(id = 4, url = "url2", description = "some description"),
            Video(id = 5, url = "url2", keywords = "some keywords"),
            Video(id = 6, url = "url2", title = "some title"),
            Video(id = 7, url = "url2", favourite = true)
    )

    @Test
    fun `should respond with videos`() {
        val (_, _, json) = "http://localhost:5000/videos".httpGet().responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        assertEquals(data, videoResult.videos)
    }

    @Test
    fun `should respond paged with videos`() {
        val (_, _, json) = "http://localhost:5000/videos/paged"
                .httpPost()
                .body(PagedRequest(0, 1).toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        assertEquals(listOf(data.first()), videoResult.videos)
    }

    @Test
    fun `should favourite video`() {
        "http://localhost:5000/videos/favourite/1".httpGet().responseString()

        val (_, _, json) = "http://localhost:5000/videos".httpGet().responseString()
        val videoResult = json.get().fromJson<VideoResult>()

        videoResult.videos.first().favourite `should be` true
    }

    @Test
    fun `should unfavourite video`() {
        "http://localhost:5000/videos/unfavourite/3".httpGet().responseString()

        val (_, _, json) = "http://localhost:5000/videos".httpGet().responseString()
        val videoResult = json.get().fromJson<VideoResult>()

        videoResult.videos[2].favourite `should be` false
    }

    @Test
    fun `should search by id`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.Id, 1).toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        videoResult.videos `should equal` listOf(data.first())
    }

    @Test
    fun `should search by favourite`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.Favourite, true).toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()
        videoResult.videos.count() `should be greater than` 0
        videoResult.videos.forEach { it.favourite `should be` true }
    }


    private fun assertHasProperty(videos: List<Video>, expected: String, selector: (Video) -> Any) {
        assertTrue(videos.any())
        videos.forEach { assertEquals(expected, selector(it)) }
    }

    @Test
    fun `should search by actor`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.Actor, "some actor").toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        assertHasProperty(videoResult.videos, "some actor") { it.actor }
    }

    @Test
    fun `should search by description`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.Description, "some description").toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        assertHasProperty(videoResult.videos, "some description") { it.description }
    }

    @Test
    fun `should search by keywords`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.Keywords, "some keywords").toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        assertHasProperty(videoResult.videos, "some keywords") { it.keywords }
    }

    @Test
    fun `should search by title`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.Title, "some title").toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()


    }

    @Test
    fun `should check for contains while searching`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.Title, "title").toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        assertHasProperty(videoResult.videos, "some title") { it.title }
    }


    @Test
    fun `should search by all`() {
        val (_, _, json) = "http://localhost:5000/videos/search"
                .httpPost()
                .body(SearchRequest(SearchType.All, "some").toJson())
                .responseString()

        val videoResult = json.get().fromJson<VideoResult>()

        assertTrue(videoResult.videos.any())
        videoResult.videos.map { it.id }.forEach { assertTrue(listOf(3, 4, 5, 6).contains(it)) }
    }


}

