package Node;

import Analysis.NodeVisitor;

import java.util.List;

public class FunctionBody extends Node {
    private List<VariableDeclaration> vars;
    private List<Statement> stms;
    private Expression ret;

    public FunctionBody(List<VariableDeclaration> vars, List<Statement> stms, Expression ret) {
        this.vars = vars;
        this.stms = stms;
        this.ret = ret;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            for (VariableDeclaration var : vars) {
                var.accept(v);
            }

            for (Statement stm : stms) {
                stm.accept(v);
            }

            ret.accept(v);

            v.postVisit(this);
        }
    }
}
