package portablejim.veinminer.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 10/10/13
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityPlayerMPTransformer extends GenericTransformer implements IClassTransformer {
    final static String commandName = "veinminer";

    public EntityPlayerMPTransformer() {
        super();
        srgMappings.put("canCommandSenderUseCommand", "func_70003_b");
    }
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if("net.minecraft.entity.player.EntityPlayerMP".equals(transformedName)) {
            obfuscated = !transformedName.equals(name);
            bytes = transformEntityPlayerMP(name, bytes);
        }
        return bytes;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private byte[] transformEntityPlayerMP(String obfuscatedClassName, byte[] bytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext()) {
            MethodNode curMethod = methods.next();
            String srgFunctionName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(obfuscatedClassName, curMethod.name, curMethod.desc);

            if(getCorrectName("canCommandSenderUseCommand").equals(srgFunctionName)) {
                transformCanCommandSenderUseCommand(curMethod, obfuscatedClassName);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();

    }

    private void transformCanCommandSenderUseCommand(MethodNode curMethod, String obfuscatedClassName) {
        int index = 0;

        while(!(curMethod.instructions.get(index) instanceof LabelNode)) {
            ++index;
        }

        LabelNode skipTarget = (LabelNode)curMethod.instructions.get(index);

        InsnList allowNewCommand = new InsnList();
        allowNewCommand.add(new LabelNode(new Label()));
        allowNewCommand.add(new LdcInsnNode(commandName));
        allowNewCommand.add(new VarInsnNode(Opcodes.ALOAD, 2));
        allowNewCommand.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z"));
        allowNewCommand.add(new JumpInsnNode(Opcodes.IFEQ, skipTarget));
        allowNewCommand.add(new InsnNode(Opcodes.ICONST_1));
        allowNewCommand.add(new InsnNode(Opcodes.IRETURN));
        curMethod.instructions.insertBefore(curMethod.instructions.get(index), allowNewCommand);
        InsnList newFrame = new InsnList();
        newFrame.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
        curMethod.instructions.insert(curMethod.instructions.get(index), newFrame);
    }
}
