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
   public void a(SkillLevelUpEvent event) {
      OfflinePlayer player = event.getPlayer();
      if (player.isOnline()) {
         PlayerData.get(player).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData playerData) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new SkillsHook.SkillsPlayer(playerData);
   }

   public static class SkillsPlayer extends RPGPlayer {
      public SkillsPlayer(PlayerData playerData) {
         super(playerData);
      }

      @Override
      public int getLevel() {
         PlayerInfo playerInfo = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         return playerInfo.getLevel();
      }

      @Override
      public String getClassName() {
         PlayerInfo playerInfo = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         return playerInfo.getSkill().getLanguageName().getDefault();
      }

      @Override
      public double getMana() {
         PlayerInfo playerInfo = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         return playerInfo.getActiveStatType(ScalingType.ENERGY);
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double value) {
         PlayerInfo playerInfo = Skills.get().getPlayerDataManager().loadPlayerInfo(this.getPlayer());
         playerInfo.setActiveStatType(ScalingType.ENERGY, value);
      }

      @Override
      public void setStamina(double value) {
         this.getPlayer().setFoodLevel((int)value);
      }
   }
}
