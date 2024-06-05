
import com.github.encryptsl.magenta.common.extensions.evaluate
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.max
import kotlin.math.min


class StringTest {

    private val config = ConfigUtil(this.javaClass.getResource("string_ip_test.yml").path)

    fun getProgressBar(current: Int, max: Int, totalBars: Int): String {
        if (current < 0 || current > max || max <= 0 || totalBars <= 0) {
            throw IllegalArgumentException("Invalid progress bar parameters")
        }

        val progressRatio = current.toDouble() / max
        val completedBars = (progressRatio * totalBars).toInt()

        val completedBar = CharArray(completedBars) { '⛆' }
        val remainingBar = CharArray(totalBars - completedBars) { '⛆' }

        return "${completedBar.concatToString()}${remainingBar.concatToString()}"
    }
    @Test
    fun progressBarTest() {
        // Example usage
        val currentProgress = 42
        val maxValue = 100
        val totalProgressBarBars = 20
        val progressBarString = getProgressBar(currentProgress, maxValue, totalProgressBarBars)
        println(progressBarString)
    }

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
    fun testLevenshsteinDistance() {
        println(similarityScore("test test test spammm spammm", "test test test spammm kkt kkt idiot"))
    }

    private fun levenshteinDistance(str1: String, str2: String) : Int {
        val matrix = Array(str1.length + 1) {
            Array(str2.length + 1) { 0 }
        }

        for (i in 0..str1.length) {
            matrix[i][0] = i
        }

        for (i in 0..str2.length) {
            matrix[0][i] = i
        }

        for (i in 1..str1.length) {
            for (j in 1..str2.length) {
                if (str1[i - 1] == str2[j - 1]) {
                    matrix[i][j] = matrix[i - 1][j - 1]
                } else {
                    matrix[i][j] = min(min(matrix[i - 1][j], matrix[i][j - 1]), matrix[i - 1][j - 1]) + 1
                }
            }
        }

        return matrix[str1.length][str2.length]
    }

    private fun similarityScore(str1: String, str2: String): Int {
        val levenshteinDistance = levenshteinDistance(str1, str2)
        val longerStringLength = max(str1.length, str2.length)

        val result: Double = 1 - levenshteinDistance / longerStringLength.toDouble()
        return (result * 100).toInt()
    }

    fun checkSimilarity(str1: String, str2: String, maxSimiliarity: Int): Boolean {
        return (similarityScore(str1, str2) > maxSimiliarity)
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