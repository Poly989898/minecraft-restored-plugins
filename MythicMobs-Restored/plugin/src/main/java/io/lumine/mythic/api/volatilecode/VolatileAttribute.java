package io.lumine.mythic.api.volatilecode;

import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;

public class VolatileAttribute {
   public static final Attribute SPAWN_REINFORCEMENTS = ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3)
      ? Registry.ATTRIBUTE.get(NamespacedKey.minecraft("spawn_reinforcements"))
      : Registry.ATTRIBUTE.get(NamespacedKey.minecraft("zombie.spawn_reinforcements"));
   public static final Attribute BLOCK_BREAK_SPEED = ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3)
      ? Attribute.valueOf("BLOCK_BREAK_SPEED")
      : Attribute.valueOf("PLAYER_BLOCK_BREAK_SPEED");
}
