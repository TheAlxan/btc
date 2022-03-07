package controller

import dto.Receipt
import exception.Common
import service.BalanceService
import java.math.BigDecimal

class SaveHandler : BaseController<Receipt>(Receipt::class.java) {
    private val balanceService = BalanceService()

    override fun guard(input: Receipt) {
        require(input.amount > BigDecimal.ZERO) { throw Common.NonePositiveAmountException() }
    }

    override fun handleRequest(input: Receipt) {
        balanceService.saveTransaction(input)
    }
}