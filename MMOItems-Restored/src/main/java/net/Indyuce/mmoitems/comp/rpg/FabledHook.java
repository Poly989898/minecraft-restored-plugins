package net.Indyuce.mmoitems.comp.rpg;

import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.event.PlayerLevelUpEvent;

public class FabledHook implements RPGHandler, Listener {
   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new FabledHook.PlayerWrapper(playerData);
   }

   @EventHandler
   public void b(PlayerLevelUpEvent event) {
      PlayerData.get(event.getPlayerData().getPlayer()).getInventory().scheduleUpdate();
   }

   @Override
   public void refreshStats(PlayerData playerData) {
   }

   private static class PlayerWrapper extends RPGPlayer {
      public PlayerWrapper(PlayerData playerData) {
         super(playerData);
      }

      @Override
      public int getLevel() {
         studio.magemonkey.fabled.api.player.PlayerData data = Fabled.getData(this.getPlayer());
         return data.hasClass() ? data.getMainClass().getLevel() : 0;
      }

      @Override
      public String getClassName() {
         studio.magemonkey.fabled.api.player.PlayerData data = Fabled.getData(this.getPlayer());
         return data.hasClass() ? data.getMainClass().getData().getName() : "";
      }

      @Override
      public double getMana() {
         studio.magemonkey.fabled.api.player.PlayerData data = Fabled.getData(this.getPlayer());
         return data.hasClass() ? data.getMana() : 0.0;
      }

      @Override
      public double getStamina() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double value) {
         studio.magemonkey.fabled.api.player.PlayerData data = Fabled.getData(this.getPlayer());
         if (data.hasClass()) {
            data.setMana(value);
         }
      }

      @Override
      public void setStamina(double value) {
         this.getPlayer().setFoodLevel((int)value);
      }
   }
}
