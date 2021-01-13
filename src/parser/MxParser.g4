parser grammar MxParser;
options {
    tokenVocab = MxLexer;
}

program: (varDef | funcDef | classDef)* EOF;

classDef: Class Identifier LeftBrace (classMemberVarDef | funcDef)* RightBrace Semi;
funcDef: type? Identifier LeftParen paramList? RightParen suite;
classMemberVarDef: type Identifier (Comma Identifier)* Semi;
varDef: type Identifier (Assign expression)?
        (Comma Identifier (Assign expression)?)* Semi;

paramList: param (Comma param)*;
param: type Identifier;

type: singleType | arrayType | Void;
basicType: Bool | Int | String;
singleType: Identifier | basicType;
arrayType: singleType (LeftBracket RightBracket)*;

suite : LeftBrace statement* RightBrace;

statement
    : suite
    | varDef
    | ifStmt
    | forStmt
    | whileStmt
    | returnStmt
    | breakStmt
    | continueStmt
    | simpleStmt
    ;

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
    : primary                                                           #atomExpr
    | expression Dot Identifier                                         #memberExpr
    | <assoc=right> New creator                                         #newExpr
    | expression LeftBracket expression RightBracket                    #subscriptExpr
    | expression LeftParen expressionList? RightParen                   #funcCallExpr
    | expression suffix = (SelfPlus | SelfMinus)                        #suffixExpr
    | <assoc=right> prefix = unaryOp expression                         #prefixExpr
    | expression multiplicativeOp expression                            #binaryExpr
    | expression additiveOp expression                                  #binaryExpr
    | expression shiftOp expression                                     #binaryExpr
    | expression relationalCmpOp expression                             #binaryExpr
    | expression equalityCmpOp expression                               #binaryExpr
    | expression And expression                                         #binaryExpr
    | expression Xor expression                                         #binaryExpr
    | expression Or expression                                          #binaryExpr
    | expression AndAnd expression                                      #binaryExpr
    | expression OrOr expression                                        #binaryExpr
    | <assoc=right> expression Assign expression                        #assignExpr
    ;

unaryOp: Plus | Minus | SelfPlus | SelfMinus | Tilde | Not;
multiplicativeOp: Star | Div | Mod;
additiveOp: Plus | Minus;
shiftOp: LeftShift | RightShift;
relationalCmpOp: Less | Greater | LessEqual | GreaterEqual;
equalityCmpOp: Equal | NotEqual;

primary
    : LeftParen expression RightParen
    | This
    | Identifier
    | literal
    ;

literal
    : IntegerLiteral
    | StringLiteral
    | BoolLiteral
    | Null
    ;

creator
    : singleType (LeftBracket expression RightBracket)+ (LeftBracket RightBracket)+ (LeftBracket expression RightBracket)+ #errorCreator
    | singleType (LeftBracket expression RightBracket)+ (LeftBracket RightBracket)* #arrayCreator
    | singleType (LeftParen RightParen)?                                            #classCreator
    | singleType                                                                    #basicCreator
    ;
