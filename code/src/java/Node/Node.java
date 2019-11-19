package Node;

import Analysis.NodeVisitor;
import Analysis.Type;

public abstract class Node {
    private boolean setOnce;
    private Type attachedType;

    public abstract void accept(NodeVisitor v);

    public Type getAttachedType() {
        return attachedType;
    }

    public void setAttachedType(Type type) {
        if (!setOnce) {
            this.attachedType = attachedType;
            this.setOnce = true;
        }
    }
}
