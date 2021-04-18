package optimize;

import ir.Function;
import ir.IRPass;
import ir.Module;

public class Inliner extends IRPass {
    public Inliner(Module module) {
        super(module);
    }

    @Override
    public void run() {
        module.getFuncMap().values().forEach(this::runFunc);
    }

    @Override
    protected void runFunc(Function function) {
        
    }
}
