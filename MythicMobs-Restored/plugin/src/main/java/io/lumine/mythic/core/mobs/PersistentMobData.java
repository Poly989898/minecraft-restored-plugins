package io.lumine.mythic.core.mobs;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.NamespacedKey;

public class PersistentMobData {
   public static final NamespacedKey TYPE = new NamespacedKey(MythicBukkit.inst(), "mob-type");
   public static final NamespacedKey LEVEL = new NamespacedKey(MythicBukkit.inst(), "mob-level");
   public static final NamespacedKey FACTION = new NamespacedKey(MythicBukkit.inst(), "mob-faction");
   public static final NamespacedKey OWNER = new NamespacedKey(MythicBukkit.inst(), "mob-owner");
   public static final NamespacedKey PARENT = new NamespacedKey(MythicBukkit.inst(), "mob-parent");
   public static final NamespacedKey STANCE = new NamespacedKey(MythicBukkit.inst(), "mob-stance");
   public static final NamespacedKey NO_KNOCKBACK = new NamespacedKey(MythicBukkit.inst(), "no-knockback");
   public static final NamespacedKey SLIME_NO_SPLIT = new NamespacedKey(MythicBukkit.inst(), "slime-no-split");
}
