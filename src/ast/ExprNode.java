package ast;

import ir.operand.Operand;
import util.Position;
import util.type.Type;

abstract public class ExprNode extends ASTNode {
    // type is specified at semantic check stage
    private String text;
    private Type type;

    private Operand result;
    private Operand lvalueResult;

    public ExprNode(Position pos, String text) {
        super(pos);
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public boolean isAssignable() {
        return false;
    }

    public void setResult(Operand irValue) {
        this.result = irValue;
    }

    public Operand getResult() {
        return result;
    }

    public void setLvalueResult(Operand lvalueResult) {
        this.lvalueResult = lvalueResult;
    }

    public Operand getLvalueResult() {
        return lvalueResult;
    }
}
