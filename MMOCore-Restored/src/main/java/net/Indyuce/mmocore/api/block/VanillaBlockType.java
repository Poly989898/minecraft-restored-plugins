package net.Indyuce.mmocore.api.block;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

public class VanillaBlockType implements BlockType {
   private final Material type;
   private final int age;

   public VanillaBlockType(MMOLineConfig config) {
      config.validate(new String[]{"type"});
      this.type = Material.valueOf(config.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.age = config.getInt("age", 0);
      Validate.isTrue(this.age >= 0 && this.age < 8, "Age must be between 0 and 7");
   }

   public VanillaBlockType(Block block) {
      this.type = block.getType();
      this.age = 0;
   }

   public Material getType() {
      return this.type;
   }

   @Override
   public void place(BlockInfo.RegeneratingBlock block) {
      Location loc = block.getLocation();
      block.getLocation().getBlock().setType(this.type);
      BlockData state = block.getLocation().getBlock().getBlockData();
      if (this.age > 0 && state instanceof Ageable) {
         ((Ageable)state).setAge(this.age);
         loc.getBlock().setBlockData(state);
      }
   }

   @Override
   public void regenerate(BlockInfo.RegeneratingBlock block) {
      Location loc = block.getLocation();
      loc.getBlock().setType(this.type);
      loc.getBlock().setBlockData(block.getBlockData());
   }

   @Override
   public String display() {
      return "Vanilla{" + this.type.name() + "}";
   }

   @Override
   public boolean breakRestrictions(Block block) {
      return this.age == 0 || block.getBlockData() instanceof Ageable && ((Ageable)block.getBlockData()).getAge() >= this.age;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VanillaBlockType that = (VanillaBlockType)o;
         return this.type == that.type;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type);
   }
}
