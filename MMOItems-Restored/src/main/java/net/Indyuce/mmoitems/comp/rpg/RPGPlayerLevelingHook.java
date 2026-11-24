package net.Indyuce.mmoitems.comp.rpg;

import me.baks.rpl.api.API;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;

public class RPGPlayerLevelingHook implements RPGHandler {
   @Override
   public void refreshStats(PlayerData var1) {
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new RPGPlayerLevelingHook.RPGPlayerLevelingPlayer(var1);
   }

   public static class RPGPlayerLevelingPlayer extends RPGPlayer {
      public RPGPlayerLevelingPlayer(PlayerData var1) {
         super(var1);
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
      public void setMana(double var1) {
         new API().setMana(this.getPlayer(), (int)var1);
      }

      @Override
      public void setStamina(double var1) {
         new API().setPower(this.getPlayer(), (int)var1);
      }
   }
}
