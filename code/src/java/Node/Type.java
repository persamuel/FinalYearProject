package Node;

import Analysis.NodeVisitor;

public abstract class Type extends Node {

    public static class Primitive extends Type {
        private final int type;

        public Primitive(int type) {
            this.type = type;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }
    }

    public static class StackArray extends Type {
        private int type;
        private int size;

        public StackArray(int type, Integer size) {
            this.type = type;
            this.size = size;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }
    }

    public static class HeapArray extends Type {
        private int type;

        public HeapArray(int type) {
            this.type = type;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }
    }

}
