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

   public ItemStat(@NotNull String var1, @NotNull Material var2, @NotNull String var3, @Nullable String[] var4, @Nullable String[] var5, Material... var6) {
      this.id = var1;
      this.material = var2;
      this.lore = var4 == null ? new String[0] : var4;
      this.compatibleTypes = var5 == null ? new ArrayList<>() : Arrays.asList(var5);
      this.name = var3;
      this.compatibleMaterials = Arrays.asList(var6);
      this.configPath = var1.toLowerCase().replace("_", "-");
      this.nbtPath = "MMOITEMS_" + var1;
      if (this.getClass().isAnnotationPresent(VersionDependant.class)) {
         VersionDependant var7 = this.getClass().getAnnotation(VersionDependant.class);
         if (MythicLib.plugin.getVersion().isUnder(var7.version())) {
            this.disable();
         }
      }

      if (this.getClass().isAnnotationPresent(net.Indyuce.mmoitems.util.VersionDependant.class)) {
         net.Indyuce.mmoitems.util.VersionDependant var8 = this.getClass().getAnnotation(net.Indyuce.mmoitems.util.VersionDependant.class);
         if (MythicLib.plugin.getVersion().isUnder(var8.version())) {
            this.disable();
         }
      }
   }

   public abstract R whenInitialized(Object var1);

   public abstract void whenApplied(@NotNull ItemStackBuilder var1, @NotNull S var2);

   @NotNull
   public abstract ArrayList<ItemTag> getAppliedNBT(@NotNull S var1);

   public abstract void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2);

   public abstract void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3);

   public abstract void whenLoaded(@NotNull ReadMMOItem var1);

   @Nullable
   public abstract S getLoadedNBT(@NotNull ArrayList<ItemTag> var1);

   public abstract void whenDisplayed(List<String> var1, Optional<R> var2);

   public void loadConfiguration(@NotNull ConfigurationSection var1, @NotNull Object var2) {
      this.loadConfiguration(var2);
   }

   public void loadConfiguration(@NotNull Object var1) {
      this.generalStatFormat = var1.toString();
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

   public void setCategory(@Nullable StatCategory var1) {
      this.category = var1;
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

   public void setAliases(String... var1) {
      this.aliases = var1;
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

   public boolean isCompatible(@NotNull Type var1) {
      if (this.compatibleTypes.isEmpty()) {
         return true;
      }

      if (var1.getModifierSource().isWeapon() && this.compatibleTypes.contains("weapon")) {
         return true;
      }

      if (var1.getModifierSource().isEquipment() && this.compatibleTypes.contains("equipment")) {
         return true;
      }

      if (var1.getModifierSource().isHandheld() && this.compatibleTypes.contains("handheld")) {
         return true;
      }

      String var2 = var1.getSupertype().getId().toLowerCase();
      return !this.compatibleTypes.contains("!" + var2) && (this.compatibleTypes.contains("all") || this.compatibleTypes.contains(var2));
   }

   public boolean hasValidMaterial(ItemStack var1) {
      return this.compatibleMaterials.isEmpty() || this.compatibleMaterials.contains(var1.getType());
   }

   public void disable() {
      this.enabled = false;
   }

   @Deprecated
   public String formatNumericStat(double var1, String... var3) {
      String var4 = this.getGeneralStatFormat().replace("<plus>", var1 > 0.0 ? "+" : "");

      for (byte var5 = 0; var5 < var3.length; var5 += 2) {
         var4 = var4.replace(var3[var5], var3[var5 + 1]);
      }

      return var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ItemStat var2 = (ItemStat)var1;
         return this.id.equals(var2.id);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }

   @Deprecated
   public static String translate(String var0) {
      return MMOItems.plugin.getLanguage().getStatFormat(var0);
   }

   @NotNull
   public abstract S getClearStatData();
}
