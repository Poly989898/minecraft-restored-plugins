package io.lumine.mythic.api.volatilecode;

import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import org.bukkit.Material;

public class VolatileMaterial {
   public static final Material SHORT_GRASS = ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_3)
      ? Material.valueOf("SHORT_GRASS")
      : Material.valueOf("GRASS");
}
