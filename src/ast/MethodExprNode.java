package ast;

import util.Position;

import java.util.ArrayList;

public class MethodExprNode extends ExprNode {
    private ExprNode prefixExpr;
    private String methodName;
    private ArrayList<ExprNode> params;

    public MethodExprNode(Position pos, String text, ExprNode prefixExpr, String methodName, ArrayList<ExprNode> params) {
        super(pos, text);
        this.prefixExpr = prefixExpr;
        this.methodName = methodName;
        this.params = params;
    }

    public ExprNode getPrefixExpr() {
        return prefixExpr;
    }

    public String getMethodName() {
        return methodName;
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
        StringBuilder ret = new StringBuilder("<MethodExpr>\n");
        ret.append("prefixExpr:\n" + prefixExpr.toString());
        ret.append("methodName:\n" + methodName + "params:\n");
        for(ExprNode it: params) ret.append(it.toString());
        return ret.toString();
    }
}
