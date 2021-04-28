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
        boolean changed = false;
        do {
            changed = false;
            new IRPrinter("Optcout.ll", module);
            changed |= new SCCP(module).run();
            changed |= new CFGSimplifier(module).run();
//
            var reverseDominancer = new ReverseDominancer(module);
            reverseDominancer.run();
//            reverseDominancer.print();
            changed |= new ADCE(module).run();
//            new IRPrinter("Optcout.ll", module);
            new CFGSimplifier(module).run();
//            changed |= new Inliner(module).run();
            new Dominancer(module).run();
            changed |= new LICM(module).run();
            new CFGSimplifier(module).run();
        } while (changed);
    }
}
