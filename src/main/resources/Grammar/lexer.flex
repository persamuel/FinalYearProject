package Lexer;

import Parser.sym;
import java_cup.runtime.Symbol;

%%

/* Options */

%class Lexer
%public
%unicode
%cup
%line
%column

%{
	char cbuf;

	private Symbol symbol(int type) {
		return new Symbol(type, yyline, yycolumn);
	}

	private Symbol symbol(int type, Object value) {
		return new Symbol(type, yyline, yycolumn, value);
	}
%}

/* Macros */

EOL 				    = \r|\n|\r\n
WhiteSpace 			    = {EOL} | [ \t\f]
InputCharacter		    = [^\r\n]

Comment				    = {LineComment} | {BlockComment}
LineComment			    = "//" {InputCharacter}* {EOL}?
BlockComment		    = "/*" [^*] ~"*/" | "/*" "*"+ "/"

Identifier 			    = [:jletter:] [:jletterdigit:]*
IntegerLiteral 		    = 0 | [1-9][0-9]*
/* Any 1 byte printable ascii character except ' and \ which must be escaped */
ASCIICharacterLiteral 	= \'[[!-~ ]--[\'\\]]\'

%%

<YYINITIAL> {

	/* Keywords */
	
	"return" 					{ return symbol(sym.RETURN); }
	"int" 						{ return symbol(sym.INT); }
	"main" 						{ return symbol(sym.MAIN); }
	"char" 						{ return symbol(sym.CHAR); }
	"boolean" 					{ return symbol(sym.BOOL); }
	"if" 						{ return symbol(sym.IF); }
    "else" 						{ return symbol(sym.ELSE); }
	"while" 					{ return symbol(sym.WHILE); }
	"free" 						{ return symbol(sym.FREE); }
	"print" 					{ return symbol(sym.PRINT); }
	"new" 						{ return symbol(sym.NEW); }
	
	/* Boolean Literals */
	
	"true"						{ return symbol(sym.BOOLEAN_LITERAL, Boolean.TRUE); }
	"false"						{ return symbol(sym.BOOLEAN_LITERAL, Boolean.FALSE); }

	/* Special Notation */

	";" 						{ return symbol(sym.SEMICOLON); }
    ","                         { return symbol(sym.COMMA); }
	"(" 						{ return symbol(sym.LPAREN); }
	")" 						{ return symbol(sym.RPAREN); }
	"[" 						{ return symbol(sym.LBRACK); }
	"]" 						{ return symbol(sym.RBRACK); }
	"{" 						{ return symbol(sym.LBRACE); }
	"}" 						{ return symbol(sym.RBRACE); }
	
	"!" 						{ return symbol(sym.NOT); }
	"&&"						{ return symbol(sym.AND); }
	"||" 						{ return symbol(sym.OR); }
	"!=" 						{ return symbol(sym.NOTEQ); }
	"==" 						{ return symbol(sym.EQEQ); }
	"<" 						{ return symbol(sym.LT); }
	"<=" 						{ return symbol(sym.LTE); }
	">" 						{ return symbol(sym.GT); }
	">=" 						{ return symbol(sym.GTE); }

	"="                         { return symbol(sym.EQ); }
	"+" 						{ return symbol(sym.PLUS); }
	"-" 						{ return symbol(sym.MINUS); }
	"*" 						{ return symbol(sym.MULTI); }
		
	/* Identifiers */
	
	{Identifier}				{ return symbol(sym.IDENTIFIER, yytext()); }
		
	/* Other Literals */
	
	{ASCIICharacterLiteral}		{ return symbol(sym.CHARACTER_LITERAL, yytext().charAt(1)); }
	\' \\b \'                   { return symbol(sym.CHARACTER_LITERAL, '\b'); }
	\' \\f \'                   { return symbol(sym.CHARACTER_LITERAL, '\f'); }
	\' \\n \'                   { return symbol(sym.CHARACTER_LITERAL, '\n'); }
	\' \\t \'                   { return symbol(sym.CHARACTER_LITERAL, '\t'); }
	\' \\0 \'                   { return symbol(sym.CHARACTER_LITERAL, '\0'); }
	\' \\' \'                   { return symbol(sym.CHARACTER_LITERAL, '\''); }
	\' \\\\ \'                  { return symbol(sym.CHARACTER_LITERAL, '\\'); }

	{IntegerLiteral}			{ return symbol(sym.INTEGER_LITERAL, Integer.parseInt(yytext())); }
	
	/* Comments */
	
	{Comment}                   { /* ignore */ }

	/* Whitespace */
	
	{WhiteSpace}                { /* ignore */ }
}

/* Error Fallback */
[^]                             { throw new Error("Illegal character <"+yytext()+">"); }