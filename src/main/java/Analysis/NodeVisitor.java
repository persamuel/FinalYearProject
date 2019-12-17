package Analysis;

import Node.*;

public abstract class NodeVisitor {
    public boolean preVisit(Expression.Logical node) { return true; }
    public abstract void postVisit(Expression.Logical node);

    public boolean preVisit(Expression.Equality node) { return true; }
    public abstract void postVisit(Expression.Equality node);

    public boolean preVisit(Expression.Comparison node) { return true; }
    public abstract void postVisit(Expression.Comparison node);

    public boolean preVisit(Expression.Arithmetic node) { return true; }
    public abstract void postVisit(Expression.Arithmetic node);

    public boolean preVisit(Expression.ArrayAccess node) { return true; }
    public abstract void postVisit(Expression.ArrayAccess node);

    public boolean preVisit(Expression.Call node) { return true; }
    public abstract void postVisit(Expression.Call node);

    public boolean preVisit(Expression.IntLiteral node) { return true; }
    public abstract void postVisit(Expression.IntLiteral node);

    public boolean preVisit(Expression.CharLiteral node) { return true; }
    public abstract void postVisit(Expression.CharLiteral node);

    public boolean preVisit(Expression.BoolLiteral node) { return true; }
    public abstract void postVisit(Expression.BoolLiteral node);

    public boolean preVisit(Expression.Identifier node) { return true; }
    public abstract void postVisit(Expression.Identifier node);

    public boolean preVisit(Expression.NewArray node) { return true; }
    public abstract void postVisit(Expression.NewArray node);

    public boolean preVisit(Expression.Negated node) { return true; }
    public abstract void postVisit(Expression.Negated node);

    public boolean preVisit(FunctionBody node) { return true; }
    public abstract void postVisit(FunctionBody node);

    public boolean preVisit(FunctionDeclaration node) { return true; }
    public abstract void postVisit(FunctionDeclaration node);

    public boolean preVisit(FunctionDefinition node) { return true; }
    public abstract void postVisit(FunctionDefinition node);

    public boolean preVisit(FunctionSignature node) { return true; }
    public abstract void postVisit(FunctionSignature node);

    public boolean preVisit(MainFunction node) { return true; }
    public abstract void postVisit(MainFunction node);

    public boolean preVisit(Parameter node) { return true; }
    public abstract void postVisit(Parameter node);

    public boolean preVisit(Program node) { return true; }
    public abstract void postVisit(Program node);

    public boolean preVisit(Statement.Compound node) { return true; }
    public abstract void postVisit(Statement.Compound node);

    public boolean preVisit(Statement.IfThenElse node) { return true; }
    public abstract void postVisit(Statement.IfThenElse node);

    public boolean preVisit(Statement.While node) { return true; }
    public abstract void postVisit(Statement.While node);

    public boolean preVisit(Statement.Assign node) { return true; }
    public abstract void postVisit(Statement.Assign node);

    public boolean preVisit(Statement.ArrayAssign node) { return true; }
    public abstract void postVisit(Statement.ArrayAssign node);

    public boolean preVisit(Statement.Free node) { return true; }
    public abstract void postVisit(Statement.Free node);

    public boolean preVisit(Statement.Print node) { return true; }
    public abstract void postVisit(Statement.Print node);

    public boolean preVisit(TypeLabel.Primitive node) { return true; }
    public abstract void postVisit(TypeLabel.Primitive node);

    public boolean preVisit(TypeLabel.StackArray node) { return true; }
    public abstract void postVisit(TypeLabel.StackArray node);

    public boolean preVisit(TypeLabel.HeapArray node) { return true; }
    public abstract void postVisit(TypeLabel.HeapArray node);

    public boolean preVisit(VariableDeclaration node) { return true; }
    public abstract void postVisit(VariableDeclaration node);
}
