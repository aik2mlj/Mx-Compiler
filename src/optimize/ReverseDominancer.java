package optimize;

import ir.Block;
import ir.Function;
import ir.IRPass;
import ir.Module;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

// * A Simple, Fast Dominance Algorithm by Keith D.Cooper *
public class ReverseDominancer extends IRPass {
    private HashMap<Block, Block> doms = new HashMap<>(); // literally idom
    private HashSet<Block> visited = new HashSet<>();

    private HashMap<Block, Integer> postOrder = new HashMap<>(); // just index
    private ArrayList<Block> postOrderBlocks = new ArrayList<>();
    private int cnt = 0;

    // good class name
    public ReverseDominancer(Module module) {
        super(module);
    }

    @Override
    public boolean run() {
        module.getFuncMap().values().forEach(this::runFunc);
        return true;
    }

    private void postDFS(Block block) {
        visited.add(block);
        for (Block pred : block.getPredecessors()) {
            if (!visited.contains(pred))
                postDFS(pred);
        }
        postOrderBlocks.add(block);
        postOrder.put(block, cnt++);
    }

    @Override
    protected void runFunc(Function function) {
        visited.clear();
        postOrderBlocks.clear();
        postOrder.clear();
        doms.clear();
        cnt = 0;
        postDFS(function.getRetBlock());

        function.getBlocks().forEach(block -> doms.put(block, null));
        var startNode = function.getRetBlock();

        doms.replace(startNode, startNode);
        HashSet<Block> processed = new HashSet<>();
        processed.add(startNode);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = postOrderBlocks.size() - 1; i >= 0; --i) {
                var b = postOrderBlocks.get(i);
                processed.add(b);
                var b_successors = b.getSuccessors();
                if (b == startNode || b_successors.isEmpty()) continue;
                Block newIDom = null; // pick the last one
                for (Block suc : b_successors)
                    if (processed.contains(suc)) {
                        newIDom = suc;
                        break;
                    }
                if (newIDom == null) throw new RuntimeException();
                for (Block p: b_successors) {
                    if (p != newIDom && doms.get(p) != null)
                        newIDom = intersect(p, newIDom);
                }
                if (doms.get(b) != newIDom) {
                    doms.replace(b, newIDom);
                    changed = true;
                }
            }
        }

        for (Block b : postOrderBlocks) {
            if (b.getSuccessors().size() >= 2) {
                for (Block p : b.getSuccessors()) {
                    var runner = p;
                    while (runner != doms.get(b) && runner != null) {
                        runner.getReverseDomFrontier().add(b);
                        runner = doms.get(runner);
                    }
                }
            }
        }

        doms.forEach((Block::setReverseIDom));
    }

    private Block intersect(Block b1, Block b2) {
        Block finger1 = b1, finger2 = b2;
        while (finger1 != finger2) {
            while (postOrder.get(finger1) < postOrder.get(finger2))
                finger1 = doms.get(finger1);
            while (postOrder.get(finger2) < postOrder.get(finger1))
                finger2 = doms.get(finger2);
        }
        return finger1;
    }

    public void print() {
        OutputStream os;
        PrintWriter writer;
        try {
            os = new FileOutputStream("reverse_dominator.txt");
            writer = new PrintWriter(os);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        module.getFuncMap().values().forEach(function -> {
            writer.println("func: " + function.getName());
            writer.println("  Print Reverse Dominator Tree:");
            for (Block block : function.getBlocks()) {
                if (block.getReverseIDom() != null)
                    writer.println("\t" + block.getName() + ":\t\t" + block.getReverseIDom().getName());
//                    writer.println(indent + block.getIdom().getName() + "\t--->\t" + block.getName());
            }
            writer.println("");
            writer.println("  Print Reverse Dominance Frontier:");
            for (Block block : function.getBlocks()) {
                writer.println("\t" + block.getName() + "'s dominance frontier:");
                for (Block df : block.getReverseDomFrontier())
                    writer.println("\t\t" + df.getName());
                writer.println("");
            }
            writer.println("");
        });

        try {
            writer.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
