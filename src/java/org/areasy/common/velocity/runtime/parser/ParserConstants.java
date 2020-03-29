package org.areasy.common.velocity.runtime.parser;

/*
 * Copyright (c) 2007-2020 AREasy Runtime
 *
 * This library, AREasy Runtime and API for BMC Remedy AR System, is free software ("Licensed Software");
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * including but not limited to, the implied warranty of MERCHANTABILITY, NONINFRINGEMENT,
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

public interface ParserConstants
{

	int EOF = 0;
	int LBRACKET = 1;
	int RBRACKET = 2;
	int COMMA = 3;
	int DOUBLEDOT = 4;
	int LPAREN = 5;
	int RPAREN = 6;
	int REFMOD2_RPAREN = 7;
	int ESCAPE_DIRECTIVE = 8;
	int SET_DIRECTIVE = 9;
	int DOLLAR = 10;
	int DOLLARBANG = 11;
	int HASH = 15;
	int DOUBLE_ESCAPE = 16;
	int ESCAPE = 17;
	int TEXT = 18;
	int SINGLE_LINE_COMMENT = 19;
	int FORMAL_COMMENT = 20;
	int MULTI_LINE_COMMENT = 21;
	int WHITESPACE = 23;
	int STRING_LITERAL = 24;
	int TRUE = 25;
	int FALSE = 26;
	int NEWLINE = 27;
	int MINUS = 28;
	int PLUS = 29;
	int MULTIPLY = 30;
	int DIVIDE = 31;
	int MODULUS = 32;
	int LOGICAL_AND = 33;
	int LOGICAL_OR = 34;
	int LOGICAL_LT = 35;
	int LOGICAL_LE = 36;
	int LOGICAL_GT = 37;
	int LOGICAL_GE = 38;
	int LOGICAL_EQUALS = 39;
	int LOGICAL_NOT_EQUALS = 40;
	int LOGICAL_NOT = 41;
	int EQUALS = 42;
	int END = 43;
	int IF_DIRECTIVE = 44;
	int ELSEIF_DIRECTIVE = 45;
	int ELSE_DIRECTIVE = 46;
	int STOP_DIRECTIVE = 47;
	int DIGIT = 48;
	int NUMBER_LITERAL = 49;
	int LETTER = 50;
	int DIRECTIVE_CHAR = 51;
	int WORD = 52;
	int ALPHA_CHAR = 53;
	int ALPHANUM_CHAR = 54;
	int IDENTIFIER_CHAR = 55;
	int IDENTIFIER = 56;
	int DOT = 57;
	int LCURLY = 58;
	int RCURLY = 59;
	int REFERENCE_TERMINATOR = 60;
	int DIRECTIVE_TERMINATOR = 61;

	int DIRECTIVE = 0;
	int REFMOD2 = 1;
	int REFMODIFIER = 2;
	int DEFAULT = 3;
	int PRE_DIRECTIVE = 4;
	int REFERENCE = 5;
	int IN_MULTI_LINE_COMMENT = 6;
	int IN_FORMAL_COMMENT = 7;
	int IN_SINGLE_LINE_COMMENT = 8;

	String[] tokenImage = {
		"<EOF>",
		"\"[\"",
		"\"]\"",
		"\",\"",
		"\"..\"",
		"\"(\"",
		"<RPAREN>",
		"\")\"",
		"<ESCAPE_DIRECTIVE>",
		"<SET_DIRECTIVE>",
		"<DOLLAR>",
		"<DOLLARBANG>",
		"\"##\"",
		"<token of kind 13>",
		"\"#*\"",
		"\"#\"",
		"\"\\\\\\\\\"",
		"\"\\\\\"",
		"<TEXT>",
		"<SINGLE_LINE_COMMENT>",
		"\"*#\"",
		"\"*#\"",
		"<token of kind 22>",
		"<WHITESPACE>",
		"<STRING_LITERAL>",
		"\"true\"",
		"\"false\"",
		"<NEWLINE>",
		"\"-\"",
		"\"+\"",
		"\"*\"",
		"\"/\"",
		"\"%\"",
		"\"&&\"",
		"\"||\"",
		"\"<\"",
		"\"<=\"",
		"\">\"",
		"\">=\"",
		"\"==\"",
		"\"!=\"",
		"\"!\"",
		"\"=\"",
		"<END>",
		"\"if\"",
		"\"elseif\"",
		"<ELSE_DIRECTIVE>",
		"\"stop\"",
		"<DIGIT>",
		"<NUMBER_LITERAL>",
		"<LETTER>",
		"<DIRECTIVE_CHAR>",
		"<WORD>",
		"<ALPHA_CHAR>",
		"<ALPHANUM_CHAR>",
		"<IDENTIFIER_CHAR>",
		"<IDENTIFIER>",
		"<DOT>",
		"\"{\"",
		"\"}\"",
		"<REFERENCE_TERMINATOR>",
		"<DIRECTIVE_TERMINATOR>",
	};

}
