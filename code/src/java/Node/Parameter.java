package Node;

import Analysis.NodeVisitor;

public class Parameter extends Node {
    private Type type;
    private String id;

    public Parameter(Type type, String id) {
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
