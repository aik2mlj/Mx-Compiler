package ast;

import util.Position;

public class ReturnStmtNode extends StmtNode {
    private ExprNode returnExpr;

    public ReturnStmtNode(Position pos, ExprNode returnExpr) {
        super(pos);
        this.returnExpr = returnExpr;
    }

    public boolean hasReturnExpr() { return returnExpr != null; }

    public ExprNode getReturnExpr() {
        return returnExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<ReturnStmtNode>\n" + (hasReturnExpr()? "returnExpr:\n" + returnExpr.toString(): "");
    }
}
