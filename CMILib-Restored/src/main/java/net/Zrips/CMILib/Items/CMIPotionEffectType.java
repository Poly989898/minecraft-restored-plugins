package net.Zrips.CMILib.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public enum CMIPotionEffectType {
  ABSORPTION,
  BAD_OMEN,
  BLINDNESS,
  CONDUIT_POWER,
  NAUSEA("CONFUSION"),
  RESISTANCE("DAMAGE_RESISTANCE"),
  DARKNESS,
  DOLPHINS_GRACE,
  HASTE("FAST_DIGGING"),
  FIRE_RESISTANCE,
  GLOWING,
  INSTANT_DAMAGE,
  INSTANT_HEALTH,
  HEALTH_BOOST,
  HERO_OF_THE_VILLAGE,
  HUNGER,
  STRENGTH("INCREASE_DAMAGE"),
  INVISIBILITY,
  JUMP_BOOST("JUMP"),
  LEVITATION,
  LUCK,
  NIGHT_VISION,
  POISON,
  REGENERATION,
  SATURATION,
  SLOWNESS("SLOW"),
  MINING_FATIGUE("SLOW_DIGGING"),
  SLOW_FALLING,
  SPEED,
  UNLUCK,
  WATER_BREATHING,
  WEAKNESS,
  WITHER,
  INFESTED,
  OOZING,
  RAID_OMEN,
  TRIAL_OMEN,
  WEAVING,
  WIND_CHARGED;

  private String name = null;
  private String translatedName = null;
  private List<String> alternativeNames = new ArrayList<>();
  private PotionEffectType type = null;
  private static final Map<String, PotionEffectType> byName = new HashMap<>();
  private static final Map<PotionEffectType, CMIPotionEffectType> byType = new HashMap<>();
  private static HashMap<String, CMIPotionEffectType> localizedPotionEffectTypeList =
      new HashMap<>();

  CMIPotionEffectType(String string) {
    if (string != null) {
      this.alternativeNames.add(string);
    }

    this.name = CMIText.everyFirstToUpperCase(this.toString());

    for (PotionEffectType one : PotionEffectType.values()) {
      if (one != null
          && (Version.isCurrentEqualOrHigher(Version.v1_20_R1)
              ? one.getKey()
                  .getKey()
                  .replace("_", "")
                  .replace(" ", "")
                  .equalsIgnoreCase(this.toString().replace("_", ""))
              : one.getName()
                  .replace("_", "")
                  .replace(" ", "")
                  .equalsIgnoreCase(this.toString().replace("_", "")))) {
        this.type = one;
      }
    }

    if (this.type == null) {
      for (String alternative : this.getAlternativeNames()) {
        for (PotionEffectType one : PotionEffectType.values()) {
          if (one != null
              && one.getName()
                  .replace("_", "")
                  .replace(" ", "")
                  .equalsIgnoreCase(alternative.replace("_", ""))) {
            this.type = one;
            break;
          }
        }
      }
    }
  }

  CMIPotionEffectType() {
    this(null);
  }

  public PotionEffectType getType() {
    return this.type;
  }

  public String getName() {
    return this.name;
  }

  public static void loadLocalization() {
    ConfigReader locale = CMILib.getInstance().getConfigManager().getLocaleConfig();

    for (CMIPotionEffectType one : byType.values()) {
      if (one != null) {
        String name = one.getName().replace(" ", "").toLowerCase();

        for (String oneL :
            locale.get(
                "info.PotionEffectAliases." + name,
                Arrays.asList(CMIText.firstToUpperCase(one.getName())))) {
          one.translatedName = oneL;
          localizedPotionEffectTypeList.put(
              CMIChatColor.stripColor(oneL).replace(" ", "").replace("_", "").toLowerCase(), one);
        }
      }
    }
  }

  public static PotionEffectType[] effectValues() {
    return byName.values().toArray(new PotionEffectType[byName.size()]);
  }

  public static PotionEffectType get(String name) {
    name = name.replace(" ", "").replace("_", "").toLowerCase();
    PotionEffectType effect = byName.get(name);
    if (effect != null) {
      return effect;
    } else {
      CMIPotionEffectType cmiEffect = localizedPotionEffectTypeList.get(name);
      if (cmiEffect == null) {
        PotionType potionType = CMIPotionType.get(name);
        return potionType != null ? potionType.getEffectType() : null;
      } else {
        return cmiEffect.getType();
      }
    }
  }

  public static CMIPotionEffectType get(PotionEffectType type) {
    return type == null ? null : byType.get(type);
  }

  public String getTranslatedName() {
    return this.translatedName == null ? this.name : this.translatedName;
  }

  public List<String> getAlternativeNames() {
    return this.alternativeNames;
  }

  static {
    for (CMIPotionEffectType one : values()) {
      if (one != null && one.getType() != null) {
        byName.put(one.toString().replace(" ", "").replace("_", "").toLowerCase(), one.getType());
        byType.put(one.getType(), one);

        for (String alternative : one.getAlternativeNames()) {
          byName.put(alternative.replace(" ", "").replace("_", "").toLowerCase(), one.getType());
        }
      }
    }

    CMIScheduler.runTask(
        CMILib.getInstance(),
        () -> {
          for (PotionEffectType onex : PotionEffectType.values()) {
            if (onex != null) {
              byName.putIfAbsent(
                  onex.toString().replace(" ", "").replace("_", "").toLowerCase(), onex);
              byName.putIfAbsent(
                  onex.getName().replace(" ", "").replace("_", "").toLowerCase(), onex);
            }
          }
        });
  }
}
