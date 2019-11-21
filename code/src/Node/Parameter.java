package Node;

import Analysis.NodeVisitor;

public class Parameter extends Node {
    private TypeLabel typeLabel;
    private String name;

    public Parameter(TypeLabel type, String name) {
        this.typeLabel = type;
        this.name = name;
    }

    public TypeLabel getTypeLabel() {
        return typeLabel;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            typeLabel.accept(v);

            v.postVisit(this);
        }
    }
}
