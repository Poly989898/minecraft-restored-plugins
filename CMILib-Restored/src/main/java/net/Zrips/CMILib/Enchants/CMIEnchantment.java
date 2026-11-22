package net.Zrips.CMILib.Enchants;

import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class CMIEnchantment {
  private String keyName = null;
  private String translatedName = null;
  private Enchantment enchant = null;
  private CMIEnchantEnum enumEnchant = null;
  private static final Map<String, CMIEnchantment> byName = new HashMap<>();
  private static final Map<Enchantment, CMIEnchantment> byEnchant = new HashMap<>();
  private static HashMap<String, String> enchantList = new HashMap<>();

  CMIEnchantment(Enchantment enchant, CMIEnchantEnum enumEnchant) {
    this(enchant);
    this.enumEnchant = enumEnchant;
  }

  CMIEnchantment(Enchantment enchant) {
    this.enchant = enchant;

    try {
      this.keyName = enchant.getKey().getKey().replace("minecraft:", "");
    } catch (Throwable var3) {
    }
  }

  public String getName() {
    return this.enchant.getName();
  }

  public String getKeyName() {
    try {
      return this.keyName == null
          ? this.enchant.getKey().getKey().replace("minecraft:", "")
          : this.keyName;
    } catch (Throwable e) {
      return this.getName();
    }
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  public String getTranslatedName() {
    return this.translatedName == null
        ? (this.getKeyName() == null ? this.getName() : this.getKeyName())
        : this.translatedName;
  }

  public void setTranslatedName(String translatedName) {
    this.translatedName = translatedName;
  }

  public Enchantment getEnchant() {
    return this.enchant;
  }

  public boolean equalEnum(CMIEnchantEnum enumEnchant) {
    return this.enumEnchant == null ? false : this.enumEnchant.equals(enumEnchant);
  }

  public CMIEnchantEnum getCMIEnum() {
    return this.enumEnchant;
  }

  public static Map<Enchantment, CMIEnchantment> getEnchants() {
    return byEnchant;
  }

  private static String strip(String name) {
    return name.replace("_", "").replace(" ", "").toLowerCase();
  }

  public static void initialize() {
    HashMap<String, CMIEnchantment> eList = new HashMap<>();

    for (CMIEnchantEnum one : CMIEnchantEnum.values()) {
      Enchantment got = one.getEnchantment();
      if (got != null) {
        CMIEnchantment cmi = new CMIEnchantment(got, one);
        eList.put(strip(one.getName()), cmi);
        byName.putIfAbsent(strip(one.getName()), cmi);
        byEnchant.putIfAbsent(got, cmi);

        for (String oneAlternative : one.getAlternativeNames()) {
          eList.put(strip(oneAlternative), cmi);
          byName.putIfAbsent(strip(oneAlternative), cmi);
        }
      }
    }

    for (Enchantment one : Enchantment.values()) {
      try {
        if (one != null) {
          String name = one.getName();
          if (name != null && !name.isEmpty() && name != " ") {
            CMIEnchantment cmi = eList.getOrDefault(strip(name), new CMIEnchantment(one));
            byName.putIfAbsent(strip(one.getName()), cmi);
            byEnchant.putIfAbsent(one, cmi);
          }
        }
      } catch (Exception | Error e) {
        e.printStackTrace();
      }
    }

    enchantList =
        byName.entrySet().stream()
            .collect(
                HashMap::new,
                (map, entry) ->
                    map.put(
                        CMIText.firstToUpperCase(strip(entry.getKey())),
                        CMIText.firstToUpperCase(strip(entry.getValue().getName()))),
                HashMap::putAll);
  }

  public static void updateLocale() {
    ConfigReader locale = CMILib.getInstance().getConfigManager().getLocaleConfig();
    locale.addComment("info.EnchantNames", "Avoid adding color codes to the enchant name");

    for (CMIEnchantment cmiEnchant : byEnchant.values()) {
      String name = cmiEnchant.getKeyName();
      if (name != null && !name.isEmpty() && name != " ") {
        String existing = null;
        if (locale.getC().isList("info.EnchantAliases." + name.toLowerCase())) {
          existing =
              (String)
                  locale.getC().getStringList("info.EnchantAliases." + name.toLowerCase()).get(0);
        }

        String defaultValue =
            existing != null ? existing : CMIText.everyFirstToUpperCase(name, false);
        String translated =
            CMIChatColor.stripColor(
                locale.get("info.EnchantNames." + name.toLowerCase(), defaultValue));
        cmiEnchant.setTranslatedName(translated);
        byName.putIfAbsent(strip(cmiEnchant.getTranslatedName()), cmiEnchant);
      }
    }
  }

  @Deprecated
  public static void saveEnchants() {}

  public static Enchantment get(String nameId) {
    return getByName(nameId);
  }

  public static Enchantment getByName(String name) {
    if (name == null) {
      return null;
    }

    if (name.contains(":")) {
      name = name.split(":", 2)[0];
    }

    String original = name;
    name = name.replace("_", "").replace(" ", "").toLowerCase();
    CMIEnchantment got = byName.get(name);
    if (got != null) {
      return got.getEnchant();
    }

    Enchantment e = null;
    if (name.contains(":")) {
      String[] split = name.split(":", 2);
      e = Enchantment.getByKey(new NamespacedKey(split[0], split[1]));
      if (e != null) {
        return e;
      }
    }

    try {
      e = Enchantment.getByName(original.toUpperCase());
    } catch (Throwable ex) {
      ex.printStackTrace();
    }

    return e;
  }

  public static CMIEnchantment get(Enchantment enchantment) {
    return byEnchant.get(enchantment);
  }

  public static CMIEnchantment getCMIByName(String name) {
    if (name.contains(":")) {
      name = name.split(":", 2)[0];
    }

    return byName.get(name.replace("_", "").replace(" ", "").toLowerCase());
  }

  public static boolean isEnabled(Enchantment enchant) {
    if (enchant != null && enchant.getName() != null) {
      for (Enchantment one : Enchantment.values()) {
        if (one != null
            && one.getName() != null
            && one.getName().equalsIgnoreCase(enchant.getName())) {
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  public static Enchantment[] values() {
    return byEnchant.keySet().toArray(new Enchantment[byName.size()]);
  }

  public static String getName(Enchantment enchant) {
    if (enchant != null && enchant.getName() != null) {
      CMIEnchantment got = byEnchant.get(enchant);
      return got != null
          ? CMIText.firstToUpperCase(got.getName()).replace(" ", "")
          : CMIText.firstToUpperCase(enchant.getName()).replace(" ", "");
    } else {
      return null;
    }
  }

  public static String getTranslatedName(Enchantment enchant) {
    if (enchant != null && enchant.getName() != null) {
      CMIEnchantment got = byEnchant.get(enchant);
      return got != null ? got.getTranslatedName() : CMIText.firstToUpperCase(enchant.getName());
    } else {
      return null;
    }
  }

  public static HashMap<String, String> getEnchantList() {
    return enchantList;
  }
}
