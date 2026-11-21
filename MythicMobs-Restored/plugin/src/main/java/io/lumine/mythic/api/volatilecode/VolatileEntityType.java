package io.lumine.mythic.api.volatilecode;

import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import org.bukkit.entity.EntityType;

public class VolatileEntityType {
   public static final EntityType CHEST_MINECART = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? EntityType.valueOf("CHEST_MINECART")
      : EntityType.valueOf("MINECART_CHEST");
   public static final EntityType END_CRYSTAL = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? EntityType.valueOf("END_CRYSTAL")
      : EntityType.valueOf("ENDER_CRYSTAL");
   public static final EntityType EXPERIENCE_BOTTLE = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? EntityType.valueOf("EXPERIENCE_BOTTLE")
      : EntityType.valueOf("THROWN_EXP_BOTTLE");
   public static final EntityType FIREWORK_ROCKET = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? EntityType.valueOf("FIREWORK_ROCKET")
      : EntityType.valueOf("FIREWORK");
   public static final EntityType ITEM = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? EntityType.valueOf("ITEM") : EntityType.valueOf("DROPPED_ITEM");
   public static final EntityType MOOSHROOM = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? EntityType.valueOf("MOOSHROOM")
      : EntityType.valueOf("MUSHROOM_COW");
   public static final EntityType SNOW_GOLEM = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? EntityType.valueOf("SNOW_GOLEM")
      : EntityType.valueOf("SNOWMAN");
   public static final EntityType TNT = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5) ? EntityType.valueOf("TNT") : EntityType.valueOf("PRIMED_TNT");
   public static final EntityType BOAT = ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3) ? EntityType.valueOf("OAK_BOAT") : EntityType.valueOf("BOAT");
   public static final EntityType CHEST_BOAT = ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3)
      ? EntityType.valueOf("OAK_CHEST_BOAT")
      : EntityType.valueOf("CHEST_BOAT");
}
