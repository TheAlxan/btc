package common

import dto.Receipt
import dto.TransactionDate
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class JsonParserTest {

    @Test
    fun `should parse receipt json string`() {
        val json = """
            {
                "datetime": "2019-10-05T14:45:05+07:00",
                "amount": 10
            }
        """.trimIndent()

        val receipt = JsonParser.parseTo(json, Receipt::class.java)

        assert(receipt.amount == BigDecimal.valueOf(0.100002))
        assert(receipt.transactionDate.toEpoch == TransactionDate("2019-10-05T14:48:01+01:00").toEpoch)
    }


}