package io.lumine.mythic.lib.player.particle;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.modifier.ModifierMap;

@Deprecated
public class ParticleEffectMap extends ModifierMap<ParticleEffect> {
   public ParticleEffectMap(MMOPlayerData playerData) {
      super(playerData);
   }
}
