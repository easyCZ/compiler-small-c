package gen.util;


import ast.Op;

import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;

public class BinOpBytecodeMap extends HashMap<Op, Integer> {

    public BinOpBytecodeMap() {
        put(Op.EQ, IF_ICMPNE);
        put(Op.NE, IF_ICMPEQ);

        put(Op.LT, IF_ICMPGE);
        put(Op.GT, IF_ICMPLE);

        put(Op.LE, IF_ICMPGT);
        put(Op.GE, IF_ICMPLT);
    }
}
