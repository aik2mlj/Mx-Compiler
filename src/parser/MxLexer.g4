lexer grammar MxLexer;

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
BoolLiteral: True | False;
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