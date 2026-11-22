package com.Zrips.CMI.Modules.DataBase_NEW;

public enum CMIInventoryTablesFields {
  player_id("player_id", "int", CMITablesFieldsType.number),
  inventories("inventories", "longtext", CMITablesFieldsType.longtext);

  private String column;
  private String type;
  private CMITablesFieldsType fieldType;

  CMIInventoryTablesFields(
      String string, String typeName, CMITablesFieldsType cmiTablesFieldsType) {
    this.column = string;
    this.type = typeName;
    this.fieldType = cmiTablesFieldsType;
  }

  public String getColumn() {
    return this.column;
  }

  public String getType() {
    return this.type;
  }

  public CMITablesFieldsType getFieldType() {
    return this.fieldType;
  }
}
