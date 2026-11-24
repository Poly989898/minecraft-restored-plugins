package net.Indyuce.mmoitems.comp.rpg;

import me.leothepro555.skills.database.managers.PlayerInfo;
import me.leothepro555.skills.events.SkillLevelUpEvent;
import me.leothepro555.skills.main.Skills;
import me.leothepro555.skilltype.ScalingType;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillsHook implements RPGHandler, Listener {
   @EventHandler
   public void a(SkillLevelUpEvent var1) {
      OfflinePlayer var2 = var1.getPlayer();
      if (var2.isOnline()) {
         PlayerData.get(var2).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData var1) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new SkillsHook.SkillsPlayer(var1);
   }

   public static class SkillsPlayer extends RPGPlayer {
      public SkillsPlayer(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         PlayerInfo var1 = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         return var1.getLevel();
      }

      @Override
      public String getClassName() {
         PlayerInfo var1 = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         return var1.getSkill().getLanguageName().getDefault();
      }

      @Override
      public double getMana() {
         PlayerInfo var1 = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         return var1.getActiveStatType(ScalingType.ENERGY);
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double var1) {
         PlayerInfo var3 = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         var3.setActiveStatType(ScalingType.ENERGY, var1);
      }

      @Override
      public void setStamina(double var1) {
         this.getPlayer().setFoodLevel((int)var1);
      }
   }
}
