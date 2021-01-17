package ast;

import util.Position;

public class WhileStmtNode extends StmtNode {
    private ExprNode condition;
    private StmtNode statement;

    public WhileStmtNode(Position pos, ExprNode condition, StmtNode statement) {
        super(pos);
        this.condition = condition;
        this.statement = statement;
    }

    public ExprNode getCondition() { return condition; }

    public StmtNode getStatement() {
        return statement;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<WhileStmtNode>\ncondition:\n" + condition.toString() + "statement:\n" + statement.toString();
    }
}
