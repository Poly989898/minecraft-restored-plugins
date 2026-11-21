package io.lumine.mythic.core.volatilecode.v1_20_R1;

import io.lumine.mythic.api.adapters.AbstractBiome;
import io.lumine.mythic.api.volatilecode.handlers.VolatileWorldHandler;
import lombok.Generated;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeBase;

public class VolatileBiomeImpl extends AbstractBiome {
   private final int id;
   private final String key;
   private final BiomeBase biome;
   private final Holder<BiomeBase> holder;

   public VolatileBiomeImpl(VolatileWorldHandler volatileWorldHandler, int id, String key, Holder<BiomeBase> holder) {
      super(key);
      this.id = id;
      this.key = key;
      this.holder = holder;
      this.biome = (BiomeBase)holder.a();
   }

   @Override
   public String getType() {
      return this.key;
   }

   @Generated
   public int getId() {
      return this.id;
   }

   @Generated
   @Override
   public String getKey() {
      return this.key;
   }

   @Generated
   public BiomeBase getBiome() {
      return this.biome;
   }

   @Generated
   public Holder<BiomeBase> getHolder() {
      return this.holder;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof VolatileBiomeImpl other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getId() != other.getId()) {
         return false;
      } else {
         Object this$key = this.getKey();
         Object other$key = other.getKey();
         if (this$key == null ? other$key == null : this$key.equals(other$key)) {
            Object this$biome = this.getBiome();
            Object other$biome = other.getBiome();
            if (this$biome == null ? other$biome == null : this$biome.equals(other$biome)) {
               Object this$holder = this.getHolder();
               Object other$holder = other.getHolder();
               return this$holder == null ? other$holder == null : this$holder.equals(other$holder);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof VolatileBiomeImpl;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getId();
      Object $key = this.getKey();
      result = result * 59 + ($key == null ? 43 : $key.hashCode());
      Object $biome = this.getBiome();
      result = result * 59 + ($biome == null ? 43 : $biome.hashCode());
      Object $holder = this.getHolder();
      return result * 59 + ($holder == null ? 43 : $holder.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "VolatileBiomeImpl(id=" + this.getId() + ", key=" + this.getKey() + ", biome=" + this.getBiome() + ", holder=" + this.getHolder() + ")";
   }
}
