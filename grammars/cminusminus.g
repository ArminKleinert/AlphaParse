S : translation_unit
  | expression?
  ;

AUTO: 'auto' ;
BREAK: 'break' ;
CASE: 'case' ;
CHAR: 'char' ;
CONST: 'const' ;
CONTINUE: 'continue' ;
DEFAULT: 'default' ;
DO: 'do' ;
DOUBLE: 'double' ;
ELSE: 'else' ;
ENUM: 'enum' ;
EXTERN: 'extern' ;
FLOAT: 'float' ;
FOR: 'for' ;
GOTO: 'goto' ;
IF: 'if' ;
INLINE: 'inline' ;
INT: 'int' ;
LONG: 'long' ;
REGISTER: 'register' ;
RESTRICT: 'restrict' ;
RETURN: 'return' ;
SHORT: 'short' ;
SIGNED: 'signed' ;
SIZEOF: 'sizeof' ;
STATIC: 'static' ;
STRUCT: 'struct' ;
SWITCH: 'switch' ;
TYPEDEF: 'typedef' ;
UNION: 'union' ;
UNSIGNED: 'unsigned' ;
VOID: 'void' ;
VOLATILE: 'volatile' ;
WHILE: 'while' ;
BOOL: '_Bool' ;
COMPLEX: '_Complex' ;
IMAGINARY: '_Imaginary' ;

keyword
  : AUTO
  | BREAK
  | CASE
  | CHAR
  | CONST
  | CONTINUE
  | DEFAULT
  | DO
  | DOUBLE
  | ELSE
  | ENUM
  | EXTERN
  | FLOAT
  | FOR
  | GOTO
  | IF
  | INLINE
  | INT
  | LONG
  | REGISTER
  | RESTRICT
  | RETURN
  | SHORT
  | SIGNED
  | SIZEOF
  | STATIC
  | STRUCT
  | SWITCH
  | TYPEDEF
  | UNION
  | UNSIGNED
  | VOID
  | VOLATILE
  | WHILE
  | BOOL
  | COMPLEX
  | IMAGINARY
  ;

ELLIPSIS: "..." ;
RIGHT_ASSIGN: ">>=" ;
LEFT_ASSIGN: "<<=" ;
ADD_ASSIGN: "+=" ;
SUB_ASSIGN: "-=" ;
MUL_ASSIGN: "*=" ;
DIV_ASSIGN: "/=" ;
MOD_ASSIGN: "%=" ;
AND_ASSIGN: "&=" ;
XOR_ASSIGN: "^=" ;
OR_ASSIGN: "|=" ;
RIGHT_OP: ">>" ;
LEFT_OP: "<<" ;
INC_OP: "++" ;
DEC_OP: "--" ;
PTR_OP: "->" ;
AND_OP: "&&" ;
OR_OP: "||" ;
LE_OP: "<=" ;
GE_OP: ">=" ;
EQ_OP: "==" ;
NE_OP: "!=" ;
SEMICOLON: ";" ;
BLOCK_OPEN: ("{"|"<%") ;
BLOCK_CLOSE: ("}"|"%>") ;
COMMA: "," ;
COLON: ":" ;
ASSIGN: "=" ;
BRACE_OPEN: "(" ;
BRACE_CLOSE: ")" ;
SQUARE_OPEN: ("["|"<: ") ;
SQUARE_CLOSE: ("]"|":>") ;
DOT: "." ;
BIT_AND: "&" ;
UNARY_NOT: "!" ;
TILDE: "~" ;
MINUS: "-" ;
PLUS: "+" ;
STAR: "*" ;
SLASH: "/" ;
PERCENT: "%" ;
LESS_THAN: "<" ;
GREATER_THAN: ">" ;
BIT_XOR: "^" ;
BIT_OR: "|" ;
QUESTION: "?" ;

COMMENT_OPEN: "/*" ;
COMMENT_CLOSE: "*/" ;

punctuator
  : ELLIPSIS
  | RIGHT_ASSIGN
  | LEFT_ASSIGN
  | ADD_ASSIGN
  | SUB_ASSIGN
  | MUL_ASSIGN
  | DIV_ASSIGN
  | MOD_ASSIGN
  | AND_ASSIGN
  | XOR_ASSIGN
  | OR_ASSIGN
  | RIGHT_OP
  | LEFT_OP
  | INC_OP
  | DEC_OP
  | PTR_OP
  | AND_OP
  | OR_OP
  | LE_OP
  | GE_OP
  | EQ_OP
  | NE_OP
  | SEMICOLON
  | BLOCK_OPEN
  | BLOCK_CLOSE
  | COMMA
  | COLON
  | ASSIGN
  | BRACE_OPEN
  | BRACE_CLOSE
  | SQUARE_OPEN
  | SQUARE_CLOSE
  | DOT
  | BIT_AND
  | UNARY_NOT
  | TILDE
  | MINUS
  | PLUS
  | STAR
  | SLASH
  | PERCENT
  | LESS_THAN
  | GREATER_THAN
  | BIT_XOR
  | BIT_OR
  | QUESTION
  | COMMENT_OPEN
  | COMMENT_CLOSE
  ;

identifier : #"_*[a-zA-Z][a-zA-Z0-9_]*" ;

enumeration_constant : identifier ;

integer_constant
  : #"0[xX][a-fA-F0-9]+(ul|uL|Ul|UL|l|L|ll|LL|u|U)?" (* hex int *)
  | #"0[0-9]+(ul|uL|Ul|UL|l|L|ll|LL|u|U)*?" (* octal int *)
  | #"[1-9][0-9]*(ul|uL|Ul|UL|l|L|ll|LL|u|U)*?" (* decimal int *)
  ;

floating_constant
  : #"[0-9]+[Ee][+-]?[0-9]+(f|F|l|L)?" (* float or double *)
  | #"[0-9]*\\.[0-9]+([Ee][+-]?[0-9]+)?(f|F|l|L)?" (* float or double *)
  ;

character_constant
  : #"L?'\\[0-7]([0-7][0-7]?)?'" (* octal char *)
  | #"L?'\\x[0-9a-fA-F]+(\\x[0-9a-fA-F]+)*'" (* hex char. Yes, having multiple chars in one char is valid, but results in a warning. In that case, only the last char is taken *)
  | #"L?'\\['\\\"?abfnFtv]'" (* escape char *)
  | #"L?'[^\\']'" (* Any other char *)
  ;

constant
  : integer_constant
  | floating_constant
  | enumeration_constant
  | character_constant
  ;

string_literal
  : #'L?\"(\\.|[^\\"])*\"'
  ;

COMMENT
  : COMMENT_OPEN #".*" COMMENT_CLOSE
  ;

header_name
  : #"<[^\n>]+>"
  | #'"[^\\"\n]+"'
  ;

primary_expression
  : identifier
  | constant
  | string_literal
  | '(' expression ')'
  ;

postfix_expression
  : primary_expression
  | postfix_expression '[' expression ']'
  | postfix_expression '(' argument_expression_list? ')'
  | postfix_expression '.' identifier
  | postfix_expression '->' identifier
  | postfix_expression '++'
  | postfix_expression '--'
  | '(' type_name ')' '{' initializer_list ','? '}'
  ;

argument_expression_list
  : (assignment_expression ',')* assignment_expression
  ;

unary_expression
  : postfix_expression
  | '++' unary_expression
  | '--' unary_expression
  | unary_operator cast_expression
  | 'sizeof' (unary_expression | '(' type_name ')')
  ;

unary_operator : '&' | '*' | '+' | '-' | '~' | '!' ;

cast_expression
  : unary_expression
  | '(' type_name ')' cast_expression
  ;

multiplicative_expression
  : cast_expression
  | multiplicative_expression '*' cast_expression
  | multiplicative_expression '/' cast_expression
  | multiplicative_expression '%' cast_expression
  ;

additive_expression
  : multiplicative_expression
  | additive_expression '+' multiplicative_expression
  | additive_expression '-' multiplicative_expression
  ;

shift_expression
  : additive_expression
  | shift_expression '<<' additive_expression
  | shift_expression '>>' additive_expression
  ;

relational_expression
  : shift_expression
  | relational_expression '<' shift_expression
  | relational_expression '>' shift_expression
  | relational_expression '<=' shift_expression
  | relational_expression '>=' shift_expression
  ;

equality_expression
  : relational_expression
  | equality_expression '==' relational_expression
  | equality_expression '!=' relational_expression
  ;

AND_expression
  : equality_expression
  | AND_expression '&' equality_expression
  ;

exclusive_OR_expression
  : AND_expression
  | exclusive_OR_expression '^' AND_expression
  ;

inclusive_OR_expression
  : exclusive_OR_expression
  | inclusive_OR_expression '|' exclusive_OR_expression
  ;

logical_AND_expression
  : inclusive_OR_expression
  | logical_AND_expression '&&' inclusive_OR_expression
  ;

logical_OR_expression
  : logical_AND_expression
  | logical_OR_expression '||' logical_AND_expression
  ;

conditional_expression
  : logical_OR_expression
  | logical_OR_expression '?' expression ':' conditional_expression
  ;

assignment_expression
  : conditional_expression
  | unary_expression assignment_operator assignment_expression
  ;

assignment_operator
  : '=' | '*=' | '/=' | '%=' | '+=' | '_=' | '<<=' | '>>=' | '&=' | '^=' | '|='
  ;

expression
  : assignment_expression
  | expression ',' assignment_expression
  ;

constant_expression
  : conditional_expression
  ;

declaration
  : declaration_specifiers init_declarator_list? ';'
  ;

declaration_specifiers
  : ( storage_class_specifier | type_specifier | type_qualifier | function_specifier )+
  ;

init_declarator_list
  : init_declarator (',' init_declarator)*
  ;

init_declarator
  : declarator ('=' initializer)?
  ;

storage_class_specifier
  : 'typedef'
  | 'extern'
  | 'static'
  | 'auto'
  | 'register'
  ;

type_specifier
  : 'void'
  | 'char'
  | 'short'
  | 'int'
  | 'long'
  | 'float'
  | 'double'
  | 'signed'
  | 'unsigned'
  | '_Bool'
  | '_Complex'
  | 'struct_or_union_specifier'
  | 'enum_specifier'
  | 'typedef_name'
  ;

struct_or_union_specifier
  : struct_or_union identifier? '{' struct_declaration_list '}'
  | struct_or_union identifier
  ;

struct_or_union
  : 'struct' | 'union'
  ;

struct_declaration_list
  : struct_declaration +
  ;

struct_declaration
  : specifier_qualifier_list struct_declarator_list ';'
  ;

specifier_qualifier_list
  : (type_specifier | type_qualifier)+
  ;

struct_declarator_list
  : struct_declarator (',' struct_declarator)*
  ;

struct_declarator
  : declarator
  | declarator? ':' constant_expression
  ;

enum_specifier
  : 'enum' identifier? '{' enumerator_list ','? '}'
  | 'enum' identifier
  ;

enumerator_list
  : enumerator (',' enumerator)*
  ;

enumerator
  : enumeration_constant ('=' constant_expression)?
  ;

type_qualifier
  : 'const' | 'restrict' | 'volatile'
  ;

function_specifier
  : 'inline'
  ;

declarator
  : pointer? direct_declarator
  ;

direct_declarator
  : identifier
  | '(' declarator ')'
  | direct_declarator '[' type_qualifier_list? assignment_expression? ']'
  | direct_declarator '[' 'static' type_qualifier_list? assignment_expression ']'
  | direct_declarator '[' type_qualifier_list 'static' assignment_expression ']'
  | direct_declarator '[' type_qualifier_list? '*' ']'
  | direct_declarator '(' parameter_type_list ')'
  | direct_declarator '(' identifier_list? ')'
  ;

pointer
  : '*' type_qualifier_list? pointer?
  ;

type_qualifier_list
  : type_qualifier +
  ;

parameter_type_list
  : parameter_list (',' '...')?
  ;

parameter_list
  : parameter_declaration (',' parameter_declaration)*
  ;

parameter_declaration
  : declaration_specifiers (declarator | abstract_declarator?)
  ;

identifier_list
  : identifier (',' identifier)*
  ;

type_name
  : specifier_qualifier_list abstract_declarator?
  ;

abstract_declarator
  : pointer
  | pointer? direct_abstract_declarator
  ;

direct_abstract_declarator
  : '(' abstract_declarator ')'
  | direct_abstract_declarator? '[' type_qualifier_list? assignment_expression? ']'
  | direct_abstract_declarator? '[' 'static' type_qualifier_list? assignment_expression ']'
  | direct_abstract_declarator? '[' type_qualifier_list 'static' assignment_expression ']'
  | direct_abstract_declarator? '[' '*' ']'
  | direct_abstract_declarator? '(' parameter_type_list? ')'
  ;

typedef_name
  : identifier
  ;

initializer
  : assignment_expression
  | '(' initializer_list ')'
  | '{' initializer_list ',' '}'
  ;

initializer_list
  : designation? initializer (',' designation? initializer)*
  ;

designation
  : designator_list '='
  ;

designator_list
  : designator +
  ;

designator
  : '[' constant_expression ']'
  | '.' identifier
  ;

statement
  : labeled_statement
  | compound_statement
  | expression_statement
  | selection_statement
  | iteration_statement
  | jump_statement
  ;

labeled_statement
  : identifier ':' statement
  | 'case' constant_expression ':' statement
  | 'default' ':' statement
  ;

compound_statement
  : '{' block_item_list? '}'
  ;

block_item_list
  : block_item +
  ;

block_item
  : declaration
  | statement
  ;

expression_statement
  : expression? ';'
  ;

selection_statement
  : 'if' '(' expression ')' statement
  | 'if' '(' expression ')' statement 'else' statement
  | 'switch' '(' expression ')' statement (* [sic] technically does not require any cases, apparently... *)
  ;

iteration_statement
  : 'while' '(' expression ')' statement
  | 'do' statement 'while' '(' expression ')' ';'
  | 'for' '(' expression? ';' expression? ';' expression? ')' statement
  | 'for' '(' declaration expression? ';' expression? ')' statement
  ;

jump_statement
  : 'goto' identifier ';'
  | 'continue' ';'
  | 'break' ';'
  | 'return' expression? ';'
  ;

translation_unit
  : external_declaration +
  ;

external_declaration
  : function_definition
  | declaration
  ;

function_definition
  : declaration_specifiers declarator declaration_list? compound_statement
  ;

declaration_list
  : declaration +
  ;

preprocessing_file
  : group?
  ;

group
  : group_part +
  ;

group_part
  : if_section
  | control_line
  | text_line
  | '#' non_directive
  ;

if_section
  : if_group elif_groups? else_group? endif_line
  ;

if_group
  : '#' 'if' constant_expression new_line group?
  | '#' 'ifdef' identifier new_line group
  | '#' 'ifndef' identifier new_line group
  ;

elif_groups
  : elif_group +
  ;

elif_group
  : '#''elsif' constant_expression new_line group?
  ;

else_group
  : '#' 'else' new_line group?
  ;

endif_line
  : '#' 'endif' new_line
  ;

control_line
  : '#' 'include' pp_tokens new_line
  | '#' 'define' identifier replacement_list new_line
  | '#' 'define' identifier lparen replacement_list? ')'
  | '#' 'define' identifier lparen '...' ')' replacement_list new_line
  | '#' 'define' identifier lparen identifier_list ',' '...' ')' replacement_list new_line
  | '#' 'undef' identifier new_line
  | '#' 'line' pp_tokens new_line
  | '#' 'error' pp_tokens? new_line
  | '#' 'pragma' pp_tokens? new_line
  | '#' new_line
  ;

text_line
  : pp_tokens? new_line
  ;

non_directive
  : pp_tokens new_line
  ;

<lparen>
  : '('
  ;

replacement_list
  : pp_tokens?
  ;

pp_tokens
  : preprocessing_token +
  ;

preprocessing_token
  : header_name
  | identifier
  | pp_number
  | character_constant
  | string_literal
  | punctuator
  ;
  
pp_number
  : #"(\.)?[0-9]+([EePp][+-]|\.[0-9]*|[^0-9])"
  ;
  

new_line
  : '\n'
  ;
