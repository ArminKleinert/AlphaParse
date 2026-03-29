S: expression + ; 

brace1: '(' ;
brace2: ')' ;

expression
  : let_expr
  | define
  | defmacro
  | lambda_def
  | literal
  | brace_expr
  | symbol
  ;

brace_expr
  : brace1 expression brace2
  ;

list_literal
  : '(' expression* ')'
  ;

vector_literal
  : '[' expression* ']'
  ;

set_literal
  : '#{' expression* '}'
  ;

map_literal
  : '{' (expression expression)* '}'
  ;

bindings
  : '(' ('(' symbol expression ')')* ')'
  | '[' (symbol expression)* ']'
  ;

destructure_bindings
  : '(' ('(' (symbol | list_literal | vector_literal | set_literal | map_literal) expression ')')* ')'
  | '[' ((symbol | list_literal | vector_literal | set_literal | map_literal) expression)* ']'
  ;

let_expr
  : "let*" bindings expression*
  | "let" destructure_bindings expression*
  ;

lambda_def
  : 'lambda*' symbol bindings expression*
  | ("lambda'" | "fn") symbol? destructure_bindings expression*
  | 'lambda' bindings expression*
  ;

define
  : "define" symbol (expression | (bindings expression*))
  | "define*" symbol destructure_bindings expression*
  ;

def_generic
  : 'def-generic' symbol symbol bindings expression
  ;
