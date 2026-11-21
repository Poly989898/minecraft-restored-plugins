package com.nexomc.nexo.mechanics.misc.armor_effects;

import kotlin.Metadata;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\t\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000b¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffect;", "Lorg/bukkit/potion/PotionEffect;", "effect", "requiresFullSet", "", "resetOnUnequip", "<init>", "(Lorg/bukkit/potion/PotionEffect;ZZ)V", "getEffect", "()Lorg/bukkit/potion/PotionEffect;", "getRequiresFullSet", "()Z", "getResetOnUnequip", "com.nexomc:core"}
)
public final class ArmorEffect extends PotionEffect {
   @NotNull
   private final PotionEffect effect;
   private final boolean requiresFullSet;
   private final boolean resetOnUnequip;

   public ArmorEffect(@NotNull PotionEffect effect, boolean requiresFullSet, boolean resetOnUnequip) {
      super(effect.getType(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
      this.effect = effect;
      this.requiresFullSet = requiresFullSet;
      this.resetOnUnequip = resetOnUnequip;
   }

   @NotNull
   public final PotionEffect getEffect() {
      return this.effect;
   }

   public final boolean getRequiresFullSet() {
      return this.requiresFullSet;
   }

   public final boolean getResetOnUnequip() {
      return this.resetOnUnequip;
   }
}
