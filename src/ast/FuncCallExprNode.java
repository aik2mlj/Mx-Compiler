package ast;

import util.Position;

import java.util.ArrayList;

public class FuncCallExprNode extends ExprNode {
    private ExprNode funcExpr;
    private ArrayList<ExprNode> params;

    public FuncCallExprNode(Position pos, ExprNode funcExpr, ArrayList<ExprNode> params) {
        super(pos);
        this.funcExpr = funcExpr;
        this.params = params;
    }

    public ExprNode getFuncExpr() {
        return funcExpr;
    }

    public ArrayList<ExprNode> getParams() {
        return params;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<FuncCallExprNode>\n");
        ret.append("funcExpr:\n" + funcExpr + "params:\n");
        for(ExprNode it: params) ret.append(it.toString());
        return ret.toString();
    }
}
