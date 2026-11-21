package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;
import java.util.List;

@NmsClassTarget("NBTTagByteArray")
public interface NBTTagByteArray extends Shadow, NBTBase {
   static NBTTagByteArray create(byte[] data) {
      return ShadowFactory.global().constructShadow(NBTTagByteArray.class, data);
   }

   static NBTTagByteArray create(List<Byte> data) {
      return ShadowFactory.global().constructShadow(NBTTagByteArray.class, data);
   }

   @ObfuscatedTarget({@Mapping(
         value = "c",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "c",
         version = PackageVersion.v1_8_R3
      )})
   byte[] getByteArray();
}
