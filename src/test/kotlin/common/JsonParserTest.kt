package common

import Receipt
import TransactionDate
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.TimeZone

internal class JsonParserTest {

    @Test
    fun `should parse receipt json string`() {
        val json = """
            {
                date: "2019-10-05T14:48:01+01:00",
                amount: 0.100002
            } 
        """.trimIndent()

        val receipt = JsonParser.parseToReceipt(json)

        assert(receipt.amount == BigDecimal.valueOf(0.100002))
        assert(receipt.transactionDate.toEpoch == TransactionDate("2019-10-05T14:48:01+01:00").toEpoch)
    }


}