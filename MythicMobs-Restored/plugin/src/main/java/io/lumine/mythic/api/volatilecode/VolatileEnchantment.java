package io.lumine.mythic.api.volatilecode;

import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import org.bukkit.enchantments.Enchantment;

public class VolatileEnchantment {
   public static final Enchantment LOOTING = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? Enchantment.getByName("LOOTING")
      : Enchantment.getByName("LOOT_BONUS_MOBS");
   public static final Enchantment LUCK_OF_THE_SEA = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? Enchantment.getByName("LUCK_OF_THE_SEA")
      : Enchantment.getByName("LUCK");
   public static final Enchantment UNBREAKING = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)
      ? Enchantment.getByName("UNBREAKING")
      : Enchantment.getByName("DURABILITY");
}
