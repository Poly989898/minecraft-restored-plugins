package io.lumine.mythic.lib.damage.packet;

import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class MeleeDamagePacket extends DamagePacket {
   public MeleeDamagePacket(double value, @NotNull DamageType... types) {
      super(value, types);
   }
}
