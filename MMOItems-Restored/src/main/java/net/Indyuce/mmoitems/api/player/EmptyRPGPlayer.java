package net.Indyuce.mmoitems.api.player;

public class EmptyRPGPlayer extends RPGPlayer {
   public EmptyRPGPlayer(PlayerData playerData) {
      super(playerData);
   }

   @Override
   public int getLevel() {
      return 0;
   }

   @Override
   public String getClassName() {
      return "";
   }

   @Override
   public double getMana() {
      return 0.0;
   }

   @Override
   public double getStamina() {
      return 0.0;
   }

   @Override
   public void setMana(double value) {
   }

   @Override
   public void setStamina(double value) {
   }
}
