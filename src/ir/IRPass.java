package ir;

abstract public class IRPass {
    protected Module module;
    protected boolean changed = false;

    public IRPass(Module module) {
        this.module = module;
    }

    abstract public boolean run();
    abstract protected void runFunc(Function function);
}
