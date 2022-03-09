package exception

object Admin {
    class CommandNotFoundException: BaseException("Command not found.", 451)
}