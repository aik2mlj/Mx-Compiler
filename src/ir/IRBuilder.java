package ir;

import ast.*;
import ir.instruction.*;
import ir.operand.*;
import ir.type.*;
import ir.type.IntType;
import ir.type.VoidType;
import util.Scope;
import util.entity.FuncEntity;
import util.entity.VarEntity;
import util.type.*;
import util.type.ArrayType;

import java.util.ArrayList;
import java.util.Stack;

public class IRBuilder implements ASTVisitor {
    private Module module;

    private Scope globalScope;
    private TypeTable astTypeTable;
    private IRTypeTable irTypeTable;

    private Function currentFunc;
    private Block currentBlock;

    private Function initFunc;
    private Stack<Block> loopBreakBlock;
    private Stack<Block> loopContinueBlock;

    public IRBuilder(Scope globalScope, TypeTable astTypeTable) {
        // inherit globalScope & astTypeTable from SemanticChecker.
        this.globalScope = globalScope;
        this.astTypeTable = astTypeTable;
        currentFunc = null;
        currentBlock = null;

        // built-in functions added in Module.
        module = new Module();
        irTypeTable = new IRTypeTable(module, astTypeTable);

        initFunc = new Function(module, "__init__", new VoidType(), new ArrayList<>());
        initFunc.initialize();
        module.addFunction(initFunc);

        loopBreakBlock = new Stack<>();
        loopContinueBlock = new Stack<>();
    }

    public Module getModule() {
        return module;
    }

    @Override
    public void visit(ProgramNode node) {
        // get methodList
        for (var unit : node.getProgramUnitNodes()) {
            if (unit instanceof ClassDefNode) {
                var classType = ((ClassDefNode) unit).getClassType();
                if (((ClassDefNode) unit).hasConstructor())
                    ((ClassDefNode) unit).getConstructor().addFunctionToModule(module, astTypeTable, irTypeTable);
                ((ClassDefNode) unit).getMethods().forEach(method ->
                        method.addFunctionToModule(module, astTypeTable, irTypeTable));
            }
        }
        // get functions
        for (var unit : node.getProgramUnitNodes()) {
            if (unit instanceof FuncDefNode)
                ((FuncDefNode) unit).addFunctionToModule(module, astTypeTable, irTypeTable);
        }
        currentFunc = initFunc;
        currentBlock = initFunc.getEntryBlock();
        // declare globalVariable
        for (var unit : node.getProgramUnitNodes()) {
            if (unit instanceof VarNode) unit.accept(this);
        }
        currentBlock.appendBrInstTo_U(currentFunc.getRetBlock());
        currentFunc.appendRetBlock();

        currentFunc = null;
        currentBlock = null;

        // define classes
        for (var unit : node.getProgramUnitNodes())
            if (unit instanceof ClassDefNode) unit.accept(this);
        for (var unit : node.getProgramUnitNodes()) // define functions
            if (unit instanceof FuncDefNode) unit.accept(this);
    }

    @Override
    public void visit(SingleTypeNode node) {
    }

    @Override
    public void visit(ArrayTypeNode node) {
    }

    @Override
    public void visit(VoidTypeNode node) {
    }

    @Override
    public void visit(ClassDefNode node) {
        // struct already built in IRTypeTable, here traverse constructor & methods.
        if (node.hasConstructor())
            node.getConstructor().accept(this);
        for (var methods : node.getMethods()) methods.accept(this);
    }

    @Override
    public void visit(FuncDefNode node) {
        String name;
        if (node.getScope().inClassScope()) {
            name = node.getScope().getClassType().getTypeName() + "__" + node.getIdentifier();
        } else name = node.getIdentifier();

        // get IRFunction in module.
        Function function = module.getFunction(name);
        assert function != null;

        currentFunc = function;
        currentBlock = function.getEntryBlock();
        node.getSuite().accept(this);
        currentBlock.appendBrInstTo_U(currentFunc.getRetBlock());
        currentFunc.appendRetBlock();

        // add call "main" to "__init__" function
        if (node.getIdentifier().equals("main")) {
            var callInit = new CallInst(currentBlock, module.getFunction("__init__"), new ArrayList<>(), null);
            currentFunc.getEntryBlock().pushFrontInst(callInit);
        }
        currentFunc = null;
        currentBlock = null;
    }

    @Override
    public void visit(VarNode node) {
        // varNode here can only be GlobalVars / local vars, params are handled in visit(FuncDefNode)
        Type astType = astTypeTable.getType(node.getTypeNode());
        IRType irType = astType.getIRType(irTypeTable);
        String name = node.getIdentifier();
        VarEntity varEntity = node.getScope().getVarEntity(name);

        if (node.getScope() == globalScope) {
            // globalVars
            GlobalVar globalVar = new GlobalVar(new PointerType(irType), name + ".addr", irType.getDefaultValue());
            Operand initValue;
            if (node.hasInitExpr()) {
                var initExpr = node.getInitExpr();
                initExpr.accept(this); // traverse the exprNode
                initValue = initExpr.getResult(); // result
                if (!(initValue instanceof Constant)) { // not Constant: store the value
//                    throw new IRBuildingError("GlobalVar has initExpr that is not constant.", node.getPos());
                    currentBlock.appendInst(new StoreInst(currentBlock, initValue, globalVar));
                    initValue = astType.getDefaultValue();
                }
            } else
                initValue = irType.getDefaultValue();
            globalVar.setInitValue(initValue);
            module.addGlobalVar(globalVar);
            varEntity.setAllocaAddr(globalVar);
        } else {
            Register addrReg = new Register(new PointerType(irType), name + ".addr", currentFunc);
            Operand initValue;
            if (node.hasInitExpr()) {
                var initExpr = node.getInitExpr();
                initExpr.accept(this); // traverse ExprNode
                initValue = initExpr.getResult();
                currentBlock.appendInst(new StoreInst(currentBlock, initValue, addrReg));
            }

            Block entryBlock = currentFunc.getEntryBlock();
//            if (!node.hasInitExpr()) // if no initExpr, store default value to it at the beginning
            // change: always store default value here. Will be removed in SSAConstructor.
            entryBlock.pushFrontInst(new StoreInst(entryBlock, astType.getDefaultValue(), addrReg));
            entryBlock.pushFrontInst(new AllocaInst(entryBlock, addrReg, irType));
            currentFunc.getAllocRegs().add(addrReg);
            varEntity.setAllocaAddr(addrReg);
        }

        if (node.hasInitExpr() && node.getInitExpr() instanceof NewExprNode
                && ((NewExprNode) node.getInitExpr()).hasExpansionBlock()) {
            ((NewExprNode) node.getInitExpr()).getExpansionBlock().accept(this);
        }
    }

    @Override
    public void visit(VarListNode node) {
    }

    @Override
    public void visit(BlockStmtNode node) {
        node.getStatements().forEach(stmtNode -> stmtNode.accept(this));
    }

    @Override
    public void visit(VarDefStmtNode node) {
        node.getVarNodes().forEach(varNode -> varNode.accept(this));
    }

    @Override
    public void visit(IfStmtNode node) {
        node.getCondition().accept(this); // traverse the condition expr
        Operand condValue = node.getCondition().getResult();

        Block thenBlock = new Block(currentFunc, "if.then");
        currentFunc.appendBlock(thenBlock);
        Block elseBlock = null;
        Block endBlock = new Block(currentFunc, "if.end");
        currentFunc.appendBlock(endBlock);

        if (node.hasFalseStmt()) {
            elseBlock = new Block(currentFunc, "if.else");
            currentFunc.appendBlock(elseBlock);
            currentBlock.appendInst(new BrInst(currentBlock, condValue, thenBlock, elseBlock)); // br to then, else
        } else
            currentBlock.appendInst(new BrInst(currentBlock, condValue, thenBlock, endBlock)); // br to then, end

        currentBlock = thenBlock;
        node.getTrueStmt().accept(this);
        currentBlock.appendBrInstTo_U(endBlock);

        if (node.hasFalseStmt()) {
            currentBlock = elseBlock;
            node.getFalseStmt().accept(this);
            currentBlock.appendBrInstTo_U(endBlock);
        }

        currentBlock = endBlock;
    }

    @Override
    public void visit(ForStmtNode node) {
        Block condBlock = node.hasCondition() ? new Block(currentFunc, "for.cond") : null;
        Block bodyBlock = new Block(currentFunc, "for.body");
        Block incBlock = node.hasIncreaseExpr() ? new Block(currentFunc, "for.inc") : null;
        Block endBlock = new Block(currentFunc, "for.end");

        // -------------- preCond
        if (node.getPreCondDef() != null) {
            node.getPreCondDef().accept(this);
        }
        // --------------

        if (node.hasInitExpr()) {
            node.getInitExpr().accept(this);
        }
        if (node.hasCondition()) {
            currentFunc.appendBlock(condBlock);
            currentBlock.appendBrInstTo_U(condBlock);

            currentBlock = condBlock;
            node.getCondition().accept(this);
            var condValue = node.getCondition().getResult();
            currentBlock.appendInst(new BrInst(currentBlock, condValue, bodyBlock, endBlock));
            currentFunc.appendBlock(bodyBlock);

            loopBreakBlock.push(endBlock);
            loopContinueBlock.push(node.hasIncreaseExpr() ? incBlock : condBlock);

            currentBlock = bodyBlock;
            node.getStatement().accept(this);
            currentBlock.appendBrInstTo_U(node.hasIncreaseExpr() ? incBlock : condBlock);

            loopBreakBlock.pop();
            loopContinueBlock.pop();

            if (node.hasIncreaseExpr()) {
                currentBlock = incBlock;
                node.getIncreaseExpr().accept(this);
                currentBlock.appendBrInstTo_U(condBlock);
                currentFunc.appendBlock(incBlock);
            }
        } else {
            currentBlock.appendBrInstTo_U(bodyBlock);

            loopBreakBlock.push(endBlock);
            loopContinueBlock.push(node.hasIncreaseExpr() ? incBlock : bodyBlock);

            currentBlock = bodyBlock;
            node.getStatement().accept(this);
            currentBlock.appendBrInstTo_U(node.hasIncreaseExpr() ? incBlock : bodyBlock);
            currentFunc.appendBlock(bodyBlock);

            loopBreakBlock.pop();
            loopContinueBlock.pop();

            if (node.hasIncreaseExpr()) {
                currentBlock = incBlock;
                node.getIncreaseExpr().accept(this);
                currentBlock.appendBrInstTo_U(bodyBlock);
                currentFunc.appendBlock(incBlock);
            }
        }
        currentBlock = endBlock;
        currentFunc.appendBlock(endBlock);
    }

    @Override
    public void visit(WhileStmtNode node) {
        Block condBlock = new Block(currentFunc, "while.cond");
        Block bodyBlock = new Block(currentFunc, "while.body");
        Block endBlock = new Block(currentFunc, "while.end");

        currentFunc.appendBlock(condBlock);
        currentFunc.appendBlock(bodyBlock);
        currentFunc.appendBlock(endBlock);

        currentBlock.appendBrInstTo_U(condBlock);

        currentBlock = condBlock;
        node.getCondition().accept(this);
        var condValue = node.getCondition().getResult();
        currentBlock.appendInst(new BrInst(currentBlock, condValue, bodyBlock, endBlock));

        loopBreakBlock.push(endBlock);
        loopContinueBlock.push(condBlock);

        currentBlock = bodyBlock;
        node.getStatement().accept(this);
        currentBlock.appendBrInstTo_U(condBlock);

        loopBreakBlock.pop();
        loopContinueBlock.pop();

        currentBlock = endBlock;
    }

    @Override
    public void visit(ReturnStmtNode node) {
        // store -> retval
        // br_U retBlock
        if (node.hasReturnExpr()) {
            node.getReturnExpr().accept(this);
            var retValue = node.getReturnExpr().getResult();
            currentBlock.appendInst(new StoreInst(currentBlock, retValue, currentFunc.getRetValue()));
        }
        currentBlock.appendBrInstTo_U(currentFunc.getRetBlock());
    }

    @Override
    public void visit(BreakStmtNode node) {
        currentBlock.appendBrInstTo_U(loopBreakBlock.peek());
    }

    @Override
    public void visit(ContinueStmtNode node) {
        currentBlock.appendBrInstTo_U(loopContinueBlock.peek());
    }

    @Override
    public void visit(SimpleStmtNode node) {
        if (node.hasExpression())
            node.getExpression().accept(this);
    }

    @Override
    public void visit(MemberExprNode node) {
        //* getelementptr pointer indices -> ptr
        //* load ptr -> load_value
        node.getPrefixExpr().accept(this);
        Operand pointer = node.getPrefixExpr().getResult();

        Type classType = node.getPrefixExpr().getType();
        assert classType instanceof ClassType;
        String name = node.getMemberName();
        ArrayList<VarEntity> memberList = ((ClassType) classType).getMembers();
        int pos = 0;
        for (; pos < memberList.size(); ++pos)
            if (memberList.get(pos).getName().equals(name)) break;

        ArrayList<Operand> indices = new ArrayList<>();
        indices.add(new ConstInt(IntType.BitWidth.i32, 0));
        indices.add(new ConstInt(IntType.BitWidth.i32, pos));
        IRType irType = astTypeTable.getType(memberList.get(pos).getTypeNode()).getIRType(irTypeTable);
        String prefixString = node.getPrefixExpr().getText().replaceAll("\\[|\\]", "_");
        Register ptr = new Register(new PointerType(irType), prefixString + "." + name + ".ptr", currentFunc);
        currentBlock.appendInst(new GetElementPtrInst(currentBlock, pointer, indices, ptr));
        Register loadValue = new Register(irType, prefixString + "." + name, currentFunc);
        currentBlock.appendInst(new LoadInst(currentBlock, ptr, loadValue));

        node.setResult(loadValue);
        node.setLvalueResult(ptr);
    }

    @Override
    public void visit(MethodExprNode node) {
        IRType irType = node.getType().getIRType(irTypeTable);

        node.getPrefixExpr().accept(this);
        Operand prefixResult = node.getPrefixExpr().getResult();
        Type prefixAstType = node.getPrefixExpr().getType();
        if (prefixAstType instanceof ArrayType) {
            // get size of the array
            //* bitcast prefixResult to i32* (if necessary)
            //* getelementptr -1 (where the size info is stored)
            //* load -> size
            assert node.getMethodName().equals("size");
            Register pointer;
            if (!prefixResult.getType().equals(new PointerType(new IntType(IntType.BitWidth.i32)))) {
                pointer = new Register(new PointerType(new IntType(IntType.BitWidth.i32)), "cast_i32", currentFunc);
                currentBlock.appendInst(new BitcastToInst(currentBlock, prefixResult, pointer));
            } else pointer = (Register) prefixResult;
            Register arraySizeptr = new Register(new PointerType(new IntType(IntType.BitWidth.i32)), "arraysizeptr", currentFunc);
            ArrayList<Operand> indices = new ArrayList<>();
            indices.add(new ConstInt(IntType.BitWidth.i32, -1));
            currentBlock.appendInst(new GetElementPtrInst(currentBlock, pointer, indices, arraySizeptr));
            Register sizeReg = new Register(new IntType(IntType.BitWidth.i32), "arraysize", currentFunc);
            currentBlock.appendInst(new LoadInst(currentBlock, arraySizeptr, sizeReg));

            node.setResult(sizeReg);
        } else {
            // a class / string builtIn method
            Function function;
            if (prefixAstType instanceof ClassType) {
                function = module.getFunction(prefixAstType.getTypeName() + "__" + node.getMethodName());
            } else {
                assert prefixAstType instanceof StringType;
                function = module.getBuiltInFunction("_string_" + node.getMethodName());
            }
            ArrayList<Operand> paramOperands = new ArrayList<>();
            paramOperands.add(prefixResult); // add this ptr for the class entity
            node.getParams().forEach(param -> {
                param.accept(this);
                paramOperands.add(param.getResult());
            });
            Register callReg = (irType instanceof VoidType) ? null : new Register(irType, "call", currentFunc);
            currentBlock.appendInst(new CallInst(currentBlock, function, paramOperands, callReg));

            node.setResult(callReg);
        }
        node.setLvalueResult(null);
    }

    @Override
    public void visit(NewExprNode node) {
        if (node.getDimension() == 0) {
            // new class
            //* call "malloc"
            //* bitcast to class type
            //* call constructor
            Type classType = node.getType();
            assert classType instanceof ClassType;
            IRType irType = classType.getIRType(irTypeTable); // pointer

            // use builtin malloc function to allocate memory for the object.
            Function mallocFunc = module.getBuiltInFunction("malloc");
            int size = ((PointerType) irType).getBaseType().getBytes();
            ArrayList<Operand> parameters = new ArrayList<>();
            parameters.add(new ConstInt(IntType.BitWidth.i32, size));

            Register mallocReg = new Register(new PointerType(new IntType(IntType.BitWidth.i8)), "malloc", currentFunc);
            Register castReg = new Register(irType, "classptr", currentFunc);
            currentBlock.appendInst(new CallInst(currentBlock, mallocFunc, parameters, mallocReg));
            currentBlock.appendInst(new BitcastToInst(currentBlock, mallocReg, castReg));

            if (((ClassType) classType).hasConstructor()) {
                Function constructorFunc = module.getFunction(classType.getTypeName() + "__" + classType.getTypeName());
                parameters = new ArrayList<>();
                parameters.add(castReg);
                currentBlock.appendInst(new CallInst(currentBlock, constructorFunc, parameters, null));
            }

            node.setResult(castReg);
        } else {
            // new array
            // multi-dimensional NewExpr has been expanded to for-loops in SemanticChecker.
            //* mul bytes * size -> malloc_size_multmp
            //* add malloc_size_multmp + 4 -> malloc_size // here 4 for storing i32 size
            //* call "malloc"
            //* bitcast to i32
            //* store size
            //* getelementptr +4
            //* bitcast to array_type
            assert node.getType() instanceof ArrayType;
            Function mallocFunc = module.getBuiltInFunction("malloc");
            IRType irType = node.getType().getIRType(irTypeTable);

            // traverse the first element of exprInBrackets
            ExprNode onlyExpr = node.getExprInBrackets().get(0);
            onlyExpr.accept(this);
            Operand exprResult = onlyExpr.getResult();
            Operand size; // malloc size
            if (exprResult instanceof Constant) {
                assert exprResult instanceof ConstInt;
                size = new ConstInt(IntType.BitWidth.i32, 4 + irType.getBytes() * ((ConstInt) exprResult).getValue());
            } else {
                // construct "mul" & "add" instructions for counting size: bytes * size + 4
                Register size_multmp = new Register(new IntType(IntType.BitWidth.i32), "mallocsize_multmp", currentFunc);
                size = new Register(new IntType(IntType.BitWidth.i32), "malloc_size", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.mul,
                        new ConstInt(IntType.BitWidth.i32, irType.getBytes()), exprResult, size_multmp));
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.add,
                        new ConstInt(IntType.BitWidth.i32, 4), size_multmp, (Register) size));
            }
            ArrayList<Operand> parameters = new ArrayList<>();
            parameters.add(size);

            Register mallocReg = new Register(new PointerType(new IntType(IntType.BitWidth.i8)), "malloc", currentFunc);
            Register arraySizeptr = new Register(new PointerType(new IntType(IntType.BitWidth.i32)), "arraysizeptr", currentFunc);
            currentBlock.appendInst(new CallInst(currentBlock, mallocFunc, parameters, mallocReg));
            currentBlock.appendInst(new BitcastToInst(currentBlock, mallocReg, arraySizeptr));
            currentBlock.appendInst(new StoreInst(currentBlock, exprResult, arraySizeptr));
            Register arrayptr = new Register(new PointerType(new IntType(IntType.BitWidth.i32)), "arrayptr", currentFunc);
            ArrayList<Operand> indices = new ArrayList<>();
            indices.add(new ConstInt(IntType.BitWidth.i32, 1));
            currentBlock.appendInst(new GetElementPtrInst(currentBlock, arraySizeptr, indices, arrayptr));
            Register castReg = new Register(irType, "arrayptr", currentFunc);
            currentBlock.appendInst(new BitcastToInst(currentBlock, arrayptr, castReg));

            node.setResult(castReg);
        }
        node.setLvalueResult(null);
    }

    @Override
    public void visit(SubscriptExprNode node) {
        //* getelementptr pointer indices
        //* load result
        IRType irType = node.getType().getIRType(irTypeTable);
        node.getNameExpr().accept(this);
        Operand pointer = node.getNameExpr().getResult();

        node.getIndexExpr().accept(this);
        ArrayList<Operand> indices = new ArrayList<>();
        indices.add(node.getIndexExpr().getResult());

        Register arrayIdxPtr = new Register(new PointerType(irType), "arrayidx.ptr", currentFunc);
        Register arrayIdxLoad = new Register(irType, "arrayidx", currentFunc);
        currentBlock.appendInst(new GetElementPtrInst(currentBlock, pointer, indices, arrayIdxPtr));
        currentBlock.appendInst(new LoadInst(currentBlock, arrayIdxPtr, arrayIdxLoad));

        node.setResult(arrayIdxLoad);
        node.setLvalueResult(arrayIdxPtr);
    }

    @Override
    public void visit(FuncCallExprNode node) {
        IRType irType = node.getType().getIRType(irTypeTable);

        FuncEntity funcEntity = node.getScope().getFuncEntity(node.getFuncName());
        if (funcEntity.getEntityType() == FuncEntity.EntityType.Function) {
            // just a function call
            //* call function
            Function function;
            if (module.getFunction(node.getFuncName()) != null)
                function = module.getFunction(node.getFuncName());
            else function = module.getBuiltInFunction(node.getFuncName());
            assert function != null;
            ArrayList<Operand> paramOperands = new ArrayList<>();
            node.getParams().forEach(param -> {
                param.accept(this);
                paramOperands.add(param.getResult());
            });
            Register callReg = (irType instanceof VoidType) ? null : new Register(irType, "call", currentFunc);
            currentBlock.appendInst(new CallInst(currentBlock, function, paramOperands, callReg));

            node.setResult(callReg);
        } else {
            // a method call in a class
            //* load this from this.addr
            //* call method
            ClassType classType = (ClassType) node.getScope().getClassType();
            String name = classType.getTypeName() + "__" + node.getFuncName();
            Function function = module.getFunction(name);

            Register thisAddr = currentFunc.getThisAddr();
            IRType thisType = ((PointerType) thisAddr.getType()).getBaseType();
            Register thisReg = new Register(thisType, "this", currentFunc);
            currentBlock.appendInst(new LoadInst(currentBlock, thisAddr, thisReg));
            ArrayList<Operand> paramOperands = new ArrayList<>();
            paramOperands.add(thisReg); // add first param: this
            node.getParams().forEach(param -> {
                param.accept(this);
                paramOperands.add(param.getResult());
            });
            Register callReg = (irType instanceof VoidType) ? null : new Register(irType, "method_call", currentFunc);
            currentBlock.appendInst(new CallInst(currentBlock, function, paramOperands, callReg));

            node.setResult(callReg);
        }
        node.setLvalueResult(null);
    }

    @Override
    public void visit(SuffixExprNode node) {
        //* add/sub 1 -> result
        //* store result -> lvalue
        node.getExprNode().accept(this);
        Operand exprResult = node.getExprNode().getResult();
        Operand exprLvalueResult = node.getExprNode().getLvalueResult();

        Register result;
        if (node.getOperator() == SuffixExprNode.Operator.sufPlus) {
            result = new Register(new IntType(IntType.BitWidth.i32), "inc", currentFunc);
            currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.add, exprResult,
                    new ConstInt(IntType.BitWidth.i32, 1), result));
        } else {
            result = new Register(new IntType(IntType.BitWidth.i32), "dec", currentFunc);
            currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.sub, exprResult,
                    new ConstInt(IntType.BitWidth.i32, 1), result));
        }
        currentBlock.appendInst(new StoreInst(currentBlock, result, exprLvalueResult));

        node.setResult(exprResult);
        node.setLvalueResult(null);
    }

    @Override
    public void visit(PrefixExprNode node) {
        //* some binary inst -> result
        //* store result -> lvalue (if PrePlus/Minus)
        node.getExprNode().accept(this);
        Operand exprResult = node.getExprNode().getResult();
        Operand exprLvalueResult = node.getExprNode().getLvalueResult();

        Register result;
        node.setLvalueResult(null);
        switch (node.getOperator()) {
            case SignPos -> result = (Register) exprResult;
            case SignNeg -> {
                result = new Register(new IntType(IntType.BitWidth.i32), "sub", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.sub,
                        new ConstInt(IntType.BitWidth.i32, 0), exprResult, result));
            }
            case PrePlus -> {
                result = new Register(new IntType(IntType.BitWidth.i32), "inc", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.add, exprResult,
                        new ConstInt(IntType.BitWidth.i32, 1), result));
                currentBlock.appendInst(new StoreInst(currentBlock, result, exprLvalueResult));
                // this is lvalue!
                node.setLvalueResult(exprLvalueResult);
            }
            case PreMinus -> {
                result = new Register(new IntType(IntType.BitWidth.i32), "dec", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.sub, exprResult,
                        new ConstInt(IntType.BitWidth.i32, 1), result));
                currentBlock.appendInst(new StoreInst(currentBlock, result, exprLvalueResult));
                // this is lvalue!
                node.setLvalueResult(exprLvalueResult);
            }
            case BitwiseNot -> {
                result = new Register(new IntType(IntType.BitWidth.i32), "neg", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.xor, exprResult,
                        new ConstInt(IntType.BitWidth.i32, -1), result));
            }
            default -> { // LogicalNot
                result = new Register(new IntType(IntType.BitWidth.i1), "lnot", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.xor, exprResult,
                        new ConstBool(true), result));
            }
        }

        node.setResult(result);
    }

    @Override
    public void visit(BinaryExprNode node) {
        Operand result;
        IRType irType = node.getType().getIRType(irTypeTable);
        node.getLhsExpr().accept(this);
        Operand lhsResult = node.getLhsExpr().getResult();
        Operand rhsResult = null;
        if (node.getOperator() != BinaryExprNode.Operator.LogicalAnd && node.getOperator() != BinaryExprNode.Operator.LogicalOr) {
            node.getRhsExpr().accept(this);
            rhsResult = node.getRhsExpr().getResult();
        }

        switch (node.getOperator()) {
            case Mul -> {
                result = new Register(irType, "mul", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.mul, lhsResult,
                        rhsResult, (Register) result));
            }
            case Div -> {
                result = new Register(irType, "div", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.sdiv, lhsResult,
                        rhsResult, (Register) result));
            }
            case Mod -> {
                result = new Register(irType, "mod", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.srem, lhsResult,
                        rhsResult, (Register) result));
            }
            case Sub -> {
                result = new Register(irType, "sub", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.sub, lhsResult,
                        rhsResult, (Register) result));
            }
            case ShiftLeft -> {
                result = new Register(irType, "shl", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.shl, lhsResult,
                        rhsResult, (Register) result));
            }
            case ShiftRight -> {
                result = new Register(irType, "shr", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.ashr, lhsResult,
                        rhsResult, (Register) result));
            }
            case BitwiseAnd -> {
                result = new Register(irType, "and", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.and, lhsResult,
                        rhsResult, (Register) result));
            }
            case BitwiseXor -> {
                result = new Register(irType, "xor", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.xor, lhsResult,
                        rhsResult, (Register) result));
            }
            case BitwiseOr -> {
                result = new Register(irType, "or", currentFunc);
                currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.or, lhsResult,
                        rhsResult, (Register) result));
            }
            case Add -> {
                if (node.getLhsExpr().getType() instanceof util.type.IntType) {
                    result = new Register(irType, "add", currentFunc);
                    currentBlock.appendInst(new BinaryInst(currentBlock, BinaryInst.Operator.add, lhsResult,
                            rhsResult, (Register) result));
                } else {
                    Function function = module.getBuiltInFunction("_string_add");
                    ArrayList<Operand> params = new ArrayList<>();
                    params.add(lhsResult);
                    params.add(rhsResult);
                    result = new Register(irType, "string_add", currentFunc);
                    currentBlock.appendInst(new CallInst(currentBlock, function, params, (Register) result));
                }
            }
            case Less -> {
                if (node.getLhsExpr().getType() instanceof util.type.IntType) {
                    result = new Register(new IntType(IntType.BitWidth.i1), "lt", currentFunc);
                    currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.slt, lhsResult,
                            rhsResult, (Register) result));
                } else {
                    Function function = module.getBuiltInFunction("_string_lt");
                    ArrayList<Operand> params = new ArrayList<>();
                    params.add(lhsResult);
                    params.add(rhsResult);
                    result = new Register(new IntType(IntType.BitWidth.i1), "string_lt", currentFunc);
                    currentBlock.appendInst(new CallInst(currentBlock, function, params, (Register) result));
                }
            }
            case Greater -> {
                if (node.getLhsExpr().getType() instanceof util.type.IntType) {
                    result = new Register(new IntType(IntType.BitWidth.i1), "gt", currentFunc);
                    currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.sgt, lhsResult,
                            rhsResult, (Register) result));
                } else {
                    Function function = module.getBuiltInFunction("_string_gt");
                    ArrayList<Operand> params = new ArrayList<>();
                    params.add(lhsResult);
                    params.add(rhsResult);
                    result = new Register(new IntType(IntType.BitWidth.i1), "string_gt", currentFunc);
                    currentBlock.appendInst(new CallInst(currentBlock, function, params, (Register) result));
                }
            }
            case LessEqual -> {
                if (node.getLhsExpr().getType() instanceof util.type.IntType) {
                    result = new Register(new IntType(IntType.BitWidth.i1), "le", currentFunc);
                    currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.sle, lhsResult,
                            rhsResult, (Register) result));
                } else {
                    Function function = module.getBuiltInFunction("_string_le");
                    ArrayList<Operand> params = new ArrayList<>();
                    params.add(lhsResult);
                    params.add(rhsResult);
                    result = new Register(new IntType(IntType.BitWidth.i1), "string_le", currentFunc);
                    currentBlock.appendInst(new CallInst(currentBlock, function, params, (Register) result));
                }
            }
            case GreaterEqual -> {
                if (node.getLhsExpr().getType() instanceof util.type.IntType) {
                    result = new Register(new IntType(IntType.BitWidth.i1), "ge", currentFunc);
                    currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.sge, lhsResult,
                            rhsResult, (Register) result));
                } else {
                    Function function = module.getBuiltInFunction("_string_ge");
                    ArrayList<Operand> params = new ArrayList<>();
                    params.add(lhsResult);
                    params.add(rhsResult);
                    result = new Register(new IntType(IntType.BitWidth.i1), "string_ge", currentFunc);
                    currentBlock.appendInst(new CallInst(currentBlock, function, params, (Register) result));
                }
            }
            case Equal -> {
                Type lType = node.getLhsExpr().getType();
                Type rType = node.getRhsExpr().getType();
                if (lType instanceof NullType && rType instanceof NullType) {
                    result = new ConstBool(true);
                    break;
                } else
                    result = new Register(new IntType(IntType.BitWidth.i1), "eq", currentFunc);
                if (lType.equals(rType)) {
                    if (lType instanceof StringType) {
                        Function function = module.getBuiltInFunction("_string_eq");
                        ArrayList<Operand> params = new ArrayList<>();
                        params.add(lhsResult);
                        params.add(rhsResult);
                        currentBlock.appendInst(new CallInst(currentBlock, function, params, (Register) result));
                    } else if (!(lType instanceof NullType)) {
                        // int / bool
                        currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.eq, lhsResult,
                                rhsResult, (Register) result));
                    }
                } else {
                    if (lType instanceof NullType) {
                        // swap lType & rType && lhsResult & rhsResult
                        Type tmp = lType;
                        lType = rType;
                        rType = tmp;
                        Operand tmp2 = lhsResult;
                        lhsResult = rhsResult;
                        rhsResult = tmp2;
                    }
                    assert rType instanceof NullType;
                    currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.eq, lhsResult,
                            rhsResult, (Register) result));
                }
            }
            case NotEqual -> {
                Type lType = node.getLhsExpr().getType();
                Type rType = node.getRhsExpr().getType();
                if (lType instanceof NullType && rType instanceof NullType) {
                    result = new ConstBool(false);
                    break;
                } else
                    result = new Register(new IntType(IntType.BitWidth.i1), "ne", currentFunc);
                if (lType.equals(rType)) {
                    if (lType instanceof StringType) {
                        Function function = module.getBuiltInFunction("_string_ne");
                        ArrayList<Operand> params = new ArrayList<>();
                        params.add(lhsResult);
                        params.add(rhsResult);
                        currentBlock.appendInst(new CallInst(currentBlock, function, params, (Register) result));
                    } else if (!(lType instanceof NullType)) {
                        // int / bool
                        currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.ne, lhsResult,
                                rhsResult, (Register) result));
                    }
                } else {
                    if (lType instanceof NullType) {
                        // swap lType & rType && lhsResult & rhsResult
                        Type tmp = lType;
                        lType = rType;
                        rType = tmp;
                        Operand tmp2 = lhsResult;
                        lhsResult = rhsResult;
                        rhsResult = tmp2;
                    }
                    assert rType instanceof NullType;
                    currentBlock.appendInst(new IcmpInst(currentBlock, IcmpInst.Operator.ne, lhsResult,
                            rhsResult, (Register) result));
                }
            }
            case LogicalAnd -> {
                // create a new block & phi inst here.
                Block rhsBlock = new Block(currentFunc, "land.rhs");
                Block endBlock = new Block(currentFunc, "land.end");
                Block formerBlock = currentBlock;

                // ---------
                // store the value
//                Register storePhi = new Register(new PointerType(new IntType(IntType.BitWidth.i1)), "store_phi");
//                currentFunc.getEntryBlock().pushFrontInst(new AllocaInst(currentFunc.getEntryBlock(), storePhi,
//                        new IntType(IntType.BitWidth.i1)));
//                currentFunc.getAllocRegs().add(storePhi);
//                currentBlock.appendInst(new StoreInst(currentBlock, lhsResult, storePhi));
                // ---------
                currentBlock.appendInst(new BrInst(currentBlock, lhsResult, rhsBlock, endBlock));

                currentBlock = rhsBlock;
                node.getRhsExpr().accept(this);
                rhsResult = node.getRhsExpr().getResult();
                // ---------
//                currentBlock.appendInst(new StoreInst(currentBlock, rhsResult, storePhi));
                // ---------
                currentBlock.appendBrInstTo_U(endBlock);
                currentFunc.appendBlock(rhsBlock);

                ArrayList<Block> predecessors = new ArrayList<>();
                ArrayList<Operand> values = new ArrayList<>();
                predecessors.add(formerBlock);
                values.add(new ConstBool(false));
                predecessors.add(currentBlock);
                values.add(rhsResult);
                result = new Register(new IntType(IntType.BitWidth.i1), "land", currentFunc);
                currentBlock = endBlock;
                PhiInst newPhi = new PhiInst(currentBlock, predecessors, values, (Register) result);
                currentBlock.appendInst(newPhi);
                // ----------
//                currentBlock.appendInst(new LoadInst(currentBlock, storePhi, (Register) result));
                // ----------
                currentFunc.appendBlock(endBlock);
            }
            default -> {
                // logicalOr
                // create a new block & phi inst here.
                Block rhsBlock = new Block(currentFunc, "lor.rhs");
                Block endBlock = new Block(currentFunc, "lor.end");
                Block formerBlock = currentBlock;
                // ---------
                // store the value
//                Register storePhi = new Register(new PointerType(new IntType(IntType.BitWidth.i1)), "store_phi");
//                currentFunc.getEntryBlock().pushFrontInst(new AllocaInst(currentFunc.getEntryBlock(), storePhi,
//                        new IntType(IntType.BitWidth.i1)));
//                currentFunc.getAllocRegs().add(storePhi);
//                currentBlock.appendInst(new StoreInst(currentBlock, lhsResult, storePhi));
                // ---------
                currentBlock.appendInst(new BrInst(currentBlock, lhsResult, endBlock, rhsBlock));

                currentBlock = rhsBlock;
                node.getRhsExpr().accept(this);
                rhsResult = node.getRhsExpr().getResult();
                // ---------
//                currentBlock.appendInst(new StoreInst(currentBlock, rhsResult, storePhi));
                // ---------
                currentBlock.appendBrInstTo_U(endBlock);
                currentFunc.appendBlock(rhsBlock);

                ArrayList<Block> predecessors = new ArrayList<>();
                ArrayList<Operand> values = new ArrayList<>();
                predecessors.add(formerBlock);
                values.add(new ConstBool(true));
                predecessors.add(currentBlock);
                values.add(rhsResult);
                result = new Register(new IntType(IntType.BitWidth.i1), "lor", currentFunc);
                currentBlock = endBlock;
                PhiInst newPhi = new PhiInst(currentBlock, predecessors, values, (Register) result);
                currentBlock.appendInst(newPhi);
                // ----------
//                currentBlock.appendInst(new LoadInst(currentBlock, storePhi, (Register) result));
                // ----------
                currentFunc.appendBlock(endBlock);
            }
        }
        node.setResult(result);
        node.setLvalueResult(null);
    }

    @Override
    public void visit(AssignExprNode node) {
        node.getLhsExpr().accept(this);
        node.getRhsExpr().accept(this);
        currentBlock.appendInst(new StoreInst(currentBlock, node.getRhsExpr().getResult(), node.getLhsExpr().getLvalueResult()));

        // if rhsExpr is a NewExpr: traverse the expansion block here.
        if (node.getRhsExpr() instanceof NewExprNode
                && ((NewExprNode) node.getRhsExpr()).hasExpansionBlock()) {
            ((NewExprNode) node.getRhsExpr()).getExpansionBlock().accept(this);
        }
    }

    @Override
    public void visit(ThisExprNode node) {
        //* load %this
        Register thisAddr = currentFunc.getThisAddr();
        IRType irType = ((PointerType) thisAddr.getType()).getBaseType();
        Register result = new Register(irType, "this", currentFunc);
        currentBlock.appendInst(new LoadInst(currentBlock, thisAddr, result));

        node.setResult(result);
        node.setLvalueResult(null);
    }

    @Override
    public void visit(IdExprNode node) {
        VarEntity varEntity = node.getVarEntity();
        Operand allocaAddr = node.getVarEntity().getAllocaAddr();
        if (allocaAddr == null) {
//            System.err.println(varEntity + ": " + varEntity.getName() + " " + varEntity.getEntityType());
            // member entity
            //* getelementptr for member ptr
            //* load member
            Operand thisParam = currentFunc.getThisParam();
            ArrayList<Operand> indices = new ArrayList<>();
            ArrayList<VarEntity> memberList = ((ClassType) node.getScope().getClassType()).getMembers();
            int pos = 0;
            for (; pos < memberList.size(); ++pos)
                if (memberList.get(pos).getName().equals(node.getIdentifier())) break;
            indices.add(new ConstInt(IntType.BitWidth.i32, 0));
            indices.add(new ConstInt(IntType.BitWidth.i32, pos));
            IRType memberType = astTypeTable.getType(memberList.get(pos).getTypeNode()).getIRType(irTypeTable);
            Register memberPtr = new Register(new PointerType(memberType), node.getIdentifier() + ".ptr", currentFunc);
            currentBlock.appendInst(new GetElementPtrInst(currentBlock, thisParam, indices, memberPtr));
            Register member = new Register(memberType, node.getIdentifier(), currentFunc);
            currentBlock.appendInst(new LoadInst(currentBlock, memberPtr, member));
            // TODO: Check whether the expr is used as lvalue or rvalue.
            //  If only lvalue is needed, the LoadInst is not need
            node.setResult(member);
            node.setLvalueResult(memberPtr);
        } else {
            IRType baseType = ((PointerType) allocaAddr.getType()).getBaseType();
            Register value = new Register(baseType, node.getIdentifier(), currentFunc);
            currentBlock.appendInst(new LoadInst(currentBlock, allocaAddr, value));

            node.setResult(value);
            node.setLvalueResult(allocaAddr);
        }
    }

    @Override
    public void visit(IntLiteralNode node) {
        node.setResult(new ConstInt(IntType.BitWidth.i32, node.getValue()));
        node.setLvalueResult(null);
    }

    @Override
    public void visit(StringLiteralNode node) {
        //* add str to module
        //* getelementptr 0, 0
        String str = node.getValue();
        int size = str.length();
        GlobalVar strVar = module.addConstString(str);
        ArrayList<Operand> indices = new ArrayList<>();
        indices.add(new ConstInt(IntType.BitWidth.i32, 0));
        indices.add(new ConstInt(IntType.BitWidth.i32, 0));
        Register result = new Register(new PointerType(new IntType(IntType.BitWidth.i8)), "string", currentFunc);
        currentBlock.appendInst(new GetElementPtrInst(currentBlock, strVar, indices, result));

        node.setResult(result);
        node.setLvalueResult(null);
    }

    @Override
    public void visit(BoolLiteralNode node) {
        node.setResult(new ConstBool(node.getValue()));
        node.setLvalueResult(null);
    }

    @Override
    public void visit(NullLiteralNode node) {
        node.setResult(new ConstNull());
        node.setLvalueResult(null);
    }
}
