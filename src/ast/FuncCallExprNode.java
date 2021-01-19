package ast;

import util.Position;

import java.util.ArrayList;

public class FuncCallExprNode extends ExprNode {
    private String funcName;
    private ArrayList<ExprNode> params;

    public FuncCallExprNode(Position pos, String text, String funcName, ArrayList<ExprNode> params) {
        super(pos, text);
        this.funcName = funcName;
        this.params = params;
    }

    public String getFuncName() {
        return funcName;
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
        ret.append("funcName:\n" + funcName + "params:\n");
        for(ExprNode it: params) ret.append(it.toString());
        return ret.toString();
    }
}
