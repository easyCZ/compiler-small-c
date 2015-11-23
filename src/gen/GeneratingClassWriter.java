package gen;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getInternalName;


public class GeneratingClassWriter extends ClassWriter implements ASTVisitor<Void> {

    private static final String CLASS_NAME = "Main";
    private static final HashMap<ast.Type, String> TYPE_MAP = new HashMap() {{
        put(ast.Type.STRING, "String");
        put(ast.Type.CHAR, ast.Type.CHAR.toString().toLowerCase());
        put(ast.Type.INT, ast.Type.INT.toString().toLowerCase());
        put(ast.Type.VOID, ast.Type.VOID.toString().toLowerCase());
    }};

    private MethodVisitor currentMethod;

    private Map<ast.Op, Integer> binOpMap;

    public GeneratingClassWriter() {
        super(COMPUTE_FRAMES);

        binOpMap = new HashMap<>();

        binOpMap.put(Op.EQ, IF_ICMPNE);
        binOpMap.put(Op.NE, IF_ICMPEQ);

        binOpMap.put(Op.LT, IF_ICMPGE);
        binOpMap.put(Op.GT, IF_ICMPLE);

        binOpMap.put(Op.LE, IF_ICMPGT);
        binOpMap.put(Op.GE, IF_ICMPLT);

    }

    private Map<String, Integer> vars;

    @Override
    public Void visitProgram(Program program) {
        // Initialize the whole Main class
        createClass();

        // Create global variables
        for (VarDecl varDecl : program.varDecls)
            visitGlobalVarDecl(varDecl);

        // Create static methods
        for (Procedure p : program.procs)
            p.accept(this);

        // Create main
        program.main.accept(this);

        // End visitor
        visitEnd();

        return null;
    }

    private void visitGlobalVarDecl(VarDecl varDecl) {
        visitField(
                ACC_PUBLIC + ACC_STATIC,
                varDecl.var.name,
                Type.getDescriptor(int.class),
                null,   // signature
                null    // value
        ).visitEnd();
    }

    @Override
    public Void visitBlock(Block b) {

        for (int i = 0 ; i < b.varDecls.size(); i++) {
            VarDecl varDecl = b.varDecls.get(i);

            vars.put(varDecl.var.name, vars.size());
            varDecl.setByteCodePos(vars.get(varDecl.var.name));

            varDecl.accept(this);
        }

        for (Stmt stmt : b.statements) {
            stmt.accept(this);
        }



        return null;
    }

    @Override
    public Void visitProcedure(Procedure p) {

        if (p.isMain())
            return visitMain(p);

        return visitGlobalProcedure(p);
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        int position = vd.getByteCodePos();
        currentMethod.visitInsn(ICONST_0);
        currentMethod.visitIntInsn(ISTORE, position);
        return null;
    }

    @Override
    public Void visitVar(Var v) {
        // Need to load a variable onto the stack
        currentMethod.visitIntInsn(ILOAD, vars.get(v.name));
        return null;
    }

    @Override
    public Void visitFunctionCallStmt(FunCallStmt funCallStmt) {
//        funCallStmt.
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral strLiteral) {
        return null;
    }

    @Override
    public Void visitWhile(While whilez) {
        Label condition = new Label();
        Label next = new Label();

        currentMethod.visitLabel(condition);
        // Evaluate the condition
        whilez.expr.accept(this);

        // Compare the result against 1
        currentMethod.visitInsn(ICONST_1);
        currentMethod.visitJumpInsn(IF_ICMPNE, next);

        // Execute body
        whilez.statement.accept(this);

        // Jump to condition again
        currentMethod.visitJumpInsn(GOTO, condition);

        currentMethod.visitLabel(next);

        return null;
    }

    @Override
    public Void visitBinOp(BinOp binOp) {
        binOp.lhs.accept(this);
        binOp.rhs.accept(this);
        // Should have the two vars on the top of the stack

        if (binOp.op == Op.ADD) {
            currentMethod.visitInsn(IADD);
        }
        else if (binOp.op == Op.SUB) {
            currentMethod.visitInsn(ISUB);
        }
        else if (binOp.op == Op.MUL) {
            currentMethod.visitInsn(IMUL);
        }
        else if (binOp.op == Op.DIV) {
            currentMethod.visitInsn(IDIV);
        }
        else if (binOpMap.containsKey(binOp.op)) {
            int instruction = binOpMap.get(binOp.op);
            Label elseBlock = new Label();
            currentMethod.visitJumpInsn(instruction, elseBlock);

            Label nextInst = new Label();

            // Then
            currentMethod.visitInsn(ICONST_1); // push 1
            currentMethod.visitJumpInsn(GOTO, nextInst);

            // Else
            currentMethod.visitLabel(elseBlock);
            currentMethod.visitInsn(ICONST_0);  // push 0

            // Next Instruction
            currentMethod.visitLabel(nextInst);


        }
//        else {
//            throw new NotImplementedException();
//        }

        // Result is on the top of the stack
        return null;
    }

    @Override
    public Void visitIf(If anIf) {
        // 1 on stack for true, 0 for false
        anIf.ifExpr.accept(this);

        // Add 1 to stack for comparison
        currentMethod.visitInsn(ICONST_1);
        Label elseLabel = new Label();
        Label nextStmt = new Label();

        // If
        currentMethod.visitJumpInsn(IF_ICMPNE, elseLabel);

        // Then
        anIf.ifStmt.accept(this);
        // Jump to after else
        currentMethod.visitJumpInsn(GOTO, nextStmt);

        // Else
        currentMethod.visitLabel(elseLabel);
        if (anIf.hasElse()) anIf.elseStmt.accept(this);

        // Next isntruction
        currentMethod.visitLabel(nextStmt);

        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral intLiteral) {
        currentMethod.visitIntInsn(BIPUSH, intLiteral.value);
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral chrLiteral) {
        currentMethod.visitIntInsn(BIPUSH, (int) chrLiteral.value);
        return null;
    }

    @Override
    public Void visitReturn(Return aReturn) {
        return null;
    }

    @Override
    public Void visitAssign(Assign assign) {
        assign.expr.accept(this);
        // Now we should have the value of Expr on the top of the stack
        // We need to store it
        currentMethod.visitVarInsn(ISTORE, vars.get(assign.var.name));

        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr funCallExpr) {
        return null;
    }

    private void createClass() {
        visit(V1_7, ACC_PUBLIC, CLASS_NAME, null, getInternalName(Object.class), null);
    }

    private Void visitMain(Procedure p) {
        MethodVisitor main = visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",   // String[] args, Void return
                null,
                null
        );

        vars = new HashMap<String, Integer>();

        // Main has one argument
        vars.put("args", 0);

        for (VarDecl varDecl : p.params) {
            vars.put(varDecl.var.name, vars.size());
            varDecl.accept(this);
        }

        main.visitCode();
        currentMethod = main;



//        Label start = new Label();
//        main.visitLabel(start);

        // 1 on top
//        main.visitInsn();
//        main.visitVarInsn(ISTORE, 1);
//
//        main.visitInsn(NULL);
//        main.visitVarInsn(ISTORE, 2);

        // Addition
//        main.visitIntInsn(ILOAD, 1);
//        main.visitIntInsn(ILOAD, 1);
//        main.visitInsn(IADD);
//        main.visitVarInsn(ISTORE, 2);
//
////        main.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
////        main.visitVarInsn(ILOAD, 2;
////        main.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
//
////        Label end = new Label();
////        main.visitLabel(end);
//
//        main.visitLocalVariable("i", "I", null, null, null, 1);
//        main.visitLocalVariable("k", "I", null, null, null, 2);



        // Build contents
        p.block.accept(this);

//        currentMethod.visitFrame(F_SAME, 0, null, 0, null);

        // Add implicit return
        main.visitInsn(RETURN);

        main.visitMaxs(2, 2);
        main.visitEnd();

        return null;
    }

    private Void visitGlobalProcedure(Procedure p) {
        String type = TYPE_MAP.get(p.type);
        String args = "";
        for (VarDecl varDecl : p.params)
            args += ", " + TYPE_MAP.get(varDecl.type);
        args = args.replaceFirst(", ", "");

        // TODO: Return proper type
        String procedure = String.format("%s %s(%s)", "void", p.name, args);
        org.objectweb.asm.commons.Method method = org.objectweb.asm.commons.Method.getMethod(procedure);

        // Build args
        vars = new HashMap<String, Integer>();

        int i = 0;
        for (VarDecl varDecl : p.params) {
            vars.put(varDecl.var.name, i);
        }

        MethodVisitor proc = visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                method.getName(),
                method.getDescriptor(),
                null,
                null
        );

        proc.visitCode();
        currentMethod = proc;

//        proc.visitLocalVariable("test",
//                Type.getDescriptor(int.class),
//                "I",
//                null,
//                null,
//                0);
//        proc.visitMaxs(0, 0);




        p.block.accept(this);


        // Add return for now
        // TODO: Remove
        proc.visitInsn(RETURN);

        return null;
    }
}
