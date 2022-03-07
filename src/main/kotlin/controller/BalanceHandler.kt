package controller

import dto.BalanceRequest
import dto.Receipt
import dto.TransactionList
import exception.Common
import service.BalanceService
import java.math.BigDecimal

class BalanceHandler : BaseController<BalanceRequest>(BalanceRequest::class.java) {
    private val balanceService = BalanceService()

    override fun guard(input: BalanceRequest) {
        require(input.startDate.toEpoch < input.endDate.toEpoch) { throw Common.NonePositiveAmountException() }
    }

    override fun handleRequest(input: BalanceRequest): TransactionList {
        return balanceService.getBalanceInRange(input)
    }
}