package net.Zrips.CMILib.Equations;

public class FunctionNode implements ExpressionNode {
  public static final String[] FUNCTIONS =
      new String[] {
        "-", "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "asinh", "acosh",
        "atanh", "ln", "log", "abs", "rand", "sqrt", "erf", "erfc", "gamma", "exp", "cot", "log2"
      };
  protected ExpressionNode child;
  protected int function;
  protected ExpressionNode[] children;

  public FunctionNode(ExpressionNode child, int function) {
    this.child = child;
    this.function = function;
    this.children = new ExpressionNode[] {child};
  }

  public FunctionNode(ExpressionNode child, String function) throws IllegalArgumentException {
    this.child = child;
    this.function = -1;
    this.children = new ExpressionNode[] {child};

    for (int i = 0; i < FUNCTIONS.length; i++) {
      if (FUNCTIONS[i].equals(function)) {
        this.function = i;
        break;
      }
    }

    if (this.function < 0) {
      throw new IllegalArgumentException("Unrecognized function");
    }
  }

  @Override
  public int count() {
    return 1 + this.child.count();
  }

  @Override
  public int getDepth() {
    return 1 + this.child.getDepth();
  }

  @Override
  public String getSubtype() {
    return FUNCTIONS[this.function];
  }

  @Override
  public int getType() {
    return 3;
  }

  @Override
  public double getValue() {
    switch (this.function) {
      case 0:
        return -this.child.getValue();
      case 1:
        return Math.sin(this.child.getValue());
      case 2:
        return Math.cos(this.child.getValue());
      case 3:
        return Math.tan(this.child.getValue());
      case 4:
        return Math.asin(this.child.getValue());
      case 5:
        return Math.acos(this.child.getValue());
      case 6:
        return Math.atan(this.child.getValue());
      case 7:
        return Sfun.sinh(this.child.getValue());
      case 8:
        return Sfun.cosh(this.child.getValue());
      case 9:
        return Sfun.tanh(this.child.getValue());
      case 10:
        return Sfun.asinh(this.child.getValue());
      case 11:
        return Sfun.acosh(this.child.getValue());
      case 12:
        return Sfun.atanh(this.child.getValue());
      case 13:
        return Math.log(this.child.getValue());
      case 14:
        return Math.log(this.child.getValue()) * 0.4342944819032518;
      case 15:
        return Math.abs(this.child.getValue());
      case 16:
        return Math.random() * this.child.getValue();
      case 17:
        return Math.sqrt(this.child.getValue());
      case 18:
        return Sfun.erf(this.child.getValue());
      case 19:
        return Sfun.erfc(this.child.getValue());
      case 20:
        return Sfun.gamma(this.child.getValue());
      case 21:
        return Math.exp(this.child.getValue());
      case 22:
        return Sfun.cot(this.child.getValue());
      case 23:
        return Math.log(this.child.getValue()) * 1.4426950408889634;
      default:
        return 0.0;
    }
  }

  @Override
  public void setVariable(String name, double value) {
    this.child.setVariable(name, value);
  }

  @Override
  public ExpressionNode[] getChildrenNodes() {
    return this.children;
  }

  @Override
  public Object clone() {
    ExpressionNode n_child = (ExpressionNode) this.child.clone();
    return new FunctionNode(n_child, this.function);
  }

  @Override
  public String toString() {
    if (this.function != 0) {
      return this.getSubtype() + "(" + this.child.toString() + ")";
    } else {
      return this.child.getType() != 0
              && this.child.getType() != 1
              && (this.child.getType() != 3 || this.child.getSubtype().equals(FUNCTIONS[0]))
          ? FUNCTIONS[0] + "(" + this.child.toString() + ")"
          : FUNCTIONS[0] + this.child.toString();
    }
  }
}
