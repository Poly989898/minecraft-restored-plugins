package net.Indyuce.mmoitems.comp.mmocore;

import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import java.util.Locale;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.api.event.PlayerResourceUpdateEvent.UpdateReason;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.api.player.stats.StatType;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.experience.source.RepairItemExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.event.item.ItemCustomRepairEvent;
import net.Indyuce.mmoitems.api.event.item.RepairItemEvent;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.comp.mmocore.stat.ExtraAttribute;
import net.Indyuce.mmoitems.comp.mmocore.stat.RequiredAttribute;
import net.Indyuce.mmoitems.comp.mmocore.stat.RequiredProfession;
import net.Indyuce.mmoitems.comp.rpg.RPGHandler;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class MMOCoreHook implements RPGHandler, Listener {
   public MMOCoreHook() {
      for (PlayerAttribute var2 : MMOCore.plugin.attributeManager.getAll()) {
         MMOItems.plugin.getStats().register(new RequiredAttribute(var2));
         MMOItems.plugin.getStats().register(new ExtraAttribute(var2));
      }

      for (Profession var4 : MMOCore.plugin.professionManager.getAll()) {
         MMOItems.plugin
            .getStats()
            .register(
               new DoubleStat(
                  (StatType.ADDITIONAL_EXPERIENCE.name() + '_' + var4.getId()).replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT),
                  Material.EXPERIENCE_BOTTLE,
                  var4.getName() + ' ' + "Additional Experience (MMOCore)",
                  new String[]{"Additional MMOCore profession " + var4.getName() + " experience in %."},
                  new String[]{"!block", "all"}
               )
            );
         MMOItems.plugin.getStats().register(new RequiredProfession(var4));
      }
   }

   @Override
   public void refreshStats(PlayerData var1) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new MMOCoreHook.MMOCoreRPGPlayer(var1);
   }

   @EventHandler
   public void updateInventoryOnLevelUp(PlayerLevelUpEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void updateInventoryOnClassChange(PlayerChangeClassEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void updateInventoryOnLoad(SynchronizedDataLoadEvent var1) {
      if (var1.getManager().getOwningPlugin().equals(MMOCore.plugin) && PlayerData.has(var1.getHolder().getPlayer())) {
         PlayerData.get(var1.getHolder().getPlayer()).getInventory().scheduleUpdate();
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleVanillaRepairs(RepairItemEvent var1) {
      ExperienceSourceManager<RepairItemExperienceSource> var2 = MMOCore.plugin.experience.getManager(RepairItemExperienceSource.class);
      if (var2 != null) {
         ItemStack var3 = var1.getTargetItem().getItem();
         if (MMOCore.plugin.smithingManager.hasExperience(var3.getType())) {
            Player var4 = var1.getPlayer();
            net.Indyuce.mmocore.api.player.PlayerData var5 = net.Indyuce.mmocore.api.player.PlayerData.get(var4);
            int var6 = Math.min(var1.getRepaired(), ((Damageable)var3.getItemMeta()).getDamage());

            for (RepairItemExperienceSource var8 : var2.getSources()) {
               if (var8.matches(var5, var3)) {
                  double var9 = MMOCore.plugin.smithingManager.getBaseExperience(var3.getType()) * var6 / 100.0;
                  var8.getDispenser().giveExperience(var5, var9, var5.getPlayer().getLocation(), EXPSource.SOURCE);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleCustomRepairs(ItemCustomRepairEvent var1) {
      ExperienceSourceManager<RepairItemExperienceSource> var2 = MMOCore.plugin.experience.getManager(RepairItemExperienceSource.class);
      if (var2 != null) {
         ItemStack var3 = var1.getSourceItem().getNBTItem().getItem();
         if (MMOCore.plugin.smithingManager.hasExperience(var3.getType())) {
            Player var4 = var1.getPlayer();
            net.Indyuce.mmocore.api.player.PlayerData var5 = net.Indyuce.mmocore.api.player.PlayerData.get(var4);
            int var6 = Math.min(var1.getDurabilityIncrease(), var1.getSourceItem().getMaxDurability() - var1.getSourceItem().getDurability());

            for (RepairItemExperienceSource var8 : var2.getSources()) {
               if (var8.matches(var5, var3)) {
                  double var9 = MMOCore.plugin.smithingManager.getBaseExperience(var3.getType()) * var6 / 100.0;
                  var8.getDispenser().giveExperience(var5, var9, var5.getPlayer().getLocation(), EXPSource.SOURCE);
               }
            }
         }
      }
   }

   public static class MMOCoreRPGPlayer extends RPGPlayer {
      private final net.Indyuce.mmocore.api.player.PlayerData data;

      public MMOCoreRPGPlayer(PlayerData var1) {
         super(var1);
         this.data = net.Indyuce.mmocore.api.player.PlayerData.get(var1.getPlayer());
      }

      public net.Indyuce.mmocore.api.player.PlayerData getData() {
         return this.data;
      }

      @Override
      public int getLevel() {
         return this.data.getLevel();
      }

      @Override
      public String getClassName() {
         return this.data.getProfess().getName();
      }

      @Override
      public double getMana() {
         return this.data.getMana();
      }

      @Override
      public double getStamina() {
         return this.data.getStamina();
      }

      @Override
      public void setMana(double var1) {
         this.data.setMana(var1);
      }

      @Override
      public void setStamina(double var1) {
         this.data.setStamina(var1);
      }

      @Override
      public void giveMana(double var1) {
         this.data.giveMana(var1, UpdateReason.OTHER);
      }

      @Override
      public void giveStamina(double var1) {
         this.data.giveStamina(var1, UpdateReason.OTHER);
      }
   }
}
