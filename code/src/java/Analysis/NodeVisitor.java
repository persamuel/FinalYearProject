package Analysis;

import Node.*;

public abstract class NodeVisitor {
    public abstract boolean preVisit(Expression.Logical node);
    public abstract void postVisit(Expression.Logical node);

    public abstract boolean preVisit(Expression.Equality node);
    public abstract void postVisit(Expression.Equality node);

    public abstract boolean preVisit(Expression.Comparison node);
    public abstract void postVisit(Expression.Comparison node);

    public abstract boolean preVisit(Expression.Arithmetic node);
    public abstract void postVisit(Expression.Arithmetic node);

    public abstract boolean preVisit(Expression.ArrayAccess node);
    public abstract void postVisit(Expression.ArrayAccess node);

    public abstract boolean preVisit(Expression.Call node);
    public abstract void postVisit(Expression.Call node);

    public abstract boolean preVisit(Expression.IntLiteral node);
    public abstract void postVisit(Expression.IntLiteral node);

    public abstract boolean preVisit(Expression.CharLiteral node);
    public abstract void postVisit(Expression.CharLiteral node);

    public abstract boolean preVisit(Expression.BoolLiteral node);
    public abstract void postVisit(Expression.BoolLiteral node);

    public abstract boolean preVisit(Expression.Identifier node);
    public abstract void postVisit(Expression.Identifier node);

    public abstract boolean preVisit(Expression.NewArray node);
    public abstract void postVisit(Expression.NewArray node);

    public abstract boolean preVisit(Expression.Negated node);
    public abstract void postVisit(Expression.Negated node);

    public abstract boolean preVisit(FunctionBody node);
    public abstract void postVisit(FunctionBody node);

    public abstract boolean preVisit(FunctionDeclaration node);
    public abstract void postVisit(FunctionDeclaration node);

    public abstract boolean preVisit(FunctionDefinition node);
    public abstract void postVisit(FunctionDefinition node);

    public abstract boolean preVisit(FunctionSignature node);
    public abstract void postVisit(FunctionSignature node);

    public abstract boolean preVisit(MainFunction node);
    public abstract void postVisit(MainFunction node);

    public abstract boolean preVisit(Parameter node);
    public abstract void postVisit(Parameter node);

    public abstract boolean preVisit(Program node);
    public abstract void postVisit(Program node);

    public abstract boolean preVisit(Statement.Compound node);
    public abstract void postVisit(Statement.Compound node);

    public abstract boolean preVisit(Statement.IfThenElse node);
    public abstract void postVisit(Statement.IfThenElse node);

    public abstract boolean preVisit(Statement.While node);
    public abstract void postVisit(Statement.While node);

    public abstract boolean preVisit(Statement.Assign node);
    public abstract void postVisit(Statement.Assign node);

    public abstract boolean preVisit(Statement.ArrayAssign node);
    public abstract void postVisit(Statement.ArrayAssign node);

    public abstract boolean preVisit(Statement.Free node);
    public abstract void postVisit(Statement.Free node);

    public abstract boolean preVisit(Type node);
    public abstract void postVisit(Type node);

    public abstract boolean preVisit(VariableDeclaration node);
    public abstract void postVisit(VariableDeclaration node);
}
