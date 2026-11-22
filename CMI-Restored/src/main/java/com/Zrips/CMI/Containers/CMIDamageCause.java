package com.Zrips.CMI.Containers;

import java.util.HashMap;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum CMIDamageCause {
  BLOCK_EXPLOSION,
  CONTACT,
  CRAMMING,
  CUSTOM,
  DRAGON_BREATH,
  DROWNING,
  DRYOUT,
  ENTITY_ATTACK,
  ENTITY_EXPLOSION,
  ENTITY_SWEEP_ATTACK,
  FALL,
  FALLING_BLOCK,
  FIRE,
  FIRE_TICK,
  FLY_INTO_WALL,
  HOT_FLOOR,
  LAVA,
  LIGHTNING,
  MAGIC,
  MELTING,
  POISON,
  PROJECTILE,
  STARVATION,
  SUFFOCATION,
  SUICIDE,
  THORNS,
  VOID,
  WITHER;

  static HashMap<String, CMIDamageCause> byName = new HashMap<>();
  private String name = null;
  private String secondaryName = null;
  private DamageCause cause = null;

  static {
    CMIDamageCause[] cmiCauses;
    int cmiCount = (cmiCauses = values()).length;

    for (int cmiIndex = 0; cmiIndex < cmiCount; cmiIndex++) {
      CMIDamageCause cmiCause = cmiCauses[cmiIndex];
      DamageCause bukkitCause = null;
      DamageCause[] bukkitCauses;
      int bukkitCount = (bukkitCauses = DamageCause.values()).length;

      for (int bukkitIndex = 0; bukkitIndex < bukkitCount; bukkitIndex++) {
        DamageCause bukkitCauseCandidate = bukkitCauses[bukkitIndex];
        if (bukkitCauseCandidate.toString().equalsIgnoreCase(cmiCause.toString())) {
          bukkitCause = bukkitCauseCandidate;
          break;
        }
      }

      if (bukkitCause != null) {
        cmiCause.cause = bukkitCause;
        byName.put(cmiCause.toString().replace("_", "").toLowerCase(), cmiCause);
        if (cmiCause.name != null) {
          byName.put(cmiCause.getName().replace("_", "").replace(" ", "").toLowerCase(), cmiCause);
        }

        if (cmiCause.secondaryName != null) {
          byName.put(
              cmiCause.secondaryName.replace("_", "").replace(" ", "").toLowerCase(), cmiCause);
        }
      }
    }
  }

  CMIDamageCause() {
    this(null, null);
  }

  CMIDamageCause(String string, String secondaryNameParam) {
    this.name = string;
    this.secondaryName = secondaryNameParam;
  }

  public String getName() {
    return this.name;
  }

  public String getSecondaryName() {
    return this.secondaryName;
  }

  public static CMIDamageCause getByName(String string) {
    return byName.get(string.toLowerCase().replace("_", ""));
  }

  public DamageCause getCause() {
    return this.cause;
  }
}
