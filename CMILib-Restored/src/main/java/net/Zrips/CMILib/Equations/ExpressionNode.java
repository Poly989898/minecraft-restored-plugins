package net.Zrips.CMILib.Equations;

public interface ExpressionNode extends Cloneable {
  int CONSTANT_NODE = 0;
  int VARIABLE_NODE = 1;
  int OPERATOR_NODE = 2;
  int FUNCTION_NODE = 3;

  double getValue();

  void setVariable(String name, double value);

  int getType();

  String getSubtype();

  int getDepth();

  int count();

  ExpressionNode[] getChildrenNodes();

  String toString();

  Object clone();
}
