package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.annotation.VersionDependant;
import net.Indyuce.mmoitems.stat.category.StatCategory;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ItemStat<R extends RandomStatData<S>, S extends StatData> {
   @NotNull
   private final String id;
   @NotNull
   private final String name;
   @NotNull
   private final String configPath;
   @NotNull
   private final String nbtPath;
   @NotNull
   private final Material material;
   private final String[] lore;
   private final List<String> compatibleTypes;
   private final List<Material> compatibleMaterials;
   @NotNull
   private String[] aliases = new String[0];
   private boolean enabled = true;
   private StatCategory category;
   protected static final int LORE_LINE_WIDTH = 50;
   protected String generalStatFormat;

   public ItemStat(@NotNull String text, @NotNull Material material, @NotNull String name, @Nullable String[] texts, @Nullable String[] compatibleTypes, Material... materials) {
      this.id = text;
      this.material = material;
      this.lore = texts == null ? new String[0] : texts;
      this.compatibleTypes = compatibleTypes == null ? new ArrayList<>() : Arrays.asList(compatibleTypes);
      this.name = name;
      this.compatibleMaterials = Arrays.asList(materials);
      this.configPath = text.toLowerCase().replace("_", "-");
      this.nbtPath = "MMOITEMS_" + text;
      if (this.getClass().isAnnotationPresent(VersionDependant.class)) {
         VersionDependant annotation = this.getClass().getAnnotation(VersionDependant.class);
         if (MythicLib.plugin.getVersion().isUnder(annotation.version())) {
            this.disable();
         }
      }

      if (this.getClass().isAnnotationPresent(net.Indyuce.mmoitems.util.VersionDependant.class)) {
         net.Indyuce.mmoitems.util.VersionDependant annotation = this.getClass().getAnnotation(net.Indyuce.mmoitems.util.VersionDependant.class);
         if (MythicLib.plugin.getVersion().isUnder(annotation.version())) {
            this.disable();
         }
      }
   }

   public abstract R whenInitialized(Object obj);

   public abstract void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull S value);

   @NotNull
   public abstract ArrayList<ItemTag> getAppliedNBT(@NotNull S value);

   public abstract void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event);

   public abstract void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs);

   public abstract void whenLoaded(@NotNull ReadMMOItem readMMOItem);

   @Nullable
   public abstract S getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags);

   public abstract void whenDisplayed(List<String> texts, Optional<R> optional);

   public void loadConfiguration(@NotNull ConfigurationSection section, @NotNull Object obj) {
      this.loadConfiguration(obj);
   }

   public void loadConfiguration(@NotNull Object obj) {
      this.generalStatFormat = obj.toString();
   }

   public String getLegacyTranslationPath() {
      return this.getPath();
   }

   public String getGeneralStatFormat() {
      return this.generalStatFormat;
   }

   @Nullable
   public StatCategory getCategory() {
      return this.category;
   }

   public void setCategory(@Nullable StatCategory statCategory) {
      this.category = statCategory;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public String[] getAliases() {
      return this.aliases;
   }

   public void setAliases(String... texts) {
      this.aliases = texts;
   }

   @Deprecated
   @NotNull
   public String name() {
      return this.id;
   }

   @NotNull
   public String getPath() {
      return this.configPath;
   }

   @NotNull
   public String getNBTPath() {
      return this.nbtPath;
   }

   public Material getDisplayMaterial() {
      return this.material;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String[] getLore() {
      return this.lore;
   }

   @NotNull
   public List<String> getCompatibleTypes() {
      return this.compatibleTypes;
   }

   public boolean isCompatible(@NotNull Type type) {
      if (this.compatibleTypes.isEmpty()) {
         return true;
      }

      if (type.getModifierSource().isWeapon() && this.compatibleTypes.contains("weapon")) {
         return true;
      }

      if (type.getModifierSource().isEquipment() && this.compatibleTypes.contains("equipment")) {
         return true;
      }

      if (type.getModifierSource().isHandheld() && this.compatibleTypes.contains("handheld")) {
         return true;
      }

      String text = type.getSupertype().getId().toLowerCase();
      return !this.compatibleTypes.contains("!" + text) && (this.compatibleTypes.contains("all") || this.compatibleTypes.contains(text));
   }

   public boolean hasValidMaterial(ItemStack item) {
      return this.compatibleMaterials.isEmpty() || this.compatibleMaterials.contains(item.getType());
   }

   public void disable() {
      this.enabled = false;
   }

   @Deprecated
   public String formatNumericStat(double value, String... texts) {
      String text = this.getGeneralStatFormat().replace("<plus>", value > 0.0 ? "+" : "");

      for (byte b = 0; b < texts.length; b += 2) {
         text = text.replace(texts[b], texts[b + 1]);
      }

      return text;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         ItemStat itemStat = (ItemStat)obj;
         return this.id.equals(itemStat.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   @Deprecated
   public static String translate(String text) {
      return MMOItems.plugin.getLanguage().getStatFormat(text);
   }

   @NotNull
   public abstract S getClearStatData();
}
