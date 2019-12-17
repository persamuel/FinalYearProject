package Node;

import Analysis.NodeVisitor;

public class FunctionDefinition extends Node {
    private FunctionSignature sgnt;
    private FunctionBody body;

    public FunctionDefinition(FunctionSignature sgnt, FunctionBody body) {
        this.sgnt = sgnt;
        this.body = body;
    }

    public FunctionSignature getSignature() {
        return sgnt;
    }

    public FunctionBody getBody() {
        return body;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            sgnt.accept(v);
            body.accept(v);

            v.postVisit(this);
        }
    }
}
