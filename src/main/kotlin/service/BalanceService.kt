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
        val list = transactionList.list
        val hourOffset = 60 * 60L
        val startingHour: Long = range.getStartingHour()
        val endingHour: Long = range.getEndingHour()
        val report = list.groupBy { (it.transactionDate.toEpoch - startingHour) / hourOffset }
            .map { (it.key + 1) * hourOffset + startingHour to it.value.maxOf { r -> r.amount } }
            .toMap()
        var latestBalance = BigDecimal.ZERO
        return ((startingHour)..(endingHour + hourOffset) step hourOffset)
            .map { hour -> Receipt.of(hour, range.startDate.timeZone, report[hour]?.apply { latestBalance = this } ?: latestBalance) }
            .let { TransactionList(it) }
    }


}