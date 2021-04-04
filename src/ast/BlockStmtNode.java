package ast;

import util.Position;

import java.util.ArrayList;
import java.util.LinkedList;

public class BlockStmtNode extends StmtNode {
    private LinkedList<StmtNode> statements;

    public BlockStmtNode(Position pos, LinkedList<StmtNode> statements) {
        super(pos);
        this.statements = statements;
    }

    public BlockStmtNode(Position pos, StmtNode statement) {
        // only used for NewExpr expansion
        super(pos);
        this.statements = new LinkedList<>();
        if (statement != null)
            this.statements.add(statement);
    }

    public LinkedList<StmtNode> getStatements() {
        return statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("<BlockStmtNode>\nstatements:\n");
        for (StmtNode it : statements) ret.append(it.toString());
        return ret.toString();
    }

    public void addStmt(StmtNode stmtNode) {
        if (stmtNode != null)
            statements.add(stmtNode);
    }

    public void addStmtAtFront(StmtNode stmtNode) {
        if (stmtNode != null)
            statements.addFirst(stmtNode);
    }
}
