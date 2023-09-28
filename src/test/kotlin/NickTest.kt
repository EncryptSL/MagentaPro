import kotlin.test.Test
import kotlin.test.asserter

class NickTest {

    @Test
    fun onNickMiniMessageTags() {
        val nickname = "<red>Alex<yellow>".replace(Regex("<*[a-zA-Z]*>"), "")
        asserter.assertSame("NickMiniMessageTagsFailed", nickname, nickname)
    }

}