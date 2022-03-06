package controller

import dto.BalanceRequest
import dto.Receipt
import exception.Common
import java.math.BigDecimal

class BalanceHandler : BaseController<BalanceRequest>(BalanceRequest::class.java) {
    override fun guard(input: BalanceRequest) {
        require(input.startDate.toEpoch < input.endDate.toEpoch) { throw Common.NonePositiveAmountException() }
    }

    override fun handleRequest(input: BalanceRequest) {
        println(input.endDate.hour)
    }
}