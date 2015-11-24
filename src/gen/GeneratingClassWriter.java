package gen;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import gen.util.BinOpBytecodeMap;
import gen.util.TypeMap;
import org.objectweb.asm.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.getInternalName;


public class GeneratingClassWriter extends ClassWriter implements ASTVisitor<Void> {

    private static final String MAIN_CLASS = "Main";
    private static final String IO_CLASS = "IO";
    private static final String INTEGER = "I";
    private static final Map<ast.Type, String> TYPE_MAP = new TypeMap();
    private static final Map<ast.Op, Integer> BINOP_BYTECODE_MAP = new BinOpBytecodeMap();

    private MethodVisitor currentMethod;

    private Map<String, VarDecl> globals;

    public GeneratingClassWriter() {
        super(COMPUTE_FRAMES);

        globals = new HashMap<>();
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
        globals.put(varDecl.var.name, varDecl);
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

        Map<String, Integer> old = new HashMap<>(vars);

        for (int i = 0 ; i < b.varDecls.size(); i++) {
            VarDecl varDecl = b.varDecls.get(i);
            int next = getIndex(vars);
            vars.put(varDecl.var.name, next);
            varDecl.accept(this);
        }

        for (Stmt stmt : b.statements) {
            stmt.accept(this);
        }

        vars = old;

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
//        int position = vd.getByteCodePos();
//        currentMethod.visitInsn(ICONST_0);
//        currentMethod.visitIntInsn(ISTORE, position);
        return null;
    }

    @Override
    public Void visitVar(Var v) {
        // Load global
        if (globals.containsKey(v.name)) {
            VarDecl varDecl = globals.get(v.name);

            currentMethod.visitFieldInsn(
                    GETSTATIC,
                    MAIN_CLASS,
                    varDecl.var.name,
                    INTEGER);

        }
        // Load local
        else {
            // Need to load a variable onto the stack
            currentMethod.visitIntInsn(ILOAD, vars.get(v.name));
        }

        return null;
    }

    @Override
    public Void visitFunctionCallStmt(FunCallStmt funCallStmt) {
        return visitFuncationCall(funCallStmt.getProcedure(), funCallStmt.arguments);
    }

    public Void visitFuncationCall(Procedure procedure, List<Expr> arguments) {
        org.objectweb.asm.commons.Method method = buildMethod(procedure.type, procedure.name, procedure.params);

        if (procedure.name.equals(Procedure.PRINT_S.name))
            return visitPrintString(procedure, arguments);


        // Load arguments onto stack
        for (Expr expr : arguments) {
            expr.accept(this);
        }

        if (procedure.name.equals(Procedure.PRINT_I.name))
            return visitPrintInteger(procedure, arguments);
        if (procedure.name.equals(Procedure.PRINT_C.name))
            return visitPrintCharacter(procedure, arguments);


        currentMethod.visitMethodInsn(INVOKESTATIC, MAIN_CLASS, procedure.name, method.getDescriptor());
        return null;
    }

    private Void visitPrintCharacter(Procedure procedure, List<Expr> arguments) {
        currentMethod.visitMethodInsn(INVOKESTATIC, IO_CLASS, procedure.name, "(C)V");
        return null;
    }

    private Void visitPrintString(Procedure procedure, List<Expr> arguments) {
        StrLiteral string = (StrLiteral) arguments.get(0);
        currentMethod.visitLdcInsn(string.string);
        currentMethod.visitMethodInsn(INVOKESTATIC, IO_CLASS, procedure.name, "(Ljava/lang/String;)V");
        return null;
    }

    private Void visitPrintInteger(Procedure procedure, List<Expr> arguments) {
        currentMethod.visitMethodInsn(INVOKESTATIC, IO_CLASS, procedure.name, "(I)V");
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
        else if (BINOP_BYTECODE_MAP.containsKey(binOp.op)) {
            int instruction = BINOP_BYTECODE_MAP.get(binOp.op);
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
        if (aReturn.hasReturn()) {
            aReturn.returnz.accept(this);
            currentMethod.visitInsn(IRETURN);
        }
        else {
            currentMethod.visitInsn(RETURN);
        }
        return null;
    }

    @Override
    public Void visitAssign(Assign assign) {
        // Put the value of Expr on top of the stack
        assign.expr.accept(this);

        // We need to store it to var, can be global or local
        Var var = assign.var;

        // Save as global
        if (globals.containsKey(var.name)) {
            VarDecl varDecl = globals.get(var.name);
            currentMethod.visitFieldInsn(
                    PUTSTATIC,
                    MAIN_CLASS,
                    varDecl.var.name,
                    INTEGER);
        }
        // Save as local
        else {
            currentMethod.visitVarInsn(ISTORE, vars.get(assign.var.name));
        }

        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr funCallExpr) {
        return visitFuncationCall(funCallExpr.getProcedure(), funCallExpr.arguments);
    }

    private void createClass() {
        visit(V1_7, ACC_PUBLIC, MAIN_CLASS, null, getInternalName(Object.class), null);
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

        // Build contents
        p.block.accept(this);

        // Add implicit return
        main.visitInsn(RETURN);

        main.visitMaxs(2, 2);
        main.visitEnd();

        return null;
    }

    private Void visitGlobalProcedure(Procedure p) {
        org.objectweb.asm.commons.Method method = buildMethod(p.type, p.name, p.params);

        // Build args
        vars = new HashMap<String, Integer>();

        int i = 0;
        for (VarDecl varDecl : p.params) {
            vars.put(varDecl.var.name, i);
            i += 1;
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

        p.block.accept(this);


        // Manually add a void return
        if (p.type == ast.Type.VOID)
            proc.visitInsn(RETURN);

        currentMethod.visitMaxs(1, 1);

        return null;
    }

    private Method buildMethod(ast.Type type, String name, List<VarDecl> args) {
        String methodType = TYPE_MAP.get(type);
        String methodArguments = "";
        for (VarDecl varDecl : args)
            methodArguments += ", " + TYPE_MAP.get(varDecl.type);
        methodArguments = methodArguments.replaceFirst(", ", "");

        String procedure = String.format("%s %s(%s)", methodType, name, methodArguments);
        return org.objectweb.asm.commons.Method.getMethod(procedure);
    }

    private int getIndex(Map<String, Integer> scope) {
        int currentMax = getCurrentMax(scope);
        return currentMax + 1;
    }

    private int getCurrentMax(Map<String, Integer> scope) {
        int max = 0;
        for (Integer value : scope.values()) {
            if (value >= max) max = value;
        }
        return max;
    }
}
