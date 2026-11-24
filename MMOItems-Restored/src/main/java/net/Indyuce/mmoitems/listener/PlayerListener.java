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
   public void onDeathForUpgradeLoss(@NotNull PlayerDeathEvent var1) {
      if (PlayerData.has(var1.getEntity())) {
         new PlayerListener.DelayedDeathDowngrade(var1).runTaskLater(MMOItems.plugin, 3L);
      }
   }

   @EventHandler
   public void fullSync(SynchronizedDataLoadEvent var1) {
      if (var1.syncIsFull()) {
         PlayerData var2 = PlayerData.get(var1.getHolder().getUniqueId());
         var2.updateInventory();
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void keepItemsOnDeath(PlayerDeathEvent var1) {
      if (!var1.getKeepInventory()) {
         Player var2 = var1.getEntity();
         DeathItemsHandler var3 = new DeathItemsHandler(var2);
         Iterator var4 = var1.getDrops().iterator();

         while (var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            NBTItem var6 = NBTItem.get(var5);
            if (var6.getBoolean("MMOITEMS_DISABLE_DEATH_DROP") || MMOItems.plugin.getLanguage().keepSoulboundOnDeath && MMOUtils.isSoulboundTo(var6, var2)) {
               var4.remove();
               var3.registerItem(var5);
            }
         }

         var3.registerIfNecessary();
      }
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent var1) {
      DeathItemsHandler.readAndRemove(var1.getPlayer());
   }

   @EventHandler
   public void onArmorEquip(ArmorEquipEvent var1) {
      if (var1.getNewArmorPiece() != null) {
         if (!PlayerData.get(var1.getPlayer()).getRPG().canUse(NBTItem.get(var1.getNewArmorPiece()), true)) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void registerTridents(ProjectileLaunchEvent var1) {
      if (var1.getEntity() instanceof Trident && var1.getEntity().getShooter() instanceof Player) {
         InteractItem var2 = new InteractItem((Player)var1.getEntity().getShooter(), Material.TRIDENT);
         if (var2.hasItem()) {
            NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var2.getItem());
            Type var4 = Type.get(var3.getType());
            PlayerData var5 = PlayerData.get((Player)var1.getEntity().getShooter());
            if (var4 != null) {
               Weapon var6 = new Weapon(var5, var3);
               if (!var6.checkItemRequirements() || !var6.checkAndApplyWeaponCosts()) {
                  var1.setCancelled(true);
                  return;
               }

               ProjectileMetadata var7 = ProjectileMetadata.create(
                  var5.getMMOPlayerData(), EquipmentSlot.fromBukkit(var2.getSlot()), ProjectileType.TRIDENT, var1.getEntity()
               );
               var7.setSourceItem(var3);
               var7.setCustomDamage(true);
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void registerArrowSpecialEffects(PlayerAttackEvent var1) {
      if (var1.getAttack() instanceof ProjectileAttackMetadata) {
         ProjectileAttackMetadata var2 = (ProjectileAttackMetadata)var1.getAttack();
         ProjectileMetadata var3 = ProjectileMetadata.get(var2.getProjectile());
         if (var3 != null && var3.getSourceItem() != null) {
            this.applyPotionEffects(var3, var1.getEntity());
         }
      }
   }

   private void applyPotionEffects(ProjectileMetadata var1, LivingEntity var2) {
      if (var1.getSourceItem().hasTag("MMOITEMS_ARROW_POTION_EFFECTS")) {
         for (ArrowPotionEffectArrayItem var6 : (ArrowPotionEffectArrayItem[])MythicLib.plugin
            .getJson()
            .parse(var1.getSourceItem().getString("MMOITEMS_ARROW_POTION_EFFECTS"), ArrowPotionEffectArrayItem[].class)) {
            var2.addPotionEffect(new PotionEffectData(PotionEffectType.getByName(var6.type), var6.duration, var6.level).toEffect());
         }
      }
   }

   @Deprecated
   @EventHandler
   public void registerInventoryUpdates1(PlayerSwapHandItemsEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @Deprecated
   @EventHandler
   public void registerInventoryUpdates2(PlayerItemHeldEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @Deprecated
   @EventHandler
   public void registerInventoryUpdates3(PlayerDropItemEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   private static class DelayedDeathDowngrade extends BukkitRunnable {
      @NotNull
      final PlayerDeathEvent event;

      DelayedDeathDowngrade(@NotNull PlayerDeathEvent var1) {
         this.event = var1;
      }

      public void run() {
         DeathDowngrading.playerDeathDowngrade(this.event.getEntity());
      }
   }
}
