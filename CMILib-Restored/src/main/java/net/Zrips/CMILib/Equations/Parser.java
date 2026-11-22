package net.Zrips.CMILib.Equations;

import java.util.HashSet;

public class Parser {
  private static final char TERM = '@';
  private static final char NUM = 'n';
  private static final char FUNC = 'f';
  private static final char VAR = 'x';
  private static final char OP_BRACKET = '(';
  private static final char CL_BRACKET = ')';
  private char[] input;
  private boolean error;
  private int inp_cur;
  private Parser.Element cc;
  private ExpressionNode root;
  private HashSet<String> functions;
  private HashSet<String> variables;

  public Parser(String str) {
    this(str, false);
  }

  public Parser(String str, boolean error) {
    this.input = this.convertInput(str);
    this.root = null;
    this.inp_cur = 0;
    this.error = error;
    this.functions = new HashSet<>();
    this.variables = new HashSet<>();
  }

  protected char[] convertInput(String str) {
    char[] input = str.toCharArray();
    char[] output = new char[input.length + 1];
    int pos = 0;

    for (int i = 0; i < input.length; i++) {
      char c = input[i];
      if (isNumber(c) || isOperator(c) || isText(c) || isBracket(c)) {
        output[pos++] = c;
      }
    }

    output[pos++] = '@';
    return output;
  }

  public ExpressionNode getTree() throws ParseError {
    if (this.root == null) {
      this.cc = this.parse();
      this.root = this.S();
      if (!isEndOfExpression(this.cc.getToken())) {
        throw new ParseError("Expecting operator or end of input", this.inp_cur);
      }
    }

    return this.root;
  }

  private static boolean isNumber(char c) {
    return c >= '0' && c <= '9' || c == '.';
  }

  private static boolean isOperator(char c) {
    for (int i = 0; i < OperatorNode.OPERATIONS.length; i++) {
      if (c == OperatorNode.OPERATIONS[i]) {
        return true;
      }
    }

    return false;
  }

  private static boolean isText(char c) {
    return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_';
  }

  private static boolean isBracket(char c) {
    return c == '(' || c == ')';
  }

  private static boolean isEndOfExpression(char c) {
    return c == '@';
  }

  private Parser.Element parse() {
    if (this.detectImplicitMult()) {
      return new Parser.Element('*');
    }

    if (!isOperator(this.input[this.inp_cur])
        && !isBracket(this.input[this.inp_cur])
        && !isEndOfExpression(this.input[this.inp_cur])) {
      if (isNumber(this.input[this.inp_cur])) {
        int tmp = this.inp_cur++;

        while (isNumber(this.input[this.inp_cur])) {
          this.inp_cur++;
        }

        if (Character.toLowerCase(this.input[this.inp_cur]) == 'e') {
          if (this.input[this.inp_cur + 1] == '-' || isNumber(this.input[this.inp_cur + 1])) {
            this.inp_cur += 2;
          }

          while (isNumber(this.input[this.inp_cur])) {
            this.inp_cur++;
          }
        }

        String s = new String(this.input, tmp, this.inp_cur - tmp);

        double d;
        try {
          d = Double.valueOf(s);
        } catch (NumberFormatException ex) {
          throw new ParseError("Invalid number: " + s, this.inp_cur);
        }

        return new Parser.Element('n', d);
      } else {
        if (!isText(this.input[this.inp_cur])) {
          throw new ParseError("Unrecognized identifier", this.inp_cur);
        }

        int tmp = this.inp_cur++;

        while (isText(this.input[this.inp_cur]) || isNumber(this.input[this.inp_cur])) {
          this.inp_cur++;
        }

        String s = new String(this.input, tmp, this.inp_cur - tmp);
        String lower = s.toLowerCase();

        for (int i = 0; i < FunctionNode.FUNCTIONS.length; i++) {
          if (lower.equals(FunctionNode.FUNCTIONS[i])) {
            this.functions.add(FunctionNode.FUNCTIONS[i]);
            return new Parser.Element('f', i);
          }
        }

        for (int i = 0; i < ConstantNode.CONSTANTS.length; i++) {
          if (lower.equals(ConstantNode.CONSTANTS[i])) {
            return new Parser.Element('n', i);
          }
        }

        this.variables.add(s);
        return new Parser.Element('x', s);
      }
    } else {
      return new Parser.Element(this.input[this.inp_cur++]);
    }
  }

  private boolean detectImplicitMult() {
    if (this.cc != null && this.cc.getToken() == 'n') {
      this.cc = null;
      int old_input = this.inp_cur;
      Parser.Element next = this.parse();
      this.inp_cur = old_input;
      if (next.getToken() == 'x' || next.getToken() == 'f') {
        return true;
      }
    }

    return false;
  }

  private ExpressionNode S() {
    ExpressionNode current = this.E();

    while (this.cc.getToken() == '+' || this.cc.getToken() == '-') {
      char operator = this.cc.getToken();
      this.cc = this.parse();
      current = new OperatorNode(current, this.E(), operator);
    }

    return current;
  }

  private ExpressionNode E() {
    ExpressionNode current;
    for (current = this.G();
        this.cc.getToken() == '*';
        current = new OperatorNode(current, this.G(), '*')) {
      this.cc = this.parse();
    }

    return current;
  }

  private ExpressionNode G() {
    ExpressionNode current;
    for (current = this.H();
        this.cc.getToken() == '/';
        current = new OperatorNode(current, this.H(), '/')) {
      this.cc = this.parse();
    }

    return current;
  }

  private ExpressionNode H() {
    ExpressionNode current;
    for (current = this.T();
        this.cc.getToken() == '%';
        current = new OperatorNode(current, this.T(), '%')) {
      this.cc = this.parse();
    }

    return current;
  }

  private ExpressionNode T() {
    if (this.cc.getToken() == '-') {
      this.cc = this.parse();
      return new FunctionNode(this.F(), "-");
    } else {
      return this.F();
    }
  }

  private ExpressionNode F() {
    ExpressionNode left = this.U();
    if (this.cc.getToken() == '^') {
      this.cc = this.parse();
      return new OperatorNode(left, this.F(), '^');
    } else {
      return left;
    }
  }

  private ExpressionNode U() {
    switch (this.cc.getToken()) {
      case '(':
        this.cc = this.parse();
        ExpressionNode tmp = this.S();
        if (this.cc.getToken() == ')') {
          this.cc = this.parse();
          return tmp;
        }

        throw new ParseError("Semantic Error, expected ')'", this.inp_cur);
      case 'f':
        int function = this.cc.position;
        this.cc = this.parse();
        if (this.cc.getToken() == '(') {
          this.cc = this.parse();
          ExpressionNode argumentNode = this.S();
          if (this.cc.getToken() == ')') {
            this.cc = this.parse();
            return new FunctionNode(argumentNode, function);
          }

          throw new ParseError("Semantic Error, expected ')'", this.inp_cur);
        }

        throw new ParseError("Semantic Error, expected '('", this.inp_cur);
      case 'n':
        double n = this.cc.number;
        int constPosition = this.cc.position;
        this.cc = this.parse();
        if (constPosition >= 0) {
          return new ConstantNode(constPosition);
        }

        return new ConstantNode(n);
      case 'x':
        String name = this.cc.name;
        this.cc = this.parse();
        return new VariableNode(name, this.error);
      default:
        throw new ParseError(
            "Semantic Error, expected function or variable or constant or '('", this.inp_cur);
    }
  }

  public void setVariable(String name, double value) throws ParseError {
    this.getTree();
    this.root.setVariable(name, value);
  }

  public double getValue() throws ParseError, EvaluationException {
    this.getTree();
    return this.root.getValue();
  }

  private class Element {
    private char token;
    public double number = Double.NaN;
    public int position = -1;
    public String name = null;

    public Element(char token) {
      this.token = token;
    }

    public Element(char token, double number) {
      this.token = token;
      this.number = number;
    }

    public Element(char token, String name) {
      this.token = token;
      this.name = name;
    }

    public Element(char token, int position) {
      this.token = token;
      this.position = position;
    }

    public char getToken() {
      return this.token;
    }
  }
}
