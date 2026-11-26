package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

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
