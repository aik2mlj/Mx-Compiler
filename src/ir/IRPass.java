package ir;

abstract public class IRPass {
    protected Module module;

    public IRPass(Module module) {
        this.module = module;
    }

    abstract public void run();
    abstract protected void runFunc(Function function);
}
