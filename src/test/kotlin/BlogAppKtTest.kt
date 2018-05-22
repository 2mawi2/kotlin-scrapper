import blog.Video
import blog.VideoResult
import blog.fromJson
import com.github.kittinunf.fuel.httpGet
import org.amshove.kluent.`should equal`
import org.junit.Test

class BlogAppKtTest : IntegrationTestBase() {
    override val data = listOf(
            Video(id = 1, url = "url"),
            Video(id = 2, url = "url2")
    )

    @Test
    fun `should respond with videos`() {
        val (_, _, json) = "http://localhost:5000/".httpGet().responseString()
        val videoResult = json.get().fromJson<VideoResult>()

        videoResult.videos `should equal` data
    }

}

