package ast;

import util.Position;

public class SimpleStmtNode extends StmtNode {
    private ExprNode expression;

    public SimpleStmtNode(Position pos, ExprNode expression) {
        super(pos);
        this.expression = expression;
    }

    public boolean hasExpression() { return expression != null; }

    public ExprNode getExpression() {
        return expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<SimpleStmtNode>\n" + (hasExpression()? "expression:\n" + expression.toString(): "");
    }
}
