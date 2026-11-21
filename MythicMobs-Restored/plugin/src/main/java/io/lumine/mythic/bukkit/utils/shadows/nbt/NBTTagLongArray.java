package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Field;
import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;
import java.util.List;

@NmsClassTarget("NBTTagLongArray")
public interface NBTTagLongArray extends Shadow, NBTBase {
   static NBTTagLongArray create(long[] data) {
      return ShadowFactory.global().constructShadow(NBTTagLongArray.class, data);
   }

   static NBTTagLongArray create(List<Long> data) {
      return ShadowFactory.global().constructShadow(NBTTagLongArray.class, data);
   }

   @Field
   @ObfuscatedTarget({@Mapping(
         value = "b",
         version = PackageVersion.v1_12_R1
      )})
   long[] getLongArray();
}
