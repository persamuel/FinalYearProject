package Enviroment

final case class TypeCheckingException(private val message: String = "",
                                       private val cause: Throwable = None.orNull) extends Exception(message, cause)