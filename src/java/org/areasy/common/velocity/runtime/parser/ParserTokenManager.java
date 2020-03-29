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

import java.util.EmptyStackException;
import java.util.Hashtable;
import java.util.Stack;

public class ParserTokenManager implements ParserConstants
{
	private int fileDepth = 0;

	private int lparen = 0;
	private int rparen = 0;

	Stack stateStack = new Stack();
	public boolean debugPrint = false;

	private boolean inReference;
	public boolean inDirective;
	private boolean inComment;
	public boolean inSet;

	/**
	 * pushes the current state onto the 'state stack',
	 * and maintains the parens counts
	 * public because we need it in PD & VM handling
	 *
	 * @return boolean : success.  It can fail if the state machine
	 *         gets messed up (do don't mess it up :)
	 */
	public boolean stateStackPop()
	{
		Hashtable h;

		try
		{
			h = (Hashtable) stateStack.pop();
		}
		catch (EmptyStackException e)
		{
			lparen = 0;
			SwitchTo(DEFAULT);
			return false;
		}

		if (debugPrint)
		{
			System.out.println(" stack pop (" + stateStack.size() + ") : lparen=" +
					((Integer) h.get("lparen")).intValue() +
					" newstate=" + ((Integer) h.get("lexstate")).intValue());
		}

		lparen = ((Integer) h.get("lparen")).intValue();
		rparen = ((Integer) h.get("rparen")).intValue();

		SwitchTo(((Integer) h.get("lexstate")).intValue());

		return true;
	}

	/**
	 * pops a state off the stack, and restores paren counts
	 *
	 * @return boolean : success of operation
	 */
	public boolean stateStackPush()
	{
		if (debugPrint)
		{
			System.out.println(" (" + stateStack.size() + ") pushing cur state : " +
					curLexState);
		}

		Hashtable h = new Hashtable();

		h.put("lexstate", new Integer(curLexState));
		h.put("lparen", new Integer(lparen));
		h.put("rparen", new Integer(rparen));

		lparen = 0;

		stateStack.push(h);

		return true;
	}

	/**
	 * Clears all state variables, resets to
	 * start values, clears stateStack.  Call
	 * before parsing.
	 *
	 */
	public void clearStateVars()
	{
		stateStack.clear();

		lparen = 0;
		rparen = 0;
		inReference = false;
		inDirective = false;
		inComment = false;
		inSet = false;

		return;
	}

	/**
	 * handles the dropdown logic when encountering a RPAREN
	 */
	private void RPARENHandler()
	{
		/*
		 *  Ultimately, we want to drop down to the state below
		 *  the one that has an open (if we hit bottom (DEFAULT),
		 *  that's fine. It's just text schmoo.
		 */

		boolean closed = false;

		if (inComment)
		{
			closed = true;
		}

		while (!closed)
		{
			/*
			 * look at current state.  If we haven't seen a lparen
			 * in this state then we drop a state, because this
			 * lparen clearly closes our state
			 */

			if (lparen > 0)
			{
				/*
				 *  if rparen + 1 == lparen, then this state is closed.
				 * Otherwise, increment and keep parsing
				 */

				if (lparen == rparen + 1)
				{
					stateStackPop();
				}
				else
				{
					rparen++;
				}

				closed = true;
			}
			else
			{
				/*
				 * now, drop a state
				 */

				if (!stateStackPop())
				{
					break;
				}
			}
		}
	}

	public java.io.PrintStream debugStream = System.out;

	public void setDebugStream(java.io.PrintStream ds)
	{
		debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 52;
					return 33;
				}
				if ((active0 & 0x10000000L) != 0L)
				{
					return 31;
				}
				if ((active0 & 0xd000L) != 0L)
				{
					return 7;
				}
				return -1;
			case 1:
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 1;
					return 33;
				}
				if ((active0 & 0x4000L) != 0L)
				{
					return 5;
				}
				return -1;
			case 2:
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 2;
					return 33;
				}
				return -1;
			case 3:
				if ((active0 & 0x4000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 3;
					return 33;
				}
				if ((active0 & 0x2000000L) != 0L)
				{
					return 33;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_0(int pos, long active0)
	{
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
	}

	private final int jjStopAtPos(int pos, int kind)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private final int jjStartNfaWithStates_0(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_0(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_0()
	{
		switch (curChar)
		{
			case 33:
				jjmatchedKind = 41;
				return jjMoveStringLiteralDfa1_0(0x10000000000L);
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_0(0x5000L);
			case 37:
				return jjStopAtPos(0, 32);
			case 38:
				return jjMoveStringLiteralDfa1_0(0x200000000L);
			case 40:
				return jjStopAtPos(0, 5);
			case 42:
				return jjStopAtPos(0, 30);
			case 43:
				return jjStopAtPos(0, 29);
			case 44:
				return jjStopAtPos(0, 3);
			case 45:
				return jjStartNfaWithStates_0(0, 28, 31);
			case 46:
				return jjMoveStringLiteralDfa1_0(0x10L);
			case 47:
				return jjStopAtPos(0, 31);
			case 60:
				jjmatchedKind = 35;
				return jjMoveStringLiteralDfa1_0(0x1000000000L);
			case 61:
				jjmatchedKind = 42;
				return jjMoveStringLiteralDfa1_0(0x8000000000L);
			case 62:
				jjmatchedKind = 37;
				return jjMoveStringLiteralDfa1_0(0x4000000000L);
			case 91:
				return jjStopAtPos(0, 1);
			case 93:
				return jjStopAtPos(0, 2);
			case 102:
				return jjMoveStringLiteralDfa1_0(0x4000000L);
			case 116:
				return jjMoveStringLiteralDfa1_0(0x2000000L);
			case 124:
				return jjMoveStringLiteralDfa1_0(0x400000000L);
			default :
				return jjMoveNfa_0(0, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_0(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_0(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				break;
			case 38:
				if ((active0 & 0x200000000L) != 0L)
				{
					return jjStopAtPos(1, 33);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_0(1, 14, 5);
				}
				break;
			case 46:
				if ((active0 & 0x10L) != 0L)
				{
					return jjStopAtPos(1, 4);
				}
				break;
			case 61:
				if ((active0 & 0x1000000000L) != 0L)
				{
					return jjStopAtPos(1, 36);
				}
				else if ((active0 & 0x4000000000L) != 0L)
				{
					return jjStopAtPos(1, 38);
				}
				else if ((active0 & 0x8000000000L) != 0L)
				{
					return jjStopAtPos(1, 39);
				}
				else if ((active0 & 0x10000000000L) != 0L)
				{
					return jjStopAtPos(1, 40);
				}
				break;
			case 97:
				return jjMoveStringLiteralDfa2_0(active0, 0x4000000L);
			case 114:
				return jjMoveStringLiteralDfa2_0(active0, 0x2000000L);
			case 124:
				if ((active0 & 0x400000000L) != 0L)
				{
					return jjStopAtPos(1, 34);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_0(0, active0);
	}

	private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_0(0, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_0(1, active0);
			return 2;
		}
		switch (curChar)
		{
			case 108:
				return jjMoveStringLiteralDfa3_0(active0, 0x4000000L);
			case 117:
				return jjMoveStringLiteralDfa3_0(active0, 0x2000000L);
			default :
				break;
		}
		return jjStartNfa_0(1, active0);
	}

	private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_0(1, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_0(2, active0);
			return 3;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x2000000L) != 0L)
				{
					return jjStartNfaWithStates_0(3, 25, 33);
				}
				break;
			case 115:
				return jjMoveStringLiteralDfa4_0(active0, 0x4000000L);
			default :
				break;
		}
		return jjStartNfa_0(2, active0);
	}

	private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_0(2, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_0(3, active0);
			return 4;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x4000000L) != 0L)
				{
					return jjStartNfaWithStates_0(4, 26, 33);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_0(3, active0);
	}

	private final void jjCheckNAdd(int state)
	{
		if (jjrounds[state] != jjround)
		{
			jjstateSet[jjnewStateCnt++] = state;
			jjrounds[state] = jjround;
		}
	}

	private final void jjAddStates(int start, int end)
	{
		do
		{
			jjstateSet[jjnewStateCnt++] = jjnextStates[start];
		}
		while (start++ != end);
	}

	private final void jjCheckNAddTwoStates(int state1, int state2)
	{
		jjCheckNAdd(state1);
		jjCheckNAdd(state2);
	}

	private final void jjCheckNAddStates(int start, int end)
	{
		do
		{
			jjCheckNAdd(jjnextStates[start]);
		}
		while (start++ != end);
	}

	private final void jjCheckNAddStates(int start)
	{
		jjCheckNAdd(jjnextStates[start]);
		jjCheckNAdd(jjnextStates[start + 1]);
	}

	static final long[] jjbitVec0 = {
		0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
	};
	static final long[] jjbitVec2 = {
		0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
	};

	private final int jjMoveNfa_0(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 42;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 0:
							if ((0x3ff000000000000L & l) != 0L)
							{
								if (kind > 49)
								{
									kind = 49;
								}
								jjCheckNAdd(31);
							}
							else if ((0x2400L & l) != 0L)
							{
								if (kind > 27)
								{
									kind = 27;
								}
							}
							else if ((0x100000200L & l) != 0L)
							{
								if (kind > 23)
								{
									kind = 23;
								}
								jjCheckNAdd(9);
							}
							else if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(39, 40);
							}
							else if (curChar == 45)
							{
								jjCheckNAdd(31);
							}
							else if (curChar == 39)
							{
								jjCheckNAddStates(0, 2);
							}
							else if (curChar == 34)
							{
								jjCheckNAddStates(3, 5);
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 7;
							}
							else if (curChar == 41)
							{
								if (kind > 6)
								{
									kind = 6;
								}
								jjCheckNAddStates(6, 8);
							}
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 28;
							}
							break;
						case 1:
							if ((0x100000200L & l) != 0L)
							{
								jjCheckNAddStates(6, 8);
							}
							break;
						case 2:
							if ((0x2400L & l) != 0L && kind > 6)
							{
								kind = 6;
							}
							break;
						case 3:
							if (curChar == 10 && kind > 6)
							{
								kind = 6;
							}
							break;
						case 4:
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 3;
							}
							break;
						case 5:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 6;
							}
							break;
						case 6:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 7:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 5;
							}
							break;
						case 8:
							if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 7;
							}
							break;
						case 9:
							if ((0x100000200L & l) == 0L)
							{
								break;
							}
							if (kind > 23)
							{
								kind = 23;
							}
							jjCheckNAdd(9);
							break;
						case 10:
							if (curChar == 34)
							{
								jjCheckNAddStates(3, 5);
							}
							break;
						case 11:
							if ((0xfffffffbffffdbffL & l) != 0L)
							{
								jjCheckNAddStates(3, 5);
							}
							break;
						case 12:
							if (curChar == 34 && kind > 24)
							{
								kind = 24;
							}
							break;
						case 14:
							if ((0x8400000000L & l) != 0L)
							{
								jjCheckNAddStates(3, 5);
							}
							break;
						case 15:
							if ((0xff000000000000L & l) != 0L)
							{
								jjCheckNAddStates(9, 12);
							}
							break;
						case 16:
							if ((0xff000000000000L & l) != 0L)
							{
								jjCheckNAddStates(3, 5);
							}
							break;
						case 17:
							if ((0xf000000000000L & l) != 0L)
							{
								jjstateSet[jjnewStateCnt++] = 18;
							}
							break;
						case 18:
							if ((0xff000000000000L & l) != 0L)
							{
								jjCheckNAdd(16);
							}
							break;
						case 19:
							if (curChar == 32)
							{
								jjAddStates(13, 14);
							}
							break;
						case 20:
							if (curChar == 10)
							{
								jjCheckNAddStates(3, 5);
							}
							break;
						case 21:
							if (curChar == 39)
							{
								jjCheckNAddStates(0, 2);
							}
							break;
						case 22:
							if ((0xffffff7fffffdbffL & l) != 0L)
							{
								jjCheckNAddStates(0, 2);
							}
							break;
						case 24:
							if (curChar == 32)
							{
								jjAddStates(15, 16);
							}
							break;
						case 25:
							if (curChar == 10)
							{
								jjCheckNAddStates(0, 2);
							}
							break;
						case 26:
							if (curChar == 39 && kind > 24)
							{
								kind = 24;
							}
							break;
						case 27:
							if ((0x2400L & l) != 0L && kind > 27)
							{
								kind = 27;
							}
							break;
						case 28:
							if (curChar == 10 && kind > 27)
							{
								kind = 27;
							}
							break;
						case 29:
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 28;
							}
							break;
						case 30:
							if (curChar == 45)
							{
								jjCheckNAdd(31);
							}
							break;
						case 31:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 49)
							{
								kind = 49;
							}
							jjCheckNAdd(31);
							break;
						case 33:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 52)
							{
								kind = 52;
							}
							jjstateSet[jjnewStateCnt++] = 33;
							break;
						case 36:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 38:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(39, 40);
							}
							break;
						case 40:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 41:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(39, 40);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 0:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 52)
								{
									kind = 52;
								}
								jjCheckNAdd(33);
							}
							else if (curChar == 92)
							{
								jjCheckNAddStates(17, 20);
							}
							break;
						case 6:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 11:
							if ((0xffffffffefffffffL & l) != 0L)
							{
								jjCheckNAddStates(3, 5);
							}
							break;
						case 13:
							if (curChar == 92)
							{
								jjAddStates(21, 25);
							}
							break;
						case 14:
							if ((0x14404410000000L & l) != 0L)
							{
								jjCheckNAddStates(3, 5);
							}
							break;
						case 22:
							jjAddStates(0, 2);
							break;
						case 23:
							if (curChar == 92)
							{
								jjAddStates(15, 16);
							}
							break;
						case 32:
						case 33:
							if ((0x7fffffe87fffffeL & l) == 0L)
							{
								break;
							}
							if (kind > 52)
							{
								kind = 52;
							}
							jjCheckNAdd(33);
							break;
						case 34:
							if (curChar == 92)
							{
								jjCheckNAddStates(17, 20);
							}
							break;
						case 35:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(35, 36);
							}
							break;
						case 37:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(37, 38);
							}
							break;
						case 39:
							if (curChar == 92)
							{
								jjAddStates(26, 27);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 6:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						case 11:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
							{
								jjAddStates(3, 5);
							}
							break;
						case 22:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
							{
								jjAddStates(0, 2);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 42 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_6(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0xd000L) != 0L)
				{
					return 2;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_6(int pos, long active0)
	{
		return jjMoveNfa_6(jjStopStringLiteralDfa_6(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_6(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_6(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_6()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_6(0x5000L);
			case 42:
				return jjMoveStringLiteralDfa1_6(0x200000L);
			default :
				return jjMoveNfa_6(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_6(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_6(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				else if ((active0 & 0x200000L) != 0L)
				{
					return jjStopAtPos(1, 21);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_6(1, 14, 0);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_6(0, active0);
	}

	private final int jjMoveNfa_6(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 12;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(9, 10);
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 2;
							}
							break;
						case 0:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 2:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 0;
							}
							break;
						case 6:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 8:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(9, 10);
							}
							break;
						case 10:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 11:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(9, 10);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if (curChar == 92)
							{
								jjCheckNAddStates(28, 31);
							}
							break;
						case 1:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 5:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(5, 6);
							}
							break;
						case 7:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(7, 8);
							}
							break;
						case 9:
							if (curChar == 92)
							{
								jjAddStates(32, 33);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 1:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 12 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_4(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0xd000L) != 0L)
				{
					return 2;
				}
				if ((active0 & 0x200000000000L) != 0L)
				{
					jjmatchedKind = 52;
					return 22;
				}
				if ((active0 & 0x900000000000L) != 0L)
				{
					jjmatchedKind = 52;
					return 7;
				}
				return -1;
			case 1:
				if ((active0 & 0x4000L) != 0L)
				{
					return 0;
				}
				if ((active0 & 0x200000000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 1;
					return 28;
				}
				if ((active0 & 0x800000000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 1;
					return 7;
				}
				if ((active0 & 0x100000000000L) != 0L)
				{
					return 7;
				}
				return -1;
			case 2:
				if ((active0 & 0x200000000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 2;
					return 23;
				}
				if ((active0 & 0x800000000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 2;
					return 7;
				}
				return -1;
			case 3:
				if ((active0 & 0x800000000000L) != 0L)
				{
					return 7;
				}
				if ((active0 & 0x200000000000L) != 0L)
				{
					jjmatchedKind = 46;
					jjmatchedPos = 3;
					return 30;
				}
				return -1;
			case 4:
				if ((active0 & 0x200000000000L) != 0L)
				{
					jjmatchedKind = 52;
					jjmatchedPos = 4;
					return 7;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_4(int pos, long active0)
	{
		return jjMoveNfa_4(jjStopStringLiteralDfa_4(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_4(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_4(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_4()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_4(0x5000L);
			case 101:
				return jjMoveStringLiteralDfa1_4(0x200000000000L);
			case 105:
				return jjMoveStringLiteralDfa1_4(0x100000000000L);
			case 115:
				return jjMoveStringLiteralDfa1_4(0x800000000000L);
			default :
				return jjMoveNfa_4(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_4(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_4(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_4(1, 14, 0);
				}
				break;
			case 102:
				if ((active0 & 0x100000000000L) != 0L)
				{
					return jjStartNfaWithStates_4(1, 44, 7);
				}
				break;
			case 108:
				return jjMoveStringLiteralDfa2_4(active0, 0x200000000000L);
			case 116:
				return jjMoveStringLiteralDfa2_4(active0, 0x800000000000L);
			default :
				break;
		}
		return jjStartNfa_4(0, active0);
	}

	private final int jjMoveStringLiteralDfa2_4(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_4(0, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_4(1, active0);
			return 2;
		}
		switch (curChar)
		{
			case 111:
				return jjMoveStringLiteralDfa3_4(active0, 0x800000000000L);
			case 115:
				return jjMoveStringLiteralDfa3_4(active0, 0x200000000000L);
			default :
				break;
		}
		return jjStartNfa_4(1, active0);
	}

	private final int jjMoveStringLiteralDfa3_4(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_4(1, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_4(2, active0);
			return 3;
		}
		switch (curChar)
		{
			case 101:
				return jjMoveStringLiteralDfa4_4(active0, 0x200000000000L);
			case 112:
				if ((active0 & 0x800000000000L) != 0L)
				{
					return jjStartNfaWithStates_4(3, 47, 7);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_4(2, active0);
	}

	private final int jjMoveStringLiteralDfa4_4(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_4(2, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_4(3, active0);
			return 4;
		}
		switch (curChar)
		{
			case 105:
				return jjMoveStringLiteralDfa5_4(active0, 0x200000000000L);
			default :
				break;
		}
		return jjStartNfa_4(3, active0);
	}

	private final int jjMoveStringLiteralDfa5_4(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_4(3, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_4(4, active0);
			return 5;
		}
		switch (curChar)
		{
			case 102:
				if ((active0 & 0x200000000000L) != 0L)
				{
					return jjStartNfaWithStates_4(5, 45, 7);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_4(4, active0);
	}

	private final int jjMoveNfa_4(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 30;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if ((0x3ff000000000000L & l) != 0L)
							{
								if (kind > 49)
								{
									kind = 49;
								}
								jjCheckNAdd(5);
							}
							else if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(13, 14);
							}
							else if (curChar == 45)
							{
								jjCheckNAdd(5);
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 2;
							}
							break;
						case 30:
							if ((0x3ff000000000000L & l) != 0L)
							{
								if (kind > 52)
								{
									kind = 52;
								}
								jjCheckNAdd(7);
							}
							else if ((0x2400L & l) != 0L)
							{
								if (kind > 46)
								{
									kind = 46;
								}
							}
							else if ((0x100000200L & l) != 0L)
							{
								jjCheckNAddStates(34, 36);
							}
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 26;
							}
							break;
						case 22:
						case 7:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 52)
							{
								kind = 52;
							}
							jjCheckNAdd(7);
							break;
						case 28:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 52)
							{
								kind = 52;
							}
							jjCheckNAdd(7);
							break;
						case 23:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 52)
							{
								kind = 52;
							}
							jjCheckNAdd(7);
							break;
						case 0:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 2:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 0;
							}
							break;
						case 4:
							if (curChar == 45)
							{
								jjCheckNAdd(5);
							}
							break;
						case 5:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 49)
							{
								kind = 49;
							}
							jjCheckNAdd(5);
							break;
						case 10:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 12:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(13, 14);
							}
							break;
						case 14:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 15:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(13, 14);
							break;
						case 18:
							if ((0x100000200L & l) != 0L)
							{
								jjAddStates(37, 39);
							}
							break;
						case 19:
							if ((0x2400L & l) != 0L && kind > 43)
							{
								kind = 43;
							}
							break;
						case 20:
							if (curChar == 10 && kind > 43)
							{
								kind = 43;
							}
							break;
						case 21:
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 20;
							}
							break;
						case 24:
							if ((0x100000200L & l) != 0L)
							{
								jjCheckNAddStates(34, 36);
							}
							break;
						case 25:
							if ((0x2400L & l) != 0L && kind > 46)
							{
								kind = 46;
							}
							break;
						case 26:
							if (curChar == 10 && kind > 46)
							{
								kind = 46;
							}
							break;
						case 27:
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 26;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 52)
								{
									kind = 52;
								}
								jjCheckNAdd(7);
							}
							else if (curChar == 92)
							{
								jjCheckNAddStates(40, 43);
							}
							if (curChar == 101)
							{
								jjAddStates(44, 45);
							}
							break;
						case 30:
						case 7:
							if ((0x7fffffe87fffffeL & l) == 0L)
							{
								break;
							}
							if (kind > 52)
							{
								kind = 52;
							}
							jjCheckNAdd(7);
							break;
						case 22:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 52)
								{
									kind = 52;
								}
								jjCheckNAdd(7);
							}
							if (curChar == 108)
							{
								jjstateSet[jjnewStateCnt++] = 28;
							}
							else if (curChar == 110)
							{
								jjstateSet[jjnewStateCnt++] = 17;
							}
							break;
						case 28:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 52)
								{
									kind = 52;
								}
								jjCheckNAdd(7);
							}
							if (curChar == 115)
							{
								jjstateSet[jjnewStateCnt++] = 23;
							}
							break;
						case 23:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 52)
								{
									kind = 52;
								}
								jjCheckNAdd(7);
							}
							if (curChar == 101)
							{
								if (kind > 46)
								{
									kind = 46;
								}
								jjAddStates(34, 36);
							}
							break;
						case 1:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 6:
							if ((0x7fffffe87fffffeL & l) == 0L)
							{
								break;
							}
							if (kind > 52)
							{
								kind = 52;
							}
							jjCheckNAdd(7);
							break;
						case 8:
							if (curChar == 92)
							{
								jjCheckNAddStates(40, 43);
							}
							break;
						case 9:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(9, 10);
							}
							break;
						case 11:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(11, 12);
							}
							break;
						case 13:
							if (curChar == 92)
							{
								jjAddStates(46, 47);
							}
							break;
						case 16:
							if (curChar == 101)
							{
								jjAddStates(44, 45);
							}
							break;
						case 17:
							if (curChar != 100)
							{
								break;
							}
							if (kind > 43)
							{
								kind = 43;
							}
							jjAddStates(37, 39);
							break;
						case 29:
							if (curChar == 108)
							{
								jjstateSet[jjnewStateCnt++] = 28;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 1:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 30 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_3(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0x30000L) != 0L)
				{
					return 9;
				}
				if ((active0 & 0xd000L) != 0L)
				{
					return 16;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_3(int pos, long active0)
	{
		return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_3(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_3(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_3()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_3(0x5000L);
			case 92:
				jjmatchedKind = 17;
				return jjMoveStringLiteralDfa1_3(0x10000L);
			default :
				return jjMoveNfa_3(13, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_3(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_3(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_3(1, 14, 14);
				}
				break;
			case 92:
				if ((active0 & 0x10000L) != 0L)
				{
					return jjStartNfaWithStates_3(1, 16, 25);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_3(0, active0);
	}

	private final int jjMoveNfa_3(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 25;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 16:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 14;
							}
							break;
						case 13:
							if ((0xffffffe7ffffffffL & l) != 0L)
							{
								if (kind > 18)
								{
									kind = 18;
								}
								jjCheckNAdd(7);
							}
							else if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(22, 23);
							}
							else if (curChar == 35)
							{
								jjCheckNAddTwoStates(5, 16);
							}
							if ((0x100000200L & l) != 0L)
							{
								jjCheckNAddTwoStates(0, 6);
							}
							break;
						case 25:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(22, 23);
							}
							if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
							}
							break;
						case 9:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(22, 23);
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 11;
							}
							if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
							}
							break;
						case 0:
							if ((0x100000200L & l) != 0L)
							{
								jjCheckNAddTwoStates(0, 6);
							}
							break;
						case 2:
							if (curChar == 32)
							{
								jjAddStates(48, 49);
							}
							break;
						case 3:
							if (curChar == 40 && kind > 9)
							{
								kind = 9;
							}
							break;
						case 6:
							if (curChar == 35)
							{
								jjCheckNAdd(5);
							}
							break;
						case 7:
							if ((0xffffffe7ffffffffL & l) == 0L)
							{
								break;
							}
							if (kind > 18)
							{
								kind = 18;
							}
							jjCheckNAdd(7);
							break;
						case 10:
							if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 11;
							}
							break;
						case 12:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 8)
							{
								kind = 8;
							}
							jjstateSet[jjnewStateCnt++] = 12;
							break;
						case 14:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 15;
							}
							break;
						case 15:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 19:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 21:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(22, 23);
							}
							break;
						case 23:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 24:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(22, 23);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 16:
						case 5:
							if (curChar == 115)
							{
								jjstateSet[jjnewStateCnt++] = 4;
							}
							break;
						case 13:
							if ((0xffffffffefffffffL & l) != 0L)
							{
								if (kind > 18)
								{
									kind = 18;
								}
								jjCheckNAdd(7);
							}
							else if (curChar == 92)
							{
								jjCheckNAddStates(50, 53);
							}
							if (curChar == 92)
							{
								jjAddStates(32, 33);
							}
							break;
						case 25:
							if (curChar == 92)
							{
								jjAddStates(32, 33);
							}
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(20, 21);
							}
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(18, 19);
							}
							break;
						case 9:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(20, 21);
							}
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(18, 19);
							}
							if (curChar == 92)
							{
								jjstateSet[jjnewStateCnt++] = 8;
							}
							break;
						case 1:
							if (curChar == 116)
							{
								jjAddStates(48, 49);
							}
							break;
						case 4:
							if (curChar == 101)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 7:
							if ((0xffffffffefffffffL & l) == 0L)
							{
								break;
							}
							if (kind > 18)
							{
								kind = 18;
							}
							jjCheckNAdd(7);
							break;
						case 8:
							if (curChar == 92)
							{
								jjAddStates(32, 33);
							}
							break;
						case 11:
						case 12:
							if ((0x7fffffe87fffffeL & l) == 0L)
							{
								break;
							}
							if (kind > 8)
							{
								kind = 8;
							}
							jjCheckNAdd(12);
							break;
						case 15:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 17:
							if (curChar == 92)
							{
								jjCheckNAddStates(50, 53);
							}
							break;
						case 18:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(18, 19);
							}
							break;
						case 20:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(20, 21);
							}
							break;
						case 22:
							if (curChar == 92)
							{
								jjAddStates(54, 55);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 13:
						case 7:
							if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
							{
								break;
							}
							if (kind > 18)
							{
								kind = 18;
							}
							jjCheckNAdd(7);
							break;
						case 15:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 25 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_7(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0xd000L) != 0L)
				{
					return 2;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_7(int pos, long active0)
	{
		return jjMoveNfa_7(jjStopStringLiteralDfa_7(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_7(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_7(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_7()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_7(0x5000L);
			case 42:
				return jjMoveStringLiteralDfa1_7(0x100000L);
			default :
				return jjMoveNfa_7(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_7(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_7(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				else if ((active0 & 0x100000L) != 0L)
				{
					return jjStopAtPos(1, 20);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_7(1, 14, 0);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_7(0, active0);
	}

	private final int jjMoveNfa_7(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 12;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(9, 10);
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 2;
							}
							break;
						case 0:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 2:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 0;
							}
							break;
						case 6:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 8:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(9, 10);
							}
							break;
						case 10:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 11:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(9, 10);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if (curChar == 92)
							{
								jjCheckNAddStates(28, 31);
							}
							break;
						case 1:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 5:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(5, 6);
							}
							break;
						case 7:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(7, 8);
							}
							break;
						case 9:
							if (curChar == 92)
							{
								jjAddStates(32, 33);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 1:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 12 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_8(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0xd000L) != 0L)
				{
					return 2;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_8(int pos, long active0)
	{
		return jjMoveNfa_8(jjStopStringLiteralDfa_8(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_8(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_8(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_8()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_8(0x5000L);
			default :
				return jjMoveNfa_8(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_8(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_8(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_8(1, 14, 0);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_8(0, active0);
	}

	private final int jjMoveNfa_8(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 15;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if ((0x2400L & l) != 0L)
							{
								if (kind > 19)
								{
									kind = 19;
								}
							}
							else if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(12, 13);
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 2;
							}
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 5;
							}
							break;
						case 0:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 2:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 0;
							}
							break;
						case 4:
							if ((0x2400L & l) != 0L && kind > 19)
							{
								kind = 19;
							}
							break;
						case 5:
							if (curChar == 10 && kind > 19)
							{
								kind = 19;
							}
							break;
						case 6:
							if (curChar == 13)
							{
								jjstateSet[jjnewStateCnt++] = 5;
							}
							break;
						case 9:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 11:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(12, 13);
							}
							break;
						case 13:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 14:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(12, 13);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if (curChar == 92)
							{
								jjCheckNAddStates(56, 59);
							}
							break;
						case 1:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 8:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(8, 9);
							}
							break;
						case 10:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(10, 11);
							}
							break;
						case 12:
							if (curChar == 92)
							{
								jjAddStates(60, 61);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 1:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 15 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_5(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0xd000L) != 0L)
				{
					return 2;
				}
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					return 5;
				}
				return -1;
			case 1:
				if ((active0 & 0x4000L) != 0L)
				{
					return 0;
				}
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 1;
					return 5;
				}
				return -1;
			case 2:
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 2;
					return 5;
				}
				return -1;
			case 3:
				if ((active0 & 0x4000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 3;
					return 5;
				}
				if ((active0 & 0x2000000L) != 0L)
				{
					return 5;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_5(int pos, long active0)
	{
		return jjMoveNfa_5(jjStopStringLiteralDfa_5(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_5(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_5(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_5()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_5(0x5000L);
			case 102:
				return jjMoveStringLiteralDfa1_5(0x4000000L);
			case 116:
				return jjMoveStringLiteralDfa1_5(0x2000000L);
			case 123:
				return jjStopAtPos(0, 58);
			case 125:
				return jjStopAtPos(0, 59);
			default :
				return jjMoveNfa_5(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_5(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_5(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_5(1, 14, 0);
				}
				break;
			case 97:
				return jjMoveStringLiteralDfa2_5(active0, 0x4000000L);
			case 114:
				return jjMoveStringLiteralDfa2_5(active0, 0x2000000L);
			default :
				break;
		}
		return jjStartNfa_5(0, active0);
	}

	private final int jjMoveStringLiteralDfa2_5(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_5(0, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_5(1, active0);
			return 2;
		}
		switch (curChar)
		{
			case 108:
				return jjMoveStringLiteralDfa3_5(active0, 0x4000000L);
			case 117:
				return jjMoveStringLiteralDfa3_5(active0, 0x2000000L);
			default :
				break;
		}
		return jjStartNfa_5(1, active0);
	}

	private final int jjMoveStringLiteralDfa3_5(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_5(1, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_5(2, active0);
			return 3;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x2000000L) != 0L)
				{
					return jjStartNfaWithStates_5(3, 25, 5);
				}
				break;
			case 115:
				return jjMoveStringLiteralDfa4_5(active0, 0x4000000L);
			default :
				break;
		}
		return jjStartNfa_5(2, active0);
	}

	private final int jjMoveStringLiteralDfa4_5(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_5(2, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_5(3, active0);
			return 4;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x4000000L) != 0L)
				{
					return jjStartNfaWithStates_5(4, 26, 5);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_5(3, active0);
	}

	private final int jjMoveNfa_5(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 16;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(13, 14);
							}
							else if (curChar == 46)
							{
								jjstateSet[jjnewStateCnt++] = 7;
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 2;
							}
							break;
						case 0:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 2:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 0;
							}
							break;
						case 5:
							if ((0x3ff200000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 56)
							{
								kind = 56;
							}
							jjstateSet[jjnewStateCnt++] = 5;
							break;
						case 6:
							if (curChar == 46)
							{
								jjstateSet[jjnewStateCnt++] = 7;
							}
							break;
						case 10:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 12:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(13, 14);
							}
							break;
						case 14:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 15:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(13, 14);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 56)
								{
									kind = 56;
								}
								jjCheckNAdd(5);
							}
							else if (curChar == 92)
							{
								jjCheckNAddStates(40, 43);
							}
							break;
						case 1:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 4:
						case 5:
							if ((0x7fffffe87fffffeL & l) == 0L)
							{
								break;
							}
							if (kind > 56)
							{
								kind = 56;
							}
							jjCheckNAdd(5);
							break;
						case 7:
							if ((0x7fffffe07fffffeL & l) != 0L && kind > 57)
							{
								kind = 57;
							}
							break;
						case 8:
							if (curChar == 92)
							{
								jjCheckNAddStates(40, 43);
							}
							break;
						case 9:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(9, 10);
							}
							break;
						case 11:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(11, 12);
							}
							break;
						case 13:
							if (curChar == 92)
							{
								jjAddStates(46, 47);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 1:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 16 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_1(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0xd000L) != 0L)
				{
					return 2;
				}
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					return 25;
				}
				if ((active0 & 0x10L) != 0L)
				{
					return 27;
				}
				return -1;
			case 1:
				if ((active0 & 0x4000L) != 0L)
				{
					return 0;
				}
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 1;
					return 25;
				}
				return -1;
			case 2:
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 2;
					return 25;
				}
				return -1;
			case 3:
				if ((active0 & 0x4000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 3;
					return 25;
				}
				if ((active0 & 0x2000000L) != 0L)
				{
					return 25;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_1(int pos, long active0)
	{
		return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_1(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_1(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_1()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_1(0x5000L);
			case 41:
				return jjStopAtPos(0, 7);
			case 44:
				return jjStopAtPos(0, 3);
			case 46:
				return jjMoveStringLiteralDfa1_1(0x10L);
			case 91:
				return jjStopAtPos(0, 1);
			case 93:
				return jjStopAtPos(0, 2);
			case 102:
				return jjMoveStringLiteralDfa1_1(0x4000000L);
			case 116:
				return jjMoveStringLiteralDfa1_1(0x2000000L);
			case 123:
				return jjStopAtPos(0, 58);
			case 125:
				return jjStopAtPos(0, 59);
			default :
				return jjMoveNfa_1(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_1(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_1(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_1(1, 14, 0);
				}
				break;
			case 46:
				if ((active0 & 0x10L) != 0L)
				{
					return jjStopAtPos(1, 4);
				}
				break;
			case 97:
				return jjMoveStringLiteralDfa2_1(active0, 0x4000000L);
			case 114:
				return jjMoveStringLiteralDfa2_1(active0, 0x2000000L);
			default :
				break;
		}
		return jjStartNfa_1(0, active0);
	}

	private final int jjMoveStringLiteralDfa2_1(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_1(0, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_1(1, active0);
			return 2;
		}
		switch (curChar)
		{
			case 108:
				return jjMoveStringLiteralDfa3_1(active0, 0x4000000L);
			case 117:
				return jjMoveStringLiteralDfa3_1(active0, 0x2000000L);
			default :
				break;
		}
		return jjStartNfa_1(1, active0);
	}

	private final int jjMoveStringLiteralDfa3_1(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_1(1, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_1(2, active0);
			return 3;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x2000000L) != 0L)
				{
					return jjStartNfaWithStates_1(3, 25, 25);
				}
				break;
			case 115:
				return jjMoveStringLiteralDfa4_1(active0, 0x4000000L);
			default :
				break;
		}
		return jjStartNfa_1(2, active0);
	}

	private final int jjMoveStringLiteralDfa4_1(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_1(2, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_1(3, active0);
			return 4;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x4000000L) != 0L)
				{
					return jjStartNfaWithStates_1(4, 26, 25);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_1(3, active0);
	}

	private final int jjMoveNfa_1(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 36;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if ((0x3ff000000000000L & l) != 0L)
							{
								if (kind > 49)
								{
									kind = 49;
								}
								jjCheckNAdd(23);
							}
							else if ((0x100000200L & l) != 0L)
							{
								if (kind > 23)
								{
									kind = 23;
								}
								jjCheckNAdd(4);
							}
							else if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(33, 34);
							}
							else if (curChar == 46)
							{
								jjstateSet[jjnewStateCnt++] = 27;
							}
							else if (curChar == 45)
							{
								jjCheckNAdd(23);
							}
							else if (curChar == 39)
							{
								jjCheckNAddStates(62, 64);
							}
							else if (curChar == 34)
							{
								jjCheckNAddStates(65, 67);
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 2;
							}
							break;
						case 0:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 2:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 0;
							}
							break;
						case 4:
							if ((0x100000200L & l) == 0L)
							{
								break;
							}
							if (kind > 23)
							{
								kind = 23;
							}
							jjCheckNAdd(4);
							break;
						case 5:
							if (curChar == 34)
							{
								jjCheckNAddStates(65, 67);
							}
							break;
						case 6:
							if ((0xfffffffbffffdbffL & l) != 0L)
							{
								jjCheckNAddStates(65, 67);
							}
							break;
						case 7:
							if (curChar == 34 && kind > 24)
							{
								kind = 24;
							}
							break;
						case 9:
							if ((0x8400000000L & l) != 0L)
							{
								jjCheckNAddStates(65, 67);
							}
							break;
						case 10:
							if ((0xff000000000000L & l) != 0L)
							{
								jjCheckNAddStates(68, 71);
							}
							break;
						case 11:
							if ((0xff000000000000L & l) != 0L)
							{
								jjCheckNAddStates(65, 67);
							}
							break;
						case 12:
							if ((0xf000000000000L & l) != 0L)
							{
								jjstateSet[jjnewStateCnt++] = 13;
							}
							break;
						case 13:
							if ((0xff000000000000L & l) != 0L)
							{
								jjCheckNAdd(11);
							}
							break;
						case 14:
							if (curChar == 32)
							{
								jjAddStates(72, 73);
							}
							break;
						case 15:
							if (curChar == 10)
							{
								jjCheckNAddStates(65, 67);
							}
							break;
						case 16:
							if (curChar == 39)
							{
								jjCheckNAddStates(62, 64);
							}
							break;
						case 17:
							if ((0xffffff7fffffdbffL & l) != 0L)
							{
								jjCheckNAddStates(62, 64);
							}
							break;
						case 19:
							if (curChar == 32)
							{
								jjAddStates(13, 14);
							}
							break;
						case 20:
							if (curChar == 10)
							{
								jjCheckNAddStates(62, 64);
							}
							break;
						case 21:
							if (curChar == 39 && kind > 24)
							{
								kind = 24;
							}
							break;
						case 22:
							if (curChar == 45)
							{
								jjCheckNAdd(23);
							}
							break;
						case 23:
							if ((0x3ff000000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 49)
							{
								kind = 49;
							}
							jjCheckNAdd(23);
							break;
						case 25:
							if ((0x3ff200000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 56)
							{
								kind = 56;
							}
							jjstateSet[jjnewStateCnt++] = 25;
							break;
						case 26:
							if (curChar == 46)
							{
								jjstateSet[jjnewStateCnt++] = 27;
							}
							break;
						case 30:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 32:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(33, 34);
							}
							break;
						case 34:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 35:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(33, 34);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 56)
								{
									kind = 56;
								}
								jjCheckNAdd(25);
							}
							else if (curChar == 92)
							{
								jjCheckNAddStates(74, 77);
							}
							break;
						case 1:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 6:
							if ((0xffffffffefffffffL & l) != 0L)
							{
								jjCheckNAddStates(65, 67);
							}
							break;
						case 8:
							if (curChar == 92)
							{
								jjAddStates(78, 82);
							}
							break;
						case 9:
							if ((0x14404410000000L & l) != 0L)
							{
								jjCheckNAddStates(65, 67);
							}
							break;
						case 17:
							jjAddStates(62, 64);
							break;
						case 18:
							if (curChar == 92)
							{
								jjAddStates(13, 14);
							}
							break;
						case 24:
						case 25:
							if ((0x7fffffe87fffffeL & l) == 0L)
							{
								break;
							}
							if (kind > 56)
							{
								kind = 56;
							}
							jjCheckNAdd(25);
							break;
						case 27:
							if ((0x7fffffe07fffffeL & l) != 0L && kind > 57)
							{
								kind = 57;
							}
							break;
						case 28:
							if (curChar == 92)
							{
								jjCheckNAddStates(74, 77);
							}
							break;
						case 29:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(29, 30);
							}
							break;
						case 31:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(31, 32);
							}
							break;
						case 33:
							if (curChar == 92)
							{
								jjAddStates(83, 84);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 1:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						case 6:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
							{
								jjAddStates(65, 67);
							}
							break;
						case 17:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
							{
								jjAddStates(62, 64);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 36 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	private final int jjStopStringLiteralDfa_2(int pos, long active0)
	{
		switch (pos)
		{
			case 0:
				if ((active0 & 0xd000L) != 0L)
				{
					return 2;
				}
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					return 5;
				}
				return -1;
			case 1:
				if ((active0 & 0x4000L) != 0L)
				{
					return 0;
				}
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 1;
					return 5;
				}
				return -1;
			case 2:
				if ((active0 & 0x6000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 2;
					return 5;
				}
				return -1;
			case 3:
				if ((active0 & 0x4000000L) != 0L)
				{
					jjmatchedKind = 56;
					jjmatchedPos = 3;
					return 5;
				}
				if ((active0 & 0x2000000L) != 0L)
				{
					return 5;
				}
				return -1;
			default :
				return -1;
		}
	}

	private final int jjStartNfa_2(int pos, long active0)
	{
		return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0), pos + 1);
	}

	private final int jjStartNfaWithStates_2(int pos, int kind, int state)
	{
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			return pos + 1;
		}
		return jjMoveNfa_2(state, pos + 1);
	}

	private final int jjMoveStringLiteralDfa0_2()
	{
		switch (curChar)
		{
			case 35:
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_2(0x5000L);
			case 40:
				return jjStopAtPos(0, 5);
			case 102:
				return jjMoveStringLiteralDfa1_2(0x4000000L);
			case 116:
				return jjMoveStringLiteralDfa1_2(0x2000000L);
			case 123:
				return jjStopAtPos(0, 58);
			case 125:
				return jjStopAtPos(0, 59);
			default :
				return jjMoveNfa_2(3, 0);
		}
	}

	private final int jjMoveStringLiteralDfa1_2(long active0)
	{
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_2(0, active0);
			return 1;
		}
		switch (curChar)
		{
			case 35:
				if ((active0 & 0x1000L) != 0L)
				{
					return jjStopAtPos(1, 12);
				}
				break;
			case 42:
				if ((active0 & 0x4000L) != 0L)
				{
					return jjStartNfaWithStates_2(1, 14, 0);
				}
				break;
			case 97:
				return jjMoveStringLiteralDfa2_2(active0, 0x4000000L);
			case 114:
				return jjMoveStringLiteralDfa2_2(active0, 0x2000000L);
			default :
				break;
		}
		return jjStartNfa_2(0, active0);
	}

	private final int jjMoveStringLiteralDfa2_2(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_2(0, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_2(1, active0);
			return 2;
		}
		switch (curChar)
		{
			case 108:
				return jjMoveStringLiteralDfa3_2(active0, 0x4000000L);
			case 117:
				return jjMoveStringLiteralDfa3_2(active0, 0x2000000L);
			default :
				break;
		}
		return jjStartNfa_2(1, active0);
	}

	private final int jjMoveStringLiteralDfa3_2(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_2(1, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_2(2, active0);
			return 3;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x2000000L) != 0L)
				{
					return jjStartNfaWithStates_2(3, 25, 5);
				}
				break;
			case 115:
				return jjMoveStringLiteralDfa4_2(active0, 0x4000000L);
			default :
				break;
		}
		return jjStartNfa_2(2, active0);
	}

	private final int jjMoveStringLiteralDfa4_2(long old0, long active0)
	{
		if (((active0 &= old0)) == 0L)
		{
			return jjStartNfa_2(2, old0);
		}
		try
		{
			curChar = input_stream.readChar();
		}
		catch (java.io.IOException e)
		{
			jjStopStringLiteralDfa_2(3, active0);
			return 4;
		}
		switch (curChar)
		{
			case 101:
				if ((active0 & 0x4000000L) != 0L)
				{
					return jjStartNfaWithStates_2(4, 26, 5);
				}
				break;
			default :
				break;
		}
		return jjStartNfa_2(3, active0);
	}

	private final int jjMoveNfa_2(int startState, int curPos)
	{
		int[] nextStates;
		int startsAt = 0;
		jjnewStateCnt = 16;
		int i = 1;
		jjstateSet[0] = startState;
		int j, kind = 0x7fffffff;
		for (; ;)
		{
			if (++jjround == 0x7fffffff)
			{
				ReInitRounds();
			}
			if (curChar < 64)
			{
				long l = 1L << curChar;
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if (curChar == 36)
							{
								if (kind > 10)
								{
									kind = 10;
								}
								jjCheckNAddTwoStates(13, 14);
							}
							else if (curChar == 46)
							{
								jjstateSet[jjnewStateCnt++] = 7;
							}
							else if (curChar == 35)
							{
								jjstateSet[jjnewStateCnt++] = 2;
							}
							break;
						case 0:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 1;
							}
							break;
						case 1:
							if ((0xfffffff7ffffffffL & l) != 0L && kind > 13)
							{
								kind = 13;
							}
							break;
						case 2:
							if (curChar == 42)
							{
								jjstateSet[jjnewStateCnt++] = 0;
							}
							break;
						case 5:
							if ((0x3ff200000000000L & l) == 0L)
							{
								break;
							}
							if (kind > 56)
							{
								kind = 56;
							}
							jjstateSet[jjnewStateCnt++] = 5;
							break;
						case 6:
							if (curChar == 46)
							{
								jjstateSet[jjnewStateCnt++] = 7;
							}
							break;
						case 10:
							if (curChar == 36 && kind > 10)
							{
								kind = 10;
							}
							break;
						case 12:
							if (curChar == 36)
							{
								jjCheckNAddTwoStates(13, 14);
							}
							break;
						case 14:
							if (curChar == 33 && kind > 11)
							{
								kind = 11;
							}
							break;
						case 15:
							if (curChar != 36)
							{
								break;
							}
							if (kind > 10)
							{
								kind = 10;
							}
							jjCheckNAddTwoStates(13, 14);
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else if (curChar < 128)
			{
				long l = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 3:
							if ((0x7fffffe87fffffeL & l) != 0L)
							{
								if (kind > 56)
								{
									kind = 56;
								}
								jjCheckNAdd(5);
							}
							else if (curChar == 92)
							{
								jjCheckNAddStates(40, 43);
							}
							break;
						case 1:
							if (kind > 13)
							{
								kind = 13;
							}
							break;
						case 4:
						case 5:
							if ((0x7fffffe87fffffeL & l) == 0L)
							{
								break;
							}
							if (kind > 56)
							{
								kind = 56;
							}
							jjCheckNAdd(5);
							break;
						case 7:
							if ((0x7fffffe07fffffeL & l) != 0L && kind > 57)
							{
								kind = 57;
							}
							break;
						case 8:
							if (curChar == 92)
							{
								jjCheckNAddStates(40, 43);
							}
							break;
						case 9:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(9, 10);
							}
							break;
						case 11:
							if (curChar == 92)
							{
								jjCheckNAddTwoStates(11, 12);
							}
							break;
						case 13:
							if (curChar == 92)
							{
								jjAddStates(46, 47);
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			else
			{
				int hiByte = (int) (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				MatchLoop: do
				{
					switch (jjstateSet[--i])
					{
						case 1:
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 13)
							{
								kind = 13;
							}
							break;
						default :
							break;
					}
				}
				while (i != startsAt);
			}
			if (kind != 0x7fffffff)
			{
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 16 - (jjnewStateCnt = startsAt)))
			{
				return curPos;
			}
			try
			{
				curChar = input_stream.readChar();
			}
			catch (java.io.IOException e)
			{
				return curPos;
			}
		}
	}

	static final int[] jjnextStates = {
		22, 23, 26, 11, 12, 13, 1, 2, 4, 11, 16, 12, 13, 19, 20, 24,
		25, 35, 36, 37, 38, 14, 15, 17, 19, 20, 39, 40, 5, 6, 7, 8,
		9, 10, 24, 25, 27, 18, 19, 21, 9, 10, 11, 12, 22, 29, 13, 14,
		2, 3, 18, 19, 20, 21, 22, 23, 8, 9, 10, 11, 12, 13, 17, 18,
		21, 6, 7, 8, 6, 11, 7, 8, 14, 15, 29, 30, 31, 32, 9, 10,
		12, 14, 15, 33, 34,
	};

	private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
	{
		switch (hiByte)
		{
			case 0:
				return ((jjbitVec2[i2] & l2) != 0L);
			default :
				if ((jjbitVec0[i1] & l1) != 0L)
				{
					return true;
				}
				return false;
		}
	}

	public static final String[] jjstrLiteralImages = {
		null, null, null, null, null, null, null, null, null, null, null, null, null,
		null, null, null, null, null, null, null, null, null, null, null, null, null, null,
		null, null, null, null, null, null, null, null, null, null, null, null, null, null,
		null, null, null, null, null, null, null, null, null, null, null, null, null, null,
		null, null, null, null, null, null, null, };
	public static final String[] lexStateNames = {
		"DIRECTIVE",
		"REFMOD2",
		"REFMODIFIER",
		"DEFAULT",
		"PRE_DIRECTIVE",
		"REFERENCE",
		"IN_MULTI_LINE_COMMENT",
		"IN_FORMAL_COMMENT",
		"IN_SINGLE_LINE_COMMENT",
	};
	public static final int[] jjnewLexState = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	};
	static final long[] jjtoToken = {
		0xf12ffffffbf03ffL,
	};
	static final long[] jjtoSkip = {
		0x3000000000000000L,
	};
	static final long[] jjtoSpecial = {
		0x3000000000000000L,
	};
	static final long[] jjtoMore = {
		0x40fc00L,
	};
	private CharStream input_stream;
	private final int[] jjrounds = new int[42];
	private final int[] jjstateSet = new int[84];
	StringBuffer image;
	int jjimageLen;
	int lengthOfMatch;
	protected char curChar;

	public ParserTokenManager(CharStream stream)
	{
		input_stream = stream;
	}

	public ParserTokenManager(CharStream stream, int lexState)
	{
		this(stream);
		SwitchTo(lexState);
	}

	public void ReInit(CharStream stream)
	{
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = stream;
		ReInitRounds();
	}

	private final void ReInitRounds()
	{
		int i;
		jjround = 0x80000001;
		for (i = 42; i-- > 0;)
		{
			jjrounds[i] = 0x80000000;
		}
	}

	public void ReInit(CharStream stream, int lexState)
	{
		ReInit(stream);
		SwitchTo(lexState);
	}

	public void SwitchTo(int lexState)
	{
		if (lexState >= 9 || lexState < 0)
		{
			throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
		}
		else
		{
			curLexState = lexState;
		}
	}

	private final Token jjFillToken()
	{
		Token t = Token.newToken(jjmatchedKind);
		t.kind = jjmatchedKind;
		String im = jjstrLiteralImages[jjmatchedKind];
		t.image = (im == null) ? input_stream.GetImage() : im;
		t.beginLine = input_stream.getBeginLine();
		t.beginColumn = input_stream.getBeginColumn();
		t.endLine = input_stream.getEndLine();
		t.endColumn = input_stream.getEndColumn();
		return t;
	}

	int curLexState = 3;
	int defaultLexState = 3;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	public final Token getNextToken()
	{
		int kind;
		Token specialToken = null;
		Token matchedToken;
		int curPos = 0;

		EOFLoop :
		for (; ;)
		{
			try
			{
				curChar = input_stream.BeginToken();
			}
			catch (java.io.IOException e)
			{
				jjmatchedKind = 0;
				matchedToken = jjFillToken();
				matchedToken.specialToken = specialToken;
				return matchedToken;
			}
			image = null;
			jjimageLen = 0;

			for (; ;)
			{
				switch (curLexState)
				{
					case 0:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_0();
						break;
					case 1:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_1();
						if (jjmatchedPos == 0 && jjmatchedKind > 60)
						{
							jjmatchedKind = 60;
						}
						break;
					case 2:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_2();
						if (jjmatchedPos == 0 && jjmatchedKind > 60)
						{
							jjmatchedKind = 60;
						}
						break;
					case 3:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_3();
						break;
					case 4:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_4();
						if (jjmatchedPos == 0 && jjmatchedKind > 61)
						{
							jjmatchedKind = 61;
						}
						break;
					case 5:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_5();
						if (jjmatchedPos == 0 && jjmatchedKind > 60)
						{
							jjmatchedKind = 60;
						}
						break;
					case 6:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_6();
						if (jjmatchedPos == 0 && jjmatchedKind > 22)
						{
							jjmatchedKind = 22;
						}
						break;
					case 7:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_7();
						if (jjmatchedPos == 0 && jjmatchedKind > 22)
						{
							jjmatchedKind = 22;
						}
						break;
					case 8:
						jjmatchedKind = 0x7fffffff;
						jjmatchedPos = 0;
						curPos = jjMoveStringLiteralDfa0_8();
						if (jjmatchedPos == 0 && jjmatchedKind > 22)
						{
							jjmatchedKind = 22;
						}
						break;
				}
				if (jjmatchedKind != 0x7fffffff)
				{
					if (jjmatchedPos + 1 < curPos)
					{
						input_stream.backup(curPos - jjmatchedPos - 1);
					}
					if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
					{
						matchedToken = jjFillToken();
						matchedToken.specialToken = specialToken;
						TokenLexicalActions(matchedToken);
						if (jjnewLexState[jjmatchedKind] != -1)
						{
							curLexState = jjnewLexState[jjmatchedKind];
						}
						return matchedToken;
					}
					else if ((jjtoSkip[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
					{
						if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
						{
							matchedToken = jjFillToken();
							if (specialToken == null)
							{
								specialToken = matchedToken;
							}
							else
							{
								matchedToken.specialToken = specialToken;
								specialToken = (specialToken.next = matchedToken);
							}
							SkipLexicalActions(matchedToken);
						}
						else
						{
							SkipLexicalActions(null);
						}
						if (jjnewLexState[jjmatchedKind] != -1)
						{
							curLexState = jjnewLexState[jjmatchedKind];
						}
						continue EOFLoop;
					}
					MoreLexicalActions();
					if (jjnewLexState[jjmatchedKind] != -1)
					{
						curLexState = jjnewLexState[jjmatchedKind];
					}
					curPos = 0;
					jjmatchedKind = 0x7fffffff;
					try
					{
						curChar = input_stream.readChar();
						continue;
					}
					catch (java.io.IOException e1)
					{
					}
				}
				int error_line = input_stream.getEndLine();
				int error_column = input_stream.getEndColumn();
				String error_after = null;
				boolean EOFSeen = false;
				try
				{
					input_stream.readChar();
					input_stream.backup(1);
				}
				catch (java.io.IOException e1)
				{
					EOFSeen = true;
					error_after = curPos <= 1 ? "" : input_stream.GetImage();
					if (curChar == '\n' || curChar == '\r')
					{
						error_line++;
						error_column = 0;
					}
					else
					{
						error_column++;
					}
				}
				if (!EOFSeen)
				{
					input_stream.backup(1);
					error_after = curPos <= 1 ? "" : input_stream.GetImage();
				}
				throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
			}
		}
	}

	final void SkipLexicalActions(Token matchedToken)
	{
		switch (jjmatchedKind)
		{
			case 60:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				/*
				 * push every terminator character back into the stream
				 */

				input_stream.backup(1);

				inReference = false;

				if (debugPrint)
				{
					System.out.print("REF_TERM :");
				}

				stateStackPop();
				break;
			case 61:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				if (debugPrint)
				{
					System.out.print("DIRECTIVE_TERM :");
				}

				input_stream.backup(1);
				inDirective = false;
				stateStackPop();
				break;
			default :
				break;
		}
	}

	final void MoreLexicalActions()
	{
		jjimageLen += (lengthOfMatch = jjmatchedPos + 1);
		switch (jjmatchedKind)
		{
			case 10:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen)));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen));
				}
				jjimageLen = 0;
				if (!inComment)
				{
					/*
					 * if we find ourselves in REFERENCE, we need to pop down
					 * to end the previous ref
					 */

					if (curLexState == REFERENCE)
					{
						inReference = false;
						stateStackPop();
					}

					inReference = true;

					if (debugPrint)
					{
						System.out.print("$  : going to " + REFERENCE);
					}

					stateStackPush();
					SwitchTo(REFERENCE);
				}
				break;
			case 11:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen)));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen));
				}
				jjimageLen = 0;
				if (!inComment)
				{
					/*
					 * if we find ourselves in REFERENCE, we need to pop down
					 * to end the previous ref
					 */

					if (curLexState == REFERENCE)
					{
						inReference = false;
						stateStackPop();
					}

					inReference = true;

					if (debugPrint)
					{
						System.out.print("$!  : going to " + REFERENCE);
					}

					stateStackPush();
					SwitchTo(REFERENCE);
				}
				break;
			case 12:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen)));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen));
				}
				jjimageLen = 0;
				if (!inComment)
				{
					if (curLexState == REFERENCE)
					{
						inReference = false;
						stateStackPop();
					}

					inComment = true;
					stateStackPush();
					SwitchTo(IN_SINGLE_LINE_COMMENT);
				}
				break;
			case 13:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen)));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen));
				}
				jjimageLen = 0;
				input_stream.backup(1);
				inComment = true;
				stateStackPush();
				SwitchTo(IN_FORMAL_COMMENT);
				break;
			case 14:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen)));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen));
				}
				jjimageLen = 0;
				inComment = true;
				stateStackPush();
				SwitchTo(IN_MULTI_LINE_COMMENT);
				break;
			case 15:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen)));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen));
				}
				jjimageLen = 0;
				if (!inComment)
				{
					/*
					 * We can have the situation where #if($foo)$foo#end.
					 * We need to transition out of REFERENCE before going to DIRECTIVE.
					 * I don't really like this, but I can't think of a legal way
					 * you are going into DIRECTIVE while in REFERENCE.  -gmj
					 */

					if (curLexState == REFERENCE || curLexState == REFMODIFIER)
					{
						inReference = false;
						stateStackPop();
					}

					inDirective = true;

					if (debugPrint)
					{
						System.out.print("# :  going to " + DIRECTIVE);
					}

					stateStackPush();
					SwitchTo(PRE_DIRECTIVE);
				}
				break;
			default :
				break;
		}
	}

	final void TokenLexicalActions(Token matchedToken)
	{
		switch (jjmatchedKind)
		{
			case 5:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				if (!inComment)
				{
					lparen++;
				}

				/*
				 * If in REFERENCE and we have seen the dot, then move
				 * to REFMOD2 -> Modifier()
				 */

				if (curLexState == REFMODIFIER)
				{
					SwitchTo(REFMOD2);
				}
				break;
			case 6:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				RPARENHandler();
				break;
			case 7:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				/*
				 * need to simply switch back to REFERENCE, not drop down the stack
				 * because we can (infinitely) chain, ala
				 * $foo.bar().blargh().woogie().doogie()
				 */

				SwitchTo(REFERENCE);
				break;
			case 9:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				if (!inComment)
				{
					inDirective = true;

					if (debugPrint)
					{
						System.out.print("#set :  going to " + DIRECTIVE);
					}

					stateStackPush();
					inSet = true;
					SwitchTo(DIRECTIVE);
				}

				/*
				 *  need the LPAREN action
				 */

				if (!inComment)
				{
					lparen++;

					/*
					 * If in REFERENCE and we have seen the dot, then move
					 * to REFMOD2 -> Modifier()
					 */

					if (curLexState == REFMODIFIER)
					{
						SwitchTo(REFMOD2);
					}
				}
				break;
			case 19:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				inComment = false;
				stateStackPop();
				break;
			case 20:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				inComment = false;
				stateStackPop();
				break;
			case 21:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				inComment = false;
				stateStackPop();
				break;
			case 24:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				/*
				 *  - if we are in DIRECTIVE and haven't seen ( yet, then also drop out.
				 *      don't forget to account for the beloved yet wierd #set
				 *  - finally, if we are in REFMOD2 (remember : $foo.bar( ) then " is ok!
				 */

				if (curLexState == DIRECTIVE && !inSet && lparen == 0)
				{
					stateStackPop();
				}
				break;
			case 27:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				if (debugPrint)
				{
					System.out.println(" NEWLINE :");
				}

				stateStackPop();

				if (inSet)
				{
					inSet = false;
				}

				if (inDirective)
				{
					inDirective = false;
				}
				break;
			case 43:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				inDirective = false;
				stateStackPop();
				break;
			case 44:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				SwitchTo(DIRECTIVE);
				break;
			case 45:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				SwitchTo(DIRECTIVE);
				break;
			case 46:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				inDirective = false;
				stateStackPop();
				break;
			case 47:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				matchedToken.kind = EOF;
				fileDepth = 0;
				break;
			case 49:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				/*
				 * check to see if we are in set
				 *    ex.  #set $foo = $foo + 3
				 *  because we want to handle the \n after
				 */

				if (lparen == 0 && !inSet && curLexState != REFMOD2)
				{
					stateStackPop();
				}
				break;
			case 57:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				/*
				 * push the alpha char back into the stream so the following identifier
				 * is complete
				 */

				input_stream.backup(1);

				/*
				 * and munge the <DOT> so we just get a . when we have normal text that
				 * looks like a ref.ident
				 */

				matchedToken.image = ".";

				if (debugPrint)
				{
					System.out.print("DOT : switching to " + REFMODIFIER);
				}
				SwitchTo(REFMODIFIER);
				break;
			case 59:
				if (image == null)
				{
					image = new StringBuffer(new String(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1))));
				}
				else
				{
					image.append(input_stream.GetSuffix(jjimageLen + (lengthOfMatch = jjmatchedPos + 1)));
				}
				stateStackPop();
				break;
			default :
				break;
		}
	}
}
