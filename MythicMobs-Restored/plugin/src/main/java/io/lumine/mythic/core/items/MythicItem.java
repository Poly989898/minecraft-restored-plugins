package io.lumine.mythic.core.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lumine.mythic.api.adapters.AbstractAttributeModifier;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemComponent;
import io.lumine.mythic.api.adapters.AbstractItemFlag;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractItemStackRarity;
import io.lumine.mythic.api.adapters.items.components.AbstractItemAttackRangeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBannerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlockStateComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlocksAttacksComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBreakSoundComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBucketEntityDataComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemCustomModelDataComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDamageResistantComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDamageTypeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDeathProtectionComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemEnchantableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemEquippableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemFoodComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemGliderComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemInstrumentComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemJukeboxPlayableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemKineticWeaponComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemLodestoneTrackerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemMinimumAttackChargeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemNoteBlockSoundComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemOminousBottleAmplifierComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPaintingVariantComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPiercingWeaponComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPotDecorationsComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPotionComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemRepairableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemSpawnerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemSwingAnimationComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTooltipComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTrialSpawnerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTrimComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseCooldownComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseEffectsComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseRemainderComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemWeaponComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemWorkstationComponent;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.api.skills.placeholders.PlaceholderColor;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitAttribute;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.adapters.item.ItemComponentBukkitItemStack;
import io.lumine.mythic.bukkit.events.MythicApplyEnchantEvent;
import io.lumine.mythic.bukkit.events.MythicMobItemGenerateEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Base64Util;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import io.lumine.mythic.core.menus.items.ItemMenuContext;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMetadataKey;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.Patterns;
import io.lumine.mythic.core.utils.jnbt.ByteTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTagBuilder;
import io.lumine.mythic.core.utils.jnbt.DoubleTag;
import io.lumine.mythic.core.utils.jnbt.ListTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderDoubleTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderFloatTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderIntTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderStringTag;
import io.lumine.mythic.core.utils.jnbt.StringTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class MythicItem implements Comparable<MythicItem>, MenuData<ItemMenuContext> {
   @Getter
   private final MythicConfig config;
   private AbstractItemStack cachedBaseItem;
   @Getter
   private ItemStack cachedMenuItem;
   private boolean imported = false;
   @Getter
   private final String internalName;
   @Getter
   private final Pack pack;
   @Getter
   private final File file;
   @Getter @Setter
   private String group = "OTHER";
   @Getter @Setter
   private String displayName;
   @Getter @Setter
   private String itemName;
   @Setter
   private String strMaterial;
   @Getter
   private Material material;
   @Getter @Setter
   private int customModelData;
   @Deprecated
   private byte data;
   @Getter
   private int amount = 1;
   @Getter @Setter
   private PlaceholderInt durability;
   @Getter @Setter
   private PlaceholderInt maxDurability;
   @Getter
   private int maxStackSize;
   @Getter
   private Boolean enchantGlint;
   @Getter @Setter
   private Boolean fireResistant;
   @Getter @Setter
   private AbstractItemStackRarity rarity;
   @Getter @Setter
   private PlaceholderInt maxEnchantments;
   @Getter @Setter
   private Boolean preventEnchanting;
   @Getter @Setter
   private Boolean preventAnvilWith;
   @Getter @Setter
   private Boolean preventCraftingWith;
   @Getter @Setter
   private Boolean preventSmithingWith;
   @Getter @Setter
   private PlaceholderColor dropColor;
   @Getter @Setter
   private PlaceholderColor color;
   @Getter @Setter
   private DyeColor shieldColor;
   @Getter @Setter
   private PlaceholderString armorTrim;
   @Getter
   private String player;
   @Getter
   private String skinURL;
   @Getter
   private UUID skinUUID;
   @Getter
   private Optional<String> loreTemplate = Optional.empty();
   @Setter
   private List<PlaceholderString> lore;
   @Getter @Setter
   private Map<Enchantment, PlaceholderInt> enchantments = Maps.newConcurrentMap();
   @Getter @Setter
   private Collection<AbstractItemComponent> components = Lists.newArrayList();
   @Getter @Setter
   private PlaceholderString bookAuthor;
   @Getter @Setter
   private PlaceholderString bookTitle;
   @Getter @Setter
   private List<PlaceholderString> bookPages;
   @Getter @Setter
   private List<String> potionEffects;
   @Getter @Setter
   private List<String> fireworkColors;
   @Getter @Setter
   private List<String> fireworkFadeColors;
   @Getter @Setter
   private AbstractItemFoodComponent foodData;
   @Getter @Setter
   private AbstractItemPotionComponent potionData;
   @Getter @Setter
   private AbstractItemEquippableComponent equippableData;
   @Getter @Setter
   private AbstractItemUseCooldownComponent useCooldownData;
   @Getter @Setter
   private AbstractItemToolComponent toolData;
   @Getter @Setter
   private AbstractItemTooltipComponent tooltipComponent;
   @Getter @Setter
   private AbstractItemSpawnerComponent spawnerData;
   @Getter @Setter
   private AbstractItemTrimComponent trimComponent;
   @Getter @Setter
   private AbstractItemCustomModelDataComponent customModelDataComponent;
   @Getter @Setter
   private NamespacedKey itemModel;
   @Getter
   private Map<String, Tag> itemNBT = new HashMap<>();
   private final List<AbstractAttributeModifier> itemAttributes = new ArrayList<>();
   private final Map<String, Tag> itemOptions = new HashMap<>();
   @Getter
   private final List<String> hideOptions = new ArrayList<>();
   @Getter
   private final List<String> hiddenComponents = new ArrayList<>();
   @Getter
   private final List<String> unsetComponents = new ArrayList<>();
   private final List<Material> placeOptions = new ArrayList<>();
   private final List<Material> breakOptions = new ArrayList<>();
   @Getter
   private boolean unbreakable;
   @Getter
   private boolean hideFlags;
   @Getter
   private boolean hideTooltip;
   @Getter
   private boolean hidden = false;
   @Getter @Setter
   private String mythicTypeOverride;
   @Getter @Setter
   private Boolean appendType = true;
   @Getter
   private boolean preventStacking = false;
   @Getter
   private boolean generateUUID = false;
   @Getter
   private boolean generateTimestamp = false;
   @Getter
   private boolean dropLootsplosion = false;
   @Getter
   private boolean dropClientSide = false;
   @Getter
   private boolean dropHologram = false;
   @Getter
   private boolean dropVFX = false;
   @Getter
   private String dropVFXMaterial = null;
   @Getter
   private int dropVFXData = 0;
   @Getter
   private String dropVFXColor = null;
   @Getter
   private String dropBillboarding = "VERTICAL";
   @Getter
   private int dropBrightness = 0;
   @Getter
   private Version itemVersion;
   @Getter
   private boolean preserveStatRatioOnUpdate = true;
   @Getter
   private String dropGlowColor = null;
   @Getter
   private Chroma dropBeamColor = null;
   @Getter
   private boolean dropGlow = false;
   @Getter
   private boolean dropBeam = false;
   @Getter
   private final Map<SkillTrigger<?>, Queue<SkillMechanic>> dropSkills = new HashMap<>();

   private static MythicBukkit getPlugin() {
      return MythicBukkit.inst();
   }

   public MythicItem(Pack pack, File file, String internalName) {
      this.pack = pack;
      this.file = file;
      this.internalName = internalName;
      this.config = new MythicConfigImpl(internalName, file);
      this.config.load();
      this.config.set("Material", "STONE");
      this.config.set("Display", internalName);
      this.config.save();
      this.loadItem();
   }

   public MythicItem(Pack pack, File file, String internalName, MythicConfig mc) {
      this.pack = pack;
      this.config = mc;
      this.file = file;
      this.internalName = internalName;
      this.loadItem();
   }

   public void loadItem() {
      this.itemNBT.clear();
      this.itemOptions.clear();
      this.hideOptions.clear();
      this.hiddenComponents.clear();
      this.unsetComponents.clear();
      this.placeOptions.clear();
      this.breakOptions.clear();
      this.dropLootsplosion = this.config.getBoolean("DropOptions.DropLootsplosion", false);
      this.dropClientSide = this.config.getBoolean("DropOptions.DropClientSide", false);
      this.dropHologram = this.config.getBoolean("DropOptions.DropHologram", false);
      this.dropVFX = this.config.getBoolean("DropOptions.DropVFX", false);
      this.dropVFXMaterial = this.config.getString("DropOptions.DropVFXMaterial", null);
      this.dropVFXData = this.config.getInteger("DropOptions.DropVFXData", 0);
      this.dropVFXColor = this.config.getString("DropOptions.DropVFXColor", null);
      this.dropBillboarding = this.config.getString("DropOptions.DropBillboarding", "VERTICAL");
      this.dropBrightness = this.config.getInteger("DropOptions.DropBrightness", 0);
      this.mythicTypeOverride = this.internalName;
      this.data = (byte)this.config.getInteger("Data", 0);
      this.durability = this.config.getPlaceholderInt(new String[]{"Options.Durability", "Durability"}, String.valueOf(this.data));
      this.maxDurability = this.config.getPlaceholderInt(new String[]{"Options.MaxDurability", "MaxDurability", "CustomDurability.Durability"}, "0");
      if (this.config.isSet("ItemStack")) {
         ItemStack is = this.config.getItemStack("ItemStack", null);
         if (is != null) {
            this.cachedBaseItem = BukkitAdapter.adapt(is);

            try {
               this.displayName = is.getItemMeta().getDisplayName();
               this.lore = new ArrayList<>();

               for (String s : is.getItemMeta().getLore()) {
                  this.lore.add(PlaceholderString.of(s));
               }

               this.imported = true;
            } catch (Exception e) {
               MythicLogger.errorItemConfig(this, this.config, "Couldn't get name/lore of ItemStack");
            }
         } else {
            MythicLogger.errorItemConfig(this, this.config, "Failed to load imported Bukkit ItemStack");
            this.cachedBaseItem = BukkitAdapter.adapt(Material.STONE);
         }
      } else {
         this.strMaterial = this.config.getString(new String[]{"Id", "Material"}, ConfigExecutor.getItemDefaultMaterial()).toUpperCase(Locale.ROOT);
         this.setupMaterial(this.strMaterial);
      }

      this.group = this.config.getString(new String[]{"Group", "ItemType", "Options.Group", "Options.ItemType"}, "OTHER");
      this.amount = this.config.getInteger("Amount", 1);
      this.displayName = SkillString.parseMessageSpecialChars(this.config.getString("Display"));
      this.itemName = SkillString.parseMessageSpecialChars(this.config.getString("ItemName"));
      this.hidden = this.config.getBoolean("Hidden", false);
      if (this.config.isList("Lore")) {
         this.lore = this.config.getPlaceholderStringList("Lore");
      } else if (this.config.isConfigurationSection("Lore")) {
         this.loreTemplate = Optional.ofNullable(this.config.getString("Lore.Template", null));
      }

      this.bookAuthor = this.config.getPlaceholderString("Author", "Unknown");
      this.bookTitle = this.config.getPlaceholderString("Title", "Unknown");
      if (this.config.isList("Pages")) {
         this.bookPages = this.config.getPlaceholderStringList("Pages");
      }

      this.fireworkColors = this.config.getStringList("Firework.Colors");
      this.fireworkFadeColors = this.config.getStringList("Firework.FadeColors");
      if (this.config.isSet("Rarity")) {
         try {
            this.rarity = AbstractItemStackRarity.valueOf(this.config.getString("Rarity").toUpperCase(Locale.ROOT));
         } catch (Throwable ex) {
            MythicLogger.errorItemConfig(this, this.config, "Couldn't load rarity");
         }
      }

      String customModelDataKey = AbstractItemCustomModelDataComponent.resolveStructuredKey(this.config);
      if (customModelDataKey != null) {
         this.customModelDataComponent = new AbstractItemCustomModelDataComponent(this, this.config, customModelDataKey);
         if (this.customModelDataComponent.hasValues()) {
            this.components.add(this.customModelDataComponent);
         }
      }

      if (this.config.isSet("TooltipStyle")) {
         this.tooltipComponent = new AbstractItemTooltipComponent(this, this.config);
         this.components.add(this.tooltipComponent);
      }

      if (AbstractItemBannerComponent.isConfigured(this.config)) {
         AbstractItemBannerComponent componentData = new AbstractItemBannerComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("Equippable")) {
         this.equippableData = new AbstractItemEquippableComponent(this, this.config);
         this.components.add(this.equippableData);
      }

      if (this.config.isSet("Food")) {
         AbstractItemFoodComponent componentData = new AbstractItemFoodComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("Consumable")) {
         AbstractItemConsumableComponent componentData = getPlugin().getBootstrap().createItemConsumableComponent(this, this.config);
         if (componentData != null) {
            this.components.add(componentData);
         }
      }

      if (this.config.isSet("Potion") || this.config.isSet("PotionEffects")) {
         AbstractItemPotionComponent componentData = new AbstractItemPotionComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("Tool")) {
         AbstractItemToolComponent componentData = new AbstractItemToolComponent(this, this.config.getNestedConfig("Tool"));
         this.components.add(componentData);
      }

      if (this.config.isSet("Spawner")) {
         AbstractItemSpawnerComponent componentData = new AbstractItemSpawnerComponent(this, this.config.getNestedConfig("Spawner"));
         this.components.add(componentData);
      }

      if (this.config.isSet("TrialSpawner")) {
         AbstractItemTrialSpawnerComponent componentData = new AbstractItemTrialSpawnerComponent(this, this.config.getNestedConfig("TrialSpawner"));
         this.components.add(componentData);
      }

      if (this.config.isSet("UseCooldown")) {
         AbstractItemUseCooldownComponent componentData = new AbstractItemUseCooldownComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("Enchantability") && !this.config.isConfigurationSection("Enchantability")) {
         AbstractItemEnchantableComponent componentData = new AbstractItemEnchantableComponent(this, this.config.getInt("Enchantability"));
         this.components.add(componentData);
      } else if (this.config.isSet("Options.Enchantability") && !this.config.isConfigurationSection("Options.Enchantability")) {
         AbstractItemEnchantableComponent componentData = new AbstractItemEnchantableComponent(this, this.config.getInt("Options.Enchantability"));
         this.components.add(componentData);
      }

      if (this.config.isSet("Weapon")) {
         AbstractItemWeaponComponent componentData = new AbstractItemWeaponComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("KineticWeapon") || this.config.isSet("Options.KineticWeapon")) {
         AbstractItemKineticWeaponComponent componentData = this.config.isSet("KineticWeapon")
            ? new AbstractItemKineticWeaponComponent(this, this.config.getNestedConfig("KineticWeapon"))
            : new AbstractItemKineticWeaponComponent(this, this.config.getNestedConfig("Options.KineticWeapon"));
         this.components.add(componentData);
      }

      if (this.config.isSet("UseEffects") || this.config.isSet("Options.UseEffects")) {
         AbstractItemUseEffectsComponent componentData = this.config.isSet("UseEffects")
            ? new AbstractItemUseEffectsComponent(this, this.config.getNestedConfig("UseEffects"))
            : new AbstractItemUseEffectsComponent(this, this.config.getNestedConfig("Options.UseEffects"));
         this.components.add(componentData);
      }

      for (AbstractItemSwingAnimationComponent.Kind kind : AbstractItemSwingAnimationComponent.Kind.values()) {
         String key = this.config.isSet(kind.getSection()) ? kind.getSection() : "Options." + kind.getSection();
         if (this.config.isSet(key)) {
            this.components.add(new AbstractItemSwingAnimationComponent(this, this.config.getNestedConfig(key), kind));
         }
      }

      if (this.config.isSet("MinimumAttackCharge") && !this.config.isConfigurationSection("MinimumAttackCharge")) {
         AbstractItemMinimumAttackChargeComponent componentData = new AbstractItemMinimumAttackChargeComponent(
            this, this.config.getFloat("MinimumAttackCharge")
         );
         this.components.add(componentData);
      } else if (this.config.isSet("Options.MinimumAttackCharge") && !this.config.isConfigurationSection("Options.MinimumAttackCharge")) {
         AbstractItemMinimumAttackChargeComponent componentData = new AbstractItemMinimumAttackChargeComponent(
            this, this.config.getFloat("Options.MinimumAttackCharge")
         );
         this.components.add(componentData);
      }

      if (this.config.isSet("UseRemainder") || this.config.isSet("Options.UseRemainder")) {
         AbstractItemUseRemainderComponent componentData = new AbstractItemUseRemainderComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("BreakSound") || this.config.isSet("Options.BreakSound")) {
         AbstractItemBreakSoundComponent componentData = new AbstractItemBreakSoundComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("DamageType") || this.config.isSet("Options.DamageType")) {
         AbstractItemDamageTypeComponent componentData = new AbstractItemDamageTypeComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("DamageResistant") || this.config.isSet("Options.DamageResistant")) {
         AbstractItemDamageResistantComponent componentData = new AbstractItemDamageResistantComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("PaintingVariant") || this.config.isSet("Options.PaintingVariant")) {
         AbstractItemPaintingVariantComponent componentData = new AbstractItemPaintingVariantComponent(this, this.config);
         this.components.add(componentData);
      }

      for (AbstractItemWorkstationComponent.Kind kindx : AbstractItemWorkstationComponent.Kind.values()) {
         if (this.config.isConfigurationSection(kindx.getSection())) {
            this.components.add(new AbstractItemWorkstationComponent(this.config.getNestedConfig(kindx.getSection()), kindx));
         } else if (this.config.isSet(kindx.getSection())) {
            MythicLogger.errorItemConfig(this, this.config, kindx.getSection() + " must be a section with a " + kindx.getValueKey() + " field");
         }
      }

      if (this.config.isSet("BlocksAttacks")) {
         AbstractItemBlocksAttacksComponent componentData = new AbstractItemBlocksAttacksComponent(this, this.config.getNestedConfig("BlocksAttacks"));
         this.components.add(componentData);
      }

      if (this.config.isSet("JukeboxPlayable") || this.config.isSet("Options.JukeboxPlayable")) {
         AbstractItemJukeboxPlayableComponent componentData = new AbstractItemJukeboxPlayableComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("Instrument") || this.config.isSet("Options.Instrument")) {
         AbstractItemInstrumentComponent componentData = new AbstractItemInstrumentComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("NoteBlockSound") || this.config.isSet("Options.NoteBlockSound")) {
         AbstractItemNoteBlockSoundComponent componentData = new AbstractItemNoteBlockSoundComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("LodestoneTracker") || this.config.isSet("Options.LodestoneTracker")) {
         MythicConfig componentConfig;
         if (this.config.isConfigurationSection("LodestoneTracker")) {
            componentConfig = this.config.getNestedConfig("LodestoneTracker");
         } else if (this.config.isConfigurationSection("Options.LodestoneTracker")) {
            componentConfig = this.config.getNestedConfig("Options.LodestoneTracker");
         } else {
            componentConfig = null;
            MythicLogger.errorItemConfig(this, this.config, "LodestoneTracker must be a section with Location and/or Tracked fields");
         }

         if (componentConfig != null) {
            AbstractItemLodestoneTrackerComponent componentData = new AbstractItemLodestoneTrackerComponent(this, componentConfig);
            this.components.add(componentData);
         }
      }

      if (this.config.isSet("Glider") || this.config.isSet("Options.Glider")) {
         AbstractItemGliderComponent componentData = new AbstractItemGliderComponent(this);
         this.components.add(componentData);
      }

      if (this.config.isSet("PotDecorations")) {
         AbstractItemPotDecorationsComponent componentData = new AbstractItemPotDecorationsComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("OminousBottleAmplifier") || this.config.isSet("Options.OminousBottleAmplifier")) {
         AbstractItemOminousBottleAmplifierComponent componentData = new AbstractItemOminousBottleAmplifierComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("BucketEntityData")) {
         AbstractItemBucketEntityDataComponent componentData = new AbstractItemBucketEntityDataComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("PiercingWeapon")) {
         AbstractItemPiercingWeaponComponent componentData = new AbstractItemPiercingWeaponComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("Repairable")) {
         AbstractItemRepairableComponent componentData = new AbstractItemRepairableComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("DeathProtection")) {
         AbstractItemDeathProtectionComponent componentData = getPlugin()
            .getBootstrap()
            .createItemDeathProtectionComponent(this, this.config.getNestedConfig("DeathProtection"));
         if (componentData != null) {
            this.components.add(componentData);
         }
      }

      if (this.config.isSet("BlockStates")) {
         AbstractItemBlockStateComponent componentData = new AbstractItemBlockStateComponent(this, this.config);
         this.components.add(componentData);
      }

      if (this.config.isSet("AttackRange")) {
         if (this.config.isConfigurationSection("AttackRange")) {
            AbstractItemAttackRangeComponent componentData = new AbstractItemAttackRangeComponent(this, this.config);
            this.components.add(componentData);
         } else {
            AbstractItemAttackRangeComponent componentData = new AbstractItemAttackRangeComponent(this, this.config.getFloat("AttackRange"));
            this.components.add(componentData);
         }
      }

      if (this.config.isConfigurationSection("Trim")) {
         this.trimComponent = new AbstractItemTrimComponent(this, this.config);
      } else if (this.config.isConfigurationSection("Options.Trim")) {
         this.trimComponent = new AbstractItemTrimComponent(this, this.config);
      } else if (this.config.isSet("Trim") || this.config.isSet("Options.Trim")) {
         String armorTrim = this.config.getString(new String[]{"Trim", "Options.Trim"}, null);
         String material = "iron";
         String[] parts = armorTrim.split("\\.", 2);
         String pattern;
         if (parts.length > 1) {
            material = parts[0];
            pattern = parts[1];
         } else {
            pattern = armorTrim;
         }

         this.trimComponent = new AbstractItemTrimComponent(this, material, pattern);
      }

      if (this.trimComponent != null) {
         this.components.add(this.trimComponent);
      }

      if (this.material == Material.SHIELD) {
         try {
            this.shieldColor = DyeColor.valueOf(this.config.getString("Options.Color", null));
         } catch (Throwable ex2) {
         }
      } else {
         this.color = this.config.getPlaceholderColor("Options.Color", null);
      }

      String shieldColorString = this.config.getString("Options.Color", null);
      if (shieldColorString != null) {
         try {
            this.shieldColor = DyeColor.valueOf(shieldColorString);
         } catch (Throwable ex3) {
         }
      }

      this.player = this.config.getString("Options.Player");
      String skinId = this.config.getString("Options.SkinID", null);
      if (skinId != null) {
         skinId = "https://textures.minecraft.net/texture/" + skinId;
      }

      this.skinURL = this.config.getString("Options.SkinURL", skinId);
      String skinTexture = this.config.getString("Options.SkinTexture");
      if (skinTexture != null) {
         try {
            String decoded = Base64Util.decodeString(skinTexture);
            GsonProvider.parser();
            JsonObject obj = JsonParser.parseString(decoded).getAsJsonObject();
            this.skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
         } catch (Throwable ex4) {
            MythicLogger.errorItemConfig(this, this.config, "Invalid SkinTexture provided");
         }
      }

      if (this.skinURL != null) {
         this.skinUUID = MythicUtil.getUUIDFromString(this.skinURL);
      }

      this.unbreakable = this.config.getBoolean("Options.Indestructible", false);
      this.unbreakable = this.config.getBoolean("Options.Indestructable", this.unbreakable);
      this.unbreakable = this.config.getBoolean("Options.Unbreakable", this.unbreakable);
      this.hideFlags = this.config.getBoolean("Options.HideFlags", false);
      this.hideTooltip = this.config.getBoolean("Options.HideTooltip", false);
      this.appendType = this.config.getBoolean("Options.AppendType", true);
      String model = this.config.getString("Model", "-1");
      model = this.config.getString("Options.Model", model);
      OptionalInt maybeNumber = Numbers.parseInteger(model);
      if (maybeNumber.isPresent()) {
         this.customModelData = maybeNumber.getAsInt();
         model = null;
      }

      String itemModel = this.config.getString("ItemModel", model);
      itemModel = this.config.getString("Options.ItemModel", model);
      if (itemModel != null) {
         if (!itemModel.contains(":")) {
            itemModel = "minecraft:" + itemModel;
         }

         this.itemModel = NamespacedKey.fromString(itemModel);
      }

      this.customModelData = this.getLegacyCustomModelData("Options.CustomModelData", this.customModelData);
      this.customModelData = this.getLegacyCustomModelData("CustomModelData", this.customModelData);
      this.maxEnchantments = PlaceholderInt.of(this.config.getString("Options.MaxEnchants", "-1"));
      this.preventEnchanting = this.config.getBoolean("Options.PreventVanillaEnchanting", false);
      this.preventEnchanting = this.config.getBoolean("Options.PreventEnchanting", this.preventEnchanting);
      boolean hasCustomModelData = this.customModelData > 0 || this.customModelDataComponent != null && this.customModelDataComponent.hasValues();
      String dropSkills = String.valueOf(ConfigExecutor.getItemDefaultPreventCrafting()).toLowerCase(Locale.ROOT);

      boolean defaultPreventCrafting = switch (dropSkills) {
         case "true" -> true;
         case "false" -> false;
         default -> hasCustomModelData;
      };
      this.preventCraftingWith = this.config.getBoolean("Options.PreventVanillaCrafting", defaultPreventCrafting);
      this.preventCraftingWith = this.config.getBoolean("Options.PreventCrafting", this.preventCraftingWith);
      this.preventAnvilWith = this.config.getBoolean("Options.PreventVanillaAnvil", hasCustomModelData);
      this.preventAnvilWith = this.config.getBoolean("Options.PreventAnvil", this.preventAnvilWith);
      this.preventSmithingWith = this.config.getBoolean("Options.PreventVanillaSmithing", hasCustomModelData);
      this.preventSmithingWith = this.config.getBoolean("Options.PreventSmithing", this.preventSmithingWith);
      this.itemVersion = Version.parse(this.config.getString("ItemUpdater.Version", "0"));
      this.preserveStatRatioOnUpdate = this.config.getBoolean("ItemUpdater.PreserveStatRatio", true);
      this.dropGlowColor = this.config.getString("DropOptions.DropGlowColor", this.config.getString("Options.DropGlowColor", null));
      this.dropBeamColor = this.config.getColor("DropOptions.DropBeamColor", this.config.getColor("Options.DropBeamColor", null));
      if (this.dropGlowColor != null) {
         this.dropGlow = true;
      }

      if (this.dropBeamColor != null) {
         this.dropBeam = true;
      }

      for (String s : this.config.getStringList("DropSkills")) {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Loading mechanic line: {0}", s);
         s = MythicLineConfigImpl.unparseBlock(s);
         SkillMechanic ms = getPlugin().getSkillManager().getMechanic(s);
         if (ms != null) {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Base skill found.");
            this.dropSkills.computeIfAbsent(ms.getTrigger(), k -> new LinkedList<>()).add(ms);
         } else {
            MythicLogger.debug(MythicLogger.DebugLevel.SKILL_CHECK, "Base skill was not found.");
         }
      }

      double mmoPvEDamage = this.config.getDouble("MMOItemsStats.PvEDamage", -1.0);
      if (mmoPvEDamage > -1.0) {
         this.itemOptions.put("MMOITEMS_PVE_DAMAGE", new DoubleTag(mmoPvEDamage));
      }

      List<String> hideOptions = this.config.getStringList("Hide");
      if (hideOptions != null) {
         for (String hide : hideOptions) {
            String upper = hide.toUpperCase(Locale.ROOT);
            String flag = upper.startsWith("HIDE_") ? upper : "HIDE_" + upper;
            if (AbstractItemFlag.of(flag) != null) {
               this.hideOptions.add(flag);
            } else {
               this.hiddenComponents.add(MythicConfig.normalizeNamespacedKey(hide));
            }
         }
      }

      this.placeOptions.addAll(this.resolveBlocks("CanPlaceOn"));
      this.breakOptions.addAll(this.resolveBlocks("CanBreak"));

      for (String component : this.config.getStringList("UnsetComponents")) {
         String componentId = MythicConfig.normalizeNamespacedKey(component);
         if ("minecraft:custom_data".equals(componentId)) {
            MythicLogger.errorItemConfig(this, this.config, "UnsetComponents cannot remove minecraft:custom_data, it holds the Mythic item data");
         } else {
            this.unsetComponents.add(componentId);
         }
      }

      int defaultStackSize = "BAG".equalsIgnoreCase(this.config.getString("Type", "")) ? 1 : this.material.getMaxStackSize();
      this.maxStackSize = this.config.getInt("Options.StackSize", defaultStackSize);
      this.enchantGlint = this.config.isSet("Options.Glint") ? this.config.getBoolean("Options.Glint") : null;
      if (this.config.getString("Options.FireResistant") != null) {
         this.fireResistant = this.config.getBoolean("Options.FireResistant");
      }

      if (this.fireResistant != null && (this.config.isSet("DamageResistant") || this.config.isSet("Options.DamageResistant"))) {
         MythicLogger.errorItemConfig(this, this.config, "DamageResistant replaces Options.FireResistant, remove one of them");
         this.fireResistant = null;
      }

      this.preventStacking = this.config.getBoolean("Options.PreventStacking", false);
      this.generateUUID = this.config.getBoolean("Options.GenerateUUID", false);
      this.generateTimestamp = this.config.getBoolean("Options.GenerateTimestamp", false);
      if (this.config.isSet("NBT")) {
         try {
            this.itemNBT = this.parseNBTNode("NBT");
         } catch (Exception ex5) {
            MythicLogger.errorItemConfig(this, this.config, "NBT tags are configured incorrectly.");
            MythicLogger.handleMinorError(ex5);
         }
      }

      if (this.config.isSet("Attributes")) {
         try {
            for (String slot : this.config.getKeys("Attributes")) {
               for (String attr : this.config.getKeys("Attributes." + slot)) {
                  String configuration = this.config.getString("Attributes." + slot + "." + attr);
                  String[] fields = configuration.split(" ");
                  String rawValue = fields[0];
                  AbstractAttributeModifier.Operation operation = null;
                  BukkitAttribute attribute = BukkitAttribute.get(attr);
                  NamespacedKey attributeKey = Optional.ofNullable(attribute)
                     .map(BukkitAttribute::getAttributeKey)
                     .orElse(NamespacedKey.minecraft(attr.toLowerCase()));
                  if (fields.length > 1) {
                     operation = AbstractAttributeModifier.Operation.parse(fields[1]);
                  }

                  String value = rawValue;
                  if (rawValue.endsWith("%")) {
                     value = String.valueOf(Double.parseDouble(configuration.substring(0, configuration.length() - 1)) / 100.0);
                     if (operation == null) {
                        operation = AbstractAttributeModifier.Operation.ADD_SCALAR;
                     }
                  }

                  if (operation == null) {
                     String attrKeyStr = attributeKey.toString();
                     if (!attrKeyStr.equals(BukkitAttribute.getAttributeKey("ATTACK_SPEED"))
                        && !attrKeyStr.equals(BukkitAttribute.getAttributeKey("MOVEMENT_SPEED"))) {
                        operation = AbstractAttributeModifier.Operation.ADD;
                     } else {
                        operation = AbstractAttributeModifier.Operation.ADD_SCALAR;
                     }
                  }

                  String tagIdString = "this.internalName_" + attributeKey + "_" + slot.toUpperCase(Locale.ENGLISH) + "_" + operation;
                  UUID tagUUID = MythicUtil.getUUIDFromString(tagIdString);
                  this.itemAttributes
                     .add(
                        new AbstractAttributeModifier(
                           attributeKey, tagUUID, PlaceholderDouble.of(value), operation, EquipSlot.of(slot.toUpperCase(Locale.ENGLISH))
                        )
                     );
               }
            }
         } catch (Exception ex6) {
            MythicLogger.errorItemConfig(this, this.config, "Attributes are configured incorrectly.");
            ex6.printStackTrace();
         }
      }

      getPlugin().getClock().queuePostReload(() -> {
         for (String in : this.config.getStringList("Enchantments")) {
            if (!this.addEnchantment(in)) {
               MythicLogger.errorItemConfig(this, this.config, "Invalid enchantment configured: " + in);
            }
         }

         this.buildItemCache();
      });
   }

   private Map<String, Tag> parseNBTNode(String node) {
      MythicConfig config = this.config;
      Map<String, Tag> tags = Maps.newConcurrentMap();

      for (String baseKey : config.getKeys(node)) {
         String innerNode = node + "." + baseKey;
         if (config.isConfigurationSection(innerNode)) {
            tags.put(baseKey, CompoundTag.create(tag -> tag.putAll(this.parseNBTNode(innerNode))));
         } else if (config.isMapList(innerNode)) {
            List<Map<?, ?>> rawMaps = config.getMapList(innerNode);
            List<CompoundTag> compounds = rawMaps.stream().map(map -> CompoundTag.create(t -> {
               for (Entry<?, ?> e : map.entrySet()) {
                  t.put(e.getKey().toString(), this.getNbtValue(e.getValue().toString()));
               }
            })).toList();
            tags.put(baseKey, new ListTag(CompoundTag.class, compounds));
         } else if (config.isList(innerNode)) {
            List<Tag> parsed = config.getStringList(innerNode).stream().map(this::getNbtValue).toList();
            Class<? extends Tag> elemType = parsed.isEmpty() ? StringTag.class : parsed.get(0).getClass();
            tags.put(baseKey, new ListTag(elemType, parsed));
         } else {
            Tag nbtValue = this.getNbtValue(config.getString(innerNode));
            tags.put(baseKey, nbtValue);
         }
      }

      return tags;
   }

   private Tag getNbtValue(String string) {
      if (string.startsWith("int/")) {
         return new PlaceholderIntTag(PlaceholderInt.of(string.substring(4)));
      } else if (string.startsWith("float/")) {
         return new PlaceholderFloatTag(PlaceholderFloat.of(string.substring(6)));
      } else if (string.startsWith("double/")) {
         return new PlaceholderDoubleTag(PlaceholderDouble.of(string.substring(7)));
      } else if (string.startsWith("byte/")) {
         return new ByteTag(Byte.parseByte(string.substring(5)));
      } else if (string.startsWith("bool/")) {
         return new ByteTag((byte)(Boolean.parseBoolean(string.substring(5)) ? 1 : 0));
      } else {
         return (string.startsWith("boolean/")
            ? new ByteTag((byte)(Boolean.parseBoolean(string.substring(8)) ? 1 : 0))
            : new PlaceholderStringTag(PlaceholderString.of(string)));
      }
   }

   public void buildItemCache() {
      try {
         this.cachedMenuItem = BukkitAdapter.adapt(this.generateItemStack(1));
      } catch (Throwable e) {
         MythicLogger.errorItemConfig(this, this.config, "Failed to generate cached menu item");
         this.cachedMenuItem = new ItemStack(Material.BARRIER);
         e.printStackTrace();
      }

      String displayName = this.displayName == null ? "<gray>(No Display Name)" : this.displayName;
      String materialName = this.material == null ? "<gray>Material Not Set" : this.material.toString();
      this.cachedMenuItem = ItemFactory.of(this.cachedMenuItem).hideAttributes().name("<green><bold>" + this.internalName).prependLore(() -> {
         List<String> lines = new ArrayList<>();
         lines.add("<red> ");
         lines.add("<yellow>" + materialName + " <gray>(Model: <aqua>" + this.customModelData + "<gray>)");
         lines.add("<red> ");
         lines.add(displayName);
         return lines.iterator();
      }).lore(() -> {
         List<String> lines = new ArrayList<>();
         lines.add("<red> ");
         lines.add("<rainbow>\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583\u2583");
         lines.add("<gray>\u22b3 <yellow>Left-Click to get Item");
         lines.add("<gray>\u22b3 <yellow>Right-Click to Edit");
         return lines.iterator();
      }).build();
   }

   @Deprecated
   public ItemStack getCachedBaseItem() {
      return BukkitAdapter.adapt(this.cachedBaseItem);
   }

   @Deprecated
   public String getMaterialName() {
      return this.strMaterial;
   }

   public boolean setMaterial(String material) {
      material = material.toUpperCase(Locale.ROOT);
      if (this.setupMaterial(material)) {
         if (this.config.isSet("Id")) {
            this.config.setSave("Id", material);
         } else {
            this.config.setSave("Material", material);
         }

         this.buildItemCache();
         return true;
      } else {
         return false;
      }
   }

   private boolean setupMaterial(String material) {
      this.strMaterial = material;

      try {
         if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_15)) {
            this.material = Material.AIR;

            try {
               this.material = this.data == 0 ? Material.valueOf(this.strMaterial) : LegacyItemConverter.getMaterial(this.strMaterial, this.data);
               if (this.material == null && this.data > 0) {
                  this.material = Material.valueOf(this.strMaterial);
               }
            } catch (Exception e) {
               this.material = LegacyItemConverter.getMaterial(this.strMaterial, this.data);
               if (this.material == null && this.data > 0) {
                  this.material = Material.valueOf(this.strMaterial);
               }
            } finally {
               this.cachedBaseItem = BukkitAdapter.adapt(this.material);
            }
         } else {
            if (this.strMaterial.matches("[0-9]*")) {
               this.material = LegacyItemConverter.getMaterial(this.strMaterial, (byte)0);
            } else {
               this.material = Material.valueOf(this.strMaterial);
            }

            this.cachedBaseItem = BukkitAdapter.adapt(this.material).data(this.data).amount(this.amount * this.amount);
         }

         return true;
      } catch (Exception ex) {
         this.material = Material.STONE;
         this.cachedBaseItem = BukkitAdapter.adapt(this.material);
         MythicLogger.errorItemConfig(this, this.config, "Material type '" + this.strMaterial + "' not found");
         return false;
      }
   }

   public List<String> getLore() {
      List<String> ret = Lists.newArrayList();
      if (this.lore == null) {
         return ret;
      } else {
         for (PlaceholderString pl : this.lore) {
            ret.add(pl.toString());
         }

         return ret;
      }
   }

   public List<PlaceholderString> getLoreRaw() {
      return this.lore;
   }

   public boolean addEnchantment(String in) {
      String[] split = in.split(" ");
      Enchantment ench = null;
      PlaceholderInt enchLevel = PlaceholderInt.of("1");

      try {
         NamespacedKey key = NamespacedKey.fromString(split[0].toLowerCase(Locale.ROOT));
         if (key != null) {
            ench = Registry.ENCHANTMENT.get(key);
         }

         if (ench == null && in.contains(":")) {
            split = in.split(":");
            ench = Enchantment.getByName(split[0]);
         }
      } catch (Error | Exception e) {
         MythicLogger.errorItemConfig(this, this.config, "Could not apply enchantment");
         MythicLogger.handleMinorError(e);
         return false;
      }

      if (ench == null) {
         MythicLogger.errorItemConfig(this, this.config, "Enchantment '" + split[0] + "' not found");
         return false;
      } else {
         if (split.length > 1) {
            enchLevel = PlaceholderInt.of(split[1]);
         }

         this.enchantments.put(ench, enchLevel);
         return true;
      }
   }

   public AbstractItemStack generateItemStack(int amount) {
      return this.generateItemStack(new DropMetadataImpl(null, null), amount);
   }

   public AbstractItemStack generateItemStack(int amount, Map<String, Object> metadata) {
      DropMetadataImpl dropMetaData = new DropMetadataImpl(null, null);
      dropMetaData.setMetadata(metadata);
      return this.generateItemStack(dropMetaData, amount);
   }

   @Deprecated
   public AbstractItemStack generateItemStack(int amount, AbstractEntity dropper, AbstractEntity trigger) {
      SkillCaster d = getPlugin().getSkillManager().getCasterNullable(dropper);
      return this.generateItemStack(new DropMetadataImpl(d, trigger), amount);
   }

   public AbstractItemStack generateItemStack(DropMetadata meta, int amount) {
      if (this.imported) {
         return this.cachedBaseItem.copy();
      } else {
         AbstractEntity dropper = Optional.ofNullable(meta).flatMap(m -> m.getDropper().map(SkillCaster::getEntity)).orElse(null);
         AbstractItemStack itemStack = this.cachedBaseItem.copy().amount(this.cachedBaseItem.getAmount() * amount);
         if (this.customModelData != -1) {
            itemStack.modelData(this.customModelData);
         }

         if (this.unbreakable) {
            itemStack.breakable(false);
         }

         if (!this.placeOptions.isEmpty()) {
            itemStack.canPlaceOn(this.placeOptions);
         }

         if (!this.breakOptions.isEmpty()) {
            itemStack.canBreak(this.breakOptions);
         }

         int repairCost = !this.config.getBoolean("Options.Repairable", true) ? Integer.MAX_VALUE : this.config.getInteger("RepairCost", -1);
         if (repairCost != -1) {
            itemStack.repairCost(repairCost);
         }

         if (this.maxStackSize > 0) {
            itemStack.maxStackSize(this.maxStackSize);
         }

         if (this.enchantGlint != null) {
            itemStack.enchantmentGlow(this.enchantGlint);
         }

         for (AbstractItemComponent component : this.components) {
            component.apply(this, meta, itemStack);
         }

         if (this.itemModel != null) {
            itemStack.itemModel(this.itemModel);
         }

         if (this.displayName != null) {
            itemStack.name(Text.parse(this.displayName));
         }

         if (this.itemName != null) {
            itemStack.itemName(Text.parse(this.itemName));
         }

         if (getPlugin().getCompatibility().getCrucible().isEmpty() && this.lore != null && !this.lore.isEmpty()) {
            ArrayList<Component> loreBuilder = new ArrayList<>();

            for (PlaceholderString pl : this.lore) {
               String str = pl.get(meta);
               str = SkillString.parseMessageSpecialChars(str);
               if (str.contains("{")) {
                  Matcher pMatcher = Patterns.LoreRanges.matcher(str);

                  while (pMatcher.find()) {
                     int min = Integer.parseInt(pMatcher.group(1));
                     int max = Integer.parseInt(pMatcher.group(2));
                     int num = Numbers.randomInt(max - min) + min;
                     str = str.replace(pMatcher.group(0), num + "");
                  }
               }

               loreBuilder.add(Text.parse(str));
            }

            itemStack.lore(loreBuilder);
         }

         if (this.preventStacking) {
            itemStack.unstackable();
            this.maxStackSize = 1;
         }

         itemStack.customData(CompoundTagBuilder.create().putAll(this.itemOptions).putAll(this.itemNBT).build());
         if (!this.itemAttributes.isEmpty()) {
            this.itemAttributes.forEach(itemStack::addAttributeModifier);
         }

         if (this.appendType) {
            itemStack.mythicItemType(this.mythicTypeOverride);
         }

         if (this.itemVersion.hasVersion()) {
            itemStack.mythicItemVersion(this.itemVersion.getNumeralVersion());
         }

         if (this.preventEnchanting) {
            itemStack.preventEnchanting();
         }

         if (this.preventAnvilWith) {
            itemStack.preventRepairingWith();
         }

         if (this.preventCraftingWith) {
            itemStack.preventCraftingWith();
         }

         if (this.preventSmithingWith) {
            itemStack.preventSmithingWith();
         }

         if (this.generateUUID) {
            itemStack.UUID(UUID.randomUUID());
         }

         if (this.generateTimestamp) {
            itemStack.timestamp(System.currentTimeMillis());
         }

         int maxDurability = this.maxDurability.get(meta, dropper);
         if (maxDurability > 0) {
            itemStack.maxDurability(maxDurability);
         }

         int durability = this.durability.get(meta, Optional.ofNullable(meta).flatMap(DropMetadata::getCause).orElse(null));
         if (durability > 0) {
            itemStack.durability(Numbers.clamp(durability, 1, itemStack.getMaxDurability()));
         }

         if (this.player != null) {
            itemStack.skullOwner(this.player, this.skinUUID);
         } else if (this.skinURL != null) {
            itemStack.skullTexture(URI.create(this.skinURL));
         }

         if (itemStack instanceof BukkitItemStack bukkit) {
            bukkit.editMeta(m -> this.buildBook(m, meta));
            if (this.material == Material.FIREWORK_ROCKET) {
               bukkit.editMeta(this::buildFirework);
            }

            if (this.material == Material.FIREWORK_STAR) {
               bukkit.editMeta(this::buildFireworkCharge);
            }

            if (this.data > 0) {
               bukkit.editMeta(MapMeta.class, m -> {
                  m.setMapId(this.data);
                  m.setMapView(Bukkit.getMap(this.data));
               });
            }

            if (this.color != null) {
               bukkit.editMeta(m -> this.setItemColor(m, meta));
            }
         }

         if (itemStack instanceof ItemComponentBukkitItemStack bukkit) {
            if (this.rarity != null) {
               bukkit.rarity(this.rarity);
            }

            if (this.fireResistant != null) {
               bukkit.fireResistant(this.fireResistant);
            }
         }

         if (this.enchantments != null) {
            this.setEnchantments(dropper, itemStack);
         }

         if (!this.hideOptions.isEmpty()) {
            itemStack.flags(this.hideOptions.stream().map(AbstractItemFlag::valueOf).toList());
         }

         if (this.hideFlags) {
            itemStack.hideAll();
         }

         if (this.hideTooltip) {
            itemStack.hideTooltip();
         }

         if (!this.hiddenComponents.isEmpty()) {
            itemStack.hideTooltipComponents(this.hiddenComponents);
         }

         if (!this.unsetComponents.isEmpty()) {
            itemStack.unsetComponents(this.unsetComponents);
         }

         ItemStack stack = BukkitAdapter.adapt(itemStack);
         DropMetadata fmeta = meta.deeperClone();
         fmeta.setMetadata(PlaceholderMetadataKey.ITEM_STACK, stack);
         fmeta.setMetadata(PlaceholderMetadataKey.MYTHIC_TYPE, this.internalName);
         MythicMobItemGenerateEvent generateEvent = new MythicMobItemGenerateEvent(this, fmeta, stack);
         if (Bukkit.isPrimaryThread()) {
            Events.call(generateEvent);
         } else {
            Events.callSync(generateEvent);
         }

         return BukkitAdapter.adapt(generateEvent.getItemStack());
      }
   }

   private List<Material> resolveBlocks(String key) {
      ArrayList<Material> blocks = new ArrayList<>();

      for (String entry : this.config.getStringList(key)) {
         if (entry.startsWith("#")) {
            NamespacedKey tagKey = entry.length() > 1 ? NamespacedKey.fromString(entry.substring(1).toLowerCase(Locale.ROOT)) : null;
            org.bukkit.Tag<Material> tag = tagKey == null ? null : Bukkit.getTag("blocks", tagKey, Material.class);
            if (tag == null) {
               MythicLogger.errorItemConfig(this, this.config, key + " has an unknown block tag: " + entry);
            } else {
               blocks.addAll(tag.getValues());
            }
         } else {
            Material material = Material.matchMaterial(entry);
            if (material != null && material.isBlock()) {
               blocks.add(material);
            } else {
               MythicLogger.errorItemConfig(this, this.config, key + " has an unknown block: " + entry);
            }
         }
      }

      return blocks;
   }

   private int getLegacyCustomModelData(String key, int def) {
      String value = this.config.getString(key, null);
      if (value != null) {
         OptionalInt maybeNumber = Numbers.parseInteger(value.trim());
         if (maybeNumber.isPresent()) {
            return maybeNumber.getAsInt();
         }
      }

      return this.config.getInteger(key, def);
   }

   private void setEnchantments(AbstractEntity entity, AbstractItemStack itemStack) {
      if (this.enchantments != null && !this.enchantments.isEmpty()) {
         int enchantmentCount = Optional.ofNullable(entity).map(this.maxEnchantments::get).orElseGet(this.maxEnchantments::get);
         ArrayList<Entry<Enchantment, PlaceholderInt>> enchantsToApply = new ArrayList<>(this.enchantments.entrySet());
         if (enchantmentCount > 0) {
            Collections.shuffle(enchantsToApply);
         } else {
            enchantmentCount = enchantsToApply.size();
         }

         for (int i = 0; i < enchantmentCount; i++) {
            Entry<Enchantment, PlaceholderInt> entry = enchantsToApply.get(i);
            Enchantment enchant = entry.getKey();
            int enchantLevel = entry.getValue().get(entity);
            if (enchantLevel != 0) {
               this.applyEnchantToItem(itemStack, enchant, enchantLevel);
            }
         }
      }
   }

   private void applyEnchantToItem(AbstractItemStack itemStack, Enchantment enchant, int enchantLevel) {
      MythicApplyEnchantEvent event = new MythicApplyEnchantEvent(BukkitAdapter.adapt(itemStack), enchant, enchantLevel);
      Events.callAndReturn(event);
      if (!event.getWasHandled()) {
         itemStack.enchant(enchant, enchantLevel);
      }
   }

   private void buildFirework(ItemMeta im) {
      if (im instanceof FireworkMeta pm) {
         Builder builder = FireworkEffect.builder();
         if (this.fireworkColors != null) {
            for (String s : this.fireworkColors) {
               try {
                  String[] rgb = s.split(",");
                  int r = Integer.parseInt(rgb[0]);
                  int g = Integer.parseInt(rgb[1]);
                  int b = Integer.parseInt(rgb[2]);
                  builder.withColor(Color.fromRGB(r, g, b));
               } catch (Exception e) {
                  MythicLogger.errorItemConfig(this, this.config, "A firework color is configured incorrectly. Format is 'red,green,blue'. Line=" + s);
               }
            }
         }

         if (this.fireworkFadeColors != null) {
            for (String s : this.fireworkFadeColors) {
               try {
                  String[] rgb = s.split(",");
                  int r = Integer.parseInt(rgb[0]);
                  int g = Integer.parseInt(rgb[1]);
                  int b = Integer.parseInt(rgb[2]);
                  builder.withColor(Color.fromRGB(r, g, b));
               } catch (Exception ex) {
                  MythicLogger.errorItemConfig(this, this.config, "A firework fade color is configured incorrectly. Format is 'red,green,blue'. Line=" + s);
               }
            }
         }

         if (this.config.getBoolean("Firework.Flicker", false)) {
            builder.withFlicker();
         }

         if (this.config.getBoolean("Firework.Trail", false)) {
            builder.withTrail();
         }

         pm.addEffect(builder.build());
      }
   }

   private void buildFireworkCharge(ItemMeta im) {
      if (im instanceof FireworkEffectMeta pm) {
         Builder builder = FireworkEffect.builder();
         if (this.fireworkColors != null) {
            for (String s : this.fireworkColors) {
               try {
                  String[] rgb = s.split(",");
                  int r = Integer.parseInt(rgb[0]);
                  int g = Integer.parseInt(rgb[1]);
                  int b = Integer.parseInt(rgb[2]);
                  builder.withColor(Color.fromRGB(r, g, b));
               } catch (Exception e) {
                  MythicLogger.errorItemConfig(this, this.config, "A firework color is configured incorrectly. Format is 'red,green,blue'. Line=" + s);
               }
            }
         }

         if (this.fireworkFadeColors != null) {
            for (String s : this.fireworkFadeColors) {
               try {
                  String[] rgb = s.split(",");
                  int r = Integer.parseInt(rgb[0]);
                  int g = Integer.parseInt(rgb[1]);
                  int b = Integer.parseInt(rgb[2]);
                  builder.withColor(Color.fromRGB(r, g, b));
               } catch (Exception ex) {
                  MythicLogger.errorItemConfig(this, this.config, "A firework fade color is configured incorrectly. Format is 'red,green,blue'. Line=" + s);
               }
            }
         }

         if (this.config.getBoolean("Firework.Flicker", false)) {
            builder.withFlicker();
         }

         if (this.config.getBoolean("Firework.Trail", false)) {
            builder.withTrail();
         }

         pm.setEffect(builder.build());
      }
   }

   private void buildBook(ItemMeta im, DropMetadata dropMetadata) {
      if (im instanceof BookMeta bookMeta && this.bookPages != null) {
         String author = this.bookAuthor.get(dropMetadata);
         if (ServerVersion.isPaper()) {
            bookMeta.author(MiniMessage.miniMessage().deserialize(author));
         } else {
            bookMeta.setAuthor(Text.toLegacy(Text.parse(author)));
         }

         String title = this.bookTitle.get(dropMetadata);
         if (ServerVersion.isPaper()) {
            bookMeta.title(MiniMessage.miniMessage().deserialize(title));
         } else {
            bookMeta.setTitle(Text.toLegacy(Text.parse(title)));
         }

         for (PlaceholderString page : this.bookPages) {
            String contents = page.get(dropMetadata);
            if (ServerVersion.isPaper()) {
               bookMeta.addPages(new Component[]{MiniMessage.miniMessage().deserialize(contents)});
            } else {
               Component component = Text.parse(contents);
               bookMeta.addPage(new String[]{Text.toLegacy(component)});
            }
         }
      }
   }

   private void setItemColor(ItemMeta itemMeta, DropMetadata dropMetadata) {
      if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
         leatherArmorMeta.setColor(this.color.get(dropMetadata).toBukkitColor());
      } else if (itemMeta instanceof PotionMeta potionMeta) {
         potionMeta.setColor(this.color.get(dropMetadata).toBukkitColor());
      }
   }

   public int compareTo(MythicItem o) {
      return this.internalName.compareTo(o.getInternalName());
   }

   @Override
   public Icon<ItemMenuContext> getIcon() {
      return IconBuilder.<ItemMenuContext>create().itemStack(this.cachedMenuItem).hideFlags().click((context, player) -> {
         player.performCommand("mythicmobs i get " + this.internalName);
         player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
      }).rightClick((context, player) -> {
         ItemEditorMenuContext ctx = new ItemEditorMenuContext(this, context);
         getPlugin().getMenuManager().getItemEditorMenu().open(player, ctx);
      }).build();
   }
}
