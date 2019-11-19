package Node;

import Analysis.NodeVisitor;
import Analysis.Types;

public abstract class Node {
    private boolean setOnce;
    private Types attachedType;

    public abstract void accept(NodeVisitor v);

    public Types getAttachedType() {
        return attachedType;
    }

    public void setAttachedType(Types type) {
        if (!setOnce) {
            this.attachedType = attachedType;
            this.setOnce = true;
        }
    }
}
