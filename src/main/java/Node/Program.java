package Node;

import Analysis.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public class Program extends Node {
    private List<FunctionDeclaration> decls;
    private MainFunction main;
    private List<FunctionDefinition> defs;

    public Program(MainFunction main) {
        this(new ArrayList<>(), main, new ArrayList<>());
    }

    public Program(List<FunctionDeclaration> decls, MainFunction main, List<FunctionDefinition> defs) {
        super();
        this.decls = decls;
        this.main = main;
        this.defs = defs;
    }

    public List<FunctionDeclaration> getDeclarations() {
        return decls;
    }

    public MainFunction getMainFunction() {
        return main;
    }

    public List<FunctionDefinition> getDefinitions() {
        return defs;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            for (FunctionDeclaration decl : decls) {
                decl.accept(v);
            }

            main.accept(v);

            for (FunctionDefinition def : defs) {
                def.accept(v);
            }

            v.postVisit(this);
        }
    }
}
