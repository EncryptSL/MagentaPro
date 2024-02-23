import org.junit.jupiter.api.Test

class StringTest {

    private val config = ConfigUtil(this.javaClass.getResource("string_ip_test.yml").path)

    @Test
    fun stringTest() {
        println(config.getConfig().getString("ip-address"))
    }

}