package net.Zrips.CMILib.Items;

import java.util.HashMap;
import java.util.Map;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public enum CMIPotionType {
  AWKWARD,
  FIRE_RESISTANCE,
  HARMING,
  HEALING,
  INFESTED,
  INVISIBILITY,
  LEAPING,
  LONG_FIRE_RESISTANCE,
  LONG_INVISIBILITY,
  LONG_LEAPING,
  LONG_NIGHT_VISION,
  LONG_POISON,
  LONG_REGENERATION,
  LONG_SLOWNESS,
  LONG_SLOW_FALLING,
  LONG_STRENGTH,
  LONG_SWIFTNESS,
  LONG_TURTLE_MASTER,
  LONG_WATER_BREATHING,
  LONG_WEAKNESS,
  LUCK,
  MUNDANE,
  NIGHT_VISION,
  OOZING,
  POISON,
  REGENERATION,
  SLOWNESS,
  SLOW_FALLING,
  STRENGTH,
  STRONG_HARMING,
  STRONG_HEALING,
  STRONG_LEAPING,
  STRONG_POISON,
  STRONG_REGENERATION,
  STRONG_SLOWNESS,
  STRONG_STRENGTH,
  STRONG_SWIFTNESS,
  STRONG_TURTLE_MASTER,
  SWIFTNESS,
  THICK,
  TURTLE_MASTER,
  WATER,
  WATER_BREATHING,
  WEAKNESS,
  WEAVING,
  WIND_CHARGED;

  private boolean extended = false;
  private boolean upgraded = false;
  private String name = null;
  private PotionType type = null;
  private static final String extendedPrefix = "LONG_";
  private static final String upgradedPrefix = "STRONG_";
  private static final Map<String, PotionType> byName = new HashMap<>();
  private static final Map<PotionType, CMIPotionType> byType = new HashMap<>();

  CMIPotionType() {
    this.name =
        CMIText.everyFirstToUpperCase(this.toString().replace("LONG_", "").replace("STRONG_", ""));
    this.extended = this.toString().startsWith("LONG_");
    this.upgraded = this.toString().startsWith("STRONG_");

    for (PotionType one : PotionType.values()) {
      if (one != null && one.toString().equalsIgnoreCase(this.toString())) {
        this.type = one;
      }
    }
  }

  public PotionType getType() {
    return this.type;
  }

  public String getName() {
    return this.name;
  }

  public boolean isUpgraded() {
    return this.upgraded;
  }

  public boolean isExtended() {
    return this.extended;
  }

  @Deprecated
  public static PotionType get(PotionType potionType, boolean upgraded, boolean extended) {
    if (potionType == null) {
      return null;
    }

    CMIPotionType cmiType = get(potionType);
    String name = cmiType == null ? potionType.toString() : cmiType.toString();
    if (upgraded && !name.startsWith("STRONG_")) {
      name = "STRONG_" + name;
    }

    if (extended && !name.startsWith("LONG_")) {
      name = "LONG_" + name;
    }

    return get(name);
  }

  public static PotionType get(PotionEffectType type) {
    PotionType by = PotionType.getByEffect(type);
    return by != null ? by : get(type.getName());
  }

  public static PotionType get(PotionEffectType type, boolean upgraded, boolean extended) {
    PotionType by = PotionType.getByEffect(type);
    String name = by == null ? type.toString() : by.toString();
    if (upgraded && !name.startsWith("STRONG_")) {
      name = "STRONG_" + name;
    }

    if (extended && !name.startsWith("LONG_")) {
      name = "LONG_" + name;
    }

    return get(name);
  }

  public static PotionType get(String type, boolean upgraded, boolean extended) {
    switch (type.toLowerCase().replace("_", "")) {
      case "slow":
        type = "slowness";
        break;
      case "speed":
        type = "swiftness";
        break;
      case "jump":
        type = "leaping";
        break;
      case "heal":
        type = "healing";
        break;
      case "harm":
        type = "harming";
        break;
      case "increasedamage":
        type = "strength";
    }

    if (upgraded && !type.startsWith("STRONG_")) {
      type = "STRONG_" + type;
    }

    if (extended && !type.startsWith("LONG_")) {
      type = "LONG_" + type;
    }

    return get(type);
  }

  public static PotionType get(String name) {
    return name == null ? null : byName.get(name.replace(" ", "").replace("_", "").toLowerCase());
  }

  public static CMIPotionType get(PotionType type) {
    return type == null ? null : byType.get(type);
  }

  static {
    for (CMIPotionType one : values()) {
      if (one != null && one.getType() != null) {
        byName.put(one.toString().replace(" ", "").replace("_", "").toLowerCase(), one.getType());
        byType.put(one.getType(), one);
      }
    }

    CMIScheduler.runTask(
        CMILib.getInstance(),
        () -> {
          for (PotionType onex : PotionType.values()) {
            if (onex != null) {
              byName.putIfAbsent(
                  onex.toString().replace(" ", "").replace("_", "").toLowerCase(), onex);
            }
          }
        });
  }
}
