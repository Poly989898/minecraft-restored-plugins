package net.Zrips.CMILib.Attributes;

import net.Zrips.CMILib.Version.Version;

public enum AttributeType {
  Armor("generic", "armor", 0),
  ArmorToughness("generic", "armor_Toughness", 0),
  AttackDamage("generic", "attack_Damage", 0),
  AttackKnockback("generic", "attack_Knockback", 0),
  AttackSpeed("generic", "attack_Speed", 2),
  FlyingSpeed("generic", "flying_Speed", 0),
  FollowRange("generic", "follow_Range", 0),
  KnockbackResistance("generic", "knockback_Resistance", 0),
  Luck("generic", "luck", 0),
  MaxHealth("generic", "max_Health", 0),
  MovementSpeed("generic", "movement_Speed", 1),
  JumpHeight("horse", "jump_Height", 0),
  SpawnReinforcements("zombie", "spawn_Reinforcements", 0);

  private String name;
  private String preName;
  private int action;
  private org.bukkit.attribute.Attribute attribute = null;

  AttributeType(String preName, String name, int action) {
    this.preName = preName;
    this.name = name;
    this.action = action;
    this.getAttribute();
  }

  public String getPreName() {
    return this.preName;
  }

  public String getFullName() {
    return this.preName + "." + this.getName();
  }

  public String getName() {
    return Version.isCurrentEqualOrHigher(Version.v1_16_R1)
        ? this.name.toLowerCase()
        : this.name.replace("_", "");
  }

  public String getIdentificator() {
    return this.preName.toUpperCase() + "_" + this.name.toUpperCase();
  }

  public static AttributeType get(String code) {
    code = code.replace("_", "");

    for (AttributeType e : values()) {
      if (code.equalsIgnoreCase(e.getName().replace("_", ""))) {
        return e;
      }
    }

    return null;
  }

  public int getAction() {
    return this.action;
  }

  public org.bukkit.attribute.Attribute getAttribute() {
    if (this.attribute != null) {
      return this.attribute;
    }

    for (org.bukkit.attribute.Attribute one : org.bukkit.attribute.Attribute.values()) {
      if (one.toString().equalsIgnoreCase(this.getIdentificator())
          || one.toString().equalsIgnoreCase(this.name)) {
        this.attribute = one;
        break;
      }
    }

    return this.attribute;
  }
}
