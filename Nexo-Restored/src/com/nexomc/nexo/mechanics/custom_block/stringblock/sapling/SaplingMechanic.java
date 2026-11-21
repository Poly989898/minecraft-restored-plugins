package com.nexomc.nexo.mechanics.custom_block.stringblock.sapling;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.compatibilities.worldedit.WrappedWorldEdit;
import com.nexomc.nexo.configs.ConfigsManager;
import com.nexomc.nexo.utils.StringUtilsKt;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 62\u00020\u0001:\u000267B\u0019\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0016\u0010\"\u001a\u00020\t2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&J\u0016\u0010'\u001a\u00020(2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&J\u0006\u0010)\u001a\u00020\tJ\u0006\u0010*\u001a\u00020\tJ\u000e\u0010+\u001a\u00020\t2\u0006\u0010,\u001a\u00020-J\b\u0010.\u001a\u0004\u0018\u00010&J\u0006\u0010/\u001a\u00020\tJ\f\u00100\u001a\b\u0012\u0004\u0012\u00020201J\u0010\u00103\u001a\u0004\u0018\u00010&2\u0006\u00104\u001a\u00020\u0003J\f\u00105\u001a\b\u0012\u0004\u0012\u00020&01J\b\u0010%\u001a\u0004\u0018\u00010&R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000bR\u0011\u0010\u0012\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0017\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000fR\u0011\u0010\u0019\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u000bR\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u001c\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u000bR\u0011\u0010\u001e\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u000bR\u0011\u0010 \u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u000b¨\u00068"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/sapling/SaplingMechanic;", "", "itemId", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Ljava/lang/String;Lorg/bukkit/configuration/ConfigurationSection;)V", "canGrowNaturally", "", "getCanGrowNaturally", "()Z", "naturalGrowthTime", "", "getNaturalGrowthTime", "()I", "canGrowFromBoneMeal", "getCanGrowFromBoneMeal", "boneMealGrowthSpeedup", "getBoneMealGrowthSpeedup", "growSound", "getGrowSound", "()Ljava/lang/String;", "minLightLevel", "getMinLightLevel", "requiresWaterSource", "getRequiresWaterSource", "schematicConfig", "copyBiomes", "getCopyBiomes", "copyEntities", "getCopyEntities", "replaceBlocks", "getReplaceBlocks", "canPlaceSchematic", "location", "Lorg/bukkit/Location;", "schematic", "Ljava/io/File;", "placeSchematic", "", "requiresLight", "hasGrowSound", "isUnderWater", "block", "Lorg/bukkit/block/Block;", "selectSchematic", "hasSchematic", "schematics", "", "Lcom/nexomc/nexo/mechanics/custom_block/stringblock/sapling/SaplingMechanic$SchematicEntry;", "getSchematicFile", "schematicName", "schematicFiles", "Companion", "SchematicEntry", "com.nexomc:core"}
)
public final class SaplingMechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   private final boolean canGrowNaturally;
   private final int naturalGrowthTime;
   private final boolean canGrowFromBoneMeal;
   private final int boneMealGrowthSpeedup;
   @Nullable
   private final String growSound;
   private final int minLightLevel;
   private final boolean requiresWaterSource;
   @Nullable
   private final Object schematicConfig;
   private final boolean copyBiomes;
   private final boolean copyEntities;
   private final boolean replaceBlocks;
   @NotNull
   private static final NamespacedKey SAPLING_KEY;

   public SaplingMechanic(@Nullable String itemId, @NotNull ConfigurationSection section) {
      super();
      this.canGrowNaturally = section.getBoolean("grows_naturally", true);
      this.naturalGrowthTime = section.getInt("natural_growth_time", 6000);
      this.canGrowFromBoneMeal = section.getBoolean("grows_from_bonemeal", true);
      this.boneMealGrowthSpeedup = RangesKt.coerceAtMost(section.getInt("bonemeal_growth_speedup", this.naturalGrowthTime / 5), this.naturalGrowthTime);
      this.growSound = section.getString("grow_sound", (String)null);
      this.minLightLevel = section.getInt("min_light_level", 0);
      this.requiresWaterSource = section.getBoolean("requires_water_source", false);
      this.schematicConfig = section.get("schematic");
      this.copyBiomes = section.getBoolean("copy_biomes", false);
      this.copyEntities = section.getBoolean("copy_entities", false);
      this.replaceBlocks = section.getBoolean("replace_blocks", false);
   }

   public final boolean getCanGrowNaturally() {
      return this.canGrowNaturally;
   }

   public final int getNaturalGrowthTime() {
      return this.naturalGrowthTime;
   }

   public final boolean getCanGrowFromBoneMeal() {
      return this.canGrowFromBoneMeal;
   }

   public final int getBoneMealGrowthSpeedup() {
      return this.boneMealGrowthSpeedup;
   }

   @Nullable
   public final String getGrowSound() {
      return this.growSound;
   }

   public final int getMinLightLevel() {
      return this.minLightLevel;
   }

   public final boolean getRequiresWaterSource() {
      return this.requiresWaterSource;
   }

   public final boolean getCopyBiomes() {
      return this.copyBiomes;
   }

   public final boolean getCopyEntities() {
      return this.copyEntities;
   }

   public final boolean getReplaceBlocks() {
      return this.replaceBlocks;
   }

   public final boolean canPlaceSchematic(@NotNull Location location, @NotNull File schematic) {
      return this.replaceBlocks || WrappedWorldEdit.INSTANCE.blocksInSchematic(location, schematic, true).isEmpty();
   }

   public final void placeSchematic(@NotNull Location location, @NotNull File schematic) {
      location.getBlock().setType(Material.AIR, false);
      if (this.growSound != null) {
         location.getWorld().playSound(location, this.growSound, 1.0F, 0.8F);
      }

      WrappedWorldEdit.INSTANCE.pasteSchematic(location, schematic, this.replaceBlocks, this.copyBiomes, this.copyEntities);
   }

   public final boolean requiresLight() {
      return this.minLightLevel != 0;
   }

   public final boolean hasGrowSound() {
      return this.growSound != null;
   }

   public final boolean isUnderWater(@NotNull Block block) {
      return block.getRelative(BlockFace.DOWN).getType() == Material.WATER;
   }

   @Nullable
   public final File selectSchematic() {
      List schematics = this.schematics();
      if (schematics.isEmpty()) {
         return null;
      } else {
         Iterable iterable = (Iterable)schematics;
         double totalWeight = (double)0.0F;

         for(Object obj : iterable) {
            SchematicEntry it = (SchematicEntry)obj;
            int value = 0;
            double chance = it.getChance();
            totalWeight += chance;
         }

         double randomWeight = Random.Default.nextDouble(totalWeight);
         double cumulativeWeight = (double)0.0F;

         for(SchematicEntry schematic : (java.lang.Iterable<SchematicEntry>)(java.lang.Iterable)schematics) {
            cumulativeWeight += schematic.getChance();
            if (randomWeight <= cumulativeWeight) {
               return this.getSchematicFile(schematic.getName());
            }
         }

         return null;
      }
   }

   public final boolean hasSchematic() {
      return !((Collection)this.schematics()).isEmpty() && this.schematic() != null;
   }

   @NotNull
   public final List<SchematicEntry> schematics() {
      Object obj = this.schematicConfig;
      List list;
      if (obj instanceof String) {
         list = CollectionsKt.listOf(new SchematicEntry(StringUtilsKt.appendIfMissing((String)this.schematicConfig, ".schem"), (double)1.0F));
      } else if (obj instanceof List) {
         Iterable iterable = (Iterable)this.schematicConfig;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value = 0;
            int value2 = 0;
            Map map2 = element instanceof Map ? (Map)element : null;
            SchematicEntry schematicEntry;
            if ((element instanceof Map ? (Map)element : null) != null) {
               Map map = map2;
               int value3 = 0;
               Object obj2 = map.get("schem");
               String schemName = obj2 instanceof String ? (String)obj2 : null;
               Object obj3 = map.get("chance");
               double chance = (obj3 instanceof Double ? (Double)obj3 : null) != null ? obj3 instanceof Double ? (Double)obj3 : null : (double)1.0F;
               schematicEntry = schemName != null ? new SchematicEntry(StringUtilsKt.appendIfMissing(schemName, ".schem"), chance) : null;
            } else {
               schematicEntry = null;
            }

            if (schematicEntry != null) {
               Object it = schematicEntry;
               int value4 = 0;
               destination.add(it);
            }
         }

         list = (List)destination;
      } else {
         list = CollectionsKt.emptyList();
      }

      return list;
   }

   @Nullable
   public final File getSchematicFile(@NotNull String schematicName) {
      String absolutePath = ConfigsManager.Companion.getSchematicsFolder().getAbsolutePath();
      File schem = new File(absolutePath + "/" + schematicName);
      return !schem.exists() ? null : schem;
   }

   @NotNull
   public final List<File> schematicFiles() {
      Iterable schematics = (Iterable)this.schematics();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : schematics) {
         int value = 0;
         SchematicEntry it = (SchematicEntry)element;
         int value2 = 0;
         File schematicFile = this.getSchematicFile(it.getName());
         if (schematicFile != null) {
            Object obj = schematicFile;
            int value3 = 0;
            destination.add(obj);
         }
      }

      return (List)destination;
   }

   @Nullable
   public final File schematic() {
      SchematicEntry schematicEntry = CollectionsKt.firstOrNull(this.schematics());
      File file;
      if (schematicEntry != null) {
         SchematicEntry it = schematicEntry;
         int value = 0;
         file = this.getSchematicFile(it.getName());
      } else {
         file = null;
      }

      return file;
   }

   static {
      SAPLING_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "sapling");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/sapling/SaplingMechanic$Companion;", "", "<init>", "()V", "SAPLING_KEY", "Lorg/bukkit/NamespacedKey;", "getSAPLING_KEY", "()Lorg/bukkit/NamespacedKey;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getSAPLING_KEY() {
         return SaplingMechanic.SAPLING_KEY;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004J\n\u0010\u0014\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015"},
      d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/stringblock/sapling/SaplingMechanic$SchematicEntry;", "", "name", "", "chance", "", "<init>", "(Ljava/lang/String;D)V", "getName", "()Ljava/lang/String;", "getChance", "()D", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class SchematicEntry {
      @NotNull
      private final String name;
      private final double chance;

      public SchematicEntry(@NotNull String name, double chance) {
         super();
         this.name = name;
         this.chance = chance;
      }

      @NotNull
      public final String getName() {
         return this.name;
      }

      public final double getChance() {
         return this.chance;
      }

      @NotNull
      public final String component1() {
         return this.name;
      }

      public final double component2() {
         return this.chance;
      }

      @NotNull
      public final SchematicEntry copy(@NotNull String name, double chance) {
         return new SchematicEntry(name, chance);
      }

      @NotNull
      public String toString() {
         return "SchematicEntry(name=" + this.name + ", chance=" + this.chance + ")";
      }

      public int hashCode() {
         int result = this.name.hashCode();
         result = result * 31 + Double.hashCode(this.chance);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof SchematicEntry)) {
            return false;
         } else {
            SchematicEntry schematicEntry = (SchematicEntry)other;
            if (!Intrinsics.areEqual(this.name, schematicEntry.name)) {
               return false;
            } else {
               return Double.compare(this.chance, schematicEntry.chance) == 0;
            }
         }
      }
   }
}
