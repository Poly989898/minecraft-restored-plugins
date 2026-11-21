package io.lumine.mythic.core.drops;

import java.util.UUID;

public abstract class PityManager {
   public abstract void addPity(UUID uuid, double value);

   public abstract void addPity(UUID uuid, double value, String str);

   public abstract void resetPity(UUID uuid);

   public abstract void resetPity(UUID uuid, String str);

   public abstract void setPity(UUID uuid, double value);

   public abstract void setPity(UUID uuid, double value, String str);

   public abstract void removePity(UUID uuid, double value);

   public abstract void removePity(UUID uuid, double value, String str);

   public abstract double getPity(UUID uuid);

   public abstract double getPity(UUID uuid, String str);
}
