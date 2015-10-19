package ast;


import lexer.Token;

public enum  Op {

    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    GT,
    LT,
    GE,
    LE,
    NE,
    EQ;


    public static Op getOp(Token.TokenClass token) {
        switch (token) {

            case PLUS:
                return ADD;
            case MINUS:
                return SUB;
            case TIMES:
                return MUL;
            case DIV:
                return DIV;
            case MOD:
                return MOD;
            case GT:
                return GT;
            case LT:
                return LT;
            case GE:
                return GE;
            case LE:
                return LE;
            case NE:
                return NE;
            case EQ:
                return EQ;

            default:
                return null;
        }
    }

}
