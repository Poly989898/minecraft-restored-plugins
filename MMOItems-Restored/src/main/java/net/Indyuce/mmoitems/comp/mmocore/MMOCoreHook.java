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
      for (PlayerAttribute playerAttribute : MMOCore.plugin.attributeManager.getAll()) {
         MMOItems.plugin.getStats().register(new RequiredAttribute(playerAttribute));
         MMOItems.plugin.getStats().register(new ExtraAttribute(playerAttribute));
      }

      for (Profession profession : MMOCore.plugin.professionManager.getAll()) {
         MMOItems.plugin
            .getStats()
            .register(
               new DoubleStat(
                  (StatType.ADDITIONAL_EXPERIENCE.name() + '_' + profession.getId()).replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT),
                  Material.EXPERIENCE_BOTTLE,
                  profession.getName() + ' ' + "Additional Experience (MMOCore)",
                  new String[]{"Additional MMOCore profession " + profession.getName() + " experience in %."},
                  new String[]{"!block", "all"}
               )
            );
         MMOItems.plugin.getStats().register(new RequiredProfession(profession));
      }
   }

   @Override
   public void refreshStats(PlayerData playerData) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new MMOCoreHook.MMOCoreRPGPlayer(playerData);
   }

   @EventHandler
   public void updateInventoryOnLevelUp(PlayerLevelUpEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void updateInventoryOnClassChange(PlayerChangeClassEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void updateInventoryOnLoad(SynchronizedDataLoadEvent event) {
      if (event.getManager().getOwningPlugin().equals(MMOCore.plugin) && PlayerData.has(event.getHolder().getPlayer())) {
         PlayerData.get(event.getHolder().getPlayer()).getInventory().scheduleUpdate();
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleVanillaRepairs(RepairItemEvent event) {
      ExperienceSourceManager<RepairItemExperienceSource> manager = MMOCore.plugin.experience.getManager(RepairItemExperienceSource.class);
      if (manager != null) {
         ItemStack item = event.getTargetItem().getItem();
         if (MMOCore.plugin.smithingManager.hasExperience(item.getType())) {
            Player player = event.getPlayer();
            net.Indyuce.mmocore.api.player.PlayerData playerData = net.Indyuce.mmocore.api.player.PlayerData.get(player);
            int count = Math.min(event.getRepaired(), ((Damageable)item.getItemMeta()).getDamage());

            for (RepairItemExperienceSource source : manager.getSources()) {
               if (source.matches(playerData, item)) {
                  double value = MMOCore.plugin.smithingManager.getBaseExperience(item.getType()) * count / 100.0;
                  source.getDispenser().giveExperience(playerData, value, playerData.getPlayer().getLocation(), EXPSource.SOURCE);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleCustomRepairs(ItemCustomRepairEvent event) {
      ExperienceSourceManager<RepairItemExperienceSource> manager = MMOCore.plugin.experience.getManager(RepairItemExperienceSource.class);
      if (manager != null) {
         ItemStack item = event.getSourceItem().getNBTItem().getItem();
         if (MMOCore.plugin.smithingManager.hasExperience(item.getType())) {
            Player player = event.getPlayer();
            net.Indyuce.mmocore.api.player.PlayerData playerData = net.Indyuce.mmocore.api.player.PlayerData.get(player);
            int count = Math.min(event.getDurabilityIncrease(), event.getSourceItem().getMaxDurability() - event.getSourceItem().getDurability());

            for (RepairItemExperienceSource source : manager.getSources()) {
               if (source.matches(playerData, item)) {
                  double value = MMOCore.plugin.smithingManager.getBaseExperience(item.getType()) * count / 100.0;
                  source.getDispenser().giveExperience(playerData, value, playerData.getPlayer().getLocation(), EXPSource.SOURCE);
               }
            }
         }
      }
   }

   public static class MMOCoreRPGPlayer extends RPGPlayer {
      private final net.Indyuce.mmocore.api.player.PlayerData data;

      public MMOCoreRPGPlayer(PlayerData playerData) {
         super(playerData);
         this.data = net.Indyuce.mmocore.api.player.PlayerData.get(playerData.getPlayer());
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
      public void setMana(double value) {
         this.data.setMana(value);
      }

      @Override
      public void setStamina(double value) {
         this.data.setStamina(value);
      }

      @Override
      public void giveMana(double value) {
         this.data.giveMana(value, UpdateReason.OTHER);
      }

      @Override
      public void giveStamina(double value) {
         this.data.giveStamina(value, UpdateReason.OTHER);
      }
   }
}
