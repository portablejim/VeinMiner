package portablejim.veinminer.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.HashMap;

public abstract class GenericTransformer {
    public static HashMap<String, String> srgMappings;
    public boolean obfuscated = true;

    protected HashMap<String, String> typemap;

    public GenericTransformer() {
        typemap = new HashMap<String, String>();
    }

    protected String getCorrectName(String name) {
        if(obfuscated && srgMappings.containsKey(name)) {
            return srgMappings.get(name);
        }
        return name;
    }

    public boolean isMethodWithName(AbstractInsnNode instruction, String obfuscatedClassName, String name) {
        if(instruction.getType() == AbstractInsnNode.METHOD_INSN) {
            MethodInsnNode methodNode = (MethodInsnNode)instruction;
            String srgName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(obfuscatedClassName, methodNode.name, methodNode.desc);
            return srgName.equals(getCorrectName(name));
        }
        return false;
    }
}
