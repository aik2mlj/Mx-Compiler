package ast;

import util.Position;

public class ReturnStmtNode extends StmtNode {
    private ExprNode returnValue;

    public ReturnStmtNode(Position pos, ExprNode returnValue) {
        super(pos);
        this.returnValue = returnValue;
    }

    public boolean hasReturnValue() { return returnValue != null; }

    public ExprNode getReturnValue() {
        return returnValue;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<ReturnStmtNode>\n" + (hasReturnValue()? "returnValue:\n" + returnValue.toString(): "");
    }
}
