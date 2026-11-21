package io.lumine.mythic.core.constants;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public enum ConfigFileType {
   MOB(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Mobs")
         .withFileIdentifiers(new String[]{"mob"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(true)
         .doesReadMono(true)
         .build()
   ),
   SKILL(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Skills")
         .withFileIdentifiers(new String[]{"skill"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(true)
         .doesReadMono(true)
         .build()
   ),
   ITEM(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Items")
         .withFileIdentifiers(new String[]{"item"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(true)
         .doesReadMono(true)
         .build()
   ),
   DROP_TABLE(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("DropTables")
         .withFileIdentifiers(new String[]{"droptable", "drop"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(false)
         .build()
   ),
   RANDOM_SPAWN(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("RandomSpawns")
         .withFileIdentifiers(new String[]{"randomspawn", "rspawn", "spawn"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(false)
         .build()
   ),
   SPAWNER(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Spawners")
         .withFileIdentifiers(new String[]{"spawner"})
         .doesCreateFolderInPacks(false)
         .doesCreateFolderInBasePack(true)
         .build()
   ),
   SCHEMATIC(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Schematics")
         .withFileIdentifiers(null)
         .withFileExtensions(new String[]{"schem"}, true)
         .doesMapFileNames(true)
         .build()
   ),
   CAMERA_PATH(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("cutscenes")
         .withFileIdentifiers(new String[]{"camerapath", "camera"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(true)
         .build()
   ),
   DIALOG(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Dialogs")
         .withFileIdentifiers(new String[]{"dialog"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(false)
         .build()
   ),
   MENU(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Menus")
         .withFileIdentifiers(new String[]{"menu"})
         .doesCreateFolderInBasePack(true)
         .doesCreateFolderInPacks(false)
         .build()
   ),
   ARCHETYPE(new ConfigFileType.ConfigFileTypeRecord.Builder().withDataFolder("Archetypes").withFileIdentifiers(new String[]{"archetype"}).build()),
   PLACEHOLDER(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFile("placeholders.yml")
         .doesCreateFileInBasePack(true)
         .withFileIdentifiers(new String[]{"placeholder"})
         .build()
   ),
   STAT(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFile("stats.yml")
         .doesCreateFileInBasePack(true)
         .doesFetchFileInBasePackFromResources(true)
         .withFileIdentifiers(new String[]{"stat"})
         .build()
   ),
   MODEL(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFolder("Models")
         .withFileIdentifiers(null)
         .withFileExtensions(new String[]{"bbmodel"}, true)
         .doesMapFileNames(true)
         .build()
   ),
   LORE_TEMPLATE(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFile("lore-templates.yml")
         .withFileIdentifiers(new String[]{"loretemplate", "lore-template"})
         .build()
   ),
   FONT(new ConfigFileType.ConfigFileTypeRecord.Builder().withDataFile("font-images.yml").withFileIdentifiers(new String[]{"font"}).build()),
   WORLD_GEN(new ConfigFileType.ConfigFileTypeRecord.Builder().withDataFile("worldgen.yml").withFileIdentifiers(new String[]{"worldgen"}).build()),
   EQUIPMENT_SET(
      new ConfigFileType.ConfigFileTypeRecord.Builder()
         .withDataFile("equipment-sets.yml")
         .withFileIdentifiers(new String[]{"equipmentset", "equipment-set"})
         .build()
   ),
   TOOLTIP(new ConfigFileType.ConfigFileTypeRecord.Builder().withDataFile("tooltips.yml").withFileIdentifiers(new String[]{"tooltip"}).build()),
   AUGMENT(new ConfigFileType.ConfigFileTypeRecord.Builder().withDataFile("augments.yml").withFileIdentifiers(new String[]{"augment"}).build());

   public final ConfigFileType.ConfigFileTypeRecord info;
   private static final Map<String, ConfigFileType> IDENTIFIER_MAP;
   private static final Map<String, Collection<ConfigFileType>> EXTENSION_MAP;
   private static final Map<String, ConfigFileType> EXCLUSIVE_EXTENSION_MAP;

   private ConfigFileType(ConfigFileType.ConfigFileTypeRecord info) {
      this.info = info;
   }

   public static ConfigFileType fromIdentifier(String ext) {
      return IDENTIFIER_MAP.get(ext.toLowerCase());
   }

   public static Collection<ConfigFileType> fromExtension(String ext) {
      return EXTENSION_MAP.get(ext.toLowerCase());
   }

   public static ConfigFileType fromExclusiveExtension(String ext) {
      return EXCLUSIVE_EXTENSION_MAP.get(ext.toLowerCase());
   }

   public static Collection<String> getAllExtensions() {
      return EXTENSION_MAP.keySet();
   }

   static {
      Map<String, ConfigFileType> map = new HashMap<>();

      for (ConfigFileType type : values()) {
         if (type.info.fileIdentifiers != null) {
            for (String ext : type.info.fileIdentifiers) {
               map.put(ext.toLowerCase(), type);
            }
         }
      }

      IDENTIFIER_MAP = Map.copyOf(map);
      Map<String, Collection<ConfigFileType>> extMap = new HashMap<>();

      for (ConfigFileType typex : values()) {
         for (String ext : typex.info.fileExtensions) {
            extMap.computeIfAbsent(ext, k -> new java.util.LinkedList<ConfigFileType>()).add(typex);
         }
      }

      EXTENSION_MAP = Map.copyOf(extMap);
      Map<String, ConfigFileType> exclusiveMap = new HashMap<>();

      for (ConfigFileType typex : values()) {
         if (typex.info.exclusiveFileExtensions != null) {
            for (String ext : typex.info.exclusiveFileExtensions) {
               exclusiveMap.put(ext.toLowerCase(), typex);
            }
         }
      }

      EXCLUSIVE_EXTENSION_MAP = Map.copyOf(exclusiveMap);
   }

   public record ConfigFileTypeRecord(
      @Nullable String dataFolder,
      @Nullable String dataFile,
      @Nullable String[] fileIdentifiers,
      String[] fileExtensions,
      @Nullable String[] exclusiveFileExtensions,
      boolean createFolderInPacks,
      boolean createFolderInBasePack,
      boolean createFileInPacks,
      boolean createFileInBasePack,
      boolean fetchFileInBasePackFromResources,
      boolean readMono,
      boolean mapFileNames
   ) {
      private static class Builder {
         private String dataFolder;
         private String dataFile;
         private String[] fileIdentifiers;
         private String[] fileExtensions = new String[]{"txt", "yml", "yaml"};
         private String[] exclusiveFileExtensions = null;
         private boolean createFolderInPacks = false;
         private boolean createFolderInBasePack = false;
         private boolean createFileInPacks = false;
         private boolean createFileInBasePack = false;
         private boolean fetchFileInBasePackFromResources = false;
         private boolean readMono = false;
         private boolean mapFileNames = false;

         public ConfigFileType.ConfigFileTypeRecord build() {
            return new ConfigFileType.ConfigFileTypeRecord(
               this.dataFolder,
               this.dataFile,
               this.fileIdentifiers,
               this.fileExtensions,
               this.exclusiveFileExtensions,
               this.createFolderInPacks,
               this.createFolderInBasePack,
               this.createFileInPacks,
               this.createFileInBasePack,
               this.fetchFileInBasePackFromResources,
               this.readMono,
               this.mapFileNames
            );
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder withDataFolder(String dataFolder) {
            this.dataFolder = dataFolder;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder withDataFile(String dataFile) {
            this.dataFile = dataFile;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder withFileIdentifiers(String[] identifiers) {
            this.fileIdentifiers = identifiers;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder withFileExtensions(String[] extensions) {
            return this.withFileExtensions(extensions, false);
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder withFileExtensions(String[] extensions, boolean exclusive) {
            this.fileExtensions = extensions;
            if (exclusive) {
               this.exclusiveFileExtensions = extensions;
            }

            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder withExclusiveFileExtensions(String[] extensions) {
            this.exclusiveFileExtensions = extensions;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder doesCreateFolderInPacks(boolean createFolderInPacks) {
            this.createFolderInPacks = createFolderInPacks;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder doesCreateFolderInBasePack(boolean doesCreateFolderInBasePack) {
            this.createFolderInBasePack = doesCreateFolderInBasePack;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder doesCreateFileInPacks(boolean doesCreateFileInPacks) {
            this.createFileInPacks = doesCreateFileInPacks;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder doesCreateFileInBasePack(boolean doesCreateFileInBasePack) {
            this.createFileInBasePack = doesCreateFileInBasePack;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder doesFetchFileInBasePackFromResources(boolean fetchFileInBasePackFromResources) {
            this.fetchFileInBasePackFromResources = fetchFileInBasePackFromResources;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder doesReadMono(boolean readMono) {
            this.readMono = readMono;
            return this;
         }

         public ConfigFileType.ConfigFileTypeRecord.Builder doesMapFileNames(boolean doesMapFileNames) {
            this.mapFileNames = doesMapFileNames;
            return this;
         }
      }
   }
}
