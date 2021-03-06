package ast;

import util.Position;

public class ForStmtNode extends StmtNode {
    public final static int CondLim = 1000;

    private ExprNode initExpr;
    private ExprNode condition;
    private ExprNode increaseExpr;
    private StmtNode statement;

    private VarDefStmtNode preCondDef = null;

    public ForStmtNode(Position pos, ExprNode initExpr, ExprNode condition, ExprNode increaseExpr, StmtNode statement) {
        super(pos);
        this.initExpr = initExpr;
        this.condition = condition;
        this.increaseExpr = increaseExpr;
        this.statement = statement;
    }

    public boolean hasInitExpr() { return initExpr != null; }

    public ExprNode getInitExpr() { return initExpr; }

    public boolean hasCondition() { return condition != null; }

    public ExprNode getCondition() {
        return condition;
    }

    public void setCondition(ExprNode condition) {
        this.condition = condition;
    }

    public boolean hasIncreaseExpr() { return increaseExpr != null; }

    public ExprNode getIncreaseExpr() {
        return increaseExpr;
    }

    public StmtNode getStatement() {
        return statement;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "<ForStmtNode>\n" + (hasInitExpr()? "initExpr:\n" + initExpr.toString(): "")
                + (hasCondition()? "condition:\n" + condition.toString(): "")
                + (hasIncreaseExpr()? "increaseExpr:\n" + increaseExpr.toString(): "")
                + ("statement:\n" + statement.toString());
    }

    public VarDefStmtNode getPreCondDef() {
        return preCondDef;
    }

    public void setPreCondDef(VarDefStmtNode preCondDef) {
        this.preCondDef = preCondDef;
    }
}
