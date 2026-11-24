package net.Indyuce.mmocore.listener;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import io.lumine.mythic.lib.version.Attributes;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.profess.resource.PlayerResource;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class PlayerListener implements Listener {
   @EventHandler(priority = EventPriority.HIGHEST)
   public void a(SynchronizedDataLoadEvent event) {
      if (event.syncIsFull()) {
         PlayerData playerData = PlayerData.get(event.getHolder().getUniqueId());
         Player player = playerData.getPlayer();
         playerData.setupSkillTrees();
         playerData.applyTemporaryTriggers();
         playerData.getStats().updateStats(true);
         if (playerData.isOnline() && !player.isDead()) {
            double cachedHealth = playerData.getCachedHealth();
            double maxHealth = player.getAttribute(Attributes.MAX_HEALTH).getValue();
            double fixedHealth = MMOCoreUtils.fixResource(cachedHealth, maxHealth);
            player.setHealth(fixedHealth);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void updateCombat(PlayerAttackEvent event) {
      PlayerData.get(event.getAttacker().getPlayer()).getCombat().update();
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void updateCombat(EntityDamageEvent event) {
      if (!UtilityMethods.isFake(event)) {
         if (UtilityMethods.isRealPlayer(event.getEntity()) && MMOCore.plugin.configManager.combatLogDamageCauses.contains(event.getCause())) {
            PlayerData.get((Player)event.getEntity()).getCombat().update();
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void resourceBukkitInterface(PlayerResourceUpdateEvent event) {
      if (event.getResource() == PlayerResource.HEALTH) {
         EntityRegainHealthEvent bukkitEvent = new EntityRegainHealthEvent(event.getPlayer(), event.getAmount(), RegainReason.CUSTOM);
         Bukkit.getPluginManager().callEvent(bukkitEvent);
         event.setAmount(bukkitEvent.getAmount());
         event.setCancelled(bukkitEvent.isCancelled());
      }
   }
}
