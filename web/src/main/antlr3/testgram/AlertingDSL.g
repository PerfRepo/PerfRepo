grammar AlertingDSL;

options {
    language = Java;
    output = AST;
}

tokens {
    ANY;
}

@lexer::header {
  package org.perfrepo.web.alerting;
}

@parser::header {
  package org.perfrepo.web.alerting;
}

/* Forces parser to store an exception upon recognition error. These can be then retrieved via getErrors(), which is done
in ConditionCheckerImpl.parseTree(). Furthermore if any were encountered, IllegalArgumentExpression is thrown.
Normally, ANTLR recovers from recognition errors and prints them into console, then skips the problematic token and attempts to resync.
While it is handy, it might hurt in our case - having the parser 'strict' allows for much easier tree verification. */
@members {
    private List<String> errors = new java.util.LinkedList<String>();
    public void displayRecognitionError(String[] tokenNames,
                                        RecognitionException e) {
        String hdr = getErrorHeader(e);
        String msg = getErrorMessage(e, tokenNames);
        errors.add(hdr + " " + msg);
    }
    public List<String> getErrors() {
        return errors;
    }
}

// Lexer rules - TOKENs:
WS : ( ' ' | '\t' | '\r' | '\n' )+ { $channel = HIDDEN; };

MULTIVALUE           : 'MULTIVALUE';
STRICT               : 'STRICT';
GROUPING             : 'GROUPING';
SELECT               : 'SELECT';
DEFINE               : 'DEFINE';
CONDITION            : 'CONDITION';
WHERE                : 'WHERE';
AND                  : 'AND';
LAST                 : 'LAST';
ASSIGN               : '=';
LTE                  : '<=';
GTE                  : '>=';
IN                   : 'IN';
AVG                  : 'AVG';
MIN                  : 'MIN';
MAX                  : 'MAX';
NUMBER_NOT_ONE       : ('0' | '2'..'9');
ONE                  : '1';
COMMA                : ',';
ANY_CHAR             : . ;

// Parser rules:

// Required abstraction to retain only one rule with one EOF
expression : query_structure EOF;

query_structure :   condition define |
                    multivalue (strict)? condition define_single_var_no_grouping |
                    multivalue grouping condition define_grouping;


condition         : CONDITION^ any_with_equals;

// Following lines are related to multivalue alerting only 
multivalue        : MULTIVALUE^;
strict            : STRICT^;
grouping          : GROUPING^;

// Force only one variable in DEFINE part
define_single_var_no_grouping     : DEFINE^ assign_no_grouping;
// Force only grouping functions in DEFINE part
define_grouping   : DEFINE^ assign_sequence_grouping;

assign_no_grouping  :   any ASSIGN^ '('! multi_select ')'! |
                        any ASSIGN^ multi_select;
assign_sequence_grouping   : assign_grouping_avg  (COMMA! assign_grouping_avg)* |
                             assign_grouping_max  (COMMA! assign_grouping_max)* |
                             assign_grouping_min  (COMMA! assign_grouping_min)* ;

assign_grouping_avg : any ASSIGN^ avg;
assign_grouping_max : any ASSIGN^ max;
assign_grouping_min : any ASSIGN^ min;

// Following lines are related to singlevalue alerting only
define            : DEFINE^ assign_sequence;
assign_sequence   : assign  (COMMA! assign)*;

assign            : any ASSIGN^ '('! simple_select ')'! |
                    any ASSIGN^ simple_select |
                    any ASSIGN^ avg |
                    any ASSIGN^ max |
                    any ASSIGN^ min;

// Following lines are used in both, multi && single value alerting
avg               : AVG^ '('! multi_select ')'!;
max               : MAX^ '('! multi_select ')'!;
min               : MIN^ '('! multi_select ')'!;

simple_select     : SELECT^ equals_where simple_last? | SELECT^ simple_last;

multi_select      : SELECT^ equals_where multi_last? |
                    SELECT^ in_where |
                    SELECT^ multi_last;

equals_where      : WHERE^ equals_condition (AND! equals_condition)*;
in_where          : WHERE^ in_condition;
simple_last       : LAST^ ONE;
multi_last        : LAST^ number | LAST^ number COMMA! number;

equals_condition  : any ASSIGN^ any |
                    any ASSIGN^ '"'! any '"'! |
                    any LTE^ any |
                    any LTE^ '"'! any '"'! |
                    any GTE^ any |
                    any GTE^ '"'! any '"'!;

in_condition      : any IN^ '('! any (COMMA! any)* ')'!;

number            : (NUMBER_NOT_ONE | ONE)* -> ANY[$text];
any_with_equals   : ('=' | '>=' | '<=' | '(' | ')' | ANY_CHAR | NUMBER_NOT_ONE | ONE)* -> ANY[$text];
any               : (NUMBER_NOT_ONE | ONE | ANY_CHAR)* -> ANY[$text];
