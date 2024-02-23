
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class StringTest {

    private val config = ConfigUtil(this.javaClass.getResource("string_ip_test.yml").path)

    @Test
    fun stringTest() {
        println(config.getConfig().getString("ip-address"))
    }

    @Test
    fun timeTest() {
        val date = Date(1645597200000L)

        val from = date.toInstant()

        val localDateTime: LocalDateTime = from.atZone(ZoneId.systemDefault()).toLocalDateTime()

        val a = DateTimeFormatter.ofPattern("MM-dd hh:mm yyyy").format(localDateTime)
        println(a)
    }

}