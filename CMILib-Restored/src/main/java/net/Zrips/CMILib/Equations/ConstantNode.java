package net.Zrips.CMILib.Equations;

public class ConstantNode implements ExpressionNode {
  public static final String[] CONSTANTS = new String[] {"pi", "e"};
  public static final double[] VALUES = new double[] {Math.PI, Math.E};
  protected double constant;
  protected String name;
  protected ExpressionNode[] children = new ExpressionNode[0];

  public ConstantNode(double constant) {
    this.constant = constant;
    this.name = null;
  }

  public ConstantNode(String name) {
    this.name = name;

    for (int i = 0; i < CONSTANTS.length; i++) {
      if (CONSTANTS[i].equals(name)) {
        this.constant = VALUES[i];
        return;
      }
    }

    throw new IllegalArgumentException("Unrecognized constant");
  }

  public ConstantNode(int pos) {
    this.name = CONSTANTS[pos];
    this.constant = VALUES[pos];
  }

  @Override
  public int count() {
    return 1;
  }

  @Override
  public int getDepth() {
    return 1;
  }

  @Override
  public String getSubtype() {
    return Math.floor(this.constant) == this.constant
        ? Long.toString(Math.round(this.constant))
        : Double.toString(this.constant);
  }

  @Override
  public int getType() {
    return 0;
  }

  @Override
  public double getValue() {
    return this.constant;
  }

  @Override
  public void setVariable(String name, double value) {}

  @Override
  public ExpressionNode[] getChildrenNodes() {
    return this.children;
  }

  @Override
  public Object clone() {
    return new ConstantNode(this.constant);
  }

  @Override
  public String toString() {
    return this.name == null ? this.getSubtype() : this.name;
  }
}
