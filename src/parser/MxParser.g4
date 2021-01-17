parser grammar MxParser;
options {
    tokenVocab = MxLexer;
}

program: (varDef | funcDef | classDef)* EOF;

classDef: Class Identifier LeftBrace (varDef | funcDef)* RightBrace Semi;
funcDef: type? Identifier LeftParen paramList? RightParen suite;
varDef: type varDefUnit (Comma varDefUnit)* Semi;
varDefUnit: Identifier (Assign expression)?;

paramList: param (Comma param)*;
param: type Identifier;

type: singleType | arrayType | Void;
basicType: Bool | Int | String;
singleType: Identifier | basicType;
arrayType: singleType (LeftBracket RightBracket)+;

statement
    : suite
    | varDefStmt
    | ifStmt
    | forStmt
    | whileStmt
    | returnStmt
    | breakStmt
    | continueStmt
    | simpleStmt
    ;

suite : LeftBrace statement* RightBrace;
varDefStmt: varDef;
ifStmt: If LeftParen expression RightParen trueStmt=statement (Else falseStmt=statement)?;
forStmt: For LeftParen init=expression? Semi cond=expression? Semi
        incr=expression? RightParen statement;
whileStmt: While LeftParen expression RightParen statement;
returnStmt: Return expression? Semi;
breakStmt: Break Semi;
continueStmt: Continue Semi;
simpleStmt: expression? Semi;

expressionList: expression (Comma expression)*;

expression
    : primary                                                               #atomExpr
    | expression Dot Identifier                                             #memberExpr
    | <assoc=right> New creator                                             #newExpr
    | expression LeftBracket expression RightBracket                        #subscriptExpr
    | expression LeftParen expressionList? RightParen                       #funcCallExpr
    | expression suffix = (SelfPlus | SelfMinus)                            #suffixExpr
    | <assoc=right> prefix = unaryOp expression                             #prefixExpr
    | expression op=(Star | Div | Mod) expression                           #binaryExpr
    | expression op=(Plus | Minus) expression                               #binaryExpr
    | expression op=(LeftShift | RightShift) expression                     #binaryExpr
    | expression op=(Less | Greater | LessEqual | GreaterEqual) expression  #binaryExpr
    | expression op=(Equal | NotEqual) expression                           #binaryExpr
    | expression And expression                                             #binaryExpr
    | expression Xor expression                                             #binaryExpr
    | expression Or expression                                              #binaryExpr
    | expression AndAnd expression                                          #binaryExpr
    | expression OrOr expression                                            #binaryExpr
    | <assoc=right> expression Assign expression                            #assignExpr
    ;

unaryOp: Plus | Minus | SelfPlus | SelfMinus | Tilde | Not;

primary
    : LeftParen expression RightParen
    | This
    | Identifier
    | literal
    ;

literal
    : IntLiteral
    | StringLiteral
    | BoolLiteral
    | Null
    ;

creator
    : singleType (LeftBracket expression RightBracket)+ (LeftBracket RightBracket)+ (LeftBracket expression RightBracket)+ #errorCreator
    | singleType (LeftBracket expression RightBracket)+ (LeftBracket RightBracket)* #arrayCreator
    | singleType (LeftParen RightParen)                                             #classCreator
    | singleType                                                                    #basicCreator
    ;
