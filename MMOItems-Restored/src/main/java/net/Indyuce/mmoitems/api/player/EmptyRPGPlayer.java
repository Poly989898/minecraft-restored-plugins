package net.Indyuce.mmoitems.api.player;

public class EmptyRPGPlayer extends RPGPlayer {
   public EmptyRPGPlayer(PlayerData var1) {
      super(var1);
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
   public void setMana(double var1) {
   }

   @Override
   public void setStamina(double var1) {
   }
}
