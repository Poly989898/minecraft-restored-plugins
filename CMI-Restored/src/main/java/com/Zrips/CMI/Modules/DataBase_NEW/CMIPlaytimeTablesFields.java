package com.Zrips.CMI.Modules.DataBase_NEW;

public enum CMIPlaytimeTablesFields {
  player_id(CMITablesFieldsType.number),
  date(CMITablesFieldsType.number),
  h0(0),
  h1(1),
  h2(2),
  h3(3),
  h4(4),
  h5(5),
  h6(6),
  h7(7),
  h8(8),
  h9(9),
  h10(10),
  h11(11),
  h12(12),
  h13(13),
  h14(14),
  h15(15),
  h16(16),
  h17(17),
  h18(18),
  h19(19),
  h20(20),
  h21(21),
  h22(22),
  h23(23);

  private CMITablesFieldsType fieldType = CMITablesFieldsType.longNumber;
  private int hour = 0;

  CMIPlaytimeTablesFields(CMITablesFieldsType cmiTablesFieldsType) {
    this.fieldType = cmiTablesFieldsType;
  }

  CMIPlaytimeTablesFields(int value) {
    this.hour = value;
  }

  CMIPlaytimeTablesFields(int value, CMITablesFieldsType cmiTablesFieldsType) {
    this.fieldType = cmiTablesFieldsType;
    this.hour = value;
  }

  public String getColumn() {
    return this.name();
  }

  public String getType() {
    return this.fieldType.getType();
  }

  public CMITablesFieldsType getFieldType() {
    return this.fieldType;
  }

  public boolean isTimeField() {
    return this.name().startsWith("h") && this.name().length() < 4;
  }

  public int getHour() {
    return this.hour;
  }
}
