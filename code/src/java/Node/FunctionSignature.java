package Node;

import Analysis.NodeVisitor;

import java.util.List;

public class FunctionSignature extends Node {
    private TypeLabel type;
    private String name;
    private List<Parameter> args;

    public FunctionSignature(TypeLabel type, String name, List<Parameter> args) {
        this.type = type;
        this.name = name;
        this.args = args;
    }

    @Override
    public void accept(NodeVisitor v) {
        if (v.preVisit(this)) {
            type.accept(v);

            for (Parameter arg : args) {
                arg.accept(v);
            }

            v.postVisit(this);
        }
    }
}
