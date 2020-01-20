package Enviroment

import Analysis.MyType

class Mapping(val theType: MyType,
              val frameOffset: Int,
              val env: Option[SymbolTable]) {}
