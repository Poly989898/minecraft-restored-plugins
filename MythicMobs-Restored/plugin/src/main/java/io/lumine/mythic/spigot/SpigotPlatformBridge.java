package io.lumine.mythic.spigot;

import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDeathProtectionComponent;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.BukkitBootstrap;
import io.lumine.mythic.bukkit.BukkitPlatformBridge;
import io.lumine.mythic.core.items.MythicItem;

/**
 * Platform bridge used on Spigot (loaded reflectively by {@link BukkitBootstrap#load}).
 * Same behaviour as the Paper bridge: the components only parse their config section.
 */
public class SpigotPlatformBridge implements BukkitPlatformBridge {
   private final BukkitBootstrap bootstrap;

   public SpigotPlatformBridge(BukkitBootstrap bootstrap) {
      this.bootstrap = bootstrap;
   }

   public BukkitBootstrap getBootstrap() {
      return this.bootstrap;
   }

   @Override
   public AbstractItemConsumableComponent createItemConsumableComponent(MythicItem mythicItem, MythicConfig mythicConfig) {
      return new AbstractItemConsumableComponent(mythicItem, mythicConfig);
   }

   @Override
   public AbstractItemDeathProtectionComponent createItemDeathProtectionComponent(MythicItem mythicItem, MythicConfig mythicConfig) {
      return new AbstractItemDeathProtectionComponent(mythicItem, mythicConfig);
   }
}
