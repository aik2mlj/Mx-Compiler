package ast;

import util.Position;

import java.util.ArrayList;

public class NewExprNode extends ExprNode {
    private SingleTypeNode baseTypeNode;
    private ArrayList<ExprNode> exprInBrackets;
    private int dimension;

    public NewExprNode(Position pos, String text, SingleTypeNode baseTypeNode, ArrayList<ExprNode> exprInBrackets, int dimension) {
        super(pos, text);
        this.baseTypeNode = baseTypeNode;
        this.exprInBrackets = exprInBrackets;
        this.dimension = dimension;
    }

    public SingleTypeNode getBaseTypeNode() {
        return baseTypeNode;
    }

    public ArrayList<ExprNode> getExprInBrackets() {
        return exprInBrackets;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<NewExprNode>\nbaseTypeNode:\n");
        ret.append(baseTypeNode.toString());
        ret.append("exprInBrackets:\n");
        for(ExprNode it: exprInBrackets) ret.append(it.toString());
        ret.append("dimension: ").append(dimension).append("\n");
        return ret.toString();
    }
}
