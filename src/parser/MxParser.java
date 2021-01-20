// Generated from /home/aik2/Desktop/Mx-Compiler/src/parser/Mx.g4 by ANTLR 4.9
package parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MxParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Int=1, Bool=2, String=3, Null=4, Void=5, True=6, False=7, If=8, Else=9, 
		For=10, While=11, Break=12, Continue=13, Return=14, New=15, Class=16, 
		This=17, Plus=18, Minus=19, Star=20, Div=21, Mod=22, SelfPlus=23, SelfMinus=24, 
		Greater=25, Less=26, GreaterEqual=27, LessEqual=28, NotEqual=29, Equal=30, 
		AndAnd=31, OrOr=32, Not=33, RightShift=34, LeftShift=35, And=36, Or=37, 
		Xor=38, Tilde=39, Assign=40, Dot=41, LeftParen=42, RightParen=43, LeftBracket=44, 
		RightBracket=45, LeftBrace=46, RightBrace=47, Question=48, Colon=49, Semi=50, 
		Comma=51, StringLiteral=52, IntLiteral=53, Identifier=54, Whitespace=55, 
		Newline=56, LineComment=57, BlockComment=58;
	public static final int
		RULE_program = 0, RULE_classDef = 1, RULE_funcDef = 2, RULE_varDef = 3, 
		RULE_varDefUnit = 4, RULE_paramList = 5, RULE_param = 6, RULE_type = 7, 
		RULE_basicType = 8, RULE_singleType = 9, RULE_arrayType = 10, RULE_statement = 11, 
		RULE_suite = 12, RULE_varDefStmt = 13, RULE_ifStmt = 14, RULE_forStmt = 15, 
		RULE_whileStmt = 16, RULE_returnStmt = 17, RULE_breakStmt = 18, RULE_continueStmt = 19, 
		RULE_simpleStmt = 20, RULE_expressionList = 21, RULE_expression = 22, 
		RULE_unaryOp = 23, RULE_primary = 24, RULE_literal = 25, RULE_creator = 26;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "classDef", "funcDef", "varDef", "varDefUnit", "paramList", 
			"param", "type", "basicType", "singleType", "arrayType", "statement", 
			"suite", "varDefStmt", "ifStmt", "forStmt", "whileStmt", "returnStmt", 
			"breakStmt", "continueStmt", "simpleStmt", "expressionList", "expression", 
			"unaryOp", "primary", "literal", "creator"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'int'", "'bool'", "'string'", "'null'", "'void'", "'true'", "'false'", 
			"'if'", "'else'", "'for'", "'while'", "'break'", "'continue'", "'return'", 
			"'new'", "'class'", "'this'", "'+'", "'-'", "'*'", "'/'", "'%'", "'++'", 
			"'--'", "'>'", "'<'", "'>='", "'<='", "'!='", "'=='", "'&&'", "'||'", 
			"'!'", "'>>'", "'<<'", "'&'", "'|'", "'^'", "'~'", "'='", "'.'", "'('", 
			"')'", "'['", "']'", "'{'", "'}'", "'?'", "':'", "';'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Int", "Bool", "String", "Null", "Void", "True", "False", "If", 
			"Else", "For", "While", "Break", "Continue", "Return", "New", "Class", 
			"This", "Plus", "Minus", "Star", "Div", "Mod", "SelfPlus", "SelfMinus", 
			"Greater", "Less", "GreaterEqual", "LessEqual", "NotEqual", "Equal", 
			"AndAnd", "OrOr", "Not", "RightShift", "LeftShift", "And", "Or", "Xor", 
			"Tilde", "Assign", "Dot", "LeftParen", "RightParen", "LeftBracket", "RightBracket", 
			"LeftBrace", "RightBrace", "Question", "Colon", "Semi", "Comma", "StringLiteral", 
			"IntLiteral", "Identifier", "Whitespace", "Newline", "LineComment", "BlockComment"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Mx.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MxParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MxParser.EOF, 0); }
		public List<VarDefContext> varDef() {
			return getRuleContexts(VarDefContext.class);
		}
		public VarDefContext varDef(int i) {
			return getRuleContext(VarDefContext.class,i);
		}
		public List<FuncDefContext> funcDef() {
			return getRuleContexts(FuncDefContext.class);
		}
		public FuncDefContext funcDef(int i) {
			return getRuleContext(FuncDefContext.class,i);
		}
		public List<ClassDefContext> classDef() {
			return getRuleContexts(ClassDefContext.class);
		}
		public ClassDefContext classDef(int i) {
			return getRuleContext(ClassDefContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Int) | (1L << Bool) | (1L << String) | (1L << Void) | (1L << Class) | (1L << Identifier))) != 0)) {
				{
				setState(57);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(54);
					varDef();
					}
					break;
				case 2:
					{
					setState(55);
					funcDef();
					}
					break;
				case 3:
					{
					setState(56);
					classDef();
					}
					break;
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClassDefContext extends ParserRuleContext {
		public TerminalNode Class() { return getToken(MxParser.Class, 0); }
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public TerminalNode LeftBrace() { return getToken(MxParser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(MxParser.RightBrace, 0); }
		public TerminalNode Semi() { return getToken(MxParser.Semi, 0); }
		public List<VarDefContext> varDef() {
			return getRuleContexts(VarDefContext.class);
		}
		public VarDefContext varDef(int i) {
			return getRuleContext(VarDefContext.class,i);
		}
		public List<FuncDefContext> funcDef() {
			return getRuleContexts(FuncDefContext.class);
		}
		public FuncDefContext funcDef(int i) {
			return getRuleContext(FuncDefContext.class,i);
		}
		public ClassDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterClassDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitClassDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitClassDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassDefContext classDef() throws RecognitionException {
		ClassDefContext _localctx = new ClassDefContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_classDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(Class);
			setState(65);
			match(Identifier);
			setState(66);
			match(LeftBrace);
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Int) | (1L << Bool) | (1L << String) | (1L << Void) | (1L << Identifier))) != 0)) {
				{
				setState(69);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
				case 1:
					{
					setState(67);
					varDef();
					}
					break;
				case 2:
					{
					setState(68);
					funcDef();
					}
					break;
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(74);
			match(RightBrace);
			setState(75);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncDefContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public FuncDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterFuncDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitFuncDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitFuncDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncDefContext funcDef() throws RecognitionException {
		FuncDefContext _localctx = new FuncDefContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_funcDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(77);
				type();
				}
				break;
			}
			setState(80);
			match(Identifier);
			setState(81);
			match(LeftParen);
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Int) | (1L << Bool) | (1L << String) | (1L << Void) | (1L << Identifier))) != 0)) {
				{
				setState(82);
				paramList();
				}
			}

			setState(85);
			match(RightParen);
			setState(86);
			suite();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDefContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<VarDefUnitContext> varDefUnit() {
			return getRuleContexts(VarDefUnitContext.class);
		}
		public VarDefUnitContext varDefUnit(int i) {
			return getRuleContext(VarDefUnitContext.class,i);
		}
		public TerminalNode Semi() { return getToken(MxParser.Semi, 0); }
		public List<TerminalNode> Comma() { return getTokens(MxParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(MxParser.Comma, i);
		}
		public VarDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterVarDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitVarDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitVarDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDefContext varDef() throws RecognitionException {
		VarDefContext _localctx = new VarDefContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_varDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			type();
			setState(89);
			varDefUnit();
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(90);
				match(Comma);
				setState(91);
				varDefUnit();
				}
				}
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(97);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDefUnitContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public TerminalNode Assign() { return getToken(MxParser.Assign, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public VarDefUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDefUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterVarDefUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitVarDefUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitVarDefUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDefUnitContext varDefUnit() throws RecognitionException {
		VarDefUnitContext _localctx = new VarDefUnitContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_varDefUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(Identifier);
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Assign) {
				{
				setState(100);
				match(Assign);
				setState(101);
				expression(0);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamListContext extends ParserRuleContext {
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(MxParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(MxParser.Comma, i);
		}
		public ParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterParamList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitParamList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitParamList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamListContext paramList() throws RecognitionException {
		ParamListContext _localctx = new ParamListContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_paramList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			param();
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(105);
				match(Comma);
				setState(106);
				param();
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			type();
			setState(113);
			match(Identifier);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public TerminalNode Void() { return getToken(MxParser.Void, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_type);
		try {
			setState(118);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(115);
				singleType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(116);
				arrayType();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(117);
				match(Void);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BasicTypeContext extends ParserRuleContext {
		public TerminalNode Bool() { return getToken(MxParser.Bool, 0); }
		public TerminalNode Int() { return getToken(MxParser.Int, 0); }
		public TerminalNode String() { return getToken(MxParser.String, 0); }
		public BasicTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basicType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterBasicType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitBasicType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitBasicType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BasicTypeContext basicType() throws RecognitionException {
		BasicTypeContext _localctx = new BasicTypeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_basicType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Int) | (1L << Bool) | (1L << String))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleTypeContext extends ParserRuleContext {
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public BasicTypeContext basicType() {
			return getRuleContext(BasicTypeContext.class,0);
		}
		public SingleTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterSingleType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitSingleType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitSingleType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleTypeContext singleType() throws RecognitionException {
		SingleTypeContext _localctx = new SingleTypeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_singleType);
		try {
			setState(124);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Identifier:
				enterOuterAlt(_localctx, 1);
				{
				setState(122);
				match(Identifier);
				}
				break;
			case Int:
			case Bool:
			case String:
				enterOuterAlt(_localctx, 2);
				{
				setState(123);
				basicType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayTypeContext extends ParserRuleContext {
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public List<TerminalNode> LeftBracket() { return getTokens(MxParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(MxParser.LeftBracket, i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(MxParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(MxParser.RightBracket, i);
		}
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitArrayType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitArrayType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_arrayType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			singleType();
			setState(129); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(127);
				match(LeftBracket);
				setState(128);
				match(RightBracket);
				}
				}
				setState(131); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LeftBracket );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public SuiteContext suite() {
			return getRuleContext(SuiteContext.class,0);
		}
		public VarDefStmtContext varDefStmt() {
			return getRuleContext(VarDefStmtContext.class,0);
		}
		public IfStmtContext ifStmt() {
			return getRuleContext(IfStmtContext.class,0);
		}
		public ForStmtContext forStmt() {
			return getRuleContext(ForStmtContext.class,0);
		}
		public WhileStmtContext whileStmt() {
			return getRuleContext(WhileStmtContext.class,0);
		}
		public ReturnStmtContext returnStmt() {
			return getRuleContext(ReturnStmtContext.class,0);
		}
		public BreakStmtContext breakStmt() {
			return getRuleContext(BreakStmtContext.class,0);
		}
		public ContinueStmtContext continueStmt() {
			return getRuleContext(ContinueStmtContext.class,0);
		}
		public SimpleStmtContext simpleStmt() {
			return getRuleContext(SimpleStmtContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_statement);
		try {
			setState(142);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(133);
				suite();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(134);
				varDefStmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(135);
				ifStmt();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(136);
				forStmt();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(137);
				whileStmt();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(138);
				returnStmt();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(139);
				breakStmt();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(140);
				continueStmt();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(141);
				simpleStmt();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SuiteContext extends ParserRuleContext {
		public TerminalNode LeftBrace() { return getToken(MxParser.LeftBrace, 0); }
		public TerminalNode RightBrace() { return getToken(MxParser.RightBrace, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public SuiteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_suite; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterSuite(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitSuite(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitSuite(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SuiteContext suite() throws RecognitionException {
		SuiteContext _localctx = new SuiteContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_suite);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			match(LeftBrace);
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Int) | (1L << Bool) | (1L << String) | (1L << Null) | (1L << Void) | (1L << True) | (1L << False) | (1L << If) | (1L << For) | (1L << While) | (1L << Break) | (1L << Continue) | (1L << Return) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << LeftBrace) | (1L << Semi) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
				{
				{
				setState(145);
				statement();
				}
				}
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(151);
			match(RightBrace);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDefStmtContext extends ParserRuleContext {
		public VarDefContext varDef() {
			return getRuleContext(VarDefContext.class,0);
		}
		public VarDefStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDefStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterVarDefStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitVarDefStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitVarDefStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDefStmtContext varDefStmt() throws RecognitionException {
		VarDefStmtContext _localctx = new VarDefStmtContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_varDefStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			varDef();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfStmtContext extends ParserRuleContext {
		public StatementContext trueStmt;
		public StatementContext falseStmt;
		public TerminalNode If() { return getToken(MxParser.If, 0); }
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TerminalNode Else() { return getToken(MxParser.Else, 0); }
		public IfStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterIfStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitIfStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitIfStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfStmtContext ifStmt() throws RecognitionException {
		IfStmtContext _localctx = new IfStmtContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_ifStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(If);
			setState(156);
			match(LeftParen);
			setState(157);
			expression(0);
			setState(158);
			match(RightParen);
			setState(159);
			((IfStmtContext)_localctx).trueStmt = statement();
			setState(162);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(160);
				match(Else);
				setState(161);
				((IfStmtContext)_localctx).falseStmt = statement();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ForStmtContext extends ParserRuleContext {
		public ExpressionContext init;
		public ExpressionContext cond;
		public ExpressionContext incr;
		public TerminalNode For() { return getToken(MxParser.For, 0); }
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public List<TerminalNode> Semi() { return getTokens(MxParser.Semi); }
		public TerminalNode Semi(int i) {
			return getToken(MxParser.Semi, i);
		}
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ForStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterForStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitForStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitForStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForStmtContext forStmt() throws RecognitionException {
		ForStmtContext _localctx = new ForStmtContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_forStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(For);
			setState(165);
			match(LeftParen);
			setState(167);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Null) | (1L << True) | (1L << False) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
				{
				setState(166);
				((ForStmtContext)_localctx).init = expression(0);
				}
			}

			setState(169);
			match(Semi);
			setState(171);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Null) | (1L << True) | (1L << False) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
				{
				setState(170);
				((ForStmtContext)_localctx).cond = expression(0);
				}
			}

			setState(173);
			match(Semi);
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Null) | (1L << True) | (1L << False) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
				{
				setState(174);
				((ForStmtContext)_localctx).incr = expression(0);
				}
			}

			setState(177);
			match(RightParen);
			setState(178);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhileStmtContext extends ParserRuleContext {
		public TerminalNode While() { return getToken(MxParser.While, 0); }
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public WhileStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterWhileStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitWhileStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitWhileStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhileStmtContext whileStmt() throws RecognitionException {
		WhileStmtContext _localctx = new WhileStmtContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_whileStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			match(While);
			setState(181);
			match(LeftParen);
			setState(182);
			expression(0);
			setState(183);
			match(RightParen);
			setState(184);
			statement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReturnStmtContext extends ParserRuleContext {
		public TerminalNode Return() { return getToken(MxParser.Return, 0); }
		public TerminalNode Semi() { return getToken(MxParser.Semi, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterReturnStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitReturnStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitReturnStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnStmtContext returnStmt() throws RecognitionException {
		ReturnStmtContext _localctx = new ReturnStmtContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_returnStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			match(Return);
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Null) | (1L << True) | (1L << False) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
				{
				setState(187);
				expression(0);
				}
			}

			setState(190);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BreakStmtContext extends ParserRuleContext {
		public TerminalNode Break() { return getToken(MxParser.Break, 0); }
		public TerminalNode Semi() { return getToken(MxParser.Semi, 0); }
		public BreakStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterBreakStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitBreakStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitBreakStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BreakStmtContext breakStmt() throws RecognitionException {
		BreakStmtContext _localctx = new BreakStmtContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_breakStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(Break);
			setState(193);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContinueStmtContext extends ParserRuleContext {
		public TerminalNode Continue() { return getToken(MxParser.Continue, 0); }
		public TerminalNode Semi() { return getToken(MxParser.Semi, 0); }
		public ContinueStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterContinueStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitContinueStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitContinueStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinueStmtContext continueStmt() throws RecognitionException {
		ContinueStmtContext _localctx = new ContinueStmtContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_continueStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(Continue);
			setState(196);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleStmtContext extends ParserRuleContext {
		public TerminalNode Semi() { return getToken(MxParser.Semi, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SimpleStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterSimpleStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitSimpleStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitSimpleStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleStmtContext simpleStmt() throws RecognitionException {
		SimpleStmtContext _localctx = new SimpleStmtContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_simpleStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Null) | (1L << True) | (1L << False) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
				{
				setState(198);
				expression(0);
				}
			}

			setState(201);
			match(Semi);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> Comma() { return getTokens(MxParser.Comma); }
		public TerminalNode Comma(int i) {
			return getToken(MxParser.Comma, i);
		}
		public ExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitExpressionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitExpressionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionListContext expressionList() throws RecognitionException {
		ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_expressionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			expression(0);
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(204);
				match(Comma);
				setState(205);
				expression(0);
				}
				}
				setState(210);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NewExprContext extends ExpressionContext {
		public TerminalNode New() { return getToken(MxParser.New, 0); }
		public CreatorContext creator() {
			return getRuleContext(CreatorContext.class,0);
		}
		public NewExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterNewExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitNewExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitNewExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PrefixExprContext extends ExpressionContext {
		public UnaryOpContext prefix;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public UnaryOpContext unaryOp() {
			return getRuleContext(UnaryOpContext.class,0);
		}
		public PrefixExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterPrefixExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitPrefixExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitPrefixExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SubscriptExprContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode LeftBracket() { return getToken(MxParser.LeftBracket, 0); }
		public TerminalNode RightBracket() { return getToken(MxParser.RightBracket, 0); }
		public SubscriptExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterSubscriptExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitSubscriptExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitSubscriptExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MemberExprContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Dot() { return getToken(MxParser.Dot, 0); }
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public MemberExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterMemberExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitMemberExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitMemberExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MethodExprContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode Dot() { return getToken(MxParser.Dot, 0); }
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public MethodExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterMethodExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitMethodExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitMethodExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SuffixExprContext extends ExpressionContext {
		public Token suffix;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode SelfPlus() { return getToken(MxParser.SelfPlus, 0); }
		public TerminalNode SelfMinus() { return getToken(MxParser.SelfMinus, 0); }
		public SuffixExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterSuffixExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitSuffixExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitSuffixExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AtomExprContext extends ExpressionContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public AtomExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterAtomExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitAtomExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitAtomExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryExprContext extends ExpressionContext {
		public Token op;
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Star() { return getToken(MxParser.Star, 0); }
		public TerminalNode Div() { return getToken(MxParser.Div, 0); }
		public TerminalNode Mod() { return getToken(MxParser.Mod, 0); }
		public TerminalNode Plus() { return getToken(MxParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(MxParser.Minus, 0); }
		public TerminalNode LeftShift() { return getToken(MxParser.LeftShift, 0); }
		public TerminalNode RightShift() { return getToken(MxParser.RightShift, 0); }
		public TerminalNode Less() { return getToken(MxParser.Less, 0); }
		public TerminalNode Greater() { return getToken(MxParser.Greater, 0); }
		public TerminalNode LessEqual() { return getToken(MxParser.LessEqual, 0); }
		public TerminalNode GreaterEqual() { return getToken(MxParser.GreaterEqual, 0); }
		public TerminalNode Equal() { return getToken(MxParser.Equal, 0); }
		public TerminalNode NotEqual() { return getToken(MxParser.NotEqual, 0); }
		public TerminalNode And() { return getToken(MxParser.And, 0); }
		public TerminalNode Xor() { return getToken(MxParser.Xor, 0); }
		public TerminalNode Or() { return getToken(MxParser.Or, 0); }
		public TerminalNode AndAnd() { return getToken(MxParser.AndAnd, 0); }
		public TerminalNode OrOr() { return getToken(MxParser.OrOr, 0); }
		public BinaryExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterBinaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitBinaryExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitBinaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuncCallExprContext extends ExpressionContext {
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public FuncCallExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterFuncCallExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitFuncCallExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitFuncCallExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignExprContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Assign() { return getToken(MxParser.Assign, 0); }
		public AssignExprContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterAssignExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitAssignExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitAssignExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 44;
		enterRecursionRule(_localctx, 44, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				_localctx = new AtomExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(212);
				primary();
				}
				break;
			case 2:
				{
				_localctx = new NewExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(213);
				match(New);
				setState(214);
				creator();
				}
				break;
			case 3:
				{
				_localctx = new FuncCallExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(215);
				match(Identifier);
				setState(216);
				match(LeftParen);
				setState(218);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Null) | (1L << True) | (1L << False) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
					{
					setState(217);
					expressionList();
					}
				}

				setState(220);
				match(RightParen);
				}
				break;
			case 4:
				{
				_localctx = new PrefixExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(221);
				((PrefixExprContext)_localctx).prefix = unaryOp();
				setState(222);
				expression(12);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(279);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(277);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(226);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(227);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Star) | (1L << Div) | (1L << Mod))) != 0)) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(228);
						expression(12);
						}
						break;
					case 2:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(229);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(230);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==Plus || _la==Minus) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(231);
						expression(11);
						}
						break;
					case 3:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(232);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(233);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==RightShift || _la==LeftShift) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(234);
						expression(10);
						}
						break;
					case 4:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(235);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(236);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Greater) | (1L << Less) | (1L << GreaterEqual) | (1L << LessEqual))) != 0)) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(237);
						expression(9);
						}
						break;
					case 5:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(238);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(239);
						((BinaryExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==NotEqual || _la==Equal) ) {
							((BinaryExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(240);
						expression(8);
						}
						break;
					case 6:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(241);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(242);
						((BinaryExprContext)_localctx).op = match(And);
						setState(243);
						expression(7);
						}
						break;
					case 7:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(244);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(245);
						((BinaryExprContext)_localctx).op = match(Xor);
						setState(246);
						expression(6);
						}
						break;
					case 8:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(247);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(248);
						((BinaryExprContext)_localctx).op = match(Or);
						setState(249);
						expression(5);
						}
						break;
					case 9:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(250);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(251);
						((BinaryExprContext)_localctx).op = match(AndAnd);
						setState(252);
						expression(4);
						}
						break;
					case 10:
						{
						_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(253);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(254);
						((BinaryExprContext)_localctx).op = match(OrOr);
						setState(255);
						expression(3);
						}
						break;
					case 11:
						{
						_localctx = new AssignExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(256);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(257);
						match(Assign);
						setState(258);
						expression(1);
						}
						break;
					case 12:
						{
						_localctx = new MemberExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(259);
						if (!(precpred(_ctx, 18))) throw new FailedPredicateException(this, "precpred(_ctx, 18)");
						setState(260);
						match(Dot);
						setState(261);
						match(Identifier);
						}
						break;
					case 13:
						{
						_localctx = new MethodExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(262);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(263);
						match(Dot);
						setState(264);
						match(Identifier);
						setState(265);
						match(LeftParen);
						setState(267);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Null) | (1L << True) | (1L << False) | (1L << New) | (1L << This) | (1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde) | (1L << LeftParen) | (1L << StringLiteral) | (1L << IntLiteral) | (1L << Identifier))) != 0)) {
							{
							setState(266);
							expressionList();
							}
						}

						setState(269);
						match(RightParen);
						}
						break;
					case 14:
						{
						_localctx = new SubscriptExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(270);
						if (!(precpred(_ctx, 15))) throw new FailedPredicateException(this, "precpred(_ctx, 15)");
						setState(271);
						match(LeftBracket);
						setState(272);
						expression(0);
						setState(273);
						match(RightBracket);
						}
						break;
					case 15:
						{
						_localctx = new SuffixExprContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(275);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(276);
						((SuffixExprContext)_localctx).suffix = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==SelfPlus || _la==SelfMinus) ) {
							((SuffixExprContext)_localctx).suffix = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					}
					} 
				}
				setState(281);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class UnaryOpContext extends ParserRuleContext {
		public TerminalNode Plus() { return getToken(MxParser.Plus, 0); }
		public TerminalNode Minus() { return getToken(MxParser.Minus, 0); }
		public TerminalNode SelfPlus() { return getToken(MxParser.SelfPlus, 0); }
		public TerminalNode SelfMinus() { return getToken(MxParser.SelfMinus, 0); }
		public TerminalNode Tilde() { return getToken(MxParser.Tilde, 0); }
		public TerminalNode Not() { return getToken(MxParser.Not, 0); }
		public UnaryOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterUnaryOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitUnaryOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitUnaryOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryOpContext unaryOp() throws RecognitionException {
		UnaryOpContext _localctx = new UnaryOpContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_unaryOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(282);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Plus) | (1L << Minus) | (1L << SelfPlus) | (1L << SelfMinus) | (1L << Not) | (1L << Tilde))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public TerminalNode This() { return getToken(MxParser.This, 0); }
		public TerminalNode Identifier() { return getToken(MxParser.Identifier, 0); }
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_primary);
		try {
			setState(291);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Null:
			case True:
			case False:
			case StringLiteral:
			case IntLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(284);
				literal();
				}
				break;
			case LeftParen:
				enterOuterAlt(_localctx, 2);
				{
				setState(285);
				match(LeftParen);
				setState(286);
				expression(0);
				setState(287);
				match(RightParen);
				}
				break;
			case This:
				enterOuterAlt(_localctx, 3);
				{
				setState(289);
				match(This);
				}
				break;
			case Identifier:
				enterOuterAlt(_localctx, 4);
				{
				setState(290);
				match(Identifier);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public Token BoolLiteral;
		public TerminalNode IntLiteral() { return getToken(MxParser.IntLiteral, 0); }
		public TerminalNode StringLiteral() { return getToken(MxParser.StringLiteral, 0); }
		public TerminalNode True() { return getToken(MxParser.True, 0); }
		public TerminalNode False() { return getToken(MxParser.False, 0); }
		public TerminalNode Null() { return getToken(MxParser.Null, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_literal);
		int _la;
		try {
			setState(297);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IntLiteral:
				enterOuterAlt(_localctx, 1);
				{
				setState(293);
				match(IntLiteral);
				}
				break;
			case StringLiteral:
				enterOuterAlt(_localctx, 2);
				{
				setState(294);
				match(StringLiteral);
				}
				break;
			case True:
			case False:
				enterOuterAlt(_localctx, 3);
				{
				setState(295);
				((LiteralContext)_localctx).BoolLiteral = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==True || _la==False) ) {
					((LiteralContext)_localctx).BoolLiteral = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case Null:
				enterOuterAlt(_localctx, 4);
				{
				setState(296);
				match(Null);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreatorContext extends ParserRuleContext {
		public CreatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_creator; }
	 
		public CreatorContext() { }
		public void copyFrom(CreatorContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ClassCreatorContext extends CreatorContext {
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public TerminalNode LeftParen() { return getToken(MxParser.LeftParen, 0); }
		public TerminalNode RightParen() { return getToken(MxParser.RightParen, 0); }
		public ClassCreatorContext(CreatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterClassCreator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitClassCreator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitClassCreator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArrayCreatorContext extends CreatorContext {
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public List<TerminalNode> LeftBracket() { return getTokens(MxParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(MxParser.LeftBracket, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(MxParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(MxParser.RightBracket, i);
		}
		public ArrayCreatorContext(CreatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterArrayCreator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitArrayCreator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitArrayCreator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BasicCreatorContext extends CreatorContext {
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public BasicCreatorContext(CreatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterBasicCreator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitBasicCreator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitBasicCreator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ErrorCreatorContext extends CreatorContext {
		public SingleTypeContext singleType() {
			return getRuleContext(SingleTypeContext.class,0);
		}
		public List<TerminalNode> LeftBracket() { return getTokens(MxParser.LeftBracket); }
		public TerminalNode LeftBracket(int i) {
			return getToken(MxParser.LeftBracket, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> RightBracket() { return getTokens(MxParser.RightBracket); }
		public TerminalNode RightBracket(int i) {
			return getToken(MxParser.RightBracket, i);
		}
		public ErrorCreatorContext(CreatorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).enterErrorCreator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MxListener ) ((MxListener)listener).exitErrorCreator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MxVisitor ) return ((MxVisitor<? extends T>)visitor).visitErrorCreator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreatorContext creator() throws RecognitionException {
		CreatorContext _localctx = new CreatorContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_creator);
		try {
			int _alt;
			setState(343);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				_localctx = new ErrorCreatorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(299);
				singleType();
				setState(304); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(300);
						match(LeftBracket);
						setState(301);
						expression(0);
						setState(302);
						match(RightBracket);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(306); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(310); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(308);
						match(LeftBracket);
						setState(309);
						match(RightBracket);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(312); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,29,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(318); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(314);
						match(LeftBracket);
						setState(315);
						expression(0);
						setState(316);
						match(RightBracket);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(320); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				_localctx = new ArrayCreatorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(322);
				singleType();
				setState(327); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(323);
						match(LeftBracket);
						setState(324);
						expression(0);
						setState(325);
						match(RightBracket);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(329); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,31,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(335);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(331);
						match(LeftBracket);
						setState(332);
						match(RightBracket);
						}
						} 
					}
					setState(337);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
				}
				}
				break;
			case 3:
				_localctx = new ClassCreatorContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(338);
				singleType();
				{
				setState(339);
				match(LeftParen);
				setState(340);
				match(RightParen);
				}
				}
				break;
			case 4:
				_localctx = new BasicCreatorContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(342);
				singleType();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 22:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 11);
		case 1:
			return precpred(_ctx, 10);
		case 2:
			return precpred(_ctx, 9);
		case 3:
			return precpred(_ctx, 8);
		case 4:
			return precpred(_ctx, 7);
		case 5:
			return precpred(_ctx, 6);
		case 6:
			return precpred(_ctx, 5);
		case 7:
			return precpred(_ctx, 4);
		case 8:
			return precpred(_ctx, 3);
		case 9:
			return precpred(_ctx, 2);
		case 10:
			return precpred(_ctx, 1);
		case 11:
			return precpred(_ctx, 18);
		case 12:
			return precpred(_ctx, 17);
		case 13:
			return precpred(_ctx, 15);
		case 14:
			return precpred(_ctx, 13);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3<\u015c\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\7\2<\n\2\f\2\16\2?\13\2\3\2"+
		"\3\2\3\3\3\3\3\3\3\3\3\3\7\3H\n\3\f\3\16\3K\13\3\3\3\3\3\3\3\3\4\5\4Q"+
		"\n\4\3\4\3\4\3\4\5\4V\n\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5_\n\5\f\5\16"+
		"\5b\13\5\3\5\3\5\3\6\3\6\3\6\5\6i\n\6\3\7\3\7\3\7\7\7n\n\7\f\7\16\7q\13"+
		"\7\3\b\3\b\3\b\3\t\3\t\3\t\5\ty\n\t\3\n\3\n\3\13\3\13\5\13\177\n\13\3"+
		"\f\3\f\3\f\6\f\u0084\n\f\r\f\16\f\u0085\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\5\r\u0091\n\r\3\16\3\16\7\16\u0095\n\16\f\16\16\16\u0098\13\16"+
		"\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00a5\n\20"+
		"\3\21\3\21\3\21\5\21\u00aa\n\21\3\21\3\21\5\21\u00ae\n\21\3\21\3\21\5"+
		"\21\u00b2\n\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23"+
		"\5\23\u00bf\n\23\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3\25\3\26\5\26\u00ca"+
		"\n\26\3\26\3\26\3\27\3\27\3\27\7\27\u00d1\n\27\f\27\16\27\u00d4\13\27"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00dd\n\30\3\30\3\30\3\30\3\30"+
		"\5\30\u00e3\n\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30"+
		"\3\30\3\30\5\30\u010e\n\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\7\30"+
		"\u0118\n\30\f\30\16\30\u011b\13\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\5\32\u0126\n\32\3\33\3\33\3\33\3\33\5\33\u012c\n\33\3\34\3"+
		"\34\3\34\3\34\3\34\6\34\u0133\n\34\r\34\16\34\u0134\3\34\3\34\6\34\u0139"+
		"\n\34\r\34\16\34\u013a\3\34\3\34\3\34\3\34\6\34\u0141\n\34\r\34\16\34"+
		"\u0142\3\34\3\34\3\34\3\34\3\34\6\34\u014a\n\34\r\34\16\34\u014b\3\34"+
		"\3\34\7\34\u0150\n\34\f\34\16\34\u0153\13\34\3\34\3\34\3\34\3\34\3\34"+
		"\5\34\u015a\n\34\3\34\2\3.\35\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 "+
		"\"$&(*,.\60\62\64\66\2\13\3\2\3\5\3\2\26\30\3\2\24\25\3\2$%\3\2\33\36"+
		"\3\2\37 \3\2\31\32\6\2\24\25\31\32##))\3\2\b\t\2\u0180\2=\3\2\2\2\4B\3"+
		"\2\2\2\6P\3\2\2\2\bZ\3\2\2\2\ne\3\2\2\2\fj\3\2\2\2\16r\3\2\2\2\20x\3\2"+
		"\2\2\22z\3\2\2\2\24~\3\2\2\2\26\u0080\3\2\2\2\30\u0090\3\2\2\2\32\u0092"+
		"\3\2\2\2\34\u009b\3\2\2\2\36\u009d\3\2\2\2 \u00a6\3\2\2\2\"\u00b6\3\2"+
		"\2\2$\u00bc\3\2\2\2&\u00c2\3\2\2\2(\u00c5\3\2\2\2*\u00c9\3\2\2\2,\u00cd"+
		"\3\2\2\2.\u00e2\3\2\2\2\60\u011c\3\2\2\2\62\u0125\3\2\2\2\64\u012b\3\2"+
		"\2\2\66\u0159\3\2\2\28<\5\b\5\29<\5\6\4\2:<\5\4\3\2;8\3\2\2\2;9\3\2\2"+
		"\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>@\3\2\2\2?=\3\2\2\2@A\7\2\2"+
		"\3A\3\3\2\2\2BC\7\22\2\2CD\78\2\2DI\7\60\2\2EH\5\b\5\2FH\5\6\4\2GE\3\2"+
		"\2\2GF\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JL\3\2\2\2KI\3\2\2\2LM\7\61"+
		"\2\2MN\7\64\2\2N\5\3\2\2\2OQ\5\20\t\2PO\3\2\2\2PQ\3\2\2\2QR\3\2\2\2RS"+
		"\78\2\2SU\7,\2\2TV\5\f\7\2UT\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\7-\2\2XY\5"+
		"\32\16\2Y\7\3\2\2\2Z[\5\20\t\2[`\5\n\6\2\\]\7\65\2\2]_\5\n\6\2^\\\3\2"+
		"\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2ac\3\2\2\2b`\3\2\2\2cd\7\64\2\2d\t\3"+
		"\2\2\2eh\78\2\2fg\7*\2\2gi\5.\30\2hf\3\2\2\2hi\3\2\2\2i\13\3\2\2\2jo\5"+
		"\16\b\2kl\7\65\2\2ln\5\16\b\2mk\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2"+
		"p\r\3\2\2\2qo\3\2\2\2rs\5\20\t\2st\78\2\2t\17\3\2\2\2uy\5\24\13\2vy\5"+
		"\26\f\2wy\7\7\2\2xu\3\2\2\2xv\3\2\2\2xw\3\2\2\2y\21\3\2\2\2z{\t\2\2\2"+
		"{\23\3\2\2\2|\177\78\2\2}\177\5\22\n\2~|\3\2\2\2~}\3\2\2\2\177\25\3\2"+
		"\2\2\u0080\u0083\5\24\13\2\u0081\u0082\7.\2\2\u0082\u0084\7/\2\2\u0083"+
		"\u0081\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2"+
		"\2\2\u0086\27\3\2\2\2\u0087\u0091\5\32\16\2\u0088\u0091\5\34\17\2\u0089"+
		"\u0091\5\36\20\2\u008a\u0091\5 \21\2\u008b\u0091\5\"\22\2\u008c\u0091"+
		"\5$\23\2\u008d\u0091\5&\24\2\u008e\u0091\5(\25\2\u008f\u0091\5*\26\2\u0090"+
		"\u0087\3\2\2\2\u0090\u0088\3\2\2\2\u0090\u0089\3\2\2\2\u0090\u008a\3\2"+
		"\2\2\u0090\u008b\3\2\2\2\u0090\u008c\3\2\2\2\u0090\u008d\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0090\u008f\3\2\2\2\u0091\31\3\2\2\2\u0092\u0096\7\60\2"+
		"\2\u0093\u0095\5\30\r\2\u0094\u0093\3\2\2\2\u0095\u0098\3\2\2\2\u0096"+
		"\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0099\3\2\2\2\u0098\u0096\3\2"+
		"\2\2\u0099\u009a\7\61\2\2\u009a\33\3\2\2\2\u009b\u009c\5\b\5\2\u009c\35"+
		"\3\2\2\2\u009d\u009e\7\n\2\2\u009e\u009f\7,\2\2\u009f\u00a0\5.\30\2\u00a0"+
		"\u00a1\7-\2\2\u00a1\u00a4\5\30\r\2\u00a2\u00a3\7\13\2\2\u00a3\u00a5\5"+
		"\30\r\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\37\3\2\2\2\u00a6"+
		"\u00a7\7\f\2\2\u00a7\u00a9\7,\2\2\u00a8\u00aa\5.\30\2\u00a9\u00a8\3\2"+
		"\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ad\7\64\2\2\u00ac"+
		"\u00ae\5.\30\2\u00ad\u00ac\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00af\3\2"+
		"\2\2\u00af\u00b1\7\64\2\2\u00b0\u00b2\5.\30\2\u00b1\u00b0\3\2\2\2\u00b1"+
		"\u00b2\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b4\7-\2\2\u00b4\u00b5\5\30"+
		"\r\2\u00b5!\3\2\2\2\u00b6\u00b7\7\r\2\2\u00b7\u00b8\7,\2\2\u00b8\u00b9"+
		"\5.\30\2\u00b9\u00ba\7-\2\2\u00ba\u00bb\5\30\r\2\u00bb#\3\2\2\2\u00bc"+
		"\u00be\7\20\2\2\u00bd\u00bf\5.\30\2\u00be\u00bd\3\2\2\2\u00be\u00bf\3"+
		"\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\7\64\2\2\u00c1%\3\2\2\2\u00c2\u00c3"+
		"\7\16\2\2\u00c3\u00c4\7\64\2\2\u00c4\'\3\2\2\2\u00c5\u00c6\7\17\2\2\u00c6"+
		"\u00c7\7\64\2\2\u00c7)\3\2\2\2\u00c8\u00ca\5.\30\2\u00c9\u00c8\3\2\2\2"+
		"\u00c9\u00ca\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cc\7\64\2\2\u00cc+\3"+
		"\2\2\2\u00cd\u00d2\5.\30\2\u00ce\u00cf\7\65\2\2\u00cf\u00d1\5.\30\2\u00d0"+
		"\u00ce\3\2\2\2\u00d1\u00d4\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d2\u00d3\3\2"+
		"\2\2\u00d3-\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d5\u00d6\b\30\1\2\u00d6\u00e3"+
		"\5\62\32\2\u00d7\u00d8\7\21\2\2\u00d8\u00e3\5\66\34\2\u00d9\u00da\78\2"+
		"\2\u00da\u00dc\7,\2\2\u00db\u00dd\5,\27\2\u00dc\u00db\3\2\2\2\u00dc\u00dd"+
		"\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00e3\7-\2\2\u00df\u00e0\5\60\31\2"+
		"\u00e0\u00e1\5.\30\16\u00e1\u00e3\3\2\2\2\u00e2\u00d5\3\2\2\2\u00e2\u00d7"+
		"\3\2\2\2\u00e2\u00d9\3\2\2\2\u00e2\u00df\3\2\2\2\u00e3\u0119\3\2\2\2\u00e4"+
		"\u00e5\f\r\2\2\u00e5\u00e6\t\3\2\2\u00e6\u0118\5.\30\16\u00e7\u00e8\f"+
		"\f\2\2\u00e8\u00e9\t\4\2\2\u00e9\u0118\5.\30\r\u00ea\u00eb\f\13\2\2\u00eb"+
		"\u00ec\t\5\2\2\u00ec\u0118\5.\30\f\u00ed\u00ee\f\n\2\2\u00ee\u00ef\t\6"+
		"\2\2\u00ef\u0118\5.\30\13\u00f0\u00f1\f\t\2\2\u00f1\u00f2\t\7\2\2\u00f2"+
		"\u0118\5.\30\n\u00f3\u00f4\f\b\2\2\u00f4\u00f5\7&\2\2\u00f5\u0118\5.\30"+
		"\t\u00f6\u00f7\f\7\2\2\u00f7\u00f8\7(\2\2\u00f8\u0118\5.\30\b\u00f9\u00fa"+
		"\f\6\2\2\u00fa\u00fb\7\'\2\2\u00fb\u0118\5.\30\7\u00fc\u00fd\f\5\2\2\u00fd"+
		"\u00fe\7!\2\2\u00fe\u0118\5.\30\6\u00ff\u0100\f\4\2\2\u0100\u0101\7\""+
		"\2\2\u0101\u0118\5.\30\5\u0102\u0103\f\3\2\2\u0103\u0104\7*\2\2\u0104"+
		"\u0118\5.\30\3\u0105\u0106\f\24\2\2\u0106\u0107\7+\2\2\u0107\u0118\78"+
		"\2\2\u0108\u0109\f\23\2\2\u0109\u010a\7+\2\2\u010a\u010b\78\2\2\u010b"+
		"\u010d\7,\2\2\u010c\u010e\5,\27\2\u010d\u010c\3\2\2\2\u010d\u010e\3\2"+
		"\2\2\u010e\u010f\3\2\2\2\u010f\u0118\7-\2\2\u0110\u0111\f\21\2\2\u0111"+
		"\u0112\7.\2\2\u0112\u0113\5.\30\2\u0113\u0114\7/\2\2\u0114\u0118\3\2\2"+
		"\2\u0115\u0116\f\17\2\2\u0116\u0118\t\b\2\2\u0117\u00e4\3\2\2\2\u0117"+
		"\u00e7\3\2\2\2\u0117\u00ea\3\2\2\2\u0117\u00ed\3\2\2\2\u0117\u00f0\3\2"+
		"\2\2\u0117\u00f3\3\2\2\2\u0117\u00f6\3\2\2\2\u0117\u00f9\3\2\2\2\u0117"+
		"\u00fc\3\2\2\2\u0117\u00ff\3\2\2\2\u0117\u0102\3\2\2\2\u0117\u0105\3\2"+
		"\2\2\u0117\u0108\3\2\2\2\u0117\u0110\3\2\2\2\u0117\u0115\3\2\2\2\u0118"+
		"\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u0119\u011a\3\2\2\2\u011a/\3\2\2\2"+
		"\u011b\u0119\3\2\2\2\u011c\u011d\t\t\2\2\u011d\61\3\2\2\2\u011e\u0126"+
		"\5\64\33\2\u011f\u0120\7,\2\2\u0120\u0121\5.\30\2\u0121\u0122\7-\2\2\u0122"+
		"\u0126\3\2\2\2\u0123\u0126\7\23\2\2\u0124\u0126\78\2\2\u0125\u011e\3\2"+
		"\2\2\u0125\u011f\3\2\2\2\u0125\u0123\3\2\2\2\u0125\u0124\3\2\2\2\u0126"+
		"\63\3\2\2\2\u0127\u012c\7\67\2\2\u0128\u012c\7\66\2\2\u0129\u012c\t\n"+
		"\2\2\u012a\u012c\7\6\2\2\u012b\u0127\3\2\2\2\u012b\u0128\3\2\2\2\u012b"+
		"\u0129\3\2\2\2\u012b\u012a\3\2\2\2\u012c\65\3\2\2\2\u012d\u0132\5\24\13"+
		"\2\u012e\u012f\7.\2\2\u012f\u0130\5.\30\2\u0130\u0131\7/\2\2\u0131\u0133"+
		"\3\2\2\2\u0132\u012e\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0132\3\2\2\2\u0134"+
		"\u0135\3\2\2\2\u0135\u0138\3\2\2\2\u0136\u0137\7.\2\2\u0137\u0139\7/\2"+
		"\2\u0138\u0136\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b"+
		"\3\2\2\2\u013b\u0140\3\2\2\2\u013c\u013d\7.\2\2\u013d\u013e\5.\30\2\u013e"+
		"\u013f\7/\2\2\u013f\u0141\3\2\2\2\u0140\u013c\3\2\2\2\u0141\u0142\3\2"+
		"\2\2\u0142\u0140\3\2\2\2\u0142\u0143\3\2\2\2\u0143\u015a\3\2\2\2\u0144"+
		"\u0149\5\24\13\2\u0145\u0146\7.\2\2\u0146\u0147\5.\30\2\u0147\u0148\7"+
		"/\2\2\u0148\u014a\3\2\2\2\u0149\u0145\3\2\2\2\u014a\u014b\3\2\2\2\u014b"+
		"\u0149\3\2\2\2\u014b\u014c\3\2\2\2\u014c\u0151\3\2\2\2\u014d\u014e\7."+
		"\2\2\u014e\u0150\7/\2\2\u014f\u014d\3\2\2\2\u0150\u0153\3\2\2\2\u0151"+
		"\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u015a\3\2\2\2\u0153\u0151\3\2"+
		"\2\2\u0154\u0155\5\24\13\2\u0155\u0156\7,\2\2\u0156\u0157\7-\2\2\u0157"+
		"\u015a\3\2\2\2\u0158\u015a\5\24\13\2\u0159\u012d\3\2\2\2\u0159\u0144\3"+
		"\2\2\2\u0159\u0154\3\2\2\2\u0159\u0158\3\2\2\2\u015a\67\3\2\2\2$;=GIP"+
		"U`hox~\u0085\u0090\u0096\u00a4\u00a9\u00ad\u00b1\u00be\u00c9\u00d2\u00dc"+
		"\u00e2\u010d\u0117\u0119\u0125\u012b\u0134\u013a\u0142\u014b\u0151\u0159";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}