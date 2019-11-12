package Node;

import Analysis.NodeVisitor;

import java.util.List;

public class Program extends Node {
    private List<FunctionDeclaration> decls;
    private MainFunction main;
    private List<FunctionDefinition> defs;

    public Program(MainFunction main) {
        this(null, main, null);
    }

    public Program(List<FunctionDeclaration> decls, MainFunction main, List<FunctionDefinition> defs) {
        super();
        this.decls = decls;
        this.main = main;
        this.defs = defs;
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
