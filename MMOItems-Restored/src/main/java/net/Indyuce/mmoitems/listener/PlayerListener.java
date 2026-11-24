package net.Indyuce.mmoitems.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import io.lumine.mythic.lib.api.event.armorequip.ArmorEquipEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import io.lumine.mythic.lib.entity.ProjectileMetadata;
import io.lumine.mythic.lib.entity.ProjectileType;
import java.util.Iterator;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.DeathItemsHandler;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.projectile.ArrowPotionEffectArrayItem;
import net.Indyuce.mmoitems.api.interaction.util.InteractItem;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.DeathDowngrading;
import net.Indyuce.mmoitems.stat.data.PotionEffectData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {
   @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
   public void onDeathForUpgradeLoss(@NotNull PlayerDeathEvent event) {
      if (PlayerData.has(event.getEntity())) {
         new PlayerListener.DelayedDeathDowngrade(event).runTaskLater(MMOItems.plugin, 3L);
      }
   }

   @EventHandler
   public void fullSync(SynchronizedDataLoadEvent event) {
      if (event.syncIsFull()) {
         PlayerData playerData = PlayerData.get(event.getHolder().getUniqueId());
         playerData.updateInventory();
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void keepItemsOnDeath(PlayerDeathEvent event) {
      if (!event.getKeepInventory()) {
         Player entity = event.getEntity();
         DeathItemsHandler deathItemsHandler = new DeathItemsHandler(entity);
         Iterator iterator = event.getDrops().iterator();

         while (iterator.hasNext()) {
            ItemStack item = (ItemStack)iterator.next();
            NBTItem nbtItem = NBTItem.get(item);
            if (nbtItem.getBoolean("MMOITEMS_DISABLE_DEATH_DROP") || MMOItems.plugin.getLanguage().keepSoulboundOnDeath && MMOUtils.isSoulboundTo(nbtItem, entity)) {
               iterator.remove();
               deathItemsHandler.registerItem(item);
            }
         }

         deathItemsHandler.registerIfNecessary();
      }
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent event) {
      DeathItemsHandler.readAndRemove(event.getPlayer());
   }

   @EventHandler
   public void onArmorEquip(ArmorEquipEvent event) {
      if (event.getNewArmorPiece() != null) {
         if (!PlayerData.get(event.getPlayer()).getRPG().canUse(NBTItem.get(event.getNewArmorPiece()), true)) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void registerTridents(ProjectileLaunchEvent event) {
      if (event.getEntity() instanceof Trident && event.getEntity().getShooter() instanceof Player) {
         InteractItem interactItem = new InteractItem((Player)event.getEntity().getShooter(), Material.TRIDENT);
         if (interactItem.hasItem()) {
            NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(interactItem.getItem());
            Type type = Type.get(nBTItem.getType());
            PlayerData playerData = PlayerData.get((Player)event.getEntity().getShooter());
            if (type != null) {
               Weapon weapon = new Weapon(playerData, nBTItem);
               if (!weapon.checkItemRequirements() || !weapon.checkAndApplyWeaponCosts()) {
                  event.setCancelled(true);
                  return;
               }

               ProjectileMetadata projectileMetadata = ProjectileMetadata.create(
                  playerData.getMMOPlayerData(), EquipmentSlot.fromBukkit(interactItem.getSlot()), ProjectileType.TRIDENT, event.getEntity()
               );
               projectileMetadata.setSourceItem(nBTItem);
               projectileMetadata.setCustomDamage(true);
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void registerArrowSpecialEffects(PlayerAttackEvent event) {
      if (event.getAttack() instanceof ProjectileAttackMetadata) {
         ProjectileAttackMetadata attack = (ProjectileAttackMetadata)event.getAttack();
         ProjectileMetadata projectileMetadata = ProjectileMetadata.get(attack.getProjectile());
         if (projectileMetadata != null && projectileMetadata.getSourceItem() != null) {
            this.applyPotionEffects(projectileMetadata, event.getEntity());
         }
      }
   }

   private void applyPotionEffects(ProjectileMetadata projectileMetadata, LivingEntity entity) {
      if (projectileMetadata.getSourceItem().hasTag("MMOITEMS_ARROW_POTION_EFFECTS")) {
         for (ArrowPotionEffectArrayItem arrowPotionEffectArrayItem : (ArrowPotionEffectArrayItem[])MythicLib.plugin
            .getJson()
            .parse(projectileMetadata.getSourceItem().getString("MMOITEMS_ARROW_POTION_EFFECTS"), ArrowPotionEffectArrayItem[].class)) {
            entity.addPotionEffect(new PotionEffectData(PotionEffectType.getByName(arrowPotionEffectArrayItem.type), arrowPotionEffectArrayItem.duration, arrowPotionEffectArrayItem.level).toEffect());
         }
      }
   }

   @Deprecated
   @EventHandler
   public void registerInventoryUpdates1(PlayerSwapHandItemsEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   @Deprecated
   @EventHandler
   public void registerInventoryUpdates2(PlayerItemHeldEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   @Deprecated
   @EventHandler
   public void registerInventoryUpdates3(PlayerDropItemEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   private static class DelayedDeathDowngrade extends BukkitRunnable {
      @NotNull
      final PlayerDeathEvent event;

      DelayedDeathDowngrade(@NotNull PlayerDeathEvent event) {
         this.event = event;
      }

      public void run() {
         DeathDowngrading.playerDeathDowngrade(this.event.getEntity());
      }
   }
}
