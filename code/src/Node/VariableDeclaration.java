package Node;

import Analysis.NodeVisitor;

public class VariableDeclaration extends Node {
    private TypeLabel typeLabel;
    private String name;

    public VariableDeclaration(TypeLabel typeLabel, String name) {
        this.typeLabel = typeLabel;
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
