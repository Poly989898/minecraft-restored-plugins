package io.lumine.mythic.lib.listener;

import io.lumine.mythic.lib.entity.ProjectileMetadata;
import io.lumine.mythic.lib.version.VEnchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CustomProjectileDamage implements Listener {
   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void customProjectileDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Projectile
         && event.getEntity() instanceof LivingEntity
         && (!(event.getEntity() instanceof Player) || !event.getEntity().hasMetadata("NPC"))) {
         Projectile projectile = (Projectile)event.getDamager();
         ProjectileMetadata data = ProjectileMetadata.get(projectile);
         if (data != null) {
            if (data.isCustomDamage()) {
               double baseDamage = data.getDamage();
               if (projectile instanceof AbstractArrow
                  && data.getSourceItem() != null
                  && data.getSourceItem().getItem().hasItemMeta()
                  && data.getSourceItem().getItem().getItemMeta().getEnchants().containsKey(VEnchantment.POWER.get())) {
                  baseDamage *= 1.25 + 0.25 * data.getSourceItem().getItem().getItemMeta().getEnchantLevel(VEnchantment.POWER.get());
               }

               event.setDamage(baseDamage);
            }
         }
      }
   }
}
