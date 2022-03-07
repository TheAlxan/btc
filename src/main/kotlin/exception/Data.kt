package exception

object Data {
    class InvalidTransactionRecordStatus: BaseException("Transaction record status invalid.", 431)
}