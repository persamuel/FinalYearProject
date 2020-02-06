package Enviroment

sealed trait SymbolCategory

object SymbolCategory {
  case object FUNCTION extends SymbolCategory
  case object PARAMETER extends SymbolCategory
  case object LOCAL extends SymbolCategory
}