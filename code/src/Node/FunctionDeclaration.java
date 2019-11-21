package Node;

import Analysis.NodeVisitor;

public class FunctionDeclaration extends Node {
    private FunctionSignature sgnt;

    public FunctionDeclaration(FunctionSignature sgnt) {
        this.sgnt = sgnt;
    }

    public FunctionSignature getSignature() {
        return sgnt;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            sgnt.accept(v);

            v.postVisit(this);
        }
    }
}
