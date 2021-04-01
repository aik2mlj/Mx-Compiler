package riscv.operands;

public class RelocationImm extends Immediate {
    public enum ReloType {
        hi, lo
    }
    private ReloType type;
    private GlobalVar globalVar;

    public RelocationImm(ReloType type, GlobalVar globalVar) {
        this.type = type;
        this.globalVar = globalVar;
    }

    public GlobalVar getGlobalVar() {
        return globalVar;
    }

    public ReloType getType() {
        return type;
    }

    @Override
    public String emit() {
        return "%" + type + "(" + globalVar.getName() + ")";
    }
}
