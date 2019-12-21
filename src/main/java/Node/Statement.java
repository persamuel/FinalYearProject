package Node;

import Analysis.NodeVisitor;

import java.util.List;

public abstract class Statement extends Node {

    public static class Compound extends Statement {
        private List<Statement> stms;

        public Compound(List<Statement> stms) {
            this.stms = stms;
        }

        public List<Statement> getStms() {
            return stms;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                for (Statement stm : stms) {
                    stm.accept(v);
                }

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            String str = "{ ";

            for (int i = 0; i < stms.size() - 1; i++) {
                str += stms.get(i);
                str += "; ";
            }

            str += stms.get(stms.size() - 1);
            str += " }";

            return str;
        }
    }

    public static class IfThenElse extends Statement {
        private Expression cond;
        private Statement then;
        private Statement els;

        public IfThenElse(Expression cond, Statement then, Statement els) {
            this.cond = cond;
            this.then = then;
            this.els = els;
        }

        public Expression getCond() {
            return cond;
        }

        public Statement getThen() {
            return then;
        }

        public Statement getElse() {
            return els;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                cond.accept(v);
                then.accept(v);
                els.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return "if (" + cond + ") " + "{ " + then + " }" + " else" + "{ " + els + " }";
        }
    }

    public static class While extends Statement {
        private Expression cond;
        private Statement body;

        public While(Expression cond, Statement body) {
            this.cond = cond;
            this.body = body;
        }

        public Expression getCond() {
            return cond;
        }

        public Statement getBody() {
            return body;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                cond.accept(v);
                body.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return "while (" + cond + ") " + "{ " + body + " }";
        }
    }

    public static class Assign extends Statement {
        private String name;
        private Expression val;

        public Assign(String name, Expression val) {
            this.name = name;
            this.val = val;
        }

        public String getName() {
            return name;
        }

        public Expression getVal() {
            return val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                val.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return name + " = " + val;
        }
    }

    public static class ArrayAssign extends Statement {
        private String name;
        private Expression idx;
        private Expression val;

        public ArrayAssign(String name, Expression idx, Expression val) {
            this.name = name;
            this.idx = idx;
            this.val = val;
        }

        public String getName() {
            return name;
        }

        public Expression getIdx() {
            return idx;
        }

        public Expression getVal() {
            return val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                idx.accept(v);
                val.accept(v);

                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return name + "[" + idx + "]" + " = " + val;
        }
    }

    public static class Free extends Statement {
        private String name;

        public Free(String name) {
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
            return "free " + name;
        }
    }

    public static class Print extends Statement {
        private Expression val;

        public Print(Expression val) {
            this.val = val;
        }

        public Expression getVal() {
            return val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                val.accept(v);
                v.postVisit(this);
            }
        }

        @Override
        public String toString() {
            return "print(" + val + ')';
        }
    }
}
