package io.lumine.mythic.lib.player.potion;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.modifier.ModifierMap;
import io.lumine.mythic.lib.version.VPotionEffectType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Deprecated
public class PermanentPotionEffectMap extends ModifierMap<PermanentPotionEffect> {
   private final Map<PotionEffectType, Integer> maxAmplifier = new HashMap<>();

   public PermanentPotionEffectMap(MMOPlayerData playerData) {
      super(playerData);
   }

   public PermanentPotionEffect addModifier(PermanentPotionEffect effect) {
      PermanentPotionEffect prev = super.addModifier(effect);
      PotionEffectType key = effect.getEffect();
      this.maxAmplifier.put(key, Math.max(this.maxAmplifier.getOrDefault(key, -1), effect.getAmplifier()));
      return prev;
   }

   public PermanentPotionEffect removeModifier(UUID uniqueId) {
      PermanentPotionEffect removed = (PermanentPotionEffect)super.removeModifier(uniqueId);
      if (removed != null) {
         this.updateHighestLevel(removed.getEffect());
      }

      return removed;
   }

   public void applyPermanentEffects() {
      Validate.isTrue(this.getPlayerData().isOnline(), "Player is offline");
      Player player = this.getPlayerData().getPlayer();
      this.maxAmplifier.forEach((type, level) -> {
         int currentAmplifier = player.hasPotionEffect(type) ? player.getPotionEffect(type).getAmplifier() : -1;
         if (level >= currentAmplifier) {
            player.addPotionEffect(new PotionEffect(type, this.getEffectDuration(type), level, false, false));
         }
      });
   }

   public int getHighestLevel(PotionEffectType type) {
      int amplifier = -1;

      for (PermanentPotionEffect perm : this.getModifiers()) {
         if (perm.getEffect() == type) {
            amplifier = Math.max(amplifier, perm.getAmplifier());
         }
      }

      return amplifier;
   }

   private void updateHighestLevel(PotionEffectType type) {
      int max = this.getHighestLevel(type);
      if (max == -1) {
         this.maxAmplifier.remove(type);
      } else {
         this.maxAmplifier.put(type, max);
      }
   }

   private int getEffectDuration(PotionEffectType type) {
      return !type.equals(PotionEffectType.NIGHT_VISION) && !type.equals(VPotionEffectType.NAUSEA.get())
         ? (type.equals(PotionEffectType.BLINDNESS) ? 140 : 80)
         : 260;
   }
}
