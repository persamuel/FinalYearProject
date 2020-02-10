package Enviroment

import Analysis.MyType
import Enviroment.SymbolCategory._

import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.Map

class SymbolTable(val funcType: MyType, val funcName: String) {
  private val table = Map.empty[String, Mapping]

  private val functions = LinkedHashSet.empty[String]

  var paramOffset = 4
  private val parameters = LinkedHashSet.empty[String]

  var localOffset = 0
  private val locals = LinkedHashSet.empty[String]

  def add(name: String, theType: MyType, category: SymbolCategory) = {
    if (table.contains(name)) {
      throw RedefinitionException(s"Error: Redefinition of $name which is already in scope")
    }
    else {
      category match {
        case FUNCTION => {
          functions.add(name)
          table.put(name, new Mapping(theType, 0, Some(SymbolTable(theType, name))))
        }
        case PARAMETER => {
          parameters.add(name)
          paramOffset = roundToNearestMultipleOfFour(paramOffset + theType.getSizeInBytes)
          table.put(name, new Mapping(theType, paramOffset, None))
        }
        case LOCAL => {
          locals.add(name)
          localOffset = roundToNearestMultipleOfFour(localOffset - theType.getSizeInBytes)
          table.put(name, new Mapping(theType, localOffset, None))
        }
      }
    }
  }

  private def roundToNearestMultipleOfFour(x: Int) = (x + (if (x < 0) 1 - 4 else 4 - 1)) / 4 * 4

  def lookupMapping(name: String) = {
    table.get(name)
  }

  def lookupCategory(name: String) = {
    if (functions.contains(name)) {
      FUNCTION
    }
    else if (parameters.contains(name)) {
      PARAMETER
    }
    else {
      LOCAL
    }
  }

  def frameSize(): Int = localOffset

  def keys(category: SymbolCategory) = {
    category match {
      case FUNCTION => functions.toList
      case PARAMETER => parameters.toList
      case LOCAL => locals.toList
    }
  }

  def values(category: SymbolCategory) = {
    for (key <- keys(category)) yield table(key)
  }

  def entries(category: SymbolCategory) = {
    for (key <- keys(category)) yield (key, table(key))
  }
}

object SymbolTable {
  def apply(funcType: MyType, funcName: String): SymbolTable = new SymbolTable(funcType, funcName)
}