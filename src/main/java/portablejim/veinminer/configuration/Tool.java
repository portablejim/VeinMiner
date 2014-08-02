package portablejim.veinminer.configuration;

import portablejim.veinminer.configuration.json.ToolStruct;
import portablejim.veinminer.util.BlockID;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to manage tool tool lists and block lists.
 */
public class Tool {
    public String name;
    public String icon;
    public Set<String> toollist;
    public Set<BlockID> blocklist;

    public Tool(ToolStruct baseTool) {
        name = baseTool.name;
        icon = baseTool.icon;
        toollist = new HashSet<String>();
        Collections.addAll(toollist, baseTool.toollist);
        blocklist = new HashSet<BlockID>();
        for(String block : baseTool.blocklist) {
            blocklist.add(new BlockID(block));
        }
    }

    public Tool addTool(String tool) {
        toollist.add(tool);
        return this;
    }

    public Tool removeTool(String tool) {
        toollist.remove(tool);
        return this;
    }

    public Tool addBlock(BlockID block) {
        blocklist.add(block);
        return this;
    }

    public Tool removeBlock(BlockID block) {
        blocklist.remove(block);
        return this;
    }
}
