package frontend;

import ast.*;
import parser.MxParser;
import parser.MxParserBaseVisitor;
import util.Position;
import util.error.SemanticError;
import util.error.SyntaxError;

import java.util.ArrayList;

public class ASTBuilder extends MxParserBaseVisitor<ASTNode> {
    public ASTBuilder() {}

    @Override
    public ASTNode visitProgram(MxParser.ProgramContext ctx) {
        // return ProgramNode
        Position pos = new Position(ctx.getStart());
        ArrayList<ClassDefNode> classDefNodes = new ArrayList<>();
        ArrayList<FuncDefNode> funcDefNodes = new ArrayList<>();
        ArrayList<VarNode> globalVars = new ArrayList<>();

        for(var it: ctx.classDef())
            classDefNodes.add((ClassDefNode) visit(it));
        for(var it: ctx.funcDef())
            funcDefNodes.add((FuncDefNode) visit(it));
        for(var it: ctx.varDef())
            globalVars.addAll(((VarListNode) visit(it)).getVarNodes());

        return new ProgramNode(pos, classDefNodes, funcDefNodes, globalVars);
    }

    @Override
    public ASTNode visitClassDef(MxParser.ClassDefContext ctx) {
        // return ClassDefNode
        Position pos = new Position(ctx.getStart());
        String identifier = ctx.Identifier().getText();
        ArrayList<VarNode> members = new ArrayList<>();
        FuncDefNode constructor = null;
        ArrayList<FuncDefNode> methods = new ArrayList<>();
        for(var it: ctx.varDef())
            members.addAll(((VarListNode) visit(it)).getVarNodes());

        boolean hasConstructor = false;
        for(var it: ctx.funcDef()) {
            if(it.Identifier().getText().equals(ctx.Identifier().getText())) { // is the constructor
                if(hasConstructor)
                    throw new SemanticError("Duplicate constructor declaration", new Position(it.getStart()));
                else {
                    constructor = (FuncDefNode) visit(it);
                    hasConstructor = true;
                }
            } else { // is a method
                methods.add((FuncDefNode) visit(it));
            }
        }
        return new ClassDefNode(pos, identifier, members, constructor, methods);
    }

    @Override
    public ASTNode visitFuncDef(MxParser.FuncDefContext ctx) {
        // return FuncDefNode
        Position pos = new Position(ctx.getStart());
        TypeNode typeNode = null;
        if(ctx.type() != null)
            typeNode = (TypeNode) visit(ctx.type());
        String identifier = ctx.Identifier().getText();
        ArrayList<VarNode> params = new ArrayList<>();
        if(ctx.paramList() != null)
            params.addAll(((VarListNode) visit(ctx.paramList())).getVarNodes());
        BlockStmtNode suite = (BlockStmtNode) visit(ctx.suite());
        return new FuncDefNode(pos, typeNode, identifier, params, suite);
    }

    @Override
    public ASTNode visitVarDef(MxParser.VarDefContext ctx) {
        // return VarListNode
        Position pos = new Position(ctx.getStart());
        TypeNode typeNode = (TypeNode) visit(ctx.type());
        ArrayList<VarNode> varDefs = new ArrayList<>();
        for(var it: ctx.varDefUnit()) {
            var varDef = (VarNode) visit(it);
            varDef.setTypeNode(typeNode); // set typeNode of each unit
            varDefs.add(varDef);
        }
        return new VarListNode(pos, varDefs);
    }

    @Override
    public ASTNode visitVarDefUnit(MxParser.VarDefUnitContext ctx) {
        // return VarNode: typeNode not set yet
        Position pos = new Position(ctx.getStart());
        String identifier = ctx.Identifier().getText();
        ExprNode initExpr = null;
        if(ctx.expression() != null)
            initExpr = (ExprNode) visit(ctx.expression());
        return new VarNode(pos, null, identifier, initExpr);
    }

    @Override
    public ASTNode visitParamList(MxParser.ParamListContext ctx) {
        // return VarListNode
        Position pos = new Position(ctx.getStart());
        ArrayList<VarNode> params = new ArrayList<>();
        for(var it: ctx.param()) {
            params.add((VarNode) visit(it));
        }
        return new VarListNode(pos, params);
    }

    @Override
    public ASTNode visitParam(MxParser.ParamContext ctx) {
        // return VarNode: initExpr is null
        Position pos = new Position(ctx.getStart());
        TypeNode typeNode = (TypeNode) visit(ctx.type());
        String identifier = ctx.Identifier().getText();
        return new VarNode(pos, typeNode, identifier, null);
    }

    @Override
    public ASTNode visitType(MxParser.TypeContext ctx) {
        // return TypeNode
        Position pos = new Position(ctx.getStart());
        if(ctx.singleType() != null)
            return visit(ctx.singleType());
        else if(ctx.arrayType() != null)
            return visit(ctx.arrayType());
        else if(ctx.Void() != null)
            return new VoidTypeNode(pos);
        else return null;
    }

    @Override
    public ASTNode visitBasicType(MxParser.BasicTypeContext ctx) {
        // return SingleTypeNode
        Position pos = new Position(ctx.getStart());
        if(ctx.Bool() != null)
            return new SingleTypeNode(pos, "bool");
        else if(ctx.Int() != null)
            return new SingleTypeNode(pos, "int");
        else if(ctx.String() != null)
            return new SingleTypeNode(pos, "string");
        else return null;
    }

    @Override
    public ASTNode visitSingleType(MxParser.SingleTypeContext ctx) {
        // return SingleTypeNode
        Position pos = new Position(ctx.getStart());
        if(ctx.Identifier() != null)
            return new SingleTypeNode(pos, ctx.Identifier().getText());
        else if(ctx.basicType() != null)
            return visit(ctx.basicType());
        else return null;
    }

    @Override
    public ASTNode visitArrayType(MxParser.ArrayTypeContext ctx) {
        // return ArrayTypeNode
        Position pos = new Position(ctx.getStart());
        SingleTypeNode baseTypeNode = (SingleTypeNode) visit(ctx.singleType());
        int dimension = ctx.LeftBracket().size();
        return new ArrayTypeNode(pos, baseTypeNode, dimension);
    }

    @Override
    public ASTNode visitStatement(MxParser.StatementContext ctx) {
        // return StmtNode: no need to override
        return super.visitStatement(ctx);
    }

    @Override
    public ASTNode visitSuite(MxParser.SuiteContext ctx) {
        // return BlockStmtNode
        Position pos = new Position(ctx.getStart());
        ArrayList<StmtNode> statements = new ArrayList<>();
        for(var it: ctx.statement()) {
            statements.add((StmtNode) visit(it));
        }
        return new BlockStmtNode(pos, statements);
    }

    @Override
    public ASTNode visitVarDefStmt(MxParser.VarDefStmtContext ctx) {
        // return VarListNode
        return visit(ctx.varDef());
    }

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

    @Override
    public ASTNode visitForStmt(MxParser.ForStmtContext ctx) {
        // return ForStmtNode
        Position pos = new Position(ctx.getStart());
        ExprNode initExpr = null, condition = null, increaseExpr = null;
        if(ctx.init != null)
            initExpr = (ExprNode) visit(ctx.init);
        if(ctx.cond != null)
            condition = (ExprNode) visit(ctx.cond);
        if(ctx.incr != null)
            increaseExpr = (ExprNode) visit(ctx.incr);
        StmtNode statement = (StmtNode) visit(ctx.statement());
        return new ForStmtNode(pos, initExpr, condition, increaseExpr, statement);
    }

    @Override
    public ASTNode visitWhileStmt(MxParser.WhileStmtContext ctx) {
        // return WhileStmtNode
        Position pos = new Position(ctx.getStart());
        ExprNode condition = (ExprNode) visit(ctx.expression());
        StmtNode statement = (StmtNode) visit(ctx.statement());
        return new WhileStmtNode(pos, condition, statement);
    }

    @Override
    public ASTNode visitReturnStmt(MxParser.ReturnStmtContext ctx) {
        // return ReturnStmtNode
        Position pos = new Position(ctx.getStart());
        ExprNode returnExpr = null;
        if(ctx.expression() != null)
            returnExpr = (ExprNode) visit(ctx.expression());
        return new ReturnStmtNode(pos, returnExpr);
    }

    @Override
    public ASTNode visitBreakStmt(MxParser.BreakStmtContext ctx) {
        // return BreakStmtNode
        return new BreakStmtNode(new Position(ctx.getStart()));
    }

    @Override
    public ASTNode visitContinueStmt(MxParser.ContinueStmtContext ctx) {
        // return ContinueStmtNode
        return new ContinueStmtNode(new Position(ctx.getStart()));
    }

    @Override
    public ASTNode visitSimpleStmt(MxParser.SimpleStmtContext ctx) {
        // return SimpleStmtNode
        Position pos = new Position(ctx.getStart());
        ExprNode expression = null;
        if(ctx.expression() != null)
            expression = (ExprNode) visit(ctx.expression());
        return new SimpleStmtNode(pos, expression);
    }

    @Override
    public ASTNode visitExpressionList(MxParser.ExpressionListContext ctx) {
        // return FuncCallExprNode: only params, funcExpr not set yet
        Position pos = new Position(ctx.getStart());
        ArrayList<ExprNode> params = new ArrayList<>();
        for(var it: ctx.expression())
            params.add((ExprNode) visit(it));
        return new FuncCallExprNode(pos, null, params);
    }

    @Override
    public ASTNode visitAtomExpr(MxParser.AtomExprContext ctx) {
        // return ExprNode ((expr) | id | this | literal)
        return visit(ctx.primary());
    }

    @Override
    public ASTNode visitMemberExpr(MxParser.MemberExprContext ctx) {
        // return MemberExprNode
        Position pos = new Position(ctx.getStart());
        ExprNode prefixExpr = (ExprNode) visit(ctx.expression());
        String memberName = ctx.Identifier().getText();
        return new MemberExprNode(pos, prefixExpr, memberName);
    }

    @Override
    public ASTNode visitNewExpr(MxParser.NewExprContext ctx) {
        // return NewExprNode
        Position pos = new Position(ctx.getStart());
        NewExprNode newExprNode = (NewExprNode) visit(ctx.creator());
        return new NewExprNode(pos, newExprNode.getBaseTypeNode(), newExprNode.getExprInBrackets(),
                newExprNode.getDimension());
    }

    @Override
    public ASTNode visitSubscriptExpr(MxParser.SubscriptExprContext ctx) {
        // return SubscriptExprNode
        Position pos = new Position(ctx.getStart());
        ExprNode newNode = (ExprNode) visit(ctx.expression(0));
        ExprNode indexNode = (ExprNode) visit(ctx.expression(1));
        return new SubscriptExprNode(pos, newNode, indexNode);
    }

    @Override
    public ASTNode visitFuncCallExpr(MxParser.FuncCallExprContext ctx) {
        // return FuncCallExprNode
        Position pos = new Position(ctx.getStart());
        ExprNode funcExpr = (ExprNode) visit(ctx.expression());
        ArrayList<ExprNode> params = new ArrayList<>();
        if(ctx.expressionList() != null)
            params = ((FuncCallExprNode) visit(ctx.expressionList())).getParams();
        return new FuncCallExprNode(pos, funcExpr, params);
    }

    @Override
    public ASTNode visitSuffixExpr(MxParser.SuffixExprContext ctx) {
        // return SuffixExprNode
        Position pos = new Position(ctx.getStart());
        ExprNode exprNode = (ExprNode) visit(ctx.expression());
        SuffixExprNode.Operator operator = switch (ctx.suffix.getText()) {
            case "++" -> SuffixExprNode.Operator.sufPlus;
            case "--" -> SuffixExprNode.Operator.sufMinus;
            default -> null;
        };
        return new SuffixExprNode(pos, exprNode, operator);
    }

    @Override
    public ASTNode visitPrefixExpr(MxParser.PrefixExprContext ctx) {
        // return PrefixExprNode
        Position pos = new Position(ctx.getStart());
        ExprNode exprNode = (ExprNode) visit(ctx.expression());
        PrefixExprNode.Operator operator = switch (ctx.prefix.getText()) {
            case "++" -> PrefixExprNode.Operator.PrePlus;
            case "--" -> PrefixExprNode.Operator.PreMinus;
            case "+" -> PrefixExprNode.Operator.SignPos;
            case "-" -> PrefixExprNode.Operator.SignNeg;
            case "~" -> PrefixExprNode.Operator.BitwiseNot;
            case "!" -> PrefixExprNode.Operator.LogicalNot;
            default -> null;
        };
        return new PrefixExprNode(pos, operator, exprNode);
    }

    @Override
    public ASTNode visitBinaryExpr(MxParser.BinaryExprContext ctx) {
        // return BinaryExprNode
        Position pos = new Position(ctx.getStart());
        ExprNode lhsExpr = (ExprNode) visit(ctx.expression(0));
        ExprNode rhsExpr = (ExprNode) visit(ctx.expression(1));
        BinaryExprNode.Operator operator = switch (ctx.op.getText()) {
            case "*" -> BinaryExprNode.Operator.Mul;
            case "/" -> BinaryExprNode.Operator.Div;
            case "%" -> BinaryExprNode.Operator.Mod;
            case "+" -> BinaryExprNode.Operator.Add;
            case "-" -> BinaryExprNode.Operator.Sub;
            case "<<" -> BinaryExprNode.Operator.ShiftLeft;
            case ">>" -> BinaryExprNode.Operator.ShiftRight;
            case "<" -> BinaryExprNode.Operator.Less;
            case ">" -> BinaryExprNode.Operator.Greater;
            case "<=" -> BinaryExprNode.Operator.LessEqual;
            case ">=" -> BinaryExprNode.Operator.GreaterEqual;
            case "==" -> BinaryExprNode.Operator.Equal;
            case "!=" -> BinaryExprNode.Operator.NotEqual;
            case "&" -> BinaryExprNode.Operator.BitwiseAnd;
            case "^" -> BinaryExprNode.Operator.BitwiseXor;
            case "|" -> BinaryExprNode.Operator.BitwiseOr;
            case "&&" -> BinaryExprNode.Operator.LogicalAnd;
            case "||" -> BinaryExprNode.Operator.LogicalOr;
            default -> null;
        };
        return new BinaryExprNode(pos, lhsExpr, operator, rhsExpr);
    }

    @Override
    public ASTNode visitAssignExpr(MxParser.AssignExprContext ctx) {
        // return AssignExprNode
        Position pos = new Position(ctx.getStart());
        ExprNode lhsExpr = (ExprNode) visit(ctx.expression(0));
        ExprNode rhsExpr = (ExprNode) visit(ctx.expression(1));
        return new AssignExprNode(pos, lhsExpr, rhsExpr);
    }

    @Override
    public ASTNode visitPrimary(MxParser.PrimaryContext ctx) {
        // return ExprNode ((expr) | id | this | literal)
        Position pos = new Position(ctx.getStart());
        if(ctx.expression() != null)
            return visit(ctx.expression());
        else if(ctx.This() != null)
            return new ThisExprNode(pos);
        else if(ctx.Identifier() != null)
            return new IdExprNode(pos, ctx.Identifier().getText());
        else if(ctx.literal() != null)
            return visit(ctx.literal());
        else return null;
    }

    @Override
    public ASTNode visitLiteral(MxParser.LiteralContext ctx) {
        // return LiteralExprNode
        Position pos = new Position(ctx.getStart());
        if(ctx.IntLiteral() != null)
            return new IntLiteralNode(pos, Integer.parseInt(ctx.getText()));
        else if(ctx.BoolLiteral() != null)
            return new BoolLiteralNode(pos, ctx.getText().equals("true"));
        else if(ctx.StringLiteral() != null)
            return new StringLiteralNode(pos, ctx.getText().substring(1, ctx.getText().length() - 1));
        else return new NullLiteralNode(pos);
    }

    @Override
    public ASTNode visitErrorCreator(MxParser.ErrorCreatorContext ctx) {
        throw new SyntaxError("Invalid syntax of creator", new Position(ctx.getStart()));
    }

    @Override
    public ASTNode visitArrayCreator(MxParser.ArrayCreatorContext ctx) {
        Position pos = new Position(ctx.getStart());
        TypeNode baseTypeNode = (TypeNode) visit(ctx.singleType());
        ArrayList<ExprNode> exprInBrackets = new ArrayList<>();
        for(var it: ctx.expression())
            exprInBrackets.add((ExprNode) visit(it));
        int dimension = ctx.LeftBracket().size();
        return new NewExprNode(pos, baseTypeNode, exprInBrackets, dimension);
    }

    @Override
    public ASTNode visitClassCreator(MxParser.ClassCreatorContext ctx) {
        Position pos = new Position(ctx.getStart());
        TypeNode baseTypeNode = (TypeNode) visit(ctx.singleType());
        return new NewExprNode(pos, baseTypeNode, new ArrayList<>(), 0);
    }

    @Override
    public ASTNode visitBasicCreator(MxParser.BasicCreatorContext ctx) {
        Position pos = new Position(ctx.getStart());
        TypeNode baseTypeNode = (TypeNode) visit(ctx.singleType());
        return new NewExprNode(pos, baseTypeNode, new ArrayList<>(), 0);
    }
}
