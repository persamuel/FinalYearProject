package Node;

import Analysis.NodeVisitor;
import Analysis.MyType;

public abstract class Node {
    private boolean setOnce;
    private MyType attachedType;

    public abstract void accept(NodeVisitor v);

    public MyType getAttachedType() {
        return attachedType;
    }

    public void setAttachedType(MyType type) {
        if (!setOnce) {
            this.attachedType = type;
            this.setOnce = true;
        }
    }
}
