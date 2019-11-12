package Node;

import Analysis.NodeVisitor;

public abstract class Node {
    public abstract void accept(NodeVisitor v);
}
