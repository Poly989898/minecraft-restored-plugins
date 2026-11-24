package io.lumine.mythic.lib.player.cooldown;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class CooldownMap {
   private final Map<String, CooldownInfo> map = new HashMap<>();

   public CooldownInfo applyCooldown(CooldownObject obj, double cooldown) {
      return this.applyCooldown(obj.getCooldownPath(), cooldown);
   }

   public CooldownInfo applyCooldown(String path, double cooldown) {
      String key = UtilityMethods.enumName(path);
      CooldownInfo current = this.map.get(key);
      if (current != null && current.getRemaining() >= cooldown * 1000.0) {
         return current;
      }

      current = new CooldownInfo(cooldown);
      this.map.put(key, current);
      return current;
   }

   @Nullable
   public CooldownInfo getInfo(CooldownObject obj) {
      return this.getInfo(obj.getCooldownPath());
   }

   @Nullable
   public CooldownInfo getInfo(String path) {
      return this.map.get(UtilityMethods.enumName(path));
   }

   public double getCooldown(CooldownObject obj) {
      return this.getCooldown(obj.getCooldownPath());
   }

   public double getCooldown(String path) {
      CooldownInfo info = this.map.get(UtilityMethods.enumName(path));
      return info == null ? 0.0 : info.getRemaining() / 1000.0;
   }

   public boolean isOnCooldown(CooldownObject obj) {
      return this.isOnCooldown(obj.getCooldownPath());
   }

   public boolean isOnCooldown(String path) {
      CooldownInfo found = this.map.get(UtilityMethods.enumName(path));
      return found != null && !found.hasEnded();
   }

   public void resetCooldown(CooldownObject obj) {
      this.resetCooldown(obj.getCooldownPath());
   }

   public void resetCooldown(String path) {
      this.map.remove(UtilityMethods.enumName(path));
   }
}
