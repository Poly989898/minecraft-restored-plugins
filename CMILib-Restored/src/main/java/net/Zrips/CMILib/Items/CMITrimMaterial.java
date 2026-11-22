package net.Zrips.CMILib.Items;

import org.bukkit.inventory.meta.trim.TrimMaterial;

public enum CMITrimMaterial {
  AMETHYST,
  COPPER,
  DIAMOND,
  EMERALD,
  GOLD,
  IRON,
  LAPIS,
  NETHERITE,
  QUARTZ,
  REDSTONE,
  RESIN;

  public TrimMaterial get() {
    return getByName(this.toString());
  }

  public static TrimMaterial getByName(String name) {
    switch (name.toUpperCase()) {
      case "AMETHYST":
        return TrimMaterial.AMETHYST;
      case "COPPER":
        return TrimMaterial.COPPER;
      case "DIAMOND":
        return TrimMaterial.DIAMOND;
      case "EMERALD":
        return TrimMaterial.EMERALD;
      case "GOLD":
        return TrimMaterial.GOLD;
      case "IRON":
        return TrimMaterial.IRON;
      case "LAPIS":
        return TrimMaterial.LAPIS;
      case "NETHERITE":
        return TrimMaterial.NETHERITE;
      case "QUARTZ":
        return TrimMaterial.QUARTZ;
      case "REDSTONE":
        return TrimMaterial.REDSTONE;
      case "RESIN":
        return TrimMaterial.RESIN;
      default:
        return null;
    }
  }
}
