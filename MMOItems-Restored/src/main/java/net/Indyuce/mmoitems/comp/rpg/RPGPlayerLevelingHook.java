package net.Indyuce.mmoitems.comp.rpg;

import me.baks.rpl.api.API;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;

public class RPGPlayerLevelingHook implements RPGHandler {
   @Override
   public void refreshStats(PlayerData playerData) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new RPGPlayerLevelingHook.RPGPlayerLevelingPlayer(playerData);
   }

   public static class RPGPlayerLevelingPlayer extends RPGPlayer {
      public RPGPlayerLevelingPlayer(PlayerData playerData) {
         super(playerData);
      }

      @Override
      public int getLevel() {
         return new API().getPlayerLevel(this.getPlayer());
      }

      @Override
      public String getClassName() {
         return "";
      }

      @Override
      public double getMana() {
         return new API().getMana(this.getPlayer());
      }

      @Override
      public double getStamina() {
         return new API().getPower(this.getPlayer());
      }

      @Override
      public void setMana(double value) {
         new API().setMana(this.getPlayer(), (int)value);
      }

      @Override
      public void setStamina(double value) {
         new API().setPower(this.getPlayer(), (int)value);
      }
   }
}
