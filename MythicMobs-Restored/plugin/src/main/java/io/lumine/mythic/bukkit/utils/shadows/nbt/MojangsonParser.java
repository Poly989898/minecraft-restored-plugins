package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.Static;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;

@NmsClassTarget("MojangsonParser")
public interface MojangsonParser extends Shadow {
   @Static
   @ObfuscatedTarget({@Mapping(
         value = "parse",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "parse",
         version = PackageVersion.v1_8_R3
      )})
   NBTTagCompound parse(String str);
}
