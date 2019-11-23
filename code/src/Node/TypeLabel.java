package Node;

import Analysis.NodeVisitor;
import Parser.sym;

public abstract class TypeLabel extends Node {

    public static class Primitive extends TypeLabel {
        private int typeConst;

        public Primitive(int typeConst) {
            this.typeConst = typeConst;
        }

        public int getTypeConst() {
            return typeConst;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return sym.terminalNames[typeConst];
        }
    }

    public static class StackArray extends TypeLabel {
        private int typeConst;
        private int size;

        public StackArray(int typeConst, int size) {
            this.typeConst = typeConst;
            this.size = size;
        }

        public int getTypeConst() {
            return typeConst;
        }

        public int getSize() {
            return size;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return sym.terminalNames[typeConst] + "[" + size + "]";
        }
    }

    public static class HeapArray extends TypeLabel {
        private int typeConst;

        public HeapArray(int typeConst) {
            this.typeConst = typeConst;
        }

        public int getTypeConst() {
            return typeConst;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return sym.terminalNames[typeConst] + "[]";
        }
    }
}
