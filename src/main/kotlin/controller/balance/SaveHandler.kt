package controller.balance

import controller.BaseController
import dto.BalanceRequest
import dto.Receipt
import exception.Common
import service.BalanceService
import java.math.BigDecimal

class SaveHandler : BaseController<Receipt>(Receipt::class.java) {
    private val balanceService = BalanceService()

    override fun guard(request: Request<Receipt>) {
        require(request.input.amount > BigDecimal.ZERO) { throw Common.NonePositiveAmountException() }
    }

    override fun handleRequest(request: Request<Receipt>) {
        balanceService.saveTransaction(request.input)
    }
}