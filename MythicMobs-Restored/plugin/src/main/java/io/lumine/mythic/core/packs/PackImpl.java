package io.lumine.mythic.core.packs;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.config.properties.PropertyHolder;
import io.lumine.mythic.bukkit.utils.files.Files;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.logging.MythicLogger;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@Setter
@ToString
public class PackImpl implements Pack, PropertyHolder {
   private final File folder;
   private final String key;
   private final boolean allowFolderCreation;
   private MythicConfig config = null;
   private String name = null;
   private List<String> description;
   private String author = null;
   private String url = null;
   private String version = null;
   private String icon = null;
   private List<String> packDependencies = Collections.emptyList();
   private List<String> packOverrides = Collections.emptyList();
   private List<PackImpl.FileRule> fileDependencies = Collections.emptyList();
   private List<PackImpl.FileRule> fileOverrides = Collections.emptyList();
   private Set<String> knownPacks = Collections.emptySet();
   public Integer itemCount = 0;
   private List<String> fullDescription;
   private final Map<ConfigFileType, Collection<File>> configFiles = new EnumMap<>(ConfigFileType.class);

   public PackImpl(MythicBukkit plugin) {
      this.folder = plugin.getDataFolder();
      this.key = "Mythic";
      this.allowFolderCreation = true;
      File packinfo = new File(this.folder, "packinfo.yml");
      if (packinfo.exists()) {
         this.config = MythicConfig.of(packinfo);
      }

      this.name = "<gold>Mythic Base Pack";
      this.description = Lists.newArrayList(new String[]{"The Mythic Base Pack", "Located in the plugin directory"});
      this.author = "MythicCraft";
      this.version = "1.0.0";
      this.url = "https://www.mythiccraft.io";
      this.icon = "CHEST";
      this.setupDescription();
      this.loadConfigFiles();
   }

   public PackImpl(File folder) {
      this.folder = folder;
      this.key = folder.getName();
      this.allowFolderCreation = true;
      File packinfo = new File(folder, "packinfo.yml");
      if (packinfo.exists() && packinfo.isFile() && packinfo.length() > 0L) {
         this.config = MythicConfig.of(packinfo);
         this.name = this.config.getString("Name");
         this.description = this.config.getStringList("Description");
         this.author = this.config.getString("Author");
         this.url = this.config.getString("URL");
         this.version = this.config.getString("Version");
         if (this.config.isConfigurationSection("Icon")) {
            this.icon = this.config.getString("Icon.Material");
            this.config.set("Icon", this.icon);
            this.config.save();
         }

         this.icon = this.config.getString("Icon");
      } else if (!packinfo.exists() || packinfo.length() == 0L) {
         try {
            if (!packinfo.exists()) {
               packinfo.createNewFile();
            }

            this.config = MythicConfig.of(packinfo);
            this.config.set("Name", this.key);
            this.config.set("Description", Collections.emptyList());
            this.config.set("Author", "Unknown");
            this.config.set("URL", "Unknown");
            this.config.set("Version", "1.0.0");
            this.config.set("Icon", "CHEST");
            this.config.save();
         } catch (Throwable e) {
            MythicLogger.error("Failed to generate default packinfo.yml for pack {0}", this.key);
            e.printStackTrace();
         }
      }

      this.setDefaultData();
      this.setupDescription();
      this.loadConfigFiles();
   }

   public PackImpl(LuminePlugin plugin, String packName) {
      this(plugin.getDataFolder(), packName);
   }

   public PackImpl(File folder, String packName) {
      this.folder = folder;
      this.key = packName;
      this.allowFolderCreation = false;
      File packinfo = new File(folder, "packinfo.yml");
      if (packinfo.exists() && packinfo.isFile() && packinfo.length() > 0L) {
         this.config = MythicConfig.of(packinfo);
         this.name = this.config.getString("Name");
         this.description = this.config.getStringList("Description");
         this.author = this.config.getString("Author");
         this.url = this.config.getString("URL");
         this.version = this.config.getString("Version");
         if (this.config.isConfigurationSection("Icon")) {
            this.icon = this.config.getString("Icon.Material");
            this.config.set("Icon", this.icon);
            this.config.save();
         }

         this.icon = this.config.getString("Icon");
      }

      this.setDefaultData();
      this.setupDescription();
      this.loadConfigFiles();
   }

   private void setDefaultData() {
      if (this.name == null) {
         this.name = "<gold>" + this.folder.getName();
      }

      if (this.description == null) {
         this.description = Collections.emptyList();
      }

      if (this.author == null) {
         this.author = "Unknown";
      }

      if (this.url == null) {
         this.url = "";
      }

      if (this.version == null) {
         this.version = "";
      }

      if (this.icon == null) {
         this.icon = "CHEST";
      }
   }

   private void setupDescription() {
      this.fullDescription = Lists.newArrayList();
      if (!this.author.isEmpty()) {
         this.fullDescription.add("<yellow>Author<white>: " + this.author);
      }

      if (!this.url.isEmpty()) {
         this.fullDescription.add("<yellow>URL<white>: " + this.url);
      }

      if (!this.version.isEmpty()) {
         this.fullDescription.add("<yellow>Version<white>: " + this.version);
      }

      if (!this.description.isEmpty()) {
         this.fullDescription.add("<gray> ");
      }

      if (!this.description.isEmpty()) {
         this.fullDescription.addAll(this.description);
      }

      this.fullDescription.add("<gray> ");
      this.fullDescription.add("<gray>Click to Open Menu");
   }

   @Override
   public String getPropertyNode() {
      return "";
   }

   @Override
   public File getPackFolder(String name, boolean create) {
      if (name.contains("\\.")) {
         throw new IllegalArgumentException("Unsupported folder name");
      } else {
         File file = new File(this.folder, name.toLowerCase(Locale.ROOT));
         if (!file.exists()) {
            file = new File(this.folder, name);
            if (!file.exists()) {
               file = new File(this.folder, name.toLowerCase(Locale.ROOT));
               if (this.allowFolderCreation && create) {
                  file.mkdir();
               }
            }
         }

         return file;
      }
   }

   public Collection<File> getPackFolders(ConfigFileType.ConfigFileTypeRecord info) {
      assert info.dataFolder() != null;

      return this.getPackFolders(info.dataFolder(), info.createFolderInPacks(), info.readMono());
   }

   @Override
   public Collection<File> getPackFolders(String name, boolean create, boolean readMono) {
      if (name.contains("\\.")) {
         throw new IllegalArgumentException("Unsupported folder name");
      } else {
         Collection<File> files = Lists.newArrayList();
         File folder = new File(this.folder, name.toLowerCase(Locale.ROOT));
         File folderOG = new File(this.folder, name);
         if (folder.exists() && folder.isDirectory()) {
            files.add(folder);
         } else if (this.allowFolderCreation && create && !folderOG.exists() && !folderOG.isDirectory()) {
            folder.mkdir();
         }

         if (!folderOG.equals(folder) && folderOG.exists() && folderOG.isDirectory()) {
            files.add(folderOG);
         }

         if (readMono) {
            File monoFolder = new File(this.folder, "Mono".toLowerCase(Locale.ROOT));
            if (monoFolder.exists() && monoFolder.isDirectory()) {
               files.add(monoFolder);
            }
         }

         return files;
      }
   }

   @Override
   public void addItem() {
      Integer number = this.itemCount;
      this.itemCount = this.itemCount + 1;
   }

   @Override
   public File getPackFile(String name) {
      return new File(this.folder, name);
   }

   @Override
   public File getSchematicDirectory() {
      return this.getPackFolder("Schematics");
   }

   @Override
   public <T> Icon<T> createMenuIcon(BiConsumer<T, Player> clickEvent) {
      AbstractItemStack iconItem = MythicBukkit.inst().getBootstrap().createItem(this.icon);
      return IconBuilder.<T>create()
         .itemStack(BukkitAdapter.adapt(iconItem))
         .hideFlags(true)
         .name(this.name)
         .lore(this.fullDescription)
         .click(clickEvent)
         .build();
   }

   @Override
   public Collection<File> getConfigFiles(ConfigFileType type) {
      return this.configFiles.getOrDefault(type, Collections.emptyList());
   }

   void reloadConfigFiles() {
      this.loadConfigFiles();
   }

   private void loadConfigFiles() {
      this.loadDependencies();
      this.configFiles.clear();
      if (this.folder.exists()) {
         if (!this.getSkipReason().isPresent()) {
            for (ConfigFileType type : ConfigFileType.values()) {
               this.configFiles.put(type, Lists.newArrayList());
            }

            for (ConfigFileType type : ConfigFileType.values()) {
               if (type.info.dataFolder() != null) {
                  for (File folder : this.getPackFolders(type.info)) {
                     this.configFiles.get(type).addAll(Files.getAll(folder.getAbsolutePath(), Lists.newArrayList(type.info.fileExtensions())));
                  }
               }

               if (type.info.dataFile() != null) {
                  File file = this.getPackFile(type.info.dataFile());
                  if (!file.exists()) {
                     if (!type.info.createFileInPacks()) {
                        continue;
                     }

                     try {
                        file.createNewFile();
                     } catch (Throwable e) {
                        MythicLogger.error(
                           "There was an error while trying to create the " + type.info.dataFile() + " file in the " + this.name + "pack", e
                        );
                        continue;
                     }
                  }

                  this.configFiles.get(type).add(file);
               }
            }

            for (File folder : this.getPackFolders("Files", false, false)) {
               for (File file : Files.getAll(folder.getAbsolutePath(), Lists.newArrayList(ConfigFileType.getAllExtensions()))) {
                  String[] fileNameSegments = file.getName().split("\\.");
                  String extension = fileNameSegments[fileNameSegments.length - 1];
                  ConfigFileType maybeExclusiveFileType = ConfigFileType.fromExclusiveExtension(extension);
                  if (maybeExclusiveFileType != null) {
                     this.configFiles.get(maybeExclusiveFileType).add(file);
                  } else if (fileNameSegments.length >= 3) {
                     String identifier = fileNameSegments[fileNameSegments.length - 2];
                     ConfigFileType type = ConfigFileType.fromIdentifier(identifier);
                     if (type != null) {
                        this.configFiles.get(type).add(file);
                     }
                  }
               }
            }

            this.applyFileDependencies();
         }
      }
   }

   private void loadDependencies() {
      this.packDependencies = Collections.emptyList();
      this.packOverrides = Collections.emptyList();
      this.fileDependencies = Collections.emptyList();
      this.fileOverrides = Collections.emptyList();
      if (this.config != null) {
         this.packDependencies = this.readExpressions("PackDependencies");
         this.packOverrides = this.readExpressions("PackOverrides");
         this.fileDependencies = this.readFileRules("FileDependencies");
         this.fileOverrides = this.readFileRules("FileOverrides");
      }
   }

   private List<String> readExpressions(String key) {
      List<String> entries = this.config.getStringList(key);
      return entries != null && !entries.isEmpty()
         ? entries.stream().map(String::trim).filter(s -> !s.isEmpty()).filter(s -> this.isValidExpression(key, s)).toList()
         : Collections.emptyList();
   }

   private boolean isValidExpression(String key, String expression) {
      try {
         evaluate(expression, name -> true);
         return true;
      } catch (IllegalArgumentException e) {
         MythicLogger.error("Invalid {0} expression in pack {1}: {2}", key, this.key, e.getMessage());
         return false;
      }
   }

   private List<PackImpl.FileRule> readFileRules(String key) {
      List<String> entries = this.config.getStringList(key);
      if (entries != null && !entries.isEmpty()) {
         List<PackImpl.FileRule> parsed = Lists.newArrayList();

         for (String entry : entries) {
            PackImpl.FileRule rule = parseFileRule(key, entry);
            if (rule != null && this.isValidExpression(key, rule.expression())) {
               parsed.add(rule);
            }
         }

         return parsed;
      } else {
         return Collections.emptyList();
      }
   }

   static PackImpl.FileRule parseFileRule(String key, String entry) {
      if (entry == null) {
         return null;
      } else {
         String s = entry.trim();
         int split = -1;
         if (s.isEmpty() || s.charAt(0) != '\'' && s.charAt(0) != '"') {
            for (int i = 0; i < s.length(); i++) {
               if (Character.isWhitespace(s.charAt(i))) {
                  split = i;
                  break;
               }
            }
         } else {
            int close = s.indexOf(s.charAt(0), 1);
            if (close > 0) {
               split = close + 1;
            }
         }

         if (split < 0) {
            MythicLogger.error("Invalid {0} entry (expected '<path> <expression>'): {1}", key, entry);
            return null;
         } else {
            String glob = stripQuotes(s.substring(0, split).trim());
            String expression = s.substring(split).trim();
            if (!glob.isEmpty() && !expression.isEmpty()) {
               return new PackImpl.FileRule(glob, expression, Pattern.compile(globToRegex(glob.toLowerCase(Locale.ROOT))));
            } else {
               MythicLogger.error("Invalid {0} entry (expected '<path> <expression>'): {1}", key, entry);
               return null;
            }
         }
      }
   }

   static boolean evaluate(String expression, Predicate<String> present) {
      PackImpl.ExpressionParser parser = new PackImpl.ExpressionParser(expression, present);
      boolean result = parser.parseOr();
      parser.skipWhitespace();
      if (parser.pos != expression.length()) {
         throw new IllegalArgumentException("Unexpected '" + expression.charAt(parser.pos) + "' at index " + parser.pos + " in: " + expression);
      } else {
         return result;
      }
   }

   private static String stripQuotes(String s) {
      if (s.length() >= 2) {
         char first = s.charAt(0);
         if ((first == '\'' || first == '"') && s.charAt(s.length() - 1) == first) {
            return s.substring(1, s.length() - 1);
         }
      }

      return s;
   }

   private static String globToRegex(String glob) {
      StringBuilder sb = new StringBuilder("^");

      for (int i = 0; i < glob.length(); i++) {
         char c = glob.charAt(i);
         switch (c) {
            case '$':
            case '%':
            case '(':
            case ')':
            case '+':
            case '.':
            case '@':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '{':
            case '|':
            case '}':
               sb.append('\\').append(c);
               break;
            case '*':
               if (i + 1 < glob.length() && glob.charAt(i + 1) == '*') {
                  i++;
                  if (i + 1 < glob.length() && glob.charAt(i + 1) == '/') {
                     i++;
                     sb.append("(?:.*/)?");
                  } else {
                     sb.append(".*");
                  }
               } else {
                  sb.append("[^/]*");
               }
               break;
            case '?':
               sb.append("[^/]");
               break;
            default:
               sb.append(c);
         }
      }

      return sb.append('$').toString();
   }

   private boolean isPresent(String name) {
      return name.equalsIgnoreCase("MythicMobsPremium")
         ? MythicBukkit.isVolatile()
         : Bukkit.getPluginManager().getPlugin(name) != null || this.knownPacks.contains(name);
   }

   public Optional<String> getSkipReason() {
      for (String expression : this.packDependencies) {
         if (!evaluate(expression, this::isPresent)) {
            return Optional.of("dependency not satisfied: " + expression);
         }
      }

      for (String expressionx : this.packOverrides) {
         if (evaluate(expressionx, this::isPresent)) {
            return Optional.of("overridden by: " + expressionx);
         }
      }

      return Optional.empty();
   }

   private void applyFileDependencies() {
      if (!this.fileDependencies.isEmpty() || !this.fileOverrides.isEmpty()) {
         Path packPath = this.folder.toPath().toAbsolutePath();

         for (Collection<File> files : this.configFiles.values()) {
            files.removeIf(file -> this.isFileExcluded(packPath, file));
         }
      }
   }

   private boolean isFileExcluded(Path packPath, File file) {
      String relative;
      try {
         relative = packPath.relativize(file.toPath().toAbsolutePath()).toString().replace('\\', '/').toLowerCase(Locale.ROOT);
      } catch (IllegalArgumentException e) {
         return false;
      }

      for (PackImpl.FileRule rule : this.fileDependencies) {
         if (rule.matches(relative) && !evaluate(rule.expression(), this::isPresent)) {
            return true;
         }
      }

      for (PackImpl.FileRule rulex : this.fileOverrides) {
         if (rulex.matches(relative) && evaluate(rulex.expression(), this::isPresent)) {
            return true;
         }
      }

      return false;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PackImpl other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isAllowFolderCreation() != other.isAllowFolderCreation()) {
         return false;
      } else {
         Object this$itemCount = this.getItemCount();
         Object other$itemCount = other.getItemCount();
         if (this$itemCount == null ? other$itemCount == null : this$itemCount.equals(other$itemCount)) {
            Object this$folder = this.getFolder();
            Object other$folder = other.getFolder();
            if (this$folder == null ? other$folder == null : this$folder.equals(other$folder)) {
               Object this$key = this.getKey();
               Object other$key = other.getKey();
               if (this$key == null ? other$key == null : this$key.equals(other$key)) {
                  Object this$config = this.getConfig();
                  Object other$config = other.getConfig();
                  if (this$config == null ? other$config == null : this$config.equals(other$config)) {
                     Object this$name = this.getName();
                     Object other$name = other.getName();
                     if (this$name == null ? other$name == null : this$name.equals(other$name)) {
                        Object this$description = this.getDescription();
                        Object other$description = other.getDescription();
                        if (this$description == null ? other$description == null : this$description.equals(other$description)) {
                           Object this$author = this.getAuthor();
                           Object other$author = other.getAuthor();
                           if (this$author == null ? other$author == null : this$author.equals(other$author)) {
                              Object this$url = this.getUrl();
                              Object other$url = other.getUrl();
                              if (this$url == null ? other$url == null : this$url.equals(other$url)) {
                                 Object this$version = this.getVersion();
                                 Object other$version = other.getVersion();
                                 if (this$version == null ? other$version == null : this$version.equals(other$version)) {
                                    Object this$icon = this.getIcon();
                                    Object other$icon = other.getIcon();
                                    if (this$icon == null ? other$icon == null : this$icon.equals(other$icon)) {
                                       Object this$packDependencies = this.getPackDependencies();
                                       Object other$packDependencies = other.getPackDependencies();
                                       if (this$packDependencies == null
                                          ? other$packDependencies == null
                                          : this$packDependencies.equals(other$packDependencies)) {
                                          Object this$packOverrides = this.getPackOverrides();
                                          Object other$packOverrides = other.getPackOverrides();
                                          if (this$packOverrides == null ? other$packOverrides == null : this$packOverrides.equals(other$packOverrides)) {
                                             Object this$fileDependencies = this.getFileDependencies();
                                             Object other$fileDependencies = other.getFileDependencies();
                                             if (this$fileDependencies == null
                                                ? other$fileDependencies == null
                                                : this$fileDependencies.equals(other$fileDependencies)) {
                                                Object this$fileOverrides = this.getFileOverrides();
                                                Object other$fileOverrides = other.getFileOverrides();
                                                if (this$fileOverrides == null ? other$fileOverrides == null : this$fileOverrides.equals(other$fileOverrides)) {
                                                   Object this$knownPacks = this.getKnownPacks();
                                                   Object other$knownPacks = other.getKnownPacks();
                                                   if (this$knownPacks == null ? other$knownPacks == null : this$knownPacks.equals(other$knownPacks)) {
                                                      Object this$fullDescription = this.getFullDescription();
                                                      Object other$fullDescription = other.getFullDescription();
                                                      if (this$fullDescription == null
                                                         ? other$fullDescription == null
                                                         : this$fullDescription.equals(other$fullDescription)) {
                                                         Object this$configFiles = this.getConfigFiles();
                                                         Object other$configFiles = other.getConfigFiles();
                                                         return this$configFiles == null
                                                            ? other$configFiles == null
                                                            : this$configFiles.equals(other$configFiles);
                                                      } else {
                                                         return false;
                                                      }
                                                   } else {
                                                      return false;
                                                   }
                                                } else {
                                                   return false;
                                                }
                                             } else {
                                                return false;
                                             }
                                          } else {
                                             return false;
                                          }
                                       } else {
                                          return false;
                                       }
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof PackImpl;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isAllowFolderCreation() ? 79 : 97);
      Object $itemCount = this.getItemCount();
      result = result * 59 + ($itemCount == null ? 43 : $itemCount.hashCode());
      Object $folder = this.getFolder();
      result = result * 59 + ($folder == null ? 43 : $folder.hashCode());
      Object $key = this.getKey();
      result = result * 59 + ($key == null ? 43 : $key.hashCode());
      Object $config = this.getConfig();
      result = result * 59 + ($config == null ? 43 : $config.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $description = this.getDescription();
      result = result * 59 + ($description == null ? 43 : $description.hashCode());
      Object $author = this.getAuthor();
      result = result * 59 + ($author == null ? 43 : $author.hashCode());
      Object $url = this.getUrl();
      result = result * 59 + ($url == null ? 43 : $url.hashCode());
      Object $version = this.getVersion();
      result = result * 59 + ($version == null ? 43 : $version.hashCode());
      Object $icon = this.getIcon();
      result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
      Object $packDependencies = this.getPackDependencies();
      result = result * 59 + ($packDependencies == null ? 43 : $packDependencies.hashCode());
      Object $packOverrides = this.getPackOverrides();
      result = result * 59 + ($packOverrides == null ? 43 : $packOverrides.hashCode());
      Object $fileDependencies = this.getFileDependencies();
      result = result * 59 + ($fileDependencies == null ? 43 : $fileDependencies.hashCode());
      Object $fileOverrides = this.getFileOverrides();
      result = result * 59 + ($fileOverrides == null ? 43 : $fileOverrides.hashCode());
      Object $knownPacks = this.getKnownPacks();
      result = result * 59 + ($knownPacks == null ? 43 : $knownPacks.hashCode());
      Object $fullDescription = this.getFullDescription();
      result = result * 59 + ($fullDescription == null ? 43 : $fullDescription.hashCode());
      Object $configFiles = this.getConfigFiles();
      return result * 59 + ($configFiles == null ? 43 : $configFiles.hashCode());
   }

   private static final class ExpressionParser {
      private final String s;
      private final Predicate<String> present;
      private int pos;

      ExpressionParser(String s, Predicate<String> present) {
         this.s = s;
         this.present = present;
      }

      boolean parseOr() {
         boolean result = this.parseAnd();

         while (this.accept("||")) {
            result |= this.parseAnd();
         }

         return result;
      }

      boolean parseAnd() {
         boolean result = this.parseUnary();

         while (this.accept("&&")) {
            result &= this.parseUnary();
         }

         return result;
      }

      boolean parseUnary() {
         if (this.accept("!")) {
            return !this.parseUnary();
         } else if (this.accept("(")) {
            boolean result = this.parseOr();
            if (!this.accept(")")) {
               throw new IllegalArgumentException("Expected ')' at index " + this.pos + " in: " + this.s);
            } else {
               return result;
            }
         } else {
            this.skipWhitespace();
            int start = this.pos;

            while (this.pos < this.s.length() && !Character.isWhitespace(this.s.charAt(this.pos)) && "()&|!".indexOf(this.s.charAt(this.pos)) < 0) {
               this.pos++;
            }

            if (start == this.pos) {
               throw new IllegalArgumentException("Expected a name at index " + this.pos + " in: " + this.s);
            } else {
               return this.present.test(this.s.substring(start, this.pos));
            }
         }
      }

      boolean accept(String token) {
         this.skipWhitespace();
         if (this.s.startsWith(token, this.pos)) {
            this.pos = this.pos + token.length();
            return true;
         } else {
            return false;
         }
      }

      void skipWhitespace() {
         while (this.pos < this.s.length() && Character.isWhitespace(this.s.charAt(this.pos))) {
            this.pos++;
         }
      }
   }

   public record FileRule(String glob, String expression, Pattern pattern) {
      boolean matches(String relativePath) {
         return this.pattern.matcher(relativePath).matches();
      }
   }
}
