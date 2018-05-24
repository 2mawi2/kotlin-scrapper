import app.second
import app.third
import org.amshove.kluent.`should be`
import org.testng.annotations.Test

class UtilsKtTest {
    @Test
    fun `second should return second element of list`() {
        val result = listOf("first", "second", "third").second()
        result `should be` "second"
    }


    @Test
    fun `third should return third element of list`() {
        val result = listOf("first", "second", "third").third()
        result `should be` "third"
    }
}
