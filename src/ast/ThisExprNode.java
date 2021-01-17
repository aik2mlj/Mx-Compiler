package ast;

import util.Position;

public class ThisExprNode extends ExprNode {
    public ThisExprNode(Position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<ThisExprNode>\n";
    }
}
