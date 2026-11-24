package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Objects;
import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.block.BlockInfo.RegeneratingBlock;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class MMOItemsBlockType implements BlockType {
   private final int id;

   public MMOItemsBlockType(MMOLineConfig var1) {
      var1.validate(new String[]{"id"});
      this.id = var1.getInt("id");
   }

   public MMOItemsBlockType(CustomBlock var1) {
      this.id = var1.getId();
   }

   public int getBlockId() {
      return this.id;
   }

   public static boolean matches(Block var0) {
      return MMOItems.plugin.getCustomBlocks().isMushroomBlock(var0.getType());
   }

   @NotNull
   public String display() {
      return "MMOItems{" + this.id + "}";
   }

   public void place(RegeneratingBlock var1) {
      Location var2 = var1.getLocation();
      CustomBlock var3 = MMOItems.plugin.getCustomBlocks().getBlock(this.id);
      var2.getBlock().setType(var3.getState().getType());
      var2.getBlock().setBlockData(var3.getState().getBlockData());
   }

   public void regenerate(RegeneratingBlock var1) {
      this.place(var1);
   }

   public boolean breakRestrictions(Block var1) {
      return true;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MMOItemsBlockType var2 = (MMOItemsBlockType)var1;
         return this.id == var2.id;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
