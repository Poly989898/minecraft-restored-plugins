package net.Zrips.CMILib.Equations;

public class VariableNode implements ExpressionNode {
  protected double value;
  protected boolean error;
  protected String name;
  protected ExpressionNode[] children = new ExpressionNode[0];

  public VariableNode(String name, boolean error) {
    this.name = name;
    this.value = 0.0;
    this.error = error;
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
    return this.name;
  }

  @Override
  public int getType() {
    return 1;
  }

  @Override
  public double getValue() {
    if (!this.error) {
      return this.value;
    } else {
      throw new EvaluationException("Variable '" + this.name + "' was not initialized.");
    }
  }

  @Override
  public void setVariable(String name, double value) {
    if (this.name.equals(name)) {
      this.value = value;
      this.error = false;
    }
  }

  @Override
  public ExpressionNode[] getChildrenNodes() {
    return this.children;
  }

  @Override
  public Object clone() {
    VariableNode node = new VariableNode(this.name, this.error);
    node.value = this.value;
    return node;
  }

  @Override
  public String toString() {
    return this.getSubtype();
  }
}
