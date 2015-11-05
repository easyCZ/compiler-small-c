package sem;

import ast.Expr;
import ast.Procedure;
import ast.Type;
import ast.VarDecl;
import ast.expressions.*;
import ast.statements.Assign;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class TypeCheckVisitorTest {

    private TypeCheckVisitor sut;

    private static IntLiteral ONE = new IntLiteral(1);
    private static ChrLiteral A = new ChrLiteral('a');
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



    private void assertNoErrors() {
        assertEquals(0, sut.getErrorCount());
    }


}