package io.lumine.mythic.api.adapters;

public interface AbstractPlayer extends AbstractEntity {
   boolean hasPermission(String str);

   boolean isInCreativeMode();

   boolean isInSpectatorMode();

   void sendMessage(String str);

   float getExperience();

   void setExperience(float value);

   void hidePlayer(AbstractPlayer abstractPlayer);

   void hideEntity(AbstractEntity abstractEntity);

   boolean canSee(AbstractPlayer abstractPlayer);

   boolean canSee(AbstractEntity abstractEntity);

   boolean isOnline();

   int getLevel();

   void setLevel(int value);

   void setHealthScale(double value);

   void setHealthScaled(boolean flag);

   void setPersonalTime(long value, boolean flag);

   void resetPersonalTime();

   void setPersonalWeather(String str);

   void resetPersonalWeather();

   void setAllowFlight(boolean flag);

   boolean getAllowFlight();

   void showPlayer(AbstractPlayer abstractPlayer);

   void showEntity(AbstractEntity abstractEntity);

   void setFlying(boolean flag);

   void setFlyingSpeed(float value);

   void setWalkSpeed(float value);

   int getFoodLevel();

   void setFoodLevel(int value);

   float getFoodSaturation();

   void setFoodSaturation(float value);

   void setGameMode(AbstractGameMode abstractGameMode);

   AbstractGameMode getGameMode();

   void sendActionBarMessage(String str);

   int getRemainingAir();

   void setRemainingAir(int value);

   int getMaximumAir();

   void setMaximumAir(int value);

   void sendTitle(String str, String str2, int value, int value2, int value3);

   void updateInventory();

   int getFortuneLevel();

   float getItemGroupCooldownPercent(String str);

   boolean isItemGroupOnCooldown(String str);

   void setItemGroupCooldown(String str, int value);

   void removeItemGroupCooldown(String str);
}
