package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
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
