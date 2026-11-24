package net.Indyuce.mmocore.api.block;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Objects;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class SkullBlockType implements BlockType {
   private final String value;

   public SkullBlockType(MMOLineConfig config) {
      config.validate(new String[]{"value"});
      this.value = config.getString("value");
   }

   public SkullBlockType(Block block) {
      this.value = MythicLib.plugin.getVersion().getWrapper().getSkullValue(block);
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public void place(BlockInfo.RegeneratingBlock block) {
      Location loc = block.getLocation();
      loc.getBlock().setType(Material.PLAYER_HEAD);
      if (MMOCoreUtils.isPlayerHead(block.getBlockData().getMaterial())) {
         loc.getBlock().setBlockData(block.getBlockData());
      }

      MythicLib.plugin.getVersion().getWrapper().setSkullValue(loc.getBlock(), this.value);
   }

   @Override
   public void regenerate(BlockInfo.RegeneratingBlock block) {
      Location loc = block.getLocation();
      loc.getBlock().setBlockData(block.getBlockData());
      MythicLib.plugin.getVersion().getWrapper().setSkullValue(loc.getBlock(), this.value);
   }

   @Override
   public String display() {
      return "Skull{" + this.value + "}";
   }

   @Override
   public boolean breakRestrictions(Block block) {
      return true;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SkullBlockType that = (SkullBlockType)o;
         return Objects.equals(this.value, that.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.value);
   }
}
