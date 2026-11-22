package com.Zrips.CMI.Modules.Search;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public enum ItemSearchCriteria {
  MATERIAL,
  NAME,
  LORE,
  ENCHANT,
  MODELDATA,
  POTION,
  AMOUNT,
  EXACT,
  OVERSIZE;

  private static String prefix = "{";
  private static Pattern pname = Pattern.compile("^(?i)(name|n)\\" + prefix);
  private static Pattern plore = Pattern.compile("^(?i)(lore|l)\\" + prefix);
  private static Pattern penchant = Pattern.compile("^(?i)(e)\\" + prefix);
  private static Pattern pmodel =
      Pattern.compile("^(?i)(custommodeldata|custommodel|cm|cmd)\\" + prefix);

  public static ItemSearchCriteria getByName(String string) {
    ItemSearchCriteria[] itemSearchCriterias;
    int value = (itemSearchCriterias = values()).length;

    for (int index = 0; index < value; index++) {
      ItemSearchCriteria itemSearchCriteria = itemSearchCriterias[index];
      if (itemSearchCriteria.name().equalsIgnoreCase(string)) {
        return itemSearchCriteria;
      }
    }

    return null;
  }

  public static Set<ItemSearchCriteria> getFromString(String string) {
    HashSet<ItemSearchCriteria> hashSet = new HashSet();
    String[] strings;
    int value = (strings = string.split(";")).length;

    for (int index = 0; index < value; index++) {
      String arg = strings[index];
      if (pname.matcher(arg).find()) {
        hashSet.add(NAME);
      } else if (plore.matcher(arg).find()) {
        hashSet.add(LORE);
      } else if (penchant.matcher(arg).find()) {
        hashSet.add(ENCHANT);
      } else if (pmodel.matcher(arg).find()) {
        hashSet.add(MODELDATA);
      }
    }

    return hashSet;
  }
}
