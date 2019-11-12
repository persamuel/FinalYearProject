package Node;

import Analysis.NodeVisitor;

import java.util.List;

public abstract class Statement extends Node {

    public static class Compound extends Statement {
        private List<Statement> stms;

        public Compound(List<Statement> stms) {
            this.stms = stms;
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

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                cond.accept(v);
                then.accept(v);
                els.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class While extends Statement {
        private Expression cond;
        private Statement body;

        public While(Expression cond, Statement body) {
            this.cond = cond;
            this.body = body;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                cond.accept(v);
                body.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class Assign extends Statement {
        private String id;
        private Expression val;

        public Assign(String id, Expression val) {
            this.id = id;
            this.val = val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {
                val.accept(v);

                v.postVisit(this);
            }
        }
    }

    public static class ArrayAssign extends Statement {
        private String id;
        private Expression idx;
        private Expression val;

        public ArrayAssign(String id, Expression idx, Expression val) {
            this.id = id;
            this.idx = idx;
            this.val = val;
        }

        @Override
        public void accept(NodeVisitor v) {
            if (v.preVisit(this)) {

                v.postVisit(this);
            }
        }
    }

    public static class Free extends Statement {
        private String id;

        public Free(String id) {
            this.id = id;
        }

        @Override
        public void accept(NodeVisitor v) {

        }
    }

    public static class Print extends Statement {
        private Expression val;

        public Print(Expression val) {
            this.val = val;
        }

        @Override
        public void accept(NodeVisitor v) {

        }
    }
}
