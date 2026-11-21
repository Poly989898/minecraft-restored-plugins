package io.lumine.mythic.core.volatilecode.v1_21_R1;

import io.lumine.mythic.api.adapters.AbstractStructure;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.c;
import net.minecraft.world.level.levelgen.structure.Structure;

public class VolatileStructureImpl extends AbstractStructure {
   private final String key;
   private final int id;
   private final Structure structure;
   private final Holder<Structure> holder;

   public VolatileStructureImpl(VolatileWorldHandler volatileWorldHandler, int id, String key, c<Structure> holder, Structure structure) {
      super(key);
      this.key = key;
      this.id = id;
      this.holder = holder;
      this.structure = structure;
   }
}
