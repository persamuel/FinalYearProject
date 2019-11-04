import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            try {
                System.out.println("Parsing [" + args[i] + "]");

                Scanner s = new Lexer(new FileReader(args[i]));

                Parser p = new Parser(s);
                p.parse();

                System.out.println("No errors.");
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        }
    }
}
