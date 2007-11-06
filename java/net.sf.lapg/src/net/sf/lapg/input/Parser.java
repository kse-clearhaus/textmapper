// Parser.java

package net.sf.lapg.input;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {
	
	public Parser() {
	}
	
	private static final boolean DEBUG_SYNTAX = false;
	
	private HashMap<String,CSymbol> symbols = new HashMap<String,CSymbol>();
	
	private byte[] buff;
	private int l;
	private int deep = 0;
	
	private String rawData(int start, int end) {
		return new String(buff, start, end-start);
	}
	
	private CSymbol getSymbol(String name, int line) {
		CSymbol res = symbols.get(name);
		if( res == null ) {
			res = new CSymbol(name);
			symbols.put(name,res);
		}
		if( line > 0 ) {
			res.intDefine(line);
		}
		return res;
	}
	
	private CSymbol wrapCommand(CAction action) {
		return new CSymbol(null); // TODO
	}
	
	void error( String s ) {
		System.err.println(s);
	}
	
	public boolean parse(String s) {
		l = 0;
		try {
			buff = s.getBytes("utf-8");
		} catch( UnsupportedEncodingException ex ) {
			return false;
		}
		boolean res = parse();
		return res;
	}

	public class lapg_place {
		public int line, offset;

		public lapg_place( int line, int offset ) {
			this.line = line;
			this.offset = offset;
		}
	};

	public class lapg_symbol {
		public Object sym;
		public int  lexem, state;
		public lapg_place pos;
		public lapg_place endpos;
	};

	private static final short[] lapg_char2no = new short[] {
		   0,   1,   1,   1,   1,   1,   1,   1,   1,   2,   3,   1,   1,   4,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   5,   1,   6,   7,   1,   8,   1,   9,  10,  11,   1,   1,   1,  12,  13,  14,
		  15,  16,  17,  18,  19,  20,  21,  22,  23,  24,  25,  26,  27,  28,   1,  29,
		   1,  30,  31,  32,  33,  34,  35,  36,  37,  38,  39,  40,  41,  42,  43,  44,
		  45,  46,  47,  48,  49,  50,  51,  52,  53,  54,  55,  56,  57,  58,   1,  59,
		   1,  60,  61,  62,  63,  64,  65,  66,  67,  68,  69,  70,  71,  72,  73,  74,
		  75,  76,  77,  78,  79,  80,  81,  82,  83,  84,  85,  86,  87,  88,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
		   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,
	};

	private static final short[][] lapg_lexem = new short[][] {
		{  -2,  -1,   2,   3,   2,   2,   4,   5,  -1,   6,   7,  -1,   8,   9,  10,  11,  11,  11,  11,  11,  11,  11,  11,  11,  11,  12,  13,  14,  -1,  -1,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  16,  -1,  17,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  18,  19,  -1, },
		{  -1,  20,  20,  20,  20,  20,  21,  20,  20,  22,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  23,  20,  24, },
		{  -9,  -9,   2,  -9,   2,   2,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9, },
		{  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  25,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9, },
		{  -1,   4,   4,  -1,   4,   4,  26,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,  -1,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4, },
		{  -9,   5,   5,  -9,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5,   5, },
		{  -1,  27,  27,  -1,  27,  27,  27,  27,  27,  -1,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27, },
		{  -1,  28,  28,  -1,  28,  28,  28,  28,  28,  28,  28,  -1,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28, },
		{  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  11,  11,  11,  11,  11,  11,  11,  11,  11,  11,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, },
		{ -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, -13, },
		{  -1,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  29,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  30,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10, },
		{  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  11,  11,  11,  11,  11,  11,  11,  11,  11,  11,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7,  -7, },
		{ -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14,  31, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, },
		{ -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, -12, },
		{  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  32,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, },
		{  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  -3,  -3,  -3,  -3,  -3,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  -3,  -3,  -3,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  15,  -3,  -3,  -3, },
		{ -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, },
		{ -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, -16, },
		{ -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, -18, },
		{ -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, -11, },
		{  -9,  20,  20,  20,  20,  20,  -9,  20,  20,  -9,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  20,  -9,  20,  -9, },
		{  -1,  21,  21,  -1,  21,  21,  33,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  34,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21, },
		{  -1,  22,  22,  -1,  22,  22,  22,  22,  22,  35,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  36,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22, },
		{ -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, -19, },
		{ -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, -20, },
		{  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  37,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8,  -8, },
		{  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5,  -5, },
		{  -1,  27,  27,  -1,  27,  27,  27,  27,  27,  38,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27,  27, },
		{  -1,  28,  28,  -1,  28,  28,  28,  28,  28,  28,  28,  39,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28,  28, },
		{  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4,  -4, },
		{  -1,  10,  10,  -1,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10,  10, },
		{  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  40,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, },
		{ -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, -17, },
		{  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9, },
		{  -1,  -1,  -1,  -1,  -1,  -1,  21,  -1,  -1,  21,  -1,  -1,  -1,  -1,  -1,  41,  41,  41,  41,  41,  41,  41,  41,  -1,  -1,  -1,  -1,  -1,  -1,  21,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  21,  -1,  -1,  21,  21,  -1,  -1,  -1,  21,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  21,  -1,  -1,  -1,  21,  -1,  21,  -1,  21,  -1,  42,  -1,  -1,  -1,  -1,  -1, },
		{  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9,  -9, },
		{  -1,  -1,  -1,  -1,  -1,  -1,  22,  -1,  -1,  22,  -1,  -1,  -1,  -1,  -1,  43,  43,  43,  43,  43,  43,  43,  43,  -1,  -1,  -1,  -1,  -1,  -1,  22,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  22,  -1,  -1,  22,  22,  -1,  -1,  -1,  22,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  22,  -1,  -1,  -1,  22,  -1,  22,  -1,  22,  -1,  44,  -1,  -1,  -1,  -1,  -1, },
		{  -2,  37,  37,  -2,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37,  37, },
		{  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3,  -3, },
		{  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6,  -6, },
		{ -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, },
		{  -1,  21,  21,  -1,  21,  21,  33,  21,  21,  21,  21,  21,  21,  21,  21,  45,  45,  45,  45,  45,  45,  45,  45,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  34,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21, },
		{  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  46,  46,  46,  46,  46,  46,  46,  46,  46,  46,  -1,  -1,  -1,  -1,  -1,  46,  46,  46,  46,  46,  46,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  46,  46,  46,  46,  46,  46,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, },
		{  -1,  22,  22,  -1,  22,  22,  22,  22,  22,  35,  22,  22,  22,  22,  22,  47,  47,  47,  47,  47,  47,  47,  47,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  36,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22, },
		{  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  48,  48,  48,  48,  48,  48,  48,  48,  48,  48,  -1,  -1,  -1,  -1,  -1,  48,  48,  48,  48,  48,  48,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  48,  48,  48,  48,  48,  48,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1, },
		{  -1,  21,  21,  -1,  21,  21,  33,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  34,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21, },
		{  -1,  21,  21,  -1,  21,  21,  33,  21,  21,  21,  21,  21,  21,  21,  21,  46,  46,  46,  46,  46,  46,  46,  46,  46,  46,  21,  21,  21,  21,  21,  46,  46,  46,  46,  46,  46,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  34,  21,  21,  46,  46,  46,  46,  46,  46,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21,  21, },
		{  -1,  22,  22,  -1,  22,  22,  22,  22,  22,  35,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  36,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22, },
		{  -1,  22,  22,  -1,  22,  22,  22,  22,  22,  35,  22,  22,  22,  22,  22,  48,  48,  48,  48,  48,  48,  48,  48,  48,  48,  22,  22,  22,  22,  22,  48,  48,  48,  48,  48,  48,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  36,  22,  22,  48,  48,  48,  48,  48,  48,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22,  22, },
	};

	private static final int[] lapg_action = new int[] {
		  -1,  -1,  -1,   2,  -1,  39,  -1,  -1,   1,   6,  -3,   3,   4,  16,  -1,  -1,
		  -9,   5, -17,  18,   8,  -1,  17,   7,  -1, -25,  19,  -1, -31,  24,  -1,  -1,
		 -41, -47,  25,  21,  -1,  -1,  13,  -1,  23, -59,  11, -71, -81,  -1,  34,  38,
		  30, -87,  20, -93,  15,-103,  -1,  32,  33,  22,  -1,  28,  26,  29,  37,  31,
		  -1,  -2,
	};

	private static final short[] lapg_lalr = new short[] {
		   4,  -1,  12,   9,  -1,  -2,   1,  -1,   6,  -1,   0,   0,  -1,  -2,   4,  -1,
		   8,   9,  12,   9,  -1,  -2,   4,  -1,   8,   9,  -1,  -2,   2,  -1,   1,  10,
		   6,  10,  13,  10,  -1,  -2,  16,  -1,   1,  14,  -1,  -2,   5,  -1,   1,  12,
		   6,  12,  13,  12,  16,  12,  -1,  -2,  16,  -1,   1,  14,   9,  14,  10,  14,
		  15,  14,  -1,  -2,  16,  -1,   1,  14,   6,  14,  13,  14,  -1,  -2,  17,  -1,
		  18,  36,  -1,  -2,  16,  -1,   1,  14,  -1,  -2,   1,  -1,  15,  -1,   9,  27,
		  10,  27,  -1,  -2,  17,  -1,  18,  35,  -1,  -2,
	};

	private static final short[] lapg_sym_goto = new short[] {
		   0,   1,  11,  12,  13,  16,  20,  22,  22,  24,  25,  27,  29,  31,  33,  34,
		  35,  39,  43,  45,  46,  47,  48,  49,  51,  53,  54,  57,  60,  61,  65,  69,
		  71,  72,  73,  75,  77,  78,  79,  82,  84,  88,  89,
	};

	private static final short[] lapg_sym_from = new short[] {
		  64,   1,   2,   7,  15,  16,  24,  30,  37,  51,  58,  28,   4,  10,  18,  25,
		   4,   6,  14,  33,   7,  16,  27,  31,  39,  30,  39,   0,   2,  21,  27,   2,
		   7,  14,  51,  32,  41,  43,  49,  36,  44,  45,  53,  45,  54,   0,   0,   2,
		   7,   0,   2,   2,   7,   6,   2,   7,  16,  10,  18,  25,  33,  32,  41,  43,
		  49,  32,  41,  43,  49,   7,  16,  32,  24,  32,  49,  32,  49,  51,  51,  37,
		  51,  58,  36,  44,  36,  44,  45,  53,  44,
	};

	private static final short[] lapg_sym_to = new short[] {
		  65,   4,   5,   5,  24,   5,  29,  34,  47,  47,  47,  33,  11,  20,  20,  20,
		  12,  13,  22,  42,  15,  15,  32,  32,  49,  35,  50,   1,   1,  28,  28,   6,
		   6,  23,  58,  36,  36,  36,  36,  44,  44,  44,  44,  55,  62,  64,   2,   7,
		  16,   3,   8,   9,  17,  14,  10,  18,  25,  21,  27,  31,  43,  37,  51,  52,
		  37,  38,  38,  38,  38,  19,  26,  39,  30,  40,  57,  41,  41,  59,  60,  48,
		  61,  63,  45,  53,  46,  46,  56,  56,  54,
	};

	private static final short[] lapg_rlen = new short[] {
		   3,   2,   1,   3,   3,   2,   1,   3,   1,   0,   3,   1,   0,   1,   0,   6,
		   1,   2,   1,   2,   5,   4,   3,   1,   1,   2,   1,   0,   3,   3,   2,   2,
		   3,   2,   1,   1,   0,   3,   1,   1,
	};

	private static final short[] lapg_rlex = new short[] {
		  19,  20,  20,  23,  23,  21,  21,  24,  27,  27,  24,  28,  28,  29,  29,  24,
		  25,  25,  22,  22,  31,  31,  32,  32,  33,  33,  36,  36,  34,  35,  35,  37,
		  30,  39,  39,  41,  41,  40,  38,  26,
	};

	private static final String[] lapg_syms = new String[] {
		"eoi",
		"identifier",
		"regexp",
		"scon",
		"type",
		"icon",
		"'%'",
		"_skip",
		"'::='",
		"'|'",
		"';'",
		"'.'",
		"':'",
		"'['",
		"']'",
		"'<<'",
		"'{'",
		"'i{'",
		"'}'",
		"input",
		"directives",
		"lexical_definitions",
		"grammar_definitions",
		"directive",
		"lexical_definition",
		"iconlist",
		"def_symbol",
		"typeopt",
		"iconopt",
		"commandopt",
		"command",
		"grammar_definition",
		"rule_right_part",
		"identifiers",
		"rule_def",
		"rule_symbols",
		"rule_priorityopt",
		"rule_priority",
		"symbol",
		"command_tokens",
		"command_token",
		"command_tokensopt",
	};

	public enum Tokens {
		eoi,
		identifier,
		regexp,
		scon,
		type,
		icon,
		PERC,
		_skip,
		COLONCOLONEQ,
		OR,
		SEMICOLON,
		DOT,
		COLON,
		LBRACKET,
		RBRACKET,
		LESSLESS,
		LBRACE,
		iLBRACE,
		RBRACE,
		input,
		directives,
		lexical_definitions,
		grammar_definitions,
		directive,
		lexical_definition,
		iconlist,
		def_symbol,
		typeopt,
		iconopt,
		commandopt,
		command,
		grammar_definition,
		rule_right_part,
		identifiers,
		rule_def,
		rule_symbols,
		rule_priorityopt,
		rule_priority,
		symbol,
		command_tokens,
		command_token,
		command_tokensopt,
	}

	private static int lapg_next( int state, int symbol ) {
		int p;
		if( lapg_action[state] < -2 ) {
			for( p = - lapg_action[state] - 3; lapg_lalr[p] >= 0; p += 2 )
				if( lapg_lalr[p] == symbol ) break;
			return lapg_lalr[p+1];
		}
		return lapg_action[state];
	}

	private static int lapg_state_sym( int state, int symbol ) {
		int min = lapg_sym_goto[symbol], max = lapg_sym_goto[symbol+1]-1;
		int i, e;

		while( min <= max ) {
			e = (min + max) >> 1;
			i = lapg_sym_from[e];
			if( i == state )
				return lapg_sym_to[e];
			else if( i < state )
				min = e + 1;
			else
				max = e - 1;
		}
		return -1;
	}

	public boolean parse() {

		byte[]        token = new byte[4096];
		int           lapg_head = 0, group = 0, lapg_i, lapg_size, chr;
		lapg_symbol[] lapg_m = new lapg_symbol[1024];
		lapg_symbol   lapg_n;
		int           lapg_current_line = 1, lapg_current_offset = 0;

		lapg_m[0] = new lapg_symbol();
		lapg_m[0].state = 0;
		chr = l < buff.length ? buff[l++] : 0;

		do {
			lapg_n = new lapg_symbol();
			lapg_n.pos = new lapg_place( lapg_current_line, lapg_current_offset );
			for( lapg_size = 0, lapg_i = group; lapg_i >= 0; ) {
				if( lapg_size < 4096-1 ) token[lapg_size++] = (byte)chr;
				lapg_i = lapg_lexem[lapg_i][lapg_char2no[(chr+256)%256]];
				if( lapg_i >= -1 && chr != 0 ) { 
					lapg_current_offset++;
					if( chr == '\n' ) lapg_current_line++;
					chr = l < buff.length ? buff[l++] : 0;
				}
			}
			lapg_n.endpos = new lapg_place( lapg_current_line, lapg_current_offset );

			if( lapg_i == -1 ) {
				if( chr == 0 ) {
					error( "Unexpected end of file reached");
					break;
				}
				error( MessageFormat.format( "invalid lexem at line {0}: `{1}`, skipped", lapg_n.pos.line, new String(token,0,lapg_size) ) );
				lapg_n.lexem = -1;
				continue;
			}

			lapg_size--;
			lapg_n.lexem = -lapg_i-2;
			lapg_n.sym = null;

			switch( lapg_n.lexem ) {
				case 1:
					 lapg_n.sym = new String(token,0,lapg_size); break; 
				case 2:
					 lapg_n.sym = new String(token,1,lapg_size-2); break; 
				case 3:
					 lapg_n.sym = new String(token,1,lapg_size-2); break; 
				case 4:
					 lapg_n.sym = new String(token,1,lapg_size-2); break; 
				case 5:
					 lapg_n.sym = Integer.parseInt(new String(token,0,lapg_size)); break; 
				case 7:
					 continue; 
				case 16:
					 deep = 1; group = 1; break; 
				case 17:
					 deep++; break; 
				case 18:
					 if( --deep == 0 ) group = 0; break; 
			}


			do {
				lapg_i = lapg_next( lapg_m[lapg_head].state, lapg_n.lexem );

				if( lapg_i >= 0 ) {
					lapg_symbol lapg_gg = new lapg_symbol();
					lapg_gg.sym = (lapg_rlen[lapg_i]!=0)?lapg_m[lapg_head+1-lapg_rlen[lapg_i]].sym:null;
					lapg_gg.lexem = lapg_rlex[lapg_i];
					lapg_gg.state = 0;
					if( DEBUG_SYNTAX )
						System.out.println( "reduce to " + lapg_syms[lapg_rlex[lapg_i]] );
					lapg_gg.pos = (lapg_rlen[lapg_i]!=0)?lapg_m[lapg_head+1-lapg_rlen[lapg_i]].pos:lapg_n.pos;
					lapg_gg.endpos = (lapg_rlen[lapg_i]!=0)?lapg_m[lapg_head].endpos:lapg_n.pos;
					switch( lapg_i ) {
						case 31:
							 lapg_gg.sym = ((CSymbol)lapg_m[lapg_head-0].sym); 
							break;
						case 32:
							 lapg_gg.sym = new CAction(lapg_m[lapg_head-2].pos.line, rawData(lapg_m[lapg_head-2].pos.offset,lapg_m[lapg_head-2].endpos.offset)); 
							break;
						case 38:
							 lapg_gg.sym = getSymbol(((String)lapg_m[lapg_head-0].sym),-1); 
							break;
						case 39:
							 lapg_gg.sym = getSymbol(((String)lapg_m[lapg_head-0].sym),lapg_m[lapg_head-0].pos.line); 
							break;
					}
					for( int e = lapg_rlen[lapg_i]; e > 0; e-- ) 
						lapg_m[lapg_head--] = null;
					lapg_m[++lapg_head] = lapg_gg;
					lapg_m[lapg_head].state = lapg_state_sym( lapg_m[lapg_head-1].state, lapg_gg.lexem );
				} else if( lapg_i == -1 ) {
					lapg_m[++lapg_head] = lapg_n;
					lapg_m[lapg_head].state = lapg_state_sym( lapg_m[lapg_head-1].state, lapg_n.lexem );
					if( DEBUG_SYNTAX )
						System.out.println( MessageFormat.format( "shift: {0} ({1})", lapg_syms[lapg_n.lexem], new String(token,0,lapg_size) ) );
				}

			} while( lapg_i >= 0 && lapg_m[lapg_head].state != -1 );

			if( (lapg_i == -2 || lapg_m[lapg_head].state == -1) && lapg_n.lexem != 0 ) {
				break;
			}

		} while( lapg_n.lexem != 0 );

		if( lapg_m[lapg_head].state != 66-1 ) {
			error( MessageFormat.format( "syntax error before line {0}", lapg_n.pos.line ) );
			return false;
		};
		return true;
	}
}
