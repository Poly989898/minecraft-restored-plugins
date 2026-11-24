package net.Indyuce.mmoitems.comp.rpg;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.eunoians.mcrpg.api.events.mcrpg.McRPGPlayerLevelChangeEvent;
import us.eunoians.mcrpg.players.PlayerManager;

public class McRPGHook implements RPGHandler, Listener {
   @EventHandler
   public void a(McRPGPlayerLevelChangeEvent var1) {
      PlayerData.get(var1.getMcRPGPlayer().getOfflineMcRPGPlayer()).getInventory().scheduleUpdate();
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new McRPGHook.McRPGPlayer(var1);
   }

   @Override
   public void refreshStats(PlayerData var1) {
   }

   public static class McRPGPlayer extends RPGPlayer {
      public McRPGPlayer(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         try {
            return PlayerManager.getPlayer(this.getPlayer().getUniqueId()).getPowerLevel();
         } catch (Exception var2) {
            return 0;
         }
      }

      @Override
      public String getClassName() {
         return "";
      }

      @Override
      public double getMana() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public double getStamina() {
         return 0.0;
      }

      @Override
      public void setMana(double var1) {
         this.getPlayer().setFoodLevel((int)var1);
      }

      @Override
      public void setStamina(double var1) {
      }
   }
}
