package ast;

import util.Position;

public class VarNode extends ASTNode {
    // typeNode identifier = initExpr;
    // appear in VarListNode, ClassDefNode(ArrayList), FuncDefNode(ArrayList)
    private TypeNode typeNode;
    private String identifier;
    private ExprNode initExpr;

    public VarNode(Position pos, TypeNode typeNode, String identifier, ExprNode initExpr) {
        super(pos);
        this.typeNode = typeNode;
        this.identifier = identifier;
        this.initExpr = initExpr;
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean hasInitExpr() {
        return initExpr != null;
    }

    public ExprNode getInitExpr() {
        return initExpr;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<VarNode>\ntypeNode: " + typeNode.toString() + "identifier: " + identifier +
                (this.hasInitExpr()? "\ninitExpr:\n" + initExpr.toString(): "") + "\n";
    }
}
