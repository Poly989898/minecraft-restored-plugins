package net.Indyuce.mmoitems.comp.rpg;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.skills.api.events.SkillLevelUpEvent;
import org.skills.data.managers.SkilledPlayer;
import org.skills.main.SkillsPro;

public class SkillsProHook implements RPGHandler, Listener {
   @EventHandler
   public void a(SkillLevelUpEvent event) {
      Player player = event.getPlayer();
      if (player.isOnline()) {
         PlayerData.get(player).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData playerData) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new SkillsProHook.SkillsPlayer(playerData);
   }

   public static class SkillsPlayer extends RPGPlayer {
      public SkillsPlayer(PlayerData playerData) {
         super(playerData);
      }

      @Override
      public int getLevel() {
         SkilledPlayer data = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         return data.getLevel();
      }

      @Override
      public String getClassName() {
         SkilledPlayer data = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         return ChatColor.stripColor(data.getSkill().getDisplayName());
      }

      @Override
      public double getMana() {
         SkilledPlayer data = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         return data.getEnergy();
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double value) {
         SkilledPlayer data = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         data.setEnergy(value);
      }

      @Override
      public void setStamina(double value) {
         this.getPlayer().setFoodLevel((int)value);
      }
   }
}
