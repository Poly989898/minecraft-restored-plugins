package net.Indyuce.mmoitems.stat.data;

import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;

public class MaterialData implements StatData, RandomStatData<MaterialData> {
   private Material material;

   public MaterialData(Material material) {
      Validate.notNull(material, "Material must not be null");
      this.material = material;
   }

   public void setMaterial(Material material) {
      Validate.notNull(material, "Material must not be null");
      this.material = material;
   }

   public Material getMaterial() {
      return this.material;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   public MaterialData randomize(MMOItemBuilder mmoItemBuilder) {
      return this;
   }
}
