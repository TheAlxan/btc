package service

import common.DateTimeParser
import data.BalanceTable
import dto.BalanceRequest
import dto.Receipt
import dto.TransactionList
import java.math.BigDecimal

class BalanceService {
    fun saveTransaction(receipt: Receipt) {
        BalanceTable.saveTransaction(receipt)
    }

    fun getBalanceInRange(range: BalanceRequest): TransactionList {
        val transactionList = BalanceTable.getBalance(range)
        return getBalanceReportPerHour(transactionList, range)
    }

    private fun getBalanceReportPerHour(transactionList: TransactionList, range: BalanceRequest): TransactionList {
        val hourOffset = 1000 * 60 * 60
        val list = transactionList.list
        var startingHour = range.getStartingHour()
        val endingHour = range.getEndingHour()
        val report = mutableListOf<Receipt>()
        var counter = 0
        var current: Receipt? = null
        while (startingHour <= endingHour) {
            current = list.getOrNull(counter) ?: current
            while((current?.transactionDate?.toEpoch ?: Long.MAX_VALUE) < (startingHour + hourOffset)) {
                list.getOrNull(++counter) ?: current
                continue
            }
            report.add(Receipt(DateTimeParser.epochToString(startingHour + hourOffset), current?.amount ?: BigDecimal.ZERO))
            startingHour += hourOffset
        }
        return TransactionList(report)
    }


}