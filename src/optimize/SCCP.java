package optimize;

import ir.*;
import ir.Module;
import ir.instruction.*;
import ir.operand.*;
import ir.type.IntType;

import java.util.*;

public class SCCP extends IRPass {
    private Queue<Block> cfgWorkList = new LinkedList<>();
    private Queue<Register> ssaWorkList = new LinkedList<>();
    private HashSet<Block> executable = new HashSet<>();
    private HashMap<Operand, Lattice> operandLattice = new HashMap<>();

    public SCCP(Module module) {
        super(module);
    }

    private static class Lattice {
        public enum Flag {
            undefined, constant, multiDefined
        }

        public Flag flag;
        public Operand operand;

        public Lattice(Flag flag, Operand operand) {
            this.flag = flag;
            this.operand = operand;
        }

        @Override
        public String toString() {
            return switch (flag) {
                case constant -> "constant: " + operand.toString();
                default -> flag.name();
            };
        }
    }

    @Override
    public boolean run() {
        changed = false;
        module.getFuncMap().values().forEach(this::runFunc);
        return changed;
    }

    @Override
    protected void runFunc(Function function) {
        cfgWorkList.clear();
        ssaWorkList.clear();
        executable.clear();
        operandLattice.clear();

        addToCFGWorkList(function.getEntryBlock());
        while (!cfgWorkList.isEmpty() || !ssaWorkList.isEmpty()) {
            while (!cfgWorkList.isEmpty()) {
                var block = cfgWorkList.poll();


                block.getPhiInsts().forEach(this::evaluatePhi);

//                    if (markedEnteringCount(block) == 1) {
                evaluateInstsExceptPhis(block); // this will add blocks to cfgWorkList
//                    }
            }
            while (!ssaWorkList.isEmpty()) {
                var reg = ssaWorkList.poll();
                var uses = reg.getUse().keySet();
                for (Inst use : uses) {
//                    if (markedEnteringCount(use.getParentBlock()) >= 1) {
                    evaluateInst(use);
//                    }
                }
            }
        }

        for (Block block : function.getBlocks()) {
            for (var inst = block.getHeadInst(); inst != null; ) {
                var next = inst.next;
                if (inst.hasDstReg()) {
                    var dstLattice = getLattice(inst.getDstReg());
                    if (dstLattice.flag == Lattice.Flag.constant) {
                        changed = true;
                        inst.getDstReg().replaceAllUseWith(dstLattice.operand);
                        inst.removeFromBlock();
                    }
                }
                if (inst instanceof BrInst && ((BrInst) inst).getCondition() != null) {
                    var condLattice = getLattice(((BrInst) inst).getCondition());
                    if (condLattice.flag == Lattice.Flag.constant) {
                        assert condLattice.operand instanceof ConstBool;
                        changed = true;
                        if (((ConstBool) condLattice.operand).getValue())
                            inst.getParentBlock().replaceBrInst(new BrInst(inst.getParentBlock(), null, ((BrInst) inst).getTrueBlock(), null));
                        else
                            inst.getParentBlock().replaceBrInst(new BrInst(inst.getParentBlock(), null, ((BrInst) inst).getFalseBlock(), null));
                    }
                }
                inst = next;
            }
        }
        for (Block block : function.getBlocks()) {
            changed |= block.cleanPhis();
        }
    }

    private Lattice getLattice(Operand operand) {
        if (operandLattice.containsKey(operand))
            return operandLattice.get(operand);
        Lattice ret;
        if (operand instanceof Constant)
            ret = new Lattice(Lattice.Flag.constant, operand);
        else if (operand instanceof Parameter)
            ret = new Lattice(Lattice.Flag.multiDefined, operand);
        else ret = new Lattice(Lattice.Flag.undefined, null);
        operandLattice.put(operand, ret);
        return ret;
    }

    private void addToCFGWorkList(Block block) {
        if (!executable.contains(block)) {
            executable.add(block);
            cfgWorkList.add(block);
        } else {
            // already visited: update phis because new blocks are visited.
            block.getPhiInsts().forEach(this::evaluatePhi);
        }
    }

    private void evaluateInst(Inst inst) {
        if (inst instanceof PhiInst)
            evaluatePhi((PhiInst) inst);
        else if (inst instanceof BrInst)
            evaluateBr((BrInst) inst);
        else evaluateAssign(inst); // this contains binary & icmp
    }

    private void evaluateAssign(Inst inst) {
        if (inst instanceof BinaryInst)
            visitBinary((BinaryInst) inst);
        else if (inst instanceof IcmpInst)
            visitIcmp((IcmpInst) inst);
        else if (inst instanceof BitcastToInst)
            visitBitcastTo((BitcastToInst) inst);
        if (inst instanceof CallInst && inst.hasDstReg() || inst instanceof LoadInst || inst instanceof GetElementPtrInst)
            markMultiDefined(inst.getDstReg());
    }

    private void evaluateBr(BrInst inst) {
        if (inst.getCondition() == null) {
            addToCFGWorkList(inst.getTrueBlock());
        } else {
            var condLattice = getLattice(inst.getCondition());
            if (condLattice.flag == Lattice.Flag.constant) {
                assert condLattice.operand instanceof ConstBool;
                if (((ConstBool) condLattice.operand).getValue()) {
                    addToCFGWorkList(inst.getTrueBlock());
                } else {
                    addToCFGWorkList(inst.getFalseBlock());
                }
            } else if (condLattice.flag == Lattice.Flag.multiDefined) {
                addToCFGWorkList(inst.getTrueBlock());
                addToCFGWorkList(inst.getFalseBlock());
            }
        }
    }

    private void evaluatePhi(PhiInst inst) {
        var dstLattice = getLattice(inst.getDstReg());
        Constant replacedOp = null;
        if (dstLattice.flag != Lattice.Flag.multiDefined) {
            for (int i = 0; i < inst.getPredecessors().size(); ++i) {
                var pred = inst.getPredecessors().get(i);
                var value = inst.getValues().get(i);
                if (!executable.contains(pred)) continue;
                var valueLattice = getLattice(value);
                if (valueLattice.flag == Lattice.Flag.multiDefined) {
                    markMultiDefined(inst.getDstReg());
                    return;
                } else if (valueLattice.flag == Lattice.Flag.constant) {
                    if (replacedOp != null) {
                        if (!valueLattice.operand.equals(replacedOp)) {
                            markMultiDefined(inst.getDstReg());
                            return;
                        }
                    } else
                        replacedOp = (Constant) valueLattice.operand;
                }
            }
            if (replacedOp != null) {
                markConstant(inst.getDstReg(), replacedOp);
            }
        }
    }

    private void markConstant(Register register, Constant replacedOp) {
        var dstLattice = getLattice(register);
        if (dstLattice.flag == Lattice.Flag.undefined) {
//            if (!replacedOp.equals(dstLattice.operand)) {
            operandLattice.put(register, new Lattice(Lattice.Flag.constant, replacedOp));
            ssaWorkList.add(register);
//            }
        }
    }

    private void markMultiDefined(Register register) {
        var dstLattice = getLattice(register);
//        if (dstLattice != null) {
//            dstLattice.flag = Lattice.Flag.multiDefined;
//        }
//        else
        if (dstLattice.flag != Lattice.Flag.multiDefined) {
            operandLattice.put(register, new Lattice(Lattice.Flag.multiDefined, null));
            ssaWorkList.add(register);
        }
    }

    private void visitBinary(BinaryInst inst) {
        var lhsLattice = getLattice(inst.getLhs());
        var rhsLattice = getLattice(inst.getRhs());

        if (lhsLattice.flag == Lattice.Flag.constant && rhsLattice.flag == Lattice.Flag.constant) {
            Constant replacedOp;
            if (lhsLattice.operand instanceof ConstInt) {
                assert rhsLattice.operand instanceof ConstInt;
                int lvalue = ((ConstInt) lhsLattice.operand).getValue(), rvalue = ((ConstInt) rhsLattice.operand).getValue();
                int result;
                switch (inst.getOperator()) {
                    case add -> result = lvalue + rvalue;
                    case sub -> result = lvalue - rvalue;
                    case mul -> result = lvalue * rvalue;
                    case sdiv -> {
                        if (rvalue == 0) return;
                        result = lvalue / rvalue;
                    }
                    case srem -> {
                        if (rvalue == 0) return;
                        result = lvalue % rvalue;
                    }
                    case shl -> result = lvalue << rvalue;
                    case ashr -> result = lvalue >> rvalue;
                    case and -> result = lvalue & rvalue;
                    case or -> result = lvalue | rvalue;
                    case xor -> result = lvalue ^ rvalue;
                    default -> throw new RuntimeException();
                }
                replacedOp = new ConstInt(IntType.BitWidth.i32, result);
            } else if (lhsLattice.operand instanceof ConstBool) {
                assert rhsLattice.operand instanceof ConstBool;
                boolean lvalue = ((ConstBool) lhsLattice.operand).getValue(), rvalue = ((ConstBool) rhsLattice.operand).getValue();
                boolean result;
                result = switch (inst.getOperator()) {
                    case and -> lvalue && rvalue;
                    case or -> lvalue || rvalue;
                    case xor -> lvalue ^ rvalue;
                    default -> throw new RuntimeException();
                };
                replacedOp = new ConstBool(result);
            } else throw new RuntimeException();

            markConstant(inst.getDstReg(), replacedOp); // here mark this dstReg as constant.
        } else {
            if (lhsLattice.flag == Lattice.Flag.multiDefined || rhsLattice.flag == Lattice.Flag.multiDefined)
                markMultiDefined(inst.getDstReg());
        }
    }

    private void visitIcmp(IcmpInst inst) {
        var lhsLattice = getLattice(inst.getLhs());
        var rhsLattice = getLattice(inst.getRhs());
        if (lhsLattice.flag == Lattice.Flag.constant && rhsLattice.flag == Lattice.Flag.constant) {
            ConstBool replacedOp;
            if (lhsLattice.operand instanceof ConstInt) {
                // int: eq, neq
                assert rhsLattice.operand instanceof ConstInt;
                int lvalue = ((ConstInt) lhsLattice.operand).getValue(), rvalue = ((ConstInt) rhsLattice.operand).getValue();
                boolean result = switch (inst.getOperator()) {
                    case eq -> lvalue == rvalue;
                    case ne -> lvalue != rvalue;
                    case slt -> lvalue < rvalue;
                    case sle -> lvalue <= rvalue;
                    case sgt -> lvalue > rvalue;
                    case sge -> lvalue >= rvalue;
                };
                replacedOp = new ConstBool(result);
            } else if (lhsLattice.operand instanceof ConstBool) {
                assert rhsLattice.operand instanceof ConstBool;
                boolean lvalue = ((ConstBool) lhsLattice.operand).getValue(), rvalue = ((ConstBool) rhsLattice.operand).getValue();
                boolean result;
                if (inst.getOperator() == IcmpInst.Operator.eq)
                    result = (lvalue == rvalue);
                else if (inst.getOperator() == IcmpInst.Operator.ne)
                    result = (lvalue != rvalue);
                else throw new RuntimeException();
                replacedOp = new ConstBool(result);
            } else if (lhsLattice.operand instanceof ConstNull) {
                assert rhsLattice.operand instanceof ConstNull;
                replacedOp = new ConstBool(true);
            } else throw new RuntimeException();

            markConstant(inst.getDstReg(), replacedOp);
        } else {
            if (lhsLattice.flag == Lattice.Flag.multiDefined || rhsLattice.flag == Lattice.Flag.multiDefined)
                markMultiDefined(inst.getDstReg());
        }
    }

    private void visitBitcastTo(BitcastToInst inst) {
        var srcLattice = getLattice(inst.getSrc());
        if (srcLattice.flag == Lattice.Flag.constant) {
            Constant replacedOp;
            if (srcLattice.operand instanceof ConstNull)
                replacedOp = new ConstNull();
            else throw new RuntimeException();
            markConstant(inst.getDstReg(), replacedOp);
        } else {
            if (srcLattice.flag == Lattice.Flag.multiDefined)
                markMultiDefined(inst.getDstReg());
        }
    }

    private void evaluateInstsExceptPhis(Block block) {
        for (var inst = block.getHeadInst(); inst != null; inst = inst.next) {
            if (!(inst instanceof PhiInst))
                evaluateInst(inst);
        }
    }

    private int markedEnteringCount(Block block) {
        int cnt = 0;
        for (Block pred : block.getPredecessors()) {
            if (executable.contains(pred)) ++cnt;
        }
        return cnt;
    }
}
