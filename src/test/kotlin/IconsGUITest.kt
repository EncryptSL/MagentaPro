import org.junit.jupiter.api.Test


class IconsGUITest {

    private val config: ConfigUtil by lazy { ConfigUtil(this.javaClass.getResource("icons.yml").path) }
    private val ignoreSlots = listOf(17, 18, 26, 27, 35, 36, 44)

    @Test
    fun loadIcons() {
        config.getConfig().getStringList("icons").forEachIndexedSlots { index, s ->
            if (!ignoreSlots.contains(index)) {
                println("[${index}] - $s")
            } else {
                println("[${cal(index)}] - $s")
            }
        }
    }

    private fun cal(int: Int): Int = int.plus(2)

    private inline fun <T> Iterable<T>.forEachIndexedSlots(action: (index: Int, T) -> Unit) {
        var index = 10
        for (item in this) action(checkIndexOverflow(index++), item)
    }

    private fun checkIndexOverflow(index: Int): Int {
        return index
    }

}