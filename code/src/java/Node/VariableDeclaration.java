package Node;

import Analysis.NodeVisitor;

public class VariableDeclaration extends Node {
    private TypeLabel type;
    private String id;

    public VariableDeclaration(TypeLabel type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            type.accept(v);
            v.postVisit(this);
        }
    }
}
