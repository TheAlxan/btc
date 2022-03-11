package exception

object Input {
    class InvalidInputException: BaseException("Provided input value is invalid.", 441)
    class InvalidTimeRangeException: BaseException("Provided time range is invalid.", 442)
    class InvalidTimeZonesException: BaseException("Provided timezones for start and end date do not match.", 443)
}