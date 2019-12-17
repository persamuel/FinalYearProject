package Node;

import Analysis.NodeVisitor;
import Parser.sym;

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

        @Override
        public String toString() {
            return lhs + " " + sym.terminalNames[op] + " " + rhs;
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

        public int getOp() {
            return op;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                lhs.accept(v);
                rhs.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return lhs + " " + sym.terminalNames[op] + " " + rhs;
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

        public int getOp() {
            return op;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                lhs.accept(v);
                rhs.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return lhs + " " + sym.terminalNames[op] + " " + rhs;
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

        public int getOp() {
            return op;
        }

        public Expression getLhs() {
            return lhs;
        }

        public Expression getRhs() {
            return rhs;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                lhs.accept(v);
                rhs.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return lhs + " " + sym.terminalNames[op] + " " + rhs;
        }
    }

    public static class ArrayAccess extends Expression {
        private Expression name;
        private Expression idx;

        public ArrayAccess(Expression name, Expression idx) {
            this.name = name;
            this.idx = idx;
        }

        public Expression getName() {
            return name;
        }

        public Expression getIdx() {
            return idx;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                name.accept(v);
                idx.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return name + "[" + idx + "]";
        }
    }

    public static class Call extends Expression {
        private String name;
        private List<Expression> args;

        public Call(String name, List<Expression> args) {
            this.name = name;
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public List<Expression> getArgs() {
            return args;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                for (Expression arg : args) {
                    arg.accept(v);
                }

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            String str = name + '(';

            for (int i = 0; i < args.size() - 1; i++) {
                str += args.get(i).toString();
                str += ", ";
            }

            str += args.get(args.size() - 1);
            str += ')';

            return str;
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

        @Override
        public String toString() {
            return val.toString();
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

        @Override
        public String toString() {
            return "'" + val + "'";
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

        @Override
        public String toString() {
            return val.toString();
        }
    }

    public static class Identifier extends Expression {
        private String name;

        public Identifier(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class NewArray extends Expression {
        private int typeConst;
        private Expression len;

        public NewArray(int typeConst, Expression len) {
            this.typeConst = typeConst;
            this.len = len;
        }

        public int getTypeConst() {
            return typeConst;
        }

        public Expression getLength() {
            return len;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                len.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return "new " + sym.terminalNames[typeConst] + "[" + len.toString() + "]";
        }
    }

    public static class Negated extends Expression {
        private Expression exp;

        public Negated(Expression exp) {
            this.exp = exp;
        }

        public Expression getExp() {
            return exp;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                exp.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return exp.toString();
        }
    }

}
