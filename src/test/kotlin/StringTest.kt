
import com.github.encryptsl.magenta.common.extensions.evaluate
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class StringTest {

    private val config = ConfigUtil(this.javaClass.getResource("string_ip_test.yml").path)

    @Test
    fun expressionFormulaTest() {
        println(config.getConfig().getString("expression-formula", null))

        val i = config.getConfig().getString("expression-formula").toString()
            .replace("{level}", "1")
            .replace("{money}", "500").replace("{value}", "30")
        println(evaluate(i).toInt().toString())
    }

    @Test
    fun stringTest() {
        println(config.getConfig().getString("ip-address"))
    }

    @Test
    fun timeTest() {
        val date = Date(1645597200000L)

        val from = date.toInstant()

        val localDateTime: LocalDateTime = from.atZone(ZoneId.systemDefault()).toLocalDateTime()

        val a = DateTimeFormatter.ofPattern("eeee, dd. MMMM yyyy HH:mm:ss",  Locale.forLanguageTag("cs")).format(localDateTime)
        println(a)
    }

    @Test
    fun censoring_ip_test() {
        val oktety = "157.81.248.78".split(".")
        if (oktety.size != 4) {
            throw IllegalArgumentException("Not valid IP Address")
        }
        println("${oktety[0]}.${oktety[1]}.XXX.xxx")
    }

}