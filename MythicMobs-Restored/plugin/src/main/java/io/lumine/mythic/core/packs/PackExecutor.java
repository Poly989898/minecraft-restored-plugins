package io.lumine.mythic.core.packs;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.packs.PackManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class PackExecutor extends ReloadableModule<MythicBukkit> implements PackManager {
   @Getter
   private Pack basePack;
   @Getter
   private Pack internalsPack;
   private final Map<String, Pack> packs = Maps.newConcurrentMap();
   private final Map<ConfigFileType, Map<String, PackExecutor.ConfigFile>> configTypeNameMapping = new ConcurrentHashMap<>();

   public PackExecutor(MythicBukkit plugin) {
      super(plugin, false);
   }

   public void load(MythicBukkit plugin) {
      this.loadPacks();
   }

   @Override
   public void unload() {
      this.packs.clear();
   }

   public Pack registerPack(File folder) {
      PackImpl pack = new PackImpl(folder);
      this.packs.put(pack.getKey(), pack);
      return pack;
   }

   private void loadBasePacks() {
      PackImpl basePackImpl = new PackImpl(this.getPlugin());
      this.basePack = basePackImpl;
      this.packs.put("", this.basePack);
      File packsFolder = new File(this.getPlugin().getDataFolder(), "packs");
      if (!packsFolder.exists()) {
         packsFolder.mkdir();
      }

      File mythicInternalPack = new File(packsFolder, ".internal");
      this.internalsPack = new PackImpl(mythicInternalPack, "Internal");
      this.packs.put(".internal", this.internalsPack);

      for (ConfigFileType type : ConfigFileType.values()) {
         if (type.info.createFolderInBasePack() && type.info.dataFolder() != null) {
            File folder = this.basePack.getPackFolder(type.info.dataFolder());
            if (!folder.exists()) {
               MythicLogger.log(type.info.dataFolder() + " folder not found in base pack! Creating...");
               boolean result = folder.mkdir();
               if (!result) {
                  MythicLogger.log(type.info.dataFolder() + " folder could not be created!");
               }
            }
         }

         if (type.info.createFileInBasePack() && type.info.dataFile() != null) {
            File file = this.basePack.getPackFile(type.info.dataFile());
            if (!file.exists()) {
               MythicLogger.log(type.info.dataFile() + " file not found in base pack! Creating...");
               if (type.info.fetchFileInBasePackFromResources()) {
                  this.plugin.saveResource(type.info.dataFile(), false);
               } else {
                  try {
                     file.createNewFile();
                  } catch (Throwable e) {
                     MythicLogger.error(type.info.dataFile() + "folder could not be created!");
                  }
               }
            }
         }
      }

      basePackImpl.reloadConfigFiles();
   }

   public void loadPacks() {
      MythicLogger.log("Loading Packs...");
      this.packs.clear();
      this.configTypeNameMapping.clear();
      this.loadBasePacks();
      List<PackImpl> userPacks = Lists.newArrayList();

      for (File baseFolder : this.getPackFolders()) {
         if (baseFolder.exists() && baseFolder.isDirectory()) {
            for (File packFolder : baseFolder.listFiles()) {
               if (packFolder.isDirectory() && !packFolder.getName().equals(".internal")) {
                  userPacks.add((PackImpl)this.registerPack(packFolder));
               }
            }
         }
      }

      Set<String> discovered = Set.copyOf(this.packs.keySet());

      for (PackImpl pack : userPacks) {
         pack.setKnownPacks(discovered);
         pack.reloadConfigFiles();
         pack.getSkipReason().ifPresent(reason -> {
            MythicLogger.log("Skipping pack '" + pack.getKey() + "' - " + reason);
            this.packs.remove(pack.getKey());
         });
      }

      for (ConfigFileType type : ConfigFileType.values()) {
         if (type.info.mapFileNames()) {
            for (PackExecutor.ConfigFile configfile : this.getConfigFiles(type)) {
               String name = getConfigFileName(type, configfile.file.getName());
               this.configTypeNameMapping.computeIfAbsent(type, k -> new ConcurrentHashMap<>()).put(name, configfile);
            }
         }
      }
   }

   @NotNull
   private static String getConfigFileName(ConfigFileType type, String name) {
      name = name.toLowerCase(Locale.ROOT);
      String[] nameSegments = name.split("\\.");
      switch (nameSegments.length) {
         case 0:
            return name;
         case 1:
         case 2:
            return nameSegments[0];
         default:
            String maybeIdentifier = nameSegments[nameSegments.length - 2];
            ConfigFileType maybeType = ConfigFileType.fromIdentifier(maybeIdentifier);
            return maybeType != null && ConfigFileType.fromIdentifier(maybeIdentifier).equals(type)
               ? String.join(".", Arrays.copyOfRange(nameSegments, 0, nameSegments.length - 2))
               : String.join(".", Arrays.copyOfRange(nameSegments, 0, nameSegments.length - 1));
      }
   }

   private Collection<File> getPackFolders() {
      Collection<File> folders = Lists.newArrayList();
      File packsFolder1 = new File(this.getPlugin().getDataFolder() + System.getProperty("file.separator") + "packs");
      File packsFolder2 = new File(this.getPlugin().getDataFolder() + System.getProperty("file.separator") + "Packs");
      if (!packsFolder1.exists() && !packsFolder2.exists()) {
         packsFolder1.mkdir();
      }

      folders.add(packsFolder1);
      folders.add(packsFolder2);
      return folders;
   }

   public Optional<Pack> getPack(String key) {
      return key == null ? Optional.of(this.basePack) : Optional.ofNullable(this.packs.getOrDefault(key, null));
   }

   public Collection<Pack> getPacks() {
      return this.packs.values();
   }

   public Collection<PackExecutor.ConfigFile> getConfigFiles(ConfigFileType type) {
      Collection<PackExecutor.ConfigFile> files = Lists.newArrayList();

      for (Pack pack : this.packs.values()) {
         for (File file : pack.getConfigFiles(type)) {
            files.add(new PackExecutor.ConfigFile(file, pack));
         }
      }

      return files;
   }

   public Collection<File> getFiles(ConfigFileType type) {
      return this.getConfigFiles(type).stream().map(k -> k.file).toList();
   }

   public PackExecutor.ConfigFile getConfigFile(ConfigFileType type, String name) {
      Map<String, PackExecutor.ConfigFile> maybeTypeMap = this.configTypeNameMapping.get(type);
      if (maybeTypeMap == null) {
         return null;
      } else {
         String processedName = getConfigFileName(type, name);
         return maybeTypeMap.get(processedName);
      }
   }

   public record ConfigFile(File file, Pack pack) {
   }
}
