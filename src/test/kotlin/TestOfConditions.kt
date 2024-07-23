import org.junit.jupiter.api.Test

class TestOfConditions {

    private val default: Int = 3
    private val createdHomesCount: Int = 3

    @Test
    fun testConditions() {
        val boolean = !(createdHomesCount >= default)
        println(boolean)
    }

}