package Node;

import Analysis.NodeVisitor;

import java.util.List;

public abstract class Expression extends Node {

    public static class Logical extends Expression {
        private int op;
        private Expression lhs, rhs;

        public Logical(int op, Expression lhs, Expression rhs) {
            this.op = op;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                lhs.accept(v);
                rhs.accept(v);

                v.postVisit(this);
            }
        }

        public int getOp() {
            return op;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }
    }

    public static class Equality extends Expression {
        private int op;
        private Expression lhs, rhs;

        public Equality(int op, Expression lhs, Expression rhs) {
            this.op = op;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                lhs.accept(v);
                rhs.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class Comparison extends Expression {
        private int op;
        private Expression lhs, rhs;

        public Comparison(int op, Expression lhs, Expression rhs) {
            this.op = op;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                lhs.accept(v);
                rhs.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class Arithmetic extends Expression {
        private int op;
        private Expression lhs, rhs;

        public Arithmetic(int op, Expression lhs, Expression rhs) {
            this.op = op;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                lhs.accept(v);
                rhs.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class ArrayAccess extends Expression {
        private Expression arr;
        private Expression idx;

        public ArrayAccess(Expression arr, Expression idx) {
            this.arr = arr;
            this.idx = idx;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                arr.accept(v);
                idx.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class Call extends Expression {
        private String name;
        private List<Expression> args;

        public Call(String name, List<Expression> args) {
            this.name = name;
            this.args = args;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                for (Expression arg : args) { // todo: Checkout possible weird evaluation of list
                    arg.accept(v);
                }

                v.postVisit(this);
            }
        }
    }

    public static class IntLiteral extends Expression {
        private Integer val;

        public IntLiteral(Integer val) {
            this.val = val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }
    }

    public static class CharLiteral extends Expression {
        private Character val;

        public CharLiteral(Character val) {
            this.val = val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }
    }

    public static class BoolLiteral extends Expression {
        private Boolean val;

        public BoolLiteral(Boolean val) {
            this.val = val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }
    }

    public static class Identifier extends Expression {
        private String id;

        public Identifier(String id) {
            this.id = id;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }
    }

    public static class NewArray extends Expression {
        private int type;
        private Expression len;

        public NewArray(int type, Expression len) {
            this.type = type;
            this.len = len;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                len.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class Negated extends Expression {
        private Expression exp;

        public Negated(Expression exp) {
            this.exp = exp;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                exp.accept(v);

                v.postVisit(this);
            }
        }
    }

}
