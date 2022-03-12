package exception

object Common {
    class NonePositiveAmountException: BaseException("Transfer amount should be positive.", 415)
}