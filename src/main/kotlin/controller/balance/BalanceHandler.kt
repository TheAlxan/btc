package controller.balance

import controller.BaseController
import dto.BalanceRequest
import dto.TransactionList
import exception.Common
import service.BalanceService

class BalanceHandler : BaseController<BalanceRequest>(BalanceRequest::class.java) {
    private val balanceService = BalanceService()

    override fun guard(request: Request<BalanceRequest>) {
        val input = request.input
        require(input.startDate.toEpoch < input.endDate.toEpoch) { throw Common.NonePositiveAmountException() }
    }

    override fun handleRequest(request: Request<BalanceRequest>): TransactionList {
        return balanceService.getBalanceInRange(request.input)
    }
}