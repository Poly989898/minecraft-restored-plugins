package com.Zrips.CMI.Modules.DataBase_NEW;

public enum CMITablesFieldsType {
  decimal("double"),
  number("int"),
  longtext("longtext"),
  text("text"),
  stringList("longtext"),
  stringLongMap("text"),
  stringIntMap("text"),
  stringDoubleMap("text"),
  stringStringMap("text"),
  locationMap("text"),
  state("boolean"),
  location("text"),
  longNumber("bigint");

  private String type;

  CMITablesFieldsType(String string) {
    this.type = string;
  }

  public static CMITablesFieldsType get(String string) {
    CMITablesFieldsType[] cmiTablesFieldsTypes;
    int value = (cmiTablesFieldsTypes = values()).length;

    for (int index = 0; index < value; index++) {
      CMITablesFieldsType cmiTablesFieldsType = cmiTablesFieldsTypes[index];
      if (cmiTablesFieldsType.name().equalsIgnoreCase(string)) {
        return cmiTablesFieldsType;
      }
    }

    return null;
  }

  public String getType() {
    return this.type;
  }
}
