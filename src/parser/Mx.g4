grammar Mx;

// Keywords
Int: 'int';
Bool: 'bool';
String: 'string';
Null: 'null';
Void: 'void';
True: 'true';
False: 'false';
If: 'if';
Else: 'else';
For: 'for';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';
New: 'new';
Class: 'class';
This: 'this';


// Standard Operators
Plus: '+';
Minus: '-';
Star: '*';
Div: '/';
Mod: '%';

SelfPlus: '++';
SelfMinus: '--';

// Relation Operators
Greater: '>';
Less: '<';
GreaterEqual: '>=';
LessEqual: '<=';
NotEqual: '!=';
Equal: '==';

// Logical Operators
AndAnd: '&&';
OrOr: '||';
Not: '!';

// Bit Operators
RightShift: '>>';
LeftShift: '<<';
And: '&';
Or: '|';
Xor: '^';
Tilde: '~';

// Assign Operator
Assign: '=';

// Component Operator
Dot: '.';

// Parentheses, Brackets, Braces
LeftParen: '(';
RightParen: ')';
LeftBracket: '[';
RightBracket: ']';
LeftBrace: '{';
RightBrace: '}';

// Others
Question: '?';
Colon: ':';
Semi: ';';
Comma: ',';

StringLiteral: '"' Schar* '"';
IntLiteral: [1-9] [0-9]* | '0';

// String Char
fragment Schar
    : ~["\\\r\n]    // not ", \, etc
    | '\\n'         // \n
    | '\\\\'        // \\
    | '\\"'         // \"
    ;

Identifier: [a-zA-Z] [a-zA-Z0-9_]*;
Whitespace: [ \t]+ -> skip;
Newline: ('\r' '\n'? | '\n') -> skip;
LineComment: '//' ~ [\r\n]* -> skip;
BlockComment: '/*' .*? '*/' -> skip;

program: programUnit* EOF;
programUnit: varDef | funcDef | classDef;

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
    | expression Dot Identifier LeftParen expressionList? RightParen        #methodExpr
    | <assoc=right> New creator                                             #newExpr
    | expression LeftBracket expression RightBracket                        #subscriptExpr
    | Identifier LeftParen expressionList? RightParen                       #funcCallExpr
    | expression suffix = (SelfPlus | SelfMinus)                            #suffixExpr
    | <assoc=right> prefix = unaryOp expression                             #prefixExpr
    | expression op=(Star | Div | Mod) expression                           #binaryExpr
    | expression op=(Plus | Minus) expression                               #binaryExpr
    | expression op=(LeftShift | RightShift) expression                     #binaryExpr
    | expression op=(Less | Greater | LessEqual | GreaterEqual) expression  #binaryExpr
    | expression op=(Equal | NotEqual) expression                           #binaryExpr
    | expression op=And expression                                          #binaryExpr
    | expression op=Xor expression                                          #binaryExpr
    | expression op=Or expression                                           #binaryExpr
    | expression op=AndAnd expression                                       #binaryExpr
    | expression op=OrOr expression                                         #binaryExpr
    | <assoc=right> expression Assign expression                            #assignExpr
    ;

unaryOp: Plus | Minus | SelfPlus | SelfMinus | Tilde | Not;

primary
    : literal
    | LeftParen expression RightParen
    | This
    | Identifier
    ;

literal
    : IntLiteral
    | StringLiteral
    | BoolLiteral=(True | False)
    | Null
    ;

creator
    : singleType (LeftBracket expression RightBracket)+ (LeftBracket RightBracket)+ (LeftBracket expression RightBracket)+ #errorCreator
    | singleType (LeftBracket expression RightBracket)+ (LeftBracket RightBracket)* #arrayCreator
    | singleType (LeftParen RightParen)                                             #classCreator
    | singleType                                                                    #basicCreator
    ;
