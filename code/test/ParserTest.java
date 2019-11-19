import Node.Program;

import Parser.Parser;
import Lexer.Lexer;
import java_cup.runtime.Scanner;
import org.junit.Test;

import java.io.FileReader;

public class ParserTest {

    @Test
    public void testAST() {
        try {
            Scanner s = new Lexer(new FileReader("res/example/program/strcpy.rop"));

            Parser p = new Parser(s);
            Program tmp = (Program) p.parse().value;

            System.out.println("No errors.");
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}