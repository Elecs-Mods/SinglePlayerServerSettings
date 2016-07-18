package elec332.singleplayerserversettings;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

/**
 * Created by Elec332 on 18-7-2016.
 */
public class SinglePlayerServerSettingsTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals(HTTP_UTIL_CLASS)) {
            ClassNode node = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(node, 0);
            transformClass(node);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            return writer.toByteArray();
        } else {
            return basicClass;
        }
    }

    private static final String HTTP_UTIL_CLASS = "net.minecraft.util.HttpUtil";

    private void transformClass(ClassNode classNode){
        for (MethodNode mn : classNode.methods) {
            if (mn != null && isPortMethod(mn)) {
                InsnList insnList = mn.instructions;
                for (int i = 0; i < insnList.size(); i++) {
                    AbstractInsnNode ain = insnList.get(i);
                    if (ain != null && ain.getType() == AbstractInsnNode.METHOD_INSN && ain.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode)ain).desc.equals("()I")){
                        AbstractInsnNode ain2 = ain.getNext();
                        if (ain2 != null && ain2.getType() == AbstractInsnNode.VAR_INSN && ain2.getOpcode() == Opcodes.ISTORE){
                            //Pretty positive we've found the right entry-point.
                            insnList.remove(ain.getPrevious());
                            insnList.remove(ain);
                            insnList.insertBefore(ain2, new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(getClass()), "getPort", "()I", false));
                        }
                    }
                }
            }
        }
    }

    private boolean isPortMethod(MethodNode mn){
        return mn.desc.equals("()I") && mn.exceptions.size() == 1 && mn.exceptions.get(0).equals("java/io/IOException") && mn.parameters == null;
    }

}
