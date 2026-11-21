package io.lumine.mythic.api.adapters;

import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDeathProtectionComponent;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface ServerInterface {
   void initialize();

   List<AbstractWorld> getWorlds();

   void dispatchCommand(String str);

   AbstractBlockMatcher getBlockMatcher(String str);

   AbstractBlockMatcher getBlockMatcher(Collection<String> strs);

   List<AbstractPlayer> getOnlinePlayers();

   int getOnlinePlayerCount();

   AbstractLocation newLocation(AbstractWorld abstractWorld, double value, double value2, double value3);

   AbstractWorld getWorld(String str);

   boolean isValidBiome(Object obj);

   AbstractBossBar createBossBar(String str, AbstractBossBar.BarColor barColor, AbstractBossBar.BarStyle barStyle);

   AbstractItemStack createItem(String str);

   default AbstractItemMatcher getItemMatcher(MythicLineConfig config) {
      return this.getItemMatcher(config, null);
   }

   AbstractItemMatcher getItemMatcher(MythicLineConfig mythicLineConfig, String str);

   default AbstractItemMatcher getItemMatcher(String name) {
      return this.getItemMatcher(name, false);
   }

   AbstractItemMatcher getItemMatcher(String str, boolean flag);

   default AbstractItemMatcher getItemMatcher(Collection<String> name) {
      return this.getItemMatcher(name, false);
   }

   AbstractItemMatcher getItemMatcher(Collection<String> strs, boolean flag);

   AbstractEnchantment createEnchantment(String str);

   AbstractPotionEffect createPotionEffect(String str, int value, int value2, boolean flag, boolean flag2, boolean flag3);

   AbstractPlayer getPlayer(UUID uuid);

   AbstractPlayer getPlayer(String str);

   MythicEntityType getMythicEntityType(EntityType entityType);

   AbstractEntity getEntity(UUID uuid);

   Collection<AbstractEntity> getEntities(AbstractWorld abstractWorld);

   Collection<AbstractEntity> getLivingEntities(AbstractWorld abstractWorld);

   Collection<AbstractPlayer> getPlayers(AbstractWorld abstractWorld);

   Collection<AbstractEntity> getEntities(Collection<UUID> uuids);

   AbstractWorld getWorld(UUID uuid);

   void handleMobDespawnEvent(ActiveMob activeMob);

   MythicEntityType getMythicEntityType(Entity entity);

   AbstractLocation newLocation(AbstractWorld abstractWorld, double value, double value2, double value3, float value4, float value5);

   AbstractFirework createFirework(String str, int value, Collection<Color> colors, Collection<Color> colors2, boolean flag, boolean flag2);

   AbstractBiome getBiome(String str);

   AbstractBiome getBiome(AbstractLocation abstractLocation);

   Collection<AbstractBiome> getBiomes(String str);

   AbstractBlock getBlock(String str);

   AbstractBlock getBlock(AbstractLocation abstractLocation);

   AbstractItemConsumableComponent createItemConsumableComponent(MythicItem mythicItem, MythicConfig mythicConfig);

   AbstractItemDeathProtectionComponent createItemDeathProtectionComponent(MythicItem mythicItem, MythicConfig mythicConfig);

   AbstractTeleportData createTeleportData(MythicLineConfig mythicLineConfig);
}
