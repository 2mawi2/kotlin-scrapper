package app

import org.amshove.kluent.`should be`
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Test

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

    @Test
    fun `should get property value`() {
        class Foo(val intValue: Int = 5)
        Foo().getPropertyValue("intValue").shouldEqual(5)
    }
}
