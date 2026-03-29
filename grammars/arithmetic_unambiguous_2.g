S      ::= expr
expr   ::= factor mul_div factor | factor
mul_div ::= '*' | '/'
factor ::= term add_sub term | term
add_sub ::= '+' | '-'
term   ::= #'[0-9]+'