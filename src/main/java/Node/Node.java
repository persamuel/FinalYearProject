package Node;

import Analysis.NodeVisitor;
import Analysis.MyType;

public abstract class Node {
    private MyType attachedType;
    private String attachedAssembly;

    public abstract void accept(NodeVisitor v);

    public MyType getAttachedType() {
        return attachedType;
    }

    public void setAttachedType(MyType type) {
        this.attachedType = type;
    }

    public String getAttachedAssembly() {
        return attachedAssembly;
    }

    public void setAttachedAssembly(String attachedAssembly) {
        this.attachedAssembly = attachedAssembly;
    }
}
