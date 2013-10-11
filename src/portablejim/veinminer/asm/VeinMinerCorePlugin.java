package portablejim.veinminer.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({ "portablejim.veinminer.asm" })
public class VeinMinerCorePlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "portablejim.veinminer.asm.ItemInWorldManagerTransformer",
            "portablejim.veinminer.asm.EntityPlayerMPTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }
}
