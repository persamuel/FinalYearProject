package Enviroment

import Analysis.MyType
import Enviroment.SymbolCategory._

import scala.collection.mutable.LinkedHashSet
import scala.collection.mutable.Map

class SymbolTable(val parent: Option[SymbolTable]) {
  private val table = Map.empty[String, (MyType, Option[SymbolTable])]

  private val functions = LinkedHashSet.empty[String]
  private val parameters = LinkedHashSet.empty[String]
  private val locals = LinkedHashSet.empty[String]

  def add(name: String, mapping: MyType, category: SymbolCategory) = {
    if (table.contains(name)) {
      throw RedefinitionException("Error: Redefinition of name \"" + name + "\" which is already in scope")
    } else {
      category match {
        case FUNCTION   => functions.add(name); table.put(name, (mapping, Some(SymbolTable(Some(this)))))
        case PARAMETER  => parameters.add(name); table.put(name, (mapping, None))
        case LOCAL      => locals.add(name); table.put(name, (mapping, None))
      }
    }
  }

  def lookupMapping(name: String) = {
    table.get(name)
  }

  def lookupCategory(name: String) = {
    if (functions.contains(name)) {
      FUNCTION
    } else if (parameters.contains(name)) {
      PARAMETER
    } else {
      LOCAL
    }
  }

  def lookupMappingInParent(name: String) = {
    parent match {
      case Some(parent) => parent.lookupMapping(name)
      case None => throw RootTableException("Error: Can not get parent of root symbol table.")
    }
  }

  def lookupCategoryInParent(name: String) = {
    parent match {
      case Some(parent) => parent.lookupCategory(name)
      case None => throw RootTableException("Error: Can not get parent of root symbol table.")
    }
  }

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
  def apply(parent: Option[SymbolTable]): SymbolTable = new SymbolTable(parent)
}