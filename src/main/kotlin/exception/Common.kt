package exception

object Common {
    class NonePositiveAmountException: BaseException("Transfer amount should be positive.", 415)
    class InvalidDateFormatException: BaseException("Provided date format is invalid.", 416)
    class InvalidDateRangeException: BaseException("Provided date range is invalid.", 417)
}