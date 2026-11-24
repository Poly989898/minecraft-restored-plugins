package net.Indyuce.mmoitems.comp.rpg;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent;
import dev.aurelium.auraskills.api.event.user.UserLoadEvent;
import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.api.stat.StatModifier;
import dev.aurelium.auraskills.api.stat.Stats;
import dev.aurelium.auraskills.api.trait.TraitModifier;
import dev.aurelium.auraskills.api.trait.Traits;
import dev.aurelium.auraskills.api.user.SkillsUser;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.EmptyRPGPlayer;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.RequiredLevelStat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuraSkillsHook implements RPGHandler, Listener {
   private AuraSkillsApi aSkills;
   private final Map<Stats, ItemStat> statExtra = new HashMap<>();
   private static final String MODIFIER_KEY_PREFIX = "mmoitems_";

   public AuraSkillsHook() {
      this.aSkills = AuraSkillsApi.get();

      for (Stats stats : Stats.values()) {
         String text = UtilityMethods.caseOnWords(stats.name().toLowerCase());
         DoubleStat doubleStat = new DoubleStat(
            "ADDITIONAL_" + stats.name(),
            Material.BOOK,
            "Additional " + text,
            new String[]{"Additional " + text + " (AuraSkills)"},
            new String[]{"!miscellaneous", "!block", "all"}
         );
         this.statExtra.put(stats, doubleStat);
         MMOItems.plugin.getStats().register(doubleStat);
      }

      for (Skills skills : Skills.values()) {
         MMOItems.plugin.getStats().register(new AuraSkillsHook.RequiredProfessionStat(skills));
      }
   }

   @EventHandler
   public void a(SkillLevelUpEvent event) {
      Player player = event.getPlayer();
      if (player.isOnline()) {
         PlayerData.get(player).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData playerData) {
      SkillsUser user = this.aSkills.getUser(playerData.getPlayer().getUniqueId());
      user.addTraitModifier(new TraitModifier("mmoitems_max_mana", Traits.MAX_MANA, playerData.getStats().getStat(ItemStats.MAX_MANA)));
      double maxMana = user.getMaxMana();
      if (user.getMana() > maxMana) {
         user.setMana(maxMana);
      }

      this.statExtra
         .forEach(
            (var2x, var3x) -> this.aSkills
               .getUser(playerData.getPlayer().getUniqueId())
               .addStatModifier(new StatModifier("mmoitems_" + var2x.name(), var2x, playerData.getStats().getStat((ItemStat<?, ?>)var3x)))
         );
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new EmptyRPGPlayer(playerData);
   }

   @EventHandler
   public void a(UserLoadEvent event) {
      Player player = event.getPlayer();
      PlayerData playerData = PlayerData.get(player);
      playerData.setRPGPlayer(new AuraSkillsHook.PlayerWrapper(playerData, event.getUser()));
   }

   private static class PlayerWrapper extends RPGPlayer {
      private final SkillsUser info;

      public PlayerWrapper(PlayerData playerData, SkillsUser skillsUser) {
         super(playerData);
         this.info = skillsUser;
      }

      @Override
      public int getLevel() {
         return this.info.getPowerLevel();
      }

      @Override
      public String getClassName() {
         return "";
      }

      @Override
      public double getMana() {
         return this.info.getMana();
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double value) {
         this.info.setMana(value);
      }

      @Override
      public void setStamina(double value) {
         this.getPlayer().setFoodLevel((int)value);
      }
   }

   private class RequiredProfessionStat extends RequiredLevelStat {
      private final Skill skill;

      public RequiredProfessionStat(Skills nullx) {
         super(
            nullx.name(),
            Material.EXPERIENCE_BOTTLE,
            nullx.getDisplayName(Locale.getDefault()),
            new String[]{"Amount of " + nullx.getDisplayName(Locale.getDefault()) + " levels the", "player needs to use the item.", "(AuraSkills)"}
         );
         this.skill = AuraSkillsHook.this.aSkills.getGlobalRegistry().getSkill(nullx.getId());
      }

      @Override
      public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
         int count = nbtItem.getInteger(this.getNBTPath());
         if (count <= 0) {
            return true;
         }

         int skillLevel = AuraSkillsHook.this.aSkills.getUser(rpgPlayer.getPlayer().getUniqueId()).getSkillLevel(this.skill);
         if (skillLevel < count && !rpgPlayer.getPlayer().hasPermission("mmoitems.bypass.level")) {
            if (flag) {
               Message.NOT_ENOUGH_PROFESSION.format(ChatColor.RED, "#profession#", this.skill.getDisplayName(Locale.getDefault())).send(rpgPlayer.getPlayer());
               rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
