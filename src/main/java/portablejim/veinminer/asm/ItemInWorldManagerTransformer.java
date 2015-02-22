/* This file is part of VeinMiner.
 *
 *    VeinMiner is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *    VeinMiner is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with VeinMiner.
 *    If not, see <http://www.gnu.org/licenses/>.
 */

package portablejim.veinminer.asm;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.util.BlockID;

import java.util.HashMap;

/**
 * Modifies ItemInWorldManager to add a call to VeinMiner.blockMined() to
 * capture the mining of a block to start VeinMiner.
 *
 * It also stores the result of tryHarvestBlock() (whether the attempt to mine
 * a block was successful) and uses it as an argument to blockMined().
 */

@SuppressWarnings("UnusedDeclaration")
public class ItemInWorldManagerTransformer extends GenericTransformer implements IClassTransformer {

    final String targetClassName = "portablejim/veinminer/core/InjectedCalls";
    final String targetClassType = "Lportablejim/veinminer/core/InjectedCalls;";
    final String targetMethodName = "blockMined";
    final String targetMethodType = "(%s%s%sZ%s)V";
    final String blockIdClassName = "portablejim/veinminer/util/BlockID";

    public  ItemInWorldManagerTransformer() {
        super();
        srgMappings = new HashMap<String, String>();
        srgMappings.put("blockRemoving", "func_180785_a");
        srgMappings.put("tryHarvestBlock", "func_180237_b");
        srgMappings.put("theWorld", "field_73092_a");
        srgMappings.put("thisPlayerMP", "field_73090_b");
        srgMappings.put("onBlockClicked", "func_180784_a");
        srgMappings.put("field_180240_f", "field_180240_f");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {

        if("net.minecraft.server.management.ItemInWorldManager".equals(transformedName)) {
            obfuscated = !transformedName.equals(name);
            bytes = transformItemInWorldManager(name, bytes);
        }
        return bytes;
    }

    private InsnList buildBlockIdFunctionCall(String obfuscatedClassName, String worldType, String blockposType, int blockVarIndex) {
        InsnList blockIdFunctionCall = new InsnList();
        blockIdFunctionCall.add(new TypeInsnNode(Opcodes.NEW, blockIdClassName));
        blockIdFunctionCall.add(new InsnNode(Opcodes.DUP));
        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        blockIdFunctionCall.add(new FieldInsnNode(Opcodes.GETFIELD, obfuscatedClassName.replace(".", "/"), getCorrectName("theWorld"), typemap.get(getCorrectName("theWorld"))));
        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 1));
        blockIdFunctionCall.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, blockIdClassName, "<init>", String.format("(%s%s)V", worldType, blockposType), false));

        blockIdFunctionCall.add(new VarInsnNode(Opcodes.ASTORE, blockVarIndex));

        return blockIdFunctionCall;
    }

    private int insertCallAfterTryHarvestBlockFunction(MethodNode curMethod, String obfuscatedClassName) throws IndexOutOfBoundsException {
        return insertCallAfterTryHarvestBlockFunction(curMethod, obfuscatedClassName, 0);
    }

    private int insertCallAfterTryHarvestBlockFunction(MethodNode curMethod, String obfuscatedClassName, int startIndex) throws IndexOutOfBoundsException {
        LocalVariablesSorter varSorter = new LocalVariablesSorter(curMethod.access, curMethod.desc, curMethod);

        String worldType = typemap.get(getCorrectName("theWorld"));
        String playerType = typemap.get(getCorrectName("thisPlayerMP"));
        String blockposType = typemap.get(getCorrectName("field_180240_f"));

        while(!isMethodWithName(curMethod.instructions.get(startIndex), "tryHarvestBlock")) {
            ++startIndex;
        }

        do {
            --startIndex;
        }
        while(curMethod.instructions.get(startIndex).getType() == AbstractInsnNode.VAR_INSN);


        int blockVarIndex = varSorter.newLocal(Type.getType(BlockID.class));
        curMethod.instructions.insert(curMethod.instructions.get(startIndex), buildBlockIdFunctionCall(obfuscatedClassName, worldType, blockposType, blockVarIndex));
        ++startIndex;

        while(!isMethodWithName(curMethod.instructions.get(startIndex), "tryHarvestBlock")) {
            ++startIndex;
        }

        // Add variable to store result
        int newVarIndex = varSorter.newLocal(Type.BOOLEAN_TYPE);
        VarInsnNode newVar = new VarInsnNode(Opcodes.ISTORE, newVarIndex);
        curMethod.instructions.insert(curMethod.instructions.get(startIndex), newVar);
        ++startIndex;

        // Add in function call to call function
        InsnList veinMinerFunctionCall = new InsnList();
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        veinMinerFunctionCall.add(new FieldInsnNode(Opcodes.GETFIELD, obfuscatedClassName.replace(".", "/"), getCorrectName("theWorld"), typemap.get(getCorrectName("theWorld"))));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        veinMinerFunctionCall.add(new FieldInsnNode(Opcodes.GETFIELD, obfuscatedClassName.replace(".", "/"), getCorrectName("thisPlayerMP"), typemap.get(getCorrectName("thisPlayerMP"))));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, 1));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ILOAD, newVarIndex));
        veinMinerFunctionCall.add(new VarInsnNode(Opcodes.ALOAD, blockVarIndex));

        String blockIdClassType = String.format("L%s;", blockIdClassName);
        veinMinerFunctionCall.add(new MethodInsnNode(Opcodes.INVOKESTATIC, targetClassName, targetMethodName, String.format(targetMethodType, worldType, playerType, blockposType, blockIdClassType), false));
        curMethod.instructions.insert(curMethod.instructions.get(startIndex), veinMinerFunctionCall);
        ++startIndex;

        // Get rid of un-needed POP.
        while (curMethod.instructions.get(startIndex).getOpcode() != Opcodes.POP) {
            ++startIndex;
        }
        curMethod.instructions.remove(curMethod.instructions.get(startIndex));

        return startIndex;
    }

    public byte[] transformItemInWorldManager(String obfuscatedClassName, byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        // Setup type map
        for(FieldNode variable : classNode.fields) {
            String srgVariableName = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(obfuscatedClassName, variable.name, variable.desc);
            if(getCorrectName("theWorld").equals(srgVariableName) ||
                    getCorrectName("thisPlayerMP").equals(srgVariableName) ||
                    getCorrectName("field_180240_f").equals(srgVariableName)) {
                typemap.put(srgVariableName, variable.desc);
            }
        }

        try {
            for (MethodNode curMethod : classNode.methods) {
                String srgFunctionName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(obfuscatedClassName, curMethod.name, curMethod.desc);

                if (getCorrectName("blockRemoving").equals(srgFunctionName)) {
                    MinerLogger.debug("Inserting call to blockRemoving (%s)", srgFunctionName);
                    insertCallAfterTryHarvestBlockFunction(curMethod, obfuscatedClassName);
                }
                else if (getCorrectName("onBlockClicked").equals(srgFunctionName)) {
                    MinerLogger.debug("Inserting call to onBlockClicked (%s)", srgFunctionName);
                    int afterFirst = insertCallAfterTryHarvestBlockFunction(curMethod, obfuscatedClassName);
                    insertCallAfterTryHarvestBlockFunction(curMethod, obfuscatedClassName, afterFirst);

                }
            }
        }
        catch(IndexOutOfBoundsException e) {
            LogManager.getLogger(ModInfo.MODID).warn("[%s] Problem inserting all required code. This mod may not function correctly. Please report a bug.", ModInfo.MODID);
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
