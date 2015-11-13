package gen;

import ast.*;
import ast.expressions.*;
import ast.statements.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.HashMap;

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

    public GeneratingClassWriter() {
        super(COMPUTE_MAXS);
    }

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
        return null;
    }

    @Override
    public Void visitVar(Var v) {
        return null;
    }

    @Override
    public Void visitFunctionCallStmt(FunCallStmt funCallStmt) {
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral strLiteral) {
        return null;
    }

    @Override
    public Void visitWhile(While whilez) {
        return null;
    }

    @Override
    public Void visitBinOp(BinOp binOp) {
        return null;
    }

    @Override
    public Void visitIf(If anIf) {
        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral intLiteral) {
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral chrLiteral) {
        return null;
    }

    @Override
    public Void visitReturn(Return aReturn) {
        return null;
    }

    @Override
    public Void visitAssign(Assign assign) {
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

        // Build contents
        p.block.accept(this);

        // Add implicit return
        main.visitInsn(RETURN);

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

        MethodVisitor proc = visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                method.getName(),
                method.getDescriptor(),
                null,
                null
        );

        p.block.accept(this);


        // Add return for now
        // TODO: Remove
        proc.visitInsn(RETURN);

        return null;
    }
}
