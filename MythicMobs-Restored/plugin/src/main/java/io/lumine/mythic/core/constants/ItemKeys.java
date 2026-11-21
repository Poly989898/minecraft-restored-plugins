package io.lumine.mythic.core.constants;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.NamespacedKey;

public class ItemKeys {
   public static final NamespacedKey TYPE = new NamespacedKey(MythicBukkit.inst(), "type");
   public static final NamespacedKey VERSION = new NamespacedKey(MythicBukkit.inst(), "version");
   public static final NamespacedKey PREVENT_ENCHANTING = new NamespacedKey(MythicBukkit.inst(), "prevent_enchanting");
   public static final NamespacedKey PREVENT_REPAIRING_WITH = new NamespacedKey(MythicBukkit.inst(), "prevent_repairing_with");
   public static final NamespacedKey PREVENT_CRAFTING_WITH = new NamespacedKey(MythicBukkit.inst(), "prevent_crafting_with");
   public static final NamespacedKey PREVENT_SMITHING_WITH = new NamespacedKey(MythicBukkit.inst(), "prevent_smithing_with");
   public static final NamespacedKey FANCY_DROP = new NamespacedKey(MythicBukkit.inst(), "fancydrop");
   public static final NamespacedKey UUID = new NamespacedKey(MythicBukkit.inst(), "uuid");
   public static final NamespacedKey TIMESTAMP = new NamespacedKey(MythicBukkit.inst(), "timestamp");
}
