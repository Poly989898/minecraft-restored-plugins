package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;

public class MaterialData implements StatData, RandomStatData<MaterialData> {
   private Material material;

   public MaterialData(Material var1) {
      Validate.notNull(var1, "Material must not be null");
      this.material = var1;
   }

   public void setMaterial(Material var1) {
      Validate.notNull(var1, "Material must not be null");
      this.material = var1;
   }

   public Material getMaterial() {
      return this.material;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   public MaterialData randomize(MMOItemBuilder var1) {
      return this;
   }
}
