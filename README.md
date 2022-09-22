# Mx Compiler

This huge project is assigned in MS208 course. We built a compiler from scratch, from the backend to the frontend (antlr4 used for parsing) and added multiple optimizing strategies to reach the -O1 baseline. It was really an Odyssey both for mind and body.

The tutorial repo is [here](https://github.com/ACMClassCourses/Compiler-Design-Implementation).

## Line Count
| File Type | Files | Lines of Code | Total lines |
| --------- | ----- | ------------- | ----------- |
| Java      | 166   | 16072         | 19318       |
| C         | 10    | 268           | 293         |
| Python    | 6     | 335           | 463         |


## Parser Tree

Use **antlr4** to generate parser tree files

## AST

### ASTNodes

- Simplify parser tree by **removing unnecessary tokens** like `;`, `(`, etc.

- Another goal is to **build a more logically organized tree** than the generated parser tree. 

  For example, `varDef` in g4 appears in `classDef`, `programUnit` and `varDefStmt`, and its meaning varies in these nodes. We can return a `VarListNode` in AST for `varDef` to maintain the information of the variables, and their specific usage is implemented in the upper nodes. The design is up to you.

### Utils

`Position` class is recommended for recording the position of ASTNode in context. It's very simple:

```java
public class Position {
    private int row, column; // private: avoid changing

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }
    public Position(Token token) {
        row = token.getLine();
        column = token.getCharPositionInLine();
    }
    public Position(TerminalNode terminal) {
        this(terminal.getSymbol());
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }
    public String toString() { return row + "," + column; }
}
```

### ASTBuilder

Build all the ASTNodes by traverse the parser tree nodes.

For instance, when visiting `ifStmt` in parser tree:

```java
public class ASTBuilder extends MxBaseVisitor<ASTNode> {
	...
    @Override
    public ASTNode visitIfStmt(MxParser.IfStmtContext ctx) {
        // return IfStmtNode
        Position pos = new Position(ctx.getStart());
        ExprNode condition = (ExprNode) visit(ctx.expression());
        StmtNode trueStmt = (StmtNode) visit(ctx.trueStmt);
        StmtNode falseStmt = null;
        if(ctx.falseStmt != null)
            falseStmt = (StmtNode) visit(ctx.falseStmt);
        return new IfStmtNode(pos, condition, trueStmt, falseStmt);
    }
    ...
}
```

