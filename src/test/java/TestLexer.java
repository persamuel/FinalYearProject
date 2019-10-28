import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class TestLexer {

    private String file = "res/examples/programs/Factorial.rope";

    @Test
    public void dummy() {
        try {
            Scanner s = new Lexer(new FileReader(file));

            Symbol symbol = s.next_token();
            while (symbol.sym != sym.EOF) {
                System.out.println(sym.terminalNames[symbol.sym]);
                symbol = s.next_token();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
