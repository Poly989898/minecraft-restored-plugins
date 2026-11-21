package io.lumine.mythic.bukkit.adapters;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lumine.mythic.api.adapters.AbstractAttributeModifier;
import io.lumine.mythic.api.adapters.AbstractItemFlag;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBannerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPotionComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemSpawnerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTrialSpawnerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTrimComponent;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.serialize.Base64Util;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.uuid.DashedUUIDs;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.constants.ItemKeys;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.utils.Patterns;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.awt.Color;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Nullable;

public abstract class BukkitItemStack implements AbstractItemStack {
   protected ItemStack itemStack;

   protected BukkitItemStack(ItemStack itemStack) {
      this.itemStack = itemStack;
   }

   protected BukkitItemStack(Material material) {
      this.itemStack = new ItemStack(material);
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public Material getType() {
      return this.itemStack.getType();
   }

   @Deprecated
   public BukkitItemStack type(Material material) {
      this.itemStack.setType(material);
      return this;
   }

   @Nullable
   public ItemMeta getItemMeta() {
      return this.itemStack.getItemMeta();
   }

   public BukkitItemStack editMeta(Consumer<ItemMeta> consumer) {
      ItemMeta meta = this.itemStack.getItemMeta();
      if (meta == null) {
         return this;
      } else {
         consumer.accept(meta);
         this.itemStack.setItemMeta(meta);
         return this;
      }
   }

   public <T extends ItemMeta> BukkitItemStack editMeta(Class<T> clazz, Consumer<T> consumer) {
      ItemMeta meta = this.itemStack.getItemMeta();
      if (meta != null && meta.getClass().equals(clazz)) {
         consumer.accept((T)meta);
         this.itemStack.setItemMeta(meta);
         return this;
      } else {
         return this;
      }
   }

   @Deprecated
   @Nullable
   @Override
   public String getName() {
      return this.name() instanceof TextComponent text ? text.content() : null;
   }

   @Override
   public Component name() {
      return this.itemStack.displayName();
   }

   public abstract BukkitItemStack setName(@Nullable String str);

   public abstract BukkitItemStack name(Component component);

   @Deprecated
   @Override
   public Collection<String> getLore() {
      return this.lore().stream().map(line -> line instanceof TextComponent text ? text.content() : null).collect(Collectors.toList());
   }

   @Override
   public Collection<Component> lore() {
      return this.itemStack.lore();
   }

   public abstract BukkitItemStack setLore(List<String> strs);

   public abstract BukkitItemStack lore(List<Component> components);

   public BukkitItemStack loreText(List<String> lore) {
      List<Component> componentList = Lists.newArrayList();

      for (String line : lore) {
         if (line != null) {
            componentList.add(Text.parse(line));
         }
      }

      return this.lore(componentList);
   }

   public BukkitItemStack clearLore() {
      ItemMeta meta = this.itemStack.getItemMeta();
      if (meta == null) {
         return this;
      } else {
         meta.setLore(null);
         this.itemStack.setItemMeta(meta);
         return this;
      }
   }

   @Override
   public int getAmount() {
      return this.itemStack.getAmount();
   }

   public abstract BukkitItemStack amount(int value);

   @Deprecated
   @Override
   public int getData() {
      return this.itemStack.getDurability();
   }

   public abstract BukkitItemStack data(int value);

   public abstract BukkitItemStack breakable(boolean flag);

   public abstract BukkitItemStack hideAll();

   public BukkitItemStack flags(AbstractItemFlag... flags) {
      return this.flags(Arrays.stream(flags).toList());
   }

   public abstract BukkitItemStack flags(Collection<AbstractItemFlag> abstractItemFlags);

   public abstract BukkitItemStack repairCost(int value);

   @Override
   public int getMaxStackSize() {
      return this.getType().getMaxStackSize();
   }

   public abstract BukkitItemStack maxStackSize(int value);

   public BukkitItemStack unstackable() {
      return this.maxStackSize(1);
   }

   @Override
   public int getModelData() {
      return Optional.ofNullable(this.getItemMeta()).<Integer>map(ItemMeta::getCustomModelData).orElse(0);
   }

   public abstract BukkitItemStack modelData(int value);

   public abstract BukkitItemStack addAttributeModifier(AbstractAttributeModifier abstractAttributeModifier);

   public BukkitItemStack skullOwner(String username) {
      return this.skullOwner(username, DashedUUIDs.getUUIDFromString(username));
   }

   public BukkitItemStack skullOwner(String username, UUID uniqueId) {
      return this.editMeta(meta -> {
         if (meta instanceof SkullMeta skullMeta) {
            PlayerProfile profile = Bukkit.createProfileExact(uniqueId, username);
            skullMeta.setPlayerProfile(profile);
         }
      });
   }

   public BukkitItemStack skullTexture(String skinTexture) {
      try {
         String decoded = Base64Util.decodeString(skinTexture);
         GsonProvider.parser();
         JsonObject obj = JsonParser.parseString(decoded).getAsJsonObject();
         String skinUrl = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
         return this.skullTexture(URI.create(skinUrl));
      } catch (Throwable e) {
         e.printStackTrace();
         return this;
      }
   }

   public BukkitItemStack skullTexture(URI url) {
      return this.editMeta(meta -> {
         if (meta instanceof SkullMeta skullMeta) {
            if (ServerVersion.isPaper()) {
               PlayerProfile profile = Bukkit.createProfileExact(DashedUUIDs.getUUIDFromString(url.toString()), null);

               try {
                  PlayerTextures textures = profile.getTextures();
                  textures.setSkin(url.toURL());
                  profile.setTextures(textures);
               } catch (Throwable e) {
                  e.printStackTrace();
               }

               skullMeta.setPlayerProfile(profile);
            } else {
               org.bukkit.profile.PlayerProfile profile = Bukkit.createPlayerProfile(DashedUUIDs.getUUIDFromString(url.toString()), null);

               try {
                  PlayerTextures textures = profile.getTextures();
                  textures.setSkin(url.toURL());
                  profile.setTextures(textures);
               } catch (Throwable ex) {
                  ex.printStackTrace();
               }

               skullMeta.setOwnerProfile(profile);
            }
         }
      });
   }

   public BukkitItemStack skullTextureId(String id) {
      return this.skullTexture(URI.create("https://textures.minecraft.net/texture/" + id));
   }

   public abstract BukkitItemStack enchant(Enchantment enchantment, int value);

   public abstract BukkitItemStack removeEnchant(Enchantment enchantment);

   public abstract BukkitItemStack enchantmentGlow(boolean flag);

   public abstract BukkitItemStack durability(int value);

   public abstract BukkitItemStack maxDurability(int value);

   public abstract BukkitItemStack canPlaceOn(Collection<Material> materials);

   public abstract BukkitItemStack canBreak(Collection<Material> materials);

   public abstract BukkitItemStack potionEffect(PotionEffectType potionEffectType, int value, int value2);

   public abstract BukkitItemStack color(int value, int value2, int value3);

   public BukkitItemStack color(Chroma color) {
      return this.color(color.red(), color.green(), color.blue());
   }

   public BukkitItemStack color(String color) {
      int r;
      int g;
      int b;
      if (color.startsWith("#")) {
         Color parsedColor = Color.decode(color);
         r = parsedColor.getRed();
         g = parsedColor.getRed();
         b = parsedColor.getRed();
      } else if (StringUtils.countMatches(color, ",") == 2) {
         String[] components = color.split(",");
         r = Integer.parseInt(components[0]);
         g = Integer.parseInt(components[1]);
         b = Integer.parseInt(components[2]);
      } else {
         org.bukkit.Color bukkitColor = DyeColor.valueOf(color.toUpperCase()).getColor();
         r = bukkitColor.getRed();
         g = bukkitColor.getGreen();
         b = bukkitColor.getBlue();
      }

      return this.color(r, g, b);
   }

   @Override
   public AbstractItemStack applyBannerComponent(DropMetadata dropMetadata, AbstractItemBannerComponent bannerComponent) {
      Collection<String> configuredPatterns = Optional.ofNullable(bannerComponent.getBannerLayers()).orElse(List.of());
      String parseError = "A BannerLayers entry is configured incorrectly. Format is 'COLOR PATTERN'. Line=";
      return this.editMeta(
         meta -> {
            if (meta instanceof BannerMeta bannerMeta) {
               if (bannerComponent.getColor() != null) {
                  try {
                     DyeColor baseColor = bannerComponent.getColor().get(dropMetadata).toBukkitDyeColor();
                     if (ServerVersion.isBefore(MinecraftVersions.v1_21)) {
                        MythicBukkit.inst().getVolatileCodeHandler().getCompatibilityHandler().setLegacyBannerPattern(bannerMeta, baseColor, "BASE");
                     } else {
                        PatternType patternType = this.resolveBannerPatternType("base");
                        if (patternType == null) {
                           MythicLogger.error("Could not resolve minecraft:base banner pattern.");
                        } else {
                           bannerMeta.addPattern(new Pattern(baseColor, patternType));
                        }
                     }
                  } catch (Exception e) {
                     MythicLogger.error("A banner color is configured incorrectly, must use a color from the Bukkit DyeColor ENUM.");
                  }
               }

               for (String bannerLayerConfig : configuredPatterns) {
                  Optional<AbstractItemBannerComponent.BannerPatternEntry> parsedPattern = AbstractItemBannerComponent.parsePatternEntry(bannerLayerConfig);
                  if (parsedPattern.isEmpty()) {
                     MythicLogger.error("A BannerLayers entry is configured incorrectly. Format is 'COLOR PATTERN'. Line=" + bannerLayerConfig);
                  } else {
                     AbstractItemBannerComponent.BannerPatternEntry bannerPattern = parsedPattern.get();

                     try {
                        if (ServerVersion.isBefore(MinecraftVersions.v1_21)) {
                           MythicBukkit.inst()
                              .getVolatileCodeHandler()
                              .getCompatibilityHandler()
                              .setLegacyBannerPattern(bannerMeta, bannerPattern.color(), this.toLegacyBannerPatternId(bannerPattern.patternId()));
                        } else {
                           PatternType patternType = this.resolveBannerPatternType(bannerPattern.patternId());
                           if (patternType == null) {
                              MythicLogger.error("Unknown banner pattern '{0}' configured in line '{1}'.", bannerPattern.patternId(), bannerLayerConfig);
                           } else {
                              bannerMeta.addPattern(new Pattern(bannerPattern.color(), patternType));
                           }
                        }
                     } catch (Exception ex) {
                        MythicLogger.error("A BannerLayers entry is configured incorrectly. Format is 'COLOR PATTERN'. Line=" + bannerLayerConfig);
                     }
                  }
               }
            } else if (meta instanceof BlockStateMeta blockStateMeta && blockStateMeta.getBlockState() instanceof Banner banner) {
               if (bannerComponent.getColor() != null) {
                  DyeColor baseColor = bannerComponent.getColor().get(dropMetadata).toBukkitDyeColor();
                  banner.setBaseColor(baseColor);
               }

               for (String bannerLayerConfigx : configuredPatterns) {
                  Optional<AbstractItemBannerComponent.BannerPatternEntry> parsedPattern = AbstractItemBannerComponent.parsePatternEntry(bannerLayerConfigx);
                  if (parsedPattern.isEmpty()) {
                     MythicLogger.error("A BannerLayers entry is configured incorrectly. Format is 'COLOR PATTERN'. Line=" + bannerLayerConfigx);
                  } else {
                     AbstractItemBannerComponent.BannerPatternEntry bannerPattern = parsedPattern.get();

                     try {
                        if (ServerVersion.isBefore(MinecraftVersions.v1_21)) {
                           MythicBukkit.inst()
                              .getVolatileCodeHandler()
                              .getCompatibilityHandler()
                              .setLegacyBannerPattern(banner, bannerPattern.color(), this.toLegacyBannerPatternId(bannerPattern.patternId()));
                        } else {
                           PatternType patternType = this.resolveBannerPatternType(bannerPattern.patternId());
                           if (patternType == null) {
                              MythicLogger.error("Unknown banner pattern '{0}' configured in line '{1}'.", bannerPattern.patternId(), bannerLayerConfigx);
                           } else {
                              banner.addPattern(new Pattern(bannerPattern.color(), patternType));
                           }
                        }
                     } catch (Exception ex2) {
                        MythicLogger.error("A BannerLayers entry is configured incorrectly. Format is 'COLOR PATTERN'. Line=" + bannerLayerConfigx);
                     }
                  }
               }
            }
         }
      );
   }

   @Nullable
   private PatternType resolveBannerPatternType(String patternId) {
      NamespacedKey namespacedKey = NamespacedKey.fromString(AbstractItemBannerComponent.normalizePatternId(patternId));
      return namespacedKey == null ? null : Registry.BANNER_PATTERN.get(namespacedKey);
   }

   private String toLegacyBannerPatternId(String patternId) {
      String normalized = AbstractItemBannerComponent.normalizePatternId(patternId);
      String[] namespacedSplit = normalized.split(":", 2);
      return namespacedSplit.length < 2 ? normalized : namespacedSplit[1];
   }

   @Override
   public AbstractItemStack applyPotionComponent(DropMetadata dropMetadata, AbstractItemPotionComponent potionData) {
      return this.editMeta(meta -> {
         if (meta instanceof PotionMeta potionMeta) {
            if (potionData.getPotionColor() != null) {
               try {
                  potionMeta.setColor(potionData.getPotionColor().get(dropMetadata).toBukkitColor());
               } catch (Exception e) {
                  MythicLogger.error("Potion color is configured incorrectly");
               }
            }

            if (potionData.getEffects() != null) {
               potionMeta.clearCustomEffects();

               for (BukkitPotionEffect effect : potionData.getEffects()) {
                  potionMeta.addCustomEffect(effect.getEffect(), false);
               }
            }
         }
      });
   }

   @Override
   public AbstractItemStack applyMobSpawnerComponent(AbstractItemSpawnerComponent spawnerData) {
      return this.editMeta(
         meta -> {
            if (meta instanceof BlockStateMeta blockStateMeta) {
               BlockState blockState = blockStateMeta.getBlockState();
               if (blockState instanceof CreatureSpawner creatureSpawner) {
                  if (spawnerData.getDelay() != null) {
                     creatureSpawner.setDelay(creatureSpawner.getDelay());
                  }

                  if (spawnerData.getMinSpawnDelay() != null) {
                     creatureSpawner.setMinSpawnDelay(spawnerData.getMinSpawnDelay());
                  }

                  if (spawnerData.getMaxSpawnDelay() != null) {
                     creatureSpawner.setMaxSpawnDelay(spawnerData.getMaxSpawnDelay());
                  }

                  if (spawnerData.getMaxNearbyEntities() != null) {
                     creatureSpawner.setMaxNearbyEntities(spawnerData.getMaxNearbyEntities());
                  }

                  if (spawnerData.getSpawnRange() != null) {
                     creatureSpawner.setSpawnRange(spawnerData.getSpawnRange());
                  }

                  if (spawnerData.getSpawnCount() != null) {
                     creatureSpawner.setSpawnCount(spawnerData.getSpawnCount());
                  }

                  if (spawnerData.getRequiredPlayerRange() != null) {
                     creatureSpawner.setRequiredPlayerRange(spawnerData.getRequiredPlayerRange());
                  }

                  Collection<SpawnerEntry> potentialSpawns = Lists.newArrayList();

                  for (AbstractItemSpawnerComponent.PotentialSpawns potentialSpawn : spawnerData.getPotentialSpawns()) {
                     String type = potentialSpawn.getType();
                     Optional<MythicMob> maybeMob = MythicBukkit.inst().getMobManager().getMythicMob(type);
                     if (!maybeMob.isEmpty()) {
                        MythicMob mm = maybeMob.get();
                        EntitySnapshot entitySnapshot = BukkitAdapter.getEntitySnapshot(mm);
                        int spawnWeight = potentialSpawn.getWeight();
                        SpawnRule spawnRule = new SpawnRule(
                           potentialSpawn.getMinBlockLight(),
                           potentialSpawn.getMaxBlockLight(),
                           potentialSpawn.getMinSkyLight(),
                           potentialSpawn.getMaxSkyLight()
                        );
                        SpawnerEntry spawnerEntry = new SpawnerEntry(entitySnapshot, spawnWeight, spawnRule);
                        potentialSpawns.add(spawnerEntry);
                     }
                  }

                  creatureSpawner.setPotentialSpawns(potentialSpawns);
               }

               blockStateMeta.setBlockState(blockState);
            }
         }
      );
   }

   @Override
   public AbstractItemStack applyTrialSpawnerComponent(AbstractItemTrialSpawnerComponent spawnerData) {
      return this.editMeta(meta -> {
         if (meta instanceof BlockStateMeta blockStateMeta) {
            BlockState blockState = blockStateMeta.getBlockState();
            if (blockState instanceof TrialSpawner var3) {
               ;
            }

            blockStateMeta.setBlockState(blockState);
         }
      });
   }

   @Override
   public AbstractItemStack applyTrimComponent(DropMetadata dropMetadata, AbstractItemTrimComponent trimComponent) {
      return ServerVersion.isBefore(MinecraftVersions.v1_20) ? this : this.editMeta(meta -> {
         String material = trimComponent.getTrimMaterial().get(dropMetadata);
         String pattern = trimComponent.getTrimPattern().get(dropMetadata);
         if (!material.contains(":")) {
            material = "minecraft:" + material;
         }

         if (!pattern.contains(":")) {
            pattern = "minecraft:" + pattern;
         }

         NamespacedKey materialKey = NamespacedKey.fromString(material);
         NamespacedKey patternKey = NamespacedKey.fromString(pattern);
         TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.get(materialKey);
         TrimPattern trimPattern = Registry.TRIM_PATTERN.get(patternKey);
         if (trimMaterial == null) {
            MythicLogger.error("Trim Material {0} not found", materialKey.toString());
         } else if (trimPattern == null) {
            MythicLogger.error("Trim Pattern {0} not found", patternKey.toString());
         } else {
            if (meta instanceof ArmorMeta armorMeta) {
               ArmorTrim trim = new ArmorTrim(trimMaterial, trimPattern);
               armorMeta.setTrim(trim);
            }
         }
      });
   }

   public abstract BukkitItemStack customData(String str, Tag tag);

   public abstract BukkitItemStack customData(CompoundTag compoundTag);

   @Override
   public AbstractItemStack mythicItemType(String itemType) {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.TYPE, PersistentDataType.STRING, itemType));
   }

   @Override
   public Optional<String> getMythicItemType() {
      return this.getItemMeta() != null && this.getItemMeta().getPersistentDataContainer().has(ItemKeys.TYPE, PersistentDataType.STRING)
         ? Optional.of(this.getItemMeta().getPersistentDataContainer().get(ItemKeys.TYPE, PersistentDataType.STRING))
         : Optional.empty();
   }

   @Override
   public AbstractItemStack mythicItemVersion(int itemVersion) {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.VERSION, PersistentDataType.INTEGER, itemVersion));
   }

   @Override
   public Optional<Integer> getMythicItemVersion() {
      return this.getItemMeta().getPersistentDataContainer().has(ItemKeys.VERSION, PersistentDataType.INTEGER)
         ? Optional.of(this.getItemMeta().getPersistentDataContainer().get(ItemKeys.VERSION, PersistentDataType.INTEGER))
         : Optional.empty();
   }

   public BukkitItemStack preventEnchanting() {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.PREVENT_ENCHANTING, PersistentDataType.BYTE, (byte)1));
   }

   public BukkitItemStack preventRepairingWith() {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.PREVENT_REPAIRING_WITH, PersistentDataType.BYTE, (byte)1));
   }

   public BukkitItemStack preventCraftingWith() {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.PREVENT_CRAFTING_WITH, PersistentDataType.BYTE, (byte)1));
   }

   public BukkitItemStack preventSmithingWith() {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.PREVENT_SMITHING_WITH, PersistentDataType.BYTE, (byte)1));
   }

   public BukkitItemStack UUID(UUID uuid) {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.UUID, PersistentDataType.STRING, uuid.toString()));
   }

   @Override
   public UUID getUUID() {
      String ret = this.getItemMeta().getPersistentDataContainer().get(ItemKeys.UUID, PersistentDataType.STRING);
      return ret == null ? null : UUID.fromString(ret);
   }

   public BukkitItemStack timestamp(long timestamp) {
      return this.editMeta(meta -> meta.getPersistentDataContainer().set(ItemKeys.TIMESTAMP, PersistentDataType.LONG, timestamp));
   }

   @Override
   public long getTimestamp() {
      Long ret = this.getItemMeta().getPersistentDataContainer().get(ItemKeys.TIMESTAMP, PersistentDataType.LONG);
      return ret == null ? 0L : ret;
   }

   public abstract BukkitItemStack itemModel(NamespacedKey namespacedKey);

   @Override
   public boolean isSimilar(AbstractItemStack other) {
      if (other instanceof BukkitItemStack bukkit) {
         ItemStack otherBukkit = bukkit.itemStack;
         Optional<String> maybeType = this.getMythicItemType();
         Optional<String> maybeOtherType = other.getMythicItemType();
         if (maybeType.isPresent() && maybeOtherType.isPresent() && Objects.equals(maybeType.get(), maybeOtherType.get())) {
            return true;
         } else {
            if (MythicBukkit.inst().getCompatibility().getMMOItems().isPresent()) {
               CompoundTag data = this.getCustomData();
               CompoundTag otherData = other.getCustomData();
               if (data.getString("MMOITEMS_ITEM_TYPE") != null
                  && Objects.equals(data.getString("MMOITEMS_ITEM_TYPE"), otherData.getString("MMOITEMS_ITEM_TYPE"))) {
                  return true;
               }

               if (data.getString("MMOITEMS_ITEM_ID") != null && Objects.equals(data.getString("MMOITEMS_ITEM_ID"), otherData.getString("MMOITEMS_ITEM_ID"))) {
                  return true;
               }
            }

            return this.itemStack.isSimilar(otherBukkit);
         }
      } else {
         return false;
      }
   }

   public boolean isSimilar(ItemStack other) {
      return this.itemStack.isSimilar(other);
   }

   public static Collection<AbstractItemStack> getMatches(String regex) {
      Collection<AbstractItemStack> bukkitItemStacks = new HashSet<>();
      if (regex.toLowerCase().startsWith("mmoitems.") && MythicBukkit.inst().getCompatibility().getMMOItems().isPresent()) {
         bukkitItemStacks.add(MythicBukkit.inst().getBootstrap().createItem(regex));
      } else {
         for (String itemNames : MythicBukkit.inst().getItemManager().getItemNames()) {
            if (itemNames.matches(Patterns.toRegex(regex))) {
               bukkitItemStacks.add(MythicBukkit.inst().getBootstrap().createItem(itemNames));
            }
         }

         for (Material mats : Material.values()) {
            if (mats.toString().matches(Patterns.toRegex(regex.toUpperCase()))) {
               bukkitItemStacks.add(BukkitAdapter.adapt(mats));
            }
         }
      }

      return bukkitItemStacks;
   }

   @Override
   public int hashCode() {
      Material material = this.itemStack.getType();
      int cmd = this.itemStack.hasItemMeta() && this.itemStack.getItemMeta().hasCustomModelData() ? this.itemStack.getItemMeta().getCustomModelData() : 0;
      return Objects.hash(material, cmd);
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof AbstractItemStack abstractItemStack ? this.isSimilar(abstractItemStack) : false;
   }
}
