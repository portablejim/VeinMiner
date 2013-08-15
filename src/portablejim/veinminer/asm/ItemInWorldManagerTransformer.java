package portablejim.veinminer.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.*;
import portablejim.veinminer.util.BlockID;

import java.util.HashMap;
import java.util.Iterator;

public class ItemInWorldManagerTransformer extends GenericTransformer implements IClassTransformer {

    public String targetClassName = "portablejim/veinminer/VeinMiner";
    public String targetClassType = "Lportablejim/veinminer/VeinMiner;";
    public String targetMethodName = "blockMined";
    public String targetMethodType = "(%s%sIIIZ%s)V";
    public String blockIdClassName = "portablejim/veinminer/util/BlockID";

    public  ItemInWorldManagerTransformer() {
        super();
        srgMappings = new HashMap<String, String>();
        srgMappings.put("uncheckedTryHarvestBlock", "func_73082_a");
        srgMappings.put("tryHarvestBlock", "func_73084_b");
        srgMappings.put("theWorld", "field_73092_a");
        srgMappings.put("thisPlayerMP", "field_73090_b");
        srgMappings.put("destroyBlockInWorldPartially", "func_72888_f");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if("net.minecraft.item.ItemInWorldManager".equals(transformedName)) {
            obfuscated = !transformedName.equals(name);
            bytes = transformItemInWorldManager(name, bytes);
        }
        return bytes;
    }

    private InsnList buildBlockIdFunctionCall(String obfuscatedClassName, String worldType, LocalVariablesSorter varSorter, int blockVarIndex) {
        InsnList blockIdFunctionCall = new InsnList();
        blockIdFunctionCall.add(new TypeInsnNode(Opcodes.NEW, blockIdClassName));
        blockIdFunctionCall.add(new InsnNode(Opcodes.DUP));
        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        blockIdFunctionCall.add(new FieldInsnNode(Opcodes.GETFIELD, obfuscatedClassName.replace(".", "/"), getCorrectName("theWorld"), typemap.get(getCorrectName("theWorld"))));
        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, 1));
        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, 2));
        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, 3));
        String blockMethodType = "(%sIII)V";
        blockIdFunctionCall.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, blockIdClassName, "<init>", String.format("(%sIII)V", worldType)));

        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ASTORE, blockVarIndex));

        return blockIdFunctionCall;
    }

    private void transformUncheckedTryHarvestBlock(MethodNode curMethod, String obfuscatedClassName) {
        LocalVariablesSorter varSorter = new LocalVariablesSorter(curMethod.access, curMethod.desc, curMethod);
        int index = 0;

        String worldType = typemap.get(getCorrectName("theWorld"));
        String playerType = typemap.get(getCorrectName("thisPlayerMP"));

        while(!isMethodWithName(curMethod.instructions.get(index), obfuscatedClassName, "destroyBlockInWorldPartially")) {
            ++index;
        }

        int blockVarIndex = varSorter.newLocal(Type.getType(BlockID.class));
        curMethod.instructions.insert(curMethod.instructions.get(index), buildBlockIdFunctionCall(obfuscatedClassName, worldType, varSorter, blockVarIndex));
        ++index;

        while(!isMethodWithName(curMethod.instructions.get(index), obfuscatedClassName, "tryHarvestBlock")) {
            ++index;
        }

        // Add variable to store result
        int newVarIndex = varSorter.newLocal(Type.BOOLEAN_TYPE);
        VarInsnNode newVar = new VarInsnNode(Opcodes.ISTORE, newVarIndex);
        curMethod.instructions.insert(curMethod.instructions.get(index), newVar);
        ++index;

        // Add in function call to call function
        InsnList veinMinerFunctionCall = new InsnList();
        veinMinerFunctionCall.add(new FieldInsnNode(Opcodes.GETSTATIC, targetClassName, "instance", targetClassType));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        veinMinerFunctionCall.add(new FieldInsnNode(Opcodes.GETFIELD, obfuscatedClassName.replace(".", "/"), getCorrectName("theWorld"), typemap.get(getCorrectName("theWorld"))));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        veinMinerFunctionCall.add(new FieldInsnNode(Opcodes.GETFIELD, obfuscatedClassName.replace(".", "/"), getCorrectName("thisPlayerMP"), typemap.get(getCorrectName("thisPlayerMP"))));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, 1));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, 2));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, 3));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, newVarIndex));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, blockVarIndex));

        String blockIdClassType = String.format("L%s;", blockIdClassName);
        veinMinerFunctionCall.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, targetClassName, targetMethodName, String.format(targetMethodType, worldType, playerType, blockIdClassType)));
        curMethod.instructions.insert(curMethod.instructions.get(index), veinMinerFunctionCall);
        ++index;

        // Get rid of un-needed POP.
        while (curMethod.instructions.get(index).getOpcode() != Opcodes.POP) {
            ++index;
        }
        curMethod.instructions.remove(curMethod.instructions.get(index));
    }

    public byte[] transformItemInWorldManager(String obfuscatedClassName, byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        // Setup type map
        for(FieldNode variable : classNode.fields) {
            String srgVariableName = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(obfuscatedClassName, variable.name, variable.desc);
            if(getCorrectName("theWorld").equals(srgVariableName) ||
                    getCorrectName("thisPlayerMP").equals(srgVariableName)) {
                typemap.put(srgVariableName, variable.desc);
            }
        }

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode curMethod = methods.next();
            String srgFunctionName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(obfuscatedClassName, curMethod.name, curMethod.desc);

            if(getCorrectName("uncheckedTryHarvestBlock").equals(srgFunctionName)) {
                transformUncheckedTryHarvestBlock(curMethod, obfuscatedClassName);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
