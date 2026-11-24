package net.Indyuce.mmoitems.comp.rpg;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class DefaultHook implements RPGHandler, Listener {
   @Override
   public void refreshStats(PlayerData var1) {
   }

   @EventHandler
   public void a(PlayerLevelChangeEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new DefaultHook.DefaultRPGPlayer(var1);
   }

   public static class DefaultRPGPlayer extends RPGPlayer {
      public DefaultRPGPlayer(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         return this.getPlayer().getLevel();
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
