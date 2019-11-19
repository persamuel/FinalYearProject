package Node;

import Analysis.NodeVisitor;

public abstract class TypeLabel extends Node {

    public static class Primitive extends TypeLabel {
        private int type;

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

    public static class StackArray extends TypeLabel {
        private int type;
        private int size;

        public StackArray(int type, int size) {
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

    public static class HeapArray extends TypeLabel {
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
