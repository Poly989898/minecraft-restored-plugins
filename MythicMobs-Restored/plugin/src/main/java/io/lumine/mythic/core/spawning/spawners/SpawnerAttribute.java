package io.lumine.mythic.core.spawning.spawners;

import com.google.common.collect.Lists;
import java.util.Collection;

public enum SpawnerAttribute {
   GROUP("group", "spawnergroup"),
   RADIUS("radius", "r"),
   RADIUS_Y("radiusy", "ry"),
   COOLDOWN("cooldown", "cd"),
   WARMUP("warmup", "wu"),
   MAX_MOBS("maxmobs"),
   MOB_LEVEL("moblevel", "level", "moblvl"),
   MOBS_PER_SPAWN("mobsperspawn", "mps"),
   USE_TIMER("usetimer", "timer"),
   SHOW_FLAMES("showflames", "flames"),
   ACTIVATION_RANGE("activation", "activationrange", "ar"),
   SCALING_RANGE("scalingrange", "scalerange", "sr"),
   LEASH_RANGE("leash", "leashrange"),
   HEAL_ON_LEASH("healonleash", "leashheal"),
   RESET_THREAT_ON_LEASH("resetthreatonleash", "leashresetthreat"),
   MOB_TYPE("mobtype"),
   BREAKABLE("breakable"),
   CHECK_FOR_PLAYERS("checkforplayers"),
   YAW("yaw"),
   PITCH("pitch"),
   CHANCE("chance"),
   RANDOM_ROTATION("randomrotation", "randomrot"),
   USE_WORLD_SCALING("useworldscaling", "worldscaling");

   private final Collection<String> aliases = Lists.newArrayList();

   private SpawnerAttribute(String... aliases) {
      this.aliases.add(this.toString().toLowerCase());

      for (String alias : aliases) {
         this.aliases.add(alias.toLowerCase());
      }
   }

   public static SpawnerAttribute get(String attribute) {
      String attrLowerCase = attribute.toLowerCase();

      for (SpawnerAttribute attr : values()) {
         if (attr.aliases.contains(attrLowerCase)) {
            return attr;
         }
      }

      throw new IllegalArgumentException("Attribute " + attribute + " does not exist!");
   }
}
