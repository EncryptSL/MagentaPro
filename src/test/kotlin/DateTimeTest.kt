import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import org.junit.jupiter.api.Test

class DateTimeTest {

    @Test
    fun onDateTimeDiffTest() {
        val now = Clock.System.now()
        val instantInThePast: Instant = Instant.parse("2024-07-14T10:57:14.431967900Z")
        val daysToExpire = now.daysUntil(instantInThePast, TimeZone.currentSystemDefault())
        println(daysToExpire)
    }

}
