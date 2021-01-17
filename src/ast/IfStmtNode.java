package ast;

import util.Position;

public class IfStmtNode extends StmtNode {
    private ExprNode condition; // bool
    private StmtNode trueStmt;
    private StmtNode falseStmt;

    public IfStmtNode(Position pos, ExprNode condition, StmtNode trueStmt, StmtNode falseStmt) {
        super(pos);
        this.condition = condition;
        this.trueStmt = trueStmt;
        this.falseStmt = falseStmt;
    }

    public ExprNode getCondition() {
        return condition;
    }

    public StmtNode getTrueStmt() {
        return trueStmt;
    }

    public boolean hasFalseStmt() { return falseStmt != null; }

    public StmtNode getFalseStmt() {
        return falseStmt;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<IfStmtNode>\ncondition:\n" + condition.toString() + "trueStmt:\n" + trueStmt.toString()
                + (hasFalseStmt()? "falseStmt:\n" + falseStmt.toString(): "");
    }
}
