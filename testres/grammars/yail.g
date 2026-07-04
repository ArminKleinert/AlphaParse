program : declaration* EOF ;

declaration    : classDecl
               | funDecl
               | varDecl
               | statement
               | COMMENT ;

classDecl      : CLS_DOC_COMMENT? "class" IDENTIFIER ( "<" IDENTIFIER )?
                 "{" function* "}" ;
funDecl        : FUN_DOC_COMMENT? "fun" function ;
varDecl        : VAR_DOC_COMMENT? "var" IDENTIFIER ( "=" expression )? ";" ;

statement      : exprStmt
               | forStmt
               | ifStmt
               | printStmt
               | returnStmt
               | whileStmt
               | block ;

exprStmt       : expression ";" ;
forStmt        : "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ")" statement
               | "for" "(" IDENTIFIER ":" term ")" statement ;

ifStmt         : "if" "(" expression ")" statement ( "else" statement )? ;
printStmt      : "print" expression ";" ;
returnStmt     : "return" expression? ";" ;
whileStmt      : "while" "(" expression ")" statement ;
block          : "{" declaration* "}" ;

expression     : assignment ;

assignment     : ( call "." )? IDENTIFIER "=" assignment
               | logic_or ;

logic_or       : logic_and ( "or" logic_and )* ;
logic_and      : equality ( "and" equality )* ;
equality       : comparison ( ( "!=" | "==" ) comparison )* ;
comparison     : term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           : factor ( ( "-" | "+" ) factor )* ;
factor         : unary ( ( "/" | "*" ) unary )* ;

unary          : ( "!" | "+" | "-" | "~" ) unary | call ;
call           : primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
primary        : "true" | "false" | "nil" | "this"
               | NUMBER | STRING | IDENTIFIER | "(" expression ")"
               | "super" "." IDENTIFIER ;

function       : IDENTIFIER "(" parameters? ")" block ;
parameters     : IDENTIFIER ( "," IDENTIFIER )* ;
arguments      : expression ( "," expression )* ;

NUMBER         : INT | FLOAT ;
INT            : #"0x[0-9a-fA-F]+" | #"0b[0-1]+" | #"[0-9a-fA-F]+" ;
FLOAT          : #"[0-9]+\.[0-9]+f?" ;
STRING         : "\"" #'[^\"]'* "\"" ;
IDENTIFIER     : ALPHA ( ALPHA | DIGIT )* ;
ALPHA          : #"[a-zA-z_]" ;
DIGIT          : #"[0-9]" ;

COMMENT        : BLOCKCOMMENT | LINECOMMENT ;
BLOCKCOMMENT   : #"/\*(?:(?!\*\/).)*\*/"
LINECOMMENT    : #"//[^:].*\n?"
FUN_DOC_COMMENT: #"//: (?:(?!\*\/).)*" ;
VAR_DOC_COMMENT: #"//: (?:(?!\*\/).)*" ;
CLS_DOC_COMMENT: #"//: (?:(?!\*\/).)*" ;


