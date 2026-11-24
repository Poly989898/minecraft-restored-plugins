package net.Indyuce.mmocore.util.item;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class ConfigItem {
   private final String name;
   private final String id;
   private final String texture;
   private final Material material;
   private final List<String> lore;
   private final int damage;
   private final int modeldata;
   private final boolean unbreakable;

   public ConfigItem(ConfigurationSection config) {
      this.id = UtilityMethods.enumName(config.getName());
      this.name = config.getString("name");
      this.lore = config.getStringList("lore");
      String itemFormat = Objects.requireNonNull(config.getString("item"), "Could not find item material");
      Validate.isTrue(!itemFormat.contains(":"), "Invalid custom model data format, please use 'custom-model-data: X' instead");
      this.material = Material.valueOf(UtilityMethods.enumName(itemFormat));
      Validate.notNull(this.name, "Name cannot be null");
      Validate.notNull(this.lore, "Lore can be empty but not null");
      this.damage = config.getInt("damage");
      this.texture = config.getString("texture");
      this.modeldata = config.getInt("custom-model-data");
      this.unbreakable = config.getBoolean("unbreakable");
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Material getMaterial() {
      return this.material;
   }

   public int getDamage() {
      return this.damage;
   }

   @Nullable
   public String getTexture() {
      return this.texture;
   }

   public boolean isUnbreakable() {
      return this.unbreakable;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public int getModelData() {
      return this.modeldata;
   }
}
