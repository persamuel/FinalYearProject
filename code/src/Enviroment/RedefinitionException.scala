package Enviroment

final case class RedefinitionException(private val message: String = "",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)
