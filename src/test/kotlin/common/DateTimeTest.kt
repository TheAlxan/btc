package common

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.TimeZone

internal class DateTimeTest {
    @Test
    fun `should parse date string`(){
        val dateString = "2019-10-05T14:48:01+01:00"
        val localDate = DateTimeParser.parseToDate(dateString)
        assert(localDate.year == 2019)
        assert(localDate.monthValue == 10)
        assert(localDate.dayOfMonth == 5)
        assert(localDate.hour == 14)
        assert(localDate.minute == 48)
    }
}