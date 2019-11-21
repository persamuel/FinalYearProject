package Node;

import Analysis.NodeVisitor;

import java.util.List;

public class FunctionSignature extends Node {
    private TypeLabel typeLabel;
    private String name;
    private List<Parameter> args;

    public FunctionSignature(TypeLabel typeLabel, String name, List<Parameter> args) {
        this.typeLabel = typeLabel;
        this.name = name;
        this.args = args;
    }

    public TypeLabel getTypeLabel() {
        return typeLabel;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getArgs() {
        return args;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            typeLabel.accept(v);

            for (Parameter arg : args) {
                arg.accept(v);
            }

            v.postVisit(this);
        }
    }
}
