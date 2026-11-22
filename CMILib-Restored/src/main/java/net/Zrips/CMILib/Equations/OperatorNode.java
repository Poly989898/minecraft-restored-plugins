package net.Zrips.CMILib.Equations;

public class OperatorNode implements ExpressionNode {
  public static final char[] OPERATIONS = new char[] {'+', '-', '*', '/', '%', '^'};
  protected ExpressionNode left;
  protected ExpressionNode right;
  protected char operation;
  protected ExpressionNode[] children;

  public OperatorNode(ExpressionNode left, ExpressionNode right, char operation) {
    this.operation = operation;
    this.left = left;
    this.right = right;
    this.children = new ExpressionNode[] {left, right};
  }

  @Override
  public int count() {
    return 1 + this.left.count() + this.right.count();
  }

  @Override
  public int getDepth() {
    return 1 + Math.max(this.left.getDepth(), this.right.getDepth());
  }

  @Override
  public String getSubtype() {
    return Character.toString(this.operation);
  }

  @Override
  public int getType() {
    return 2;
  }

  @Override
  public double getValue() {
    switch (this.operation) {
      case '%':
        return this.left.getValue() % this.right.getValue();
      case '*':
        return this.left.getValue() * this.right.getValue();
      case '+':
        return this.left.getValue() + this.right.getValue();
      case '-':
        return this.left.getValue() - this.right.getValue();
      case '/':
        return this.left.getValue() / this.right.getValue();
      case '^':
        return Math.pow(this.left.getValue(), this.right.getValue());
      default:
        return 0.0;
    }
  }

  @Override
  public void setVariable(String name, double value) {
    this.left.setVariable(name, value);
    this.right.setVariable(name, value);
  }

  @Override
  public ExpressionNode[] getChildrenNodes() {
    return this.children;
  }

  @Override
  public Object clone() {
    ExpressionNode n_left = (ExpressionNode) this.left.clone();
    ExpressionNode n_right = (ExpressionNode) this.right.clone();
    return new OperatorNode(n_left, n_right, this.operation);
  }

  @Override
  public String toString() {
    StringBuffer str = new StringBuffer();
    if (this.needBrackets(this.left, true)) {
      str.append('(');
    }

    str.append(this.left);
    if (this.needBrackets(this.left, true)) {
      str.append(')');
    }

    str.append(this.getSubtype());
    if (this.needBrackets(this.right, false)) {
      str.append('(');
    }

    str.append(this.right);
    if (this.needBrackets(this.right, false)) {
      str.append(')');
    }

    return str.toString();
  }

  private boolean needBrackets(ExpressionNode child, boolean isleft) {
    char childSubtype = child.getSubtype().charAt(0);
    if (child.getType() == 0 || child.getType() == 1) {
      return false;
    }

    if (child.getType() == 3) {
      return childSubtype == '-';
    }

    switch (this.getSubtype().charAt(0)) {
      case '%':
        return true;
      case '*':
        return childSubtype == '+' || childSubtype == '-' || childSubtype == '%';
      case '+':
        return false;
      case '-':
        return (childSubtype == '+' || childSubtype == '-') && !isleft;
      case '/':
        return childSubtype != '*' || !isleft;
      case '^':
        return true;
      default:
        return true;
    }
  }
}
