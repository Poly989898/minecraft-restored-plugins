package com.Zrips.CMI.Modules.DataBase_NEW;

public enum CMIPlaytimeRewardTablesFields {
  player_id(CMITablesFieldsType.number),
  repeatable(CMITablesFieldsType.stringLongMap),
  onetime(CMITablesFieldsType.stringList);

  private CMITablesFieldsType fieldType;

  CMIPlaytimeRewardTablesFields(CMITablesFieldsType cmiTablesFieldsType) {
    this.fieldType = cmiTablesFieldsType;
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
}
