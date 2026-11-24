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

      for (Stats var4 : Stats.values()) {
         String var5 = UtilityMethods.caseOnWords(var4.name().toLowerCase());
         DoubleStat var6 = new DoubleStat(
            "ADDITIONAL_" + var4.name(),
            Material.BOOK,
            "Additional " + var5,
            new String[]{"Additional " + var5 + " (AuraSkills)"},
            new String[]{"!miscellaneous", "!block", "all"}
         );
         this.statExtra.put(var4, var6);
         MMOItems.plugin.getStats().register(var6);
      }

      for (Skills var10 : Skills.values()) {
         MMOItems.plugin.getStats().register(new AuraSkillsHook.RequiredProfessionStat(var10));
      }
   }

   @EventHandler
   public void a(SkillLevelUpEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.isOnline()) {
         PlayerData.get(var2).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData var1) {
      SkillsUser var2 = this.aSkills.getUser(var1.getPlayer().getUniqueId());
      var2.addTraitModifier(new TraitModifier("mmoitems_max_mana", Traits.MAX_MANA, var1.getStats().getStat(ItemStats.MAX_MANA)));
      double var3 = var2.getMaxMana();
      if (var2.getMana() > var3) {
         var2.setMana(var3);
      }

      this.statExtra
         .forEach(
            (var2x, var3x) -> this.aSkills
               .getUser(var1.getPlayer().getUniqueId())
               .addStatModifier(new StatModifier("mmoitems_" + var2x.name(), var2x, var1.getStats().getStat((ItemStat<?, ?>)var3x)))
         );
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new EmptyRPGPlayer(var1);
   }

   @EventHandler
   public void a(UserLoadEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = PlayerData.get(var2);
      var3.setRPGPlayer(new AuraSkillsHook.PlayerWrapper(var3, var1.getUser()));
   }

   private static class PlayerWrapper extends RPGPlayer {
      private final SkillsUser info;

      public PlayerWrapper(PlayerData var1, SkillsUser var2) {
         super(var1);
         this.info = var2;
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
      public void setMana(double var1) {
         this.info.setMana(var1);
      }

      @Override
      public void setStamina(double var1) {
         this.getPlayer().setFoodLevel((int)var1);
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
      public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
         int var4 = var2.getInteger(this.getNBTPath());
         if (var4 <= 0) {
            return true;
         }

         int var5 = AuraSkillsHook.this.aSkills.getUser(var1.getPlayer().getUniqueId()).getSkillLevel(this.skill);
         if (var5 < var4 && !var1.getPlayer().hasPermission("mmoitems.bypass.level")) {
            if (var3) {
               Message.NOT_ENOUGH_PROFESSION.format(ChatColor.RED, "#profession#", this.skill.getDisplayName(Locale.getDefault())).send(var1.getPlayer());
               var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
            }

            return false;
         } else {
            return true;
         }
      }
   }
}
