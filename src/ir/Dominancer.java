package ir;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// * A Simple, Fast Dominance Algorithm by Keith D.Cooper *
public class Dominancer {
    private Module module;

    private HashMap<Block, Block> doms = new HashMap<>(); // literally idom
    private HashSet<Block> visited = new HashSet<>();

    private HashMap<Block, Integer> postOrder = new HashMap<>(); // just index
    private ArrayList<Block> postOrderBlocks = new ArrayList<>();
    private int cnt = 0;

    // good class name
    public Dominancer(Module module) {
        this.module = module;
    }

    public void run() {
        module.getFuncMap().values().forEach(this::runFunc);
    }

    private void postDFS(Block block) {
        visited.add(block);
        for (Block successor : block.getSuccessors()) {
            if (!visited.contains(successor))
                postDFS(successor);
        }
        postOrderBlocks.add(block);
        postOrder.put(block, cnt++);
    }

    private void runFunc(Function function) {
        visited.clear();
        cnt = 0;
        postDFS(function.getEntryBlock());

        function.getBlocks().forEach(block -> doms.put(block, null));
        var startNode = function.getEntryBlock();

        doms.replace(startNode, startNode);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = postOrderBlocks.size() - 1; i >= 0; --i) {
                var b = postOrderBlocks.get(i);
                if (b == startNode || b.getPredecessors().isEmpty()) continue;
                var newIDom = b.getPredecessors().iterator().next(); // just pick one
                for (Block p : b.getPredecessors())
                    if (p != newIDom && doms.get(p) != null)
                        newIDom = intersect(p, newIDom);
                if (doms.get(b) != newIDom) {
                    doms.replace(b, newIDom);
                    changed = true;
                }
            }
        }

        for (Block b : function.getBlocks()) {
            if (b.getPredecessors().size() >= 2) {
                for (Block p : b.getPredecessors()) {
                    var runner = p;
                    while (runner != doms.get(b)) {
                        runner.getDomFrontier().add(b);
                        runner = doms.get(runner);
                    }
                }
            }
        }

        doms.forEach((Block::setiDom));
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
            os = new FileOutputStream("dominator.txt");
            writer = new PrintWriter(os);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        module.getFuncMap().values().forEach(function -> {
            writer.println("func: " + function.getName());
            writer.println("  Print Dominator Tree:");
            for (Block block : function.getBlocks()) {
                if (block.getiDom() != null)
                    writer.println("\t" + block.getName() + ":\t\t" + block.getiDom().getName());
//                    writer.println(indent + block.getIdom().getName() + "\t--->\t" + block.getName());
            }
            writer.println("");
            writer.println("  Print Dominance Frontier:");
            for (Block block : function.getBlocks()) {
                writer.println("\t" + block.getName() + "'s dominance frontier:");
                for (Block df : block.getDomFrontier())
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
