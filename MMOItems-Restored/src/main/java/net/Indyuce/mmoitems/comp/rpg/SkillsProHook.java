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
   public void a(SkillLevelUpEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.isOnline()) {
         PlayerData.get(var2).getInventory().scheduleUpdate();
      }
   }

   @Override
   public void refreshStats(PlayerData var1) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new SkillsProHook.SkillsPlayer(var1);
   }

   public static class SkillsPlayer extends RPGPlayer {
      public SkillsPlayer(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         SkilledPlayer var1 = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         return var1.getLevel();
      }

      @Override
      public String getClassName() {
         SkilledPlayer var1 = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         return ChatColor.stripColor(var1.getSkill().getDisplayName());
      }

      @Override
      public double getMana() {
         SkilledPlayer var1 = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         return var1.getEnergy();
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double var1) {
         SkilledPlayer var3 = (SkilledPlayer)SkillsPro.get().getPlayerDataManager().getData(this.getPlayerData().getPlayer().getUniqueId());
         var3.setEnergy(var1);
      }

      @Override
      public void setStamina(double var1) {
         this.getPlayer().setFoodLevel((int)var1);
      }
   }
}
