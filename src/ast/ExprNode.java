package ast;

import ir.operand.IROperand;
import util.Position;
import util.type.Type;

abstract public class ExprNode extends ASTNode {
    // type is specified at semantic check stage
    private String text;
    private Type type;

    private IROperand result;
    private IROperand lvalueResult;

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

    public void setResult(IROperand irValue) {
        this.result = irValue;
    }

    public IROperand getResult() {
        return result;
    }

    public void setLvalueResult(IROperand lvalueResult) {
        this.lvalueResult = lvalueResult;
    }

    public IROperand getLvalueResult() {
        return lvalueResult;
    }
}
