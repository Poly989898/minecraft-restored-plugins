package io.lumine.mythic.paper;

import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDeathProtectionComponent;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.BukkitBootstrap;
import io.lumine.mythic.bukkit.BukkitPlatformBridge;
import io.lumine.mythic.core.items.MythicItem;

/**
 * Platform bridge used on Paper. The bootstrap loads it reflectively
 * (see {@link BukkitBootstrap#load}). Both item components only parse their
 * config section, so the same classes serve Paper and Spigot.
 */
public class PaperPlatformBridge implements BukkitPlatformBridge {
   private final BukkitBootstrap bootstrap;

   public PaperPlatformBridge(BukkitBootstrap bootstrap) {
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
