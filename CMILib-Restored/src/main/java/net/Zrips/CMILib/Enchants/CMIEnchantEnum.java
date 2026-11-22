package net.Zrips.CMILib.Enchants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.enchantments.Enchantment;

public enum CMIEnchantEnum {
  AQUA_AFFINITY("WATER_WORKER"),
  BANE_OF_ARTHROPODS("DAMAGE_ARTHROPODS"),
  BINDING_CURSE,
  BLAST_PROTECTION("PROTECTION_EXPLOSIONS"),
  CHANNELING,
  DEPTH_STRIDER,
  EFFICIENCY("DIG_SPEED"),
  FEATHER_FALLING("PROTECTION_FALL"),
  FIRE_ASPECT,
  FIRE_PROTECTION("PROTECTION_FIRE"),
  FLAME("ARROW_FIRE"),
  FORTUNE("LOOT_BONUS_BLOCKS"),
  FROST_WALKER,
  IMPALING,
  INFINITY("ARROW_INFINITE"),
  KNOCKBACK,
  LOOTING("LOOT_BONUS_MOBS"),
  LOYALTY,
  LUCK_OF_THE_SEA("LUCK"),
  LURE,
  MENDING,
  MULTISHOT,
  PIERCING,
  POWER("ARROW_DAMAGE"),
  PROJECTILE_PROTECTION("PROTECTION_PROJECTILE"),
  PROTECTION("PROTECTION_ENVIRONMENTAL"),
  PUNCH("ARROW_KNOCKBACK"),
  QUICK_CHARGE,
  RESPIRATION("OXYGEN"),
  RIPTIDE,
  SHARPNESS("DAMAGE_ALL"),
  SILK_TOUCH,
  SMITE("DAMAGE_UNDEAD"),
  SOUL_SPEED,
  SWEEPING("SWEEPING_EDGE"),
  SWIFT_SNEAK,
  THORNS,
  UNBREAKING("DURABILITY"),
  VANISHING_CURSE,
  DENSITY,
  BREACH,
  WIND_BURST;

  private List<String> alternatives = new ArrayList<>();
  private Enchantment enchant = null;

  CMIEnchantEnum() {}

  CMIEnchantEnum(String alternative) {
    this.alternatives = Arrays.asList(alternative);
  }

  public String getName() {
    return this.toString();
  }

  @Deprecated
  public String getAlternativeName() {
    return this.alternatives.isEmpty() ? null : this.alternatives.get(0);
  }

  public List<String> getAlternativeNames() {
    return this.alternatives;
  }

  private static String strip(String name) {
    return name.replace("_", "").replace(" ", "").toLowerCase();
  }

  public Enchantment getEnchantment() {
    if (this.enchant != null) {
      return this.enchant;
    }

    this.enchant = Enchantment.getByName(this.getName());
    if (this.enchant != null) {
      return this.enchant;
    }

    for (Enchantment one : Enchantment.values()) {
      try {
        if (one != null) {
          String name = one.getName();
          if (name != null && !name.isEmpty() && name != " ") {
            if (strip(name).equalsIgnoreCase(strip(this.getName()))) {
              this.enchant = one;
              return this.enchant;
            }

            for (String oneAlternative : this.getAlternativeNames()) {
              if (strip(name).equalsIgnoreCase(strip(oneAlternative))) {
                this.enchant = one;
                return this.enchant;
              }
            }
          }
        }
      } catch (Exception | Error e) {
        e.printStackTrace();
      }
    }

    return this.enchant;
  }
}
