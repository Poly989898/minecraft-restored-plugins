package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
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
