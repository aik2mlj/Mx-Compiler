package optimize;

import ir.CFGSimplifier;
import ir.Dominancer;
import ir.IRPrinter;
import ir.Module;

public class Optimization {
    private Module module;

    public Optimization(Module module) {
        this.module = module;
    }

    public void run() {
        runOnce();
//        runOnce();
    }

    private void runOnce() {
        boolean changed;
        do {
            changed = false;
            changed |= new SCCP(module).run();
            changed |= new CSE(module).run();
            changed |= new Inliner(module).run();
            new CFGSimplifier(module, true).run();
//            new IRPrinter("IRcout.ll", module);
            new Dominancer(module).run();
            changed |= new LICM(module).run();
            new ReverseDominancer(module).run();
            changed |= new ADCE(module).run();
            new CFGSimplifier(module, true).run();
        } while (changed);
    }
}
