package sem;

import ast.*;
import ast.expressions.*;
import ast.statements.Assign;
import ast.statements.Return;
import ast.statements.While;
import org.junit.Before;
import org.junit.Test;
import sem.type.TypeCheckVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class TypeCheckVisitorTest {

    private TypeCheckVisitor sut;

    private static IntLiteral ONE = new IntLiteral(1);
    private static IntLiteral TWO = new IntLiteral(2);
    private static ChrLiteral A = new ChrLiteral('a');
    private static ChrLiteral B = new ChrLiteral('b');
    private static StrLiteral WORD = new StrLiteral("word");

    private static Var X = new Var("x");
    private static Var Y = new Var("y");

    private static VarDecl X_DECL = new VarDecl(Type.INT, X);
    private static VarDecl Y_DECL = new VarDecl(Type.CHAR, Y);
    private static VarDecl VOID_DECL = new VarDecl(Type.VOID, X);

    private static List<Expr> FOO_X_ARG = new ArrayList<Expr>() {{
        add(X);
    }};
    private static List<Expr> FOO_XY_ARGS = new ArrayList<Expr>() {{
        add(X);
        add(Y);
    }};

    private static Procedure FOO_PROC = new Procedure(
            Type.INT, "foo", Arrays.asList(X_DECL, Y_DECL), null
    );

    private static Procedure FOO_PROC_ARGS_REVERSED = new Procedure(
            Type.INT, "foo", Arrays.asList(Y_DECL, X_DECL), null
    );

    private static Procedure FOO_VOID = new Procedure(
            Type.VOID, "foo", Arrays.asList(X_DECL, Y_DECL), null
    );

    private static FunCallExpr FOO_X = new FunCallExpr("foo", FOO_X_ARG);
    private static FunCallExpr FOO_XY = new FunCallExpr("foo", FOO_XY_ARGS);

    private static List<VarDecl> EMPTY_VARDECL = new ArrayList<>();
    private static List<Stmt> EMPTY_STMTS = new ArrayList<>();
    private static Block EMPTY_BLOCK = new Block(EMPTY_VARDECL, EMPTY_STMTS);


    @Before
    public void setUp() throws Exception {
        sut = new TypeCheckVisitor();

        X.setVarDecl(X_DECL);
        Y.setVarDecl(Y_DECL);
    }

    /* Int Literal */
    @Test
    public void intLiteral() {
        sut.visitIntLiteral(ONE);
        assertNoErrors();
    }

    @Test
    public void intLiteral_returnsIntType() {
        Type type = sut.visitIntLiteral(ONE);
        assertEquals(Type.INT, type);
        assertNoErrors();
    }

    /* Chr literal */
    @Test
    public void charLiteral_returnsType() {
        Type type = sut.visitChrLiteral(A);
        assertEquals(Type.CHAR, type);
        assertNoErrors();
    }

    /* Str Literal */
    @Test
    public void strLiteral_returnsType() {
        Type type = sut.visitStrLiteral(WORD);
        assertEquals(Type.STRING, type);
        assertNoErrors();
    }

    @Test
    public void var_LooksUpTypeInEnv() {
        X.setVarDecl(X_DECL);
        Type type = sut.visitVar(X);
        assertEquals(X_DECL.type, type);
        assertNoErrors();
    }

    /* Vardecl */
    @Test
    public void varDecl_AddsTypeToEnvironment() {
        sut.visitVarDecl(X_DECL);
        assertNoErrors();
    }

    @Test
    public void varDecl_ReturnsNull() {
        Type type = sut.visitVarDecl(X_DECL);
        assertNull(type);
        assertNoErrors();
    }

    @Test
    public void varDecl_FailsWithVoid() {
        Type type = sut.visitVarDecl(VOID_DECL);
        assertEquals(1, sut.getErrorCount());
    }

    /* Fun call expr */
    @Test
    public void funCallExpr_failsWithIncorrectNumberOfArguments() {
        FOO_X.setProcedure(FOO_PROC);
        Type type = sut.visitFunCallExpr(FOO_X);
        assertEquals(1, sut.getErrorCount());
        assertEquals(Type.INT, type);
    }

    @Test
    public void funCallExpr_passesWithCorrectNumberOfArguments() {
        FOO_XY.setProcedure(FOO_PROC);
        Type type = sut.visitFunCallExpr(FOO_XY);
        assertEquals(Type.INT, type);
    }

    @Test
    public void funCallExpr_typechecksArguments() {
        FOO_XY.setProcedure(FOO_PROC_ARGS_REVERSED);
        Type type = sut.visitFunCallExpr(FOO_XY);
        assertEquals(2, sut.getErrorCount());
        assertEquals(Type.INT, type);
    }

    @Test
    public void funCallExpr_voidNotAllowed() {
        FOO_XY.setProcedure(FOO_VOID);
        Type type = sut.visitFunCallExpr(FOO_XY);
        assertEquals(1, sut.getErrorCount());
        assertEquals(Type.VOID, type);
    }

    /* assign */

    private static final Assign X_EQUALS_ONE = new Assign(X, ONE);
    private static final Assign Y_EQUALS_ONE = new Assign(Y, ONE);

    @Test
    public void assign_failsWithTypeMismatch() {
        Y.setVarDecl(Y_DECL);
        Type type = sut.visitAssign(Y_EQUALS_ONE);
        assertEquals(1, sut.getErrorCount());
        assertEquals(Type.CHAR, type);
    }

    @Test
    public void assign_passesWithTypeMatch() {
        X.setVarDecl(X_DECL);
        Type type = sut.visitAssign(X_EQUALS_ONE);
        assertNoErrors();
        assertEquals(Type.INT, type);
    }

    private static final BinOp ONE_MINUS_TWO = new BinOp(ONE, Op.SUB, TWO);
    private static final BinOp ONE_PLUS_TWO = new BinOp(ONE, Op.ADD, TWO);
    private static final BinOp ONE_TIMES_TWO = new BinOp(ONE, Op.MUL, TWO);
    private static final BinOp ONE_DIV_TWO = new BinOp(ONE, Op.DIV, TWO);
    private static final BinOp ONE_MOD_TWO = new BinOp(ONE, Op.MOD, TWO);

    private static final BinOp A_PLUS_ONE = new BinOp(A, Op.ADD, ONE);
    private static final BinOp A_MINUS_ONE = new BinOp(A, Op.SUB, ONE);
    private static final BinOp A_TIMES_ONE = new BinOp(A, Op.MUL, ONE);
    private static final BinOp A_DIV_ONE = new BinOp(A, Op.DIV, ONE);
    private static final BinOp A_MOD_ONE = new BinOp(A, Op.MOD, ONE);

    private static final BinOp ONE_PLUS_A = new BinOp(ONE, Op.ADD, A);
    private static final BinOp ONE_MINUS_A = new BinOp(ONE, Op.SUB, A);
    private static final BinOp ONE_TIMES_A = new BinOp(ONE, Op.MUL, A);
    private static final BinOp ONE_DIV_A = new BinOp(ONE, Op.DIV, A);
    private static final BinOp ONE_MOD_A = new BinOp(ONE, Op.MOD, A);


    /* BinOP */
    @Test
    public void binOp_AddAllowsOnlyInts() {
        Type t = sut.visitBinOp(ONE_PLUS_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_SubAllowsOnlyInts() {
        Type t = sut.visitBinOp(ONE_MINUS_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_MultiplyAllowsOnlyInts() {
        Type t = sut.visitBinOp(ONE_TIMES_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_DivAllowsOnlyInts() {
        Type t = sut.visitBinOp(ONE_DIV_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_ModAllowsOnlyInts() {
        Type t = sut.visitBinOp(ONE_MOD_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_AddFailsWithChars() {
        Type t = sut.visitBinOp(ONE_PLUS_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_AddFailsWithChars2() {
        Type t = sut.visitBinOp(A_PLUS_ONE);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_SubFailsWithChars() {
        Type t = sut.visitBinOp(ONE_MINUS_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_SubFailsWithChars2() {
        Type t = sut.visitBinOp(A_MINUS_ONE);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_MulFailsWithChars() {
        Type t = sut.visitBinOp(ONE_TIMES_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_MulFailsWithChars2() {
        Type t = sut.visitBinOp(A_TIMES_ONE);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_DivFailsWithChars() {
        Type t = sut.visitBinOp(ONE_DIV_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_DivFailsWithChars2() {
        Type t = sut.visitBinOp(A_DIV_ONE);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_ModFailsWithChars() {
        Type t = sut.visitBinOp(ONE_MOD_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_ModFailsWithChars2() {
        Type t = sut.visitBinOp(A_MOD_ONE);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    // Legit
    private static final BinOp ONE_GT_TWO = new BinOp(ONE, Op.GT, TWO);
    private static final BinOp ONE_LT_TWO = new BinOp(ONE, Op.LT, TWO);
    private static final BinOp ONE_GE_TWO = new BinOp(ONE, Op.GE, TWO);
    private static final BinOp ONE_LE_TWO = new BinOp(ONE, Op.LE, TWO);
    private static final BinOp ONE_NE_TWO = new BinOp(ONE, Op.NE, TWO);
    private static final BinOp ONE_EQ_TWO = new BinOp(ONE, Op.EQ, TWO);

    // Legit
    private static final BinOp A_GT_B = new BinOp(A, Op.GT, B);
    private static final BinOp A_LT_B = new BinOp(A, Op.LT, B);
    private static final BinOp A_GE_B = new BinOp(A, Op.GE, B);
    private static final BinOp A_LE_B = new BinOp(A, Op.LE, B);
    private static final BinOp A_NE_B = new BinOp(A, Op.NE, B);
    private static final BinOp A_EQ_B = new BinOp(A, Op.EQ, B);

    // Illegal
    private static final BinOp ONE_GT_A = new BinOp(ONE, Op.GT, A);
    private static final BinOp ONE_LT_A = new BinOp(ONE, Op.LT, A);
    private static final BinOp ONE_GE_A = new BinOp(ONE, Op.GE, A);
    private static final BinOp ONE_LE_A = new BinOp(ONE, Op.LE, A);
    private static final BinOp ONE_NE_A = new BinOp(ONE, Op.NE, A);
    private static final BinOp ONE_EQ_A = new BinOp(ONE, Op.EQ, A);

    // Illegal
    private static final BinOp A_GT_TWO = new BinOp(A, Op.GT, TWO);
    private static final BinOp A_LT_TWO = new BinOp(A, Op.LT, TWO);
    private static final BinOp A_GE_TWO = new BinOp(A, Op.GE, TWO);
    private static final BinOp A_LE_TWO = new BinOp(A, Op.LE, TWO);
    private static final BinOp A_NE_TWO = new BinOp(A, Op.NE, TWO);
    private static final BinOp A_EQ_TWO = new BinOp(A, Op.EQ, TWO);



    @Test
    public void binOp_GtPassesIntAndInt() {
        Type t = sut.visitBinOp(ONE_GT_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_GtPassesCharAndChar() {
        Type t = sut.visitBinOp(A_GT_B);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_GtFailsWithIntAndChar() {
        Type t = sut.visitBinOp(ONE_GT_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_GtFailsWithCharAndInt() {
        Type t = sut.visitBinOp(A_GT_TWO);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    /* LT */
    @Test
    public void binOp_LtPassesIntAndInt() {
        Type t = sut.visitBinOp(ONE_LT_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_LtPassesCharAndChar() {
        Type t = sut.visitBinOp(A_LT_B);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_LtFailsWithIntAndChar() {
        Type t = sut.visitBinOp(ONE_LT_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_LtFailsWithCharAndInt() {
        Type t = sut.visitBinOp(A_LT_TWO);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }


    /* GE */
    @Test
    public void binOp_GEPassesIntAndInt() {
        Type t = sut.visitBinOp(ONE_GE_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_GEPassesCharAndChar() {
        Type t = sut.visitBinOp(A_GE_B);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_GEFailsWithIntAndChar() {
        Type t = sut.visitBinOp(ONE_GE_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_GEFailsWithCharAndInt() {
        Type t = sut.visitBinOp(A_GE_TWO);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    /* LE */
    @Test
    public void binOp_LEPassesIntAndInt() {
        Type t = sut.visitBinOp(ONE_LE_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_LEPassesCharAndChar() {
        Type t = sut.visitBinOp(A_LE_B);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_LEFailsWithIntAndChar() {
        Type t = sut.visitBinOp(ONE_LE_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_LEFailsWithCharAndInt() {
        Type t = sut.visitBinOp(A_LE_TWO);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    /* NE */
    @Test
    public void binOp_NEPassesIntAndInt() {
        Type t = sut.visitBinOp(ONE_NE_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_NEPassesCharAndChar() {
        Type t = sut.visitBinOp(A_NE_B);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_NEFailsWithIntAndChar() {
        Type t = sut.visitBinOp(ONE_NE_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_NEFailsWithCharAndInt() {
        Type t = sut.visitBinOp(A_NE_TWO);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    /* EQ */
    @Test
    public void binOp_EQPassesIntAndInt() {
        Type t = sut.visitBinOp(ONE_EQ_TWO);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_EQPassesCharAndChar() {
        Type t = sut.visitBinOp(A_EQ_B);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void binOp_EQFailsWithIntAndChar() {
        Type t = sut.visitBinOp(ONE_EQ_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void binOp_EQFailsWithCharAndInt() {
        Type t = sut.visitBinOp(A_EQ_TWO);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    /* While */

    private static final While WHILE_ONE = new While(ONE, EMPTY_BLOCK);
    private static final While WHILE_A = new While(A, EMPTY_BLOCK);
    private static final While WHILE_VOID = new While(X, EMPTY_BLOCK);

    @Test
    public void while_ExpressionMustBeInt() {
        Type t = sut.visitWhile(WHILE_ONE);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void while_ExpressionFailsWithChar() {
        Type t = sut.visitWhile(WHILE_A);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void while_FailsWithVoid() {
        X.setVarDecl(VOID_DECL);
        Type t = sut.visitWhile(WHILE_VOID);
        assertEquals(Type.INT, t);
        assertEquals(1, sut.getErrorCount());
    }

    @Test
    public void while_VisitsAllStatements() {
        // TODO
    }

    /* Return */
    private static final Return RETURN_ONE = new Return(ONE);
    private static final Return RETURN_VOID = new Return(X);
    private static final Return RETURN_NULL = new Return(null);

    @Test
    public void return_ReturnsTypeOfExpr() {
        sut.procedureType = Type.INT;

        Type t = sut.visitReturn(RETURN_ONE);
        assertEquals(Type.INT, t);
        assertNoErrors();
    }

    @Test
    public void return_VoidIfExprVoid() {
        sut.procedureType = Type.VOID;

        X.setVarDecl(VOID_DECL);
        Type t = sut.visitReturn(RETURN_VOID);
        assertEquals(Type.VOID, t);
        assertNoErrors();
    }

    @Test
    public void return_ReturnsVoidIfNoExpr() {
        sut.procedureType = Type.VOID;

        Type t = sut.visitReturn(RETURN_NULL);
        assertEquals(Type.VOID, t);
        assertNoErrors();
    }

    /* Block */

//    private static final Block BLOCK_WITH_STMTS = new Block(EMPTY_VARDECL, )

    @Test
    public void block_returnsTheLastType() {
//        Type t = sut.visitBlock()
    }


    private void assertNoErrors() {
        assertEquals(0, sut.getErrorCount());
    }


}