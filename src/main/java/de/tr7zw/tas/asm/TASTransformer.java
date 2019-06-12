package de.tr7zw.tas.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.launcher.FMLDeobfTweaker;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;


public class TASTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        ClassReader reader = new ClassReader(basicClass);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor adapter = new TASClassAdapter(writer, name);
        reader.accept(adapter, 0);
        byte[] result = writer.toByteArray();

        if (name.endsWith(".Entity") && !name.endsWith("RenderEntity")) {
            try {
                FileOutputStream out = new FileOutputStream("Entity.class");
                out.write(result);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.printf("Transformed class: %s%n" +
                          "                 : %s%n",
                          name, FMLDeobfuscatingRemapper.INSTANCE.map(name)
        );

        return result;
    }

    class TASClassAdapter extends ClassVisitor {
        private String className;

        TASClassAdapter(ClassVisitor cv, String className) {
            super(ASM5, cv);
            this.className = className;
        }


        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv;
            mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (FMLDeobfuscatingRemapper.INSTANCE.map(className).endsWith("Entity")) {
                return new EntityVisitor(mv);
            }
            return new RandomVisitor(mv);

        }
    }

    public class RandomVisitor extends MethodVisitor {

        RandomVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            //System.out.printf("%d: %s -> %s{%s}%s%n", opcode, owner, name, desc, itf);

            if (owner.equals("java/util/Random") && name.equals("<init>") && desc.equals("()V")) {
                super.visitLdcInsn(0L);
                super.visitMethodInsn(opcode, owner, name, "(J)V", itf);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }


    private class EntityVisitor extends MethodVisitor {
        public EntityVisitor(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (owner.equals("java/util/Random") && name.equals("<init>") && desc.equals("()V")) {
                super.visitLdcInsn(0L);
                super.visitMethodInsn(opcode, owner, name, "(J)V", itf);
            } else if (desc.equals("(Ljava/util/Random;)Ljava/util/UUID;")) {
                super.visitInsn(POP);
                super.visitTypeInsn(NEW, "java/util/Random");
                super.visitInsn(DUP);
                super.visitMethodInsn(INVOKESPECIAL, "java/util/Random", "<init>", "()V", false);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }
}
