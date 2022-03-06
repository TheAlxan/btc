package exception

abstract class BaseException(val msg: String, val code: Int): Exception(msg)