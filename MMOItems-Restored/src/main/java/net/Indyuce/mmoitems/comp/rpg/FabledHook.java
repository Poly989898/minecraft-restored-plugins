package net.Indyuce.mmoitems.comp.rpg;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.PlayerLevelUpEvent;

public class FabledHook implements RPGHandler, Listener {
   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new FabledHook.PlayerWrapper(var1);
   }

   @EventHandler
   public void b(PlayerLevelUpEvent var1) {
      PlayerData.get(var1.getPlayerData().getPlayer()).getInventory().scheduleUpdate();
   }

   @Override
   public void refreshStats(PlayerData var1) {
   }

   private static class PlayerWrapper extends RPGPlayer {
      public PlayerWrapper(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         studio.magemonkey.fabled.api.player.PlayerData var1 = Fabled.getData(this.getPlayer());
         return var1.hasClass() ? var1.getMainClass().getLevel() : 0;
      }

      @Override
      public String getClassName() {
         studio.magemonkey.fabled.api.player.PlayerData var1 = Fabled.getData(this.getPlayer());
         return var1.hasClass() ? var1.getMainClass().getData().getName() : "";
      }

      @Override
      public double getMana() {
         studio.magemonkey.fabled.api.player.PlayerData var1 = Fabled.getData(this.getPlayer());
         return var1.hasClass() ? var1.getMana() : 0.0;
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double var1) {
         studio.magemonkey.fabled.api.player.PlayerData var3 = Fabled.getData(this.getPlayer());
         if (var3.hasClass()) {
            var3.setMana(var1);
         }
      }

      @Override
      public void setStamina(double var1) {
         this.getPlayer().setFoodLevel((int)var1);
      }
   }
}
