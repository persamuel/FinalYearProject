package Enviroment

final case class RootTableException(private val message: String = "",
                                    private val cause: Throwable = None.orNull) extends Exception(message, cause)