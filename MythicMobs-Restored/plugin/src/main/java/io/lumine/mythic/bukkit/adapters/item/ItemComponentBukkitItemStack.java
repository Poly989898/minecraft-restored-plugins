package io.lumine.mythic.bukkit.adapters.item;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractAttributeModifier;
import io.lumine.mythic.api.adapters.AbstractItemFlag;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractItemStackRarity;
import io.lumine.mythic.api.adapters.items.components.AbstractItemAttackRangeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlockStateComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlocksAttacksComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBreakSoundComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBucketEntityDataComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemCustomModelDataComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDamageResistantComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDamageTypeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemEnchantableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemEquippableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemFoodComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemGliderComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemInstrumentComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemJukeboxPlayableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemKineticWeaponComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemLodestoneTrackerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemMinimumAttackChargeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemOminousBottleAmplifierComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPaintingVariantComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPiercingWeaponComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPotDecorationsComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPotionComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemRepairableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemSwingAnimationComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTooltipComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseCooldownComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseEffectsComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseRemainderComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemWeaponComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemWorkstationComponent;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.adapters.BukkitPotionEffect;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersion;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class ItemComponentBukkitItemStack extends BukkitItemStack {
   private static final MinecraftVersion WORKSTATION_COMPONENTS_VERSION = MinecraftVersion.parse("26.3");

   public ItemComponentBukkitItemStack(ItemStack itemStack) {
      super(itemStack);
   }

   public boolean isFireResistant() {
      return Optional.ofNullable(this.getItemMeta()).<Boolean>map(ItemMeta::isFireResistant).orElse(false);
   }

   @Nullable
   public AbstractItemStackRarity getRarity() {
      return Optional.ofNullable(this.getItemMeta()).<ItemRarity>map(ItemMeta::getRarity).map(rarity -> {
         return switch (rarity) {
            case EPIC -> AbstractItemStackRarity.EPIC;
            case RARE -> AbstractItemStackRarity.RARE;
            case UNCOMMON -> AbstractItemStackRarity.UNCOMMON;
            case COMMON -> AbstractItemStackRarity.COMMON;
            default -> throw new MatchException(null, null);
         };
      }).orElse(null);
   }

   public BukkitItemStack clone() {
      return new ItemComponentBukkitItemStack(this.itemStack.clone());
   }

   public ItemComponentBukkitItemStack editMeta(Consumer<ItemMeta> block) {
      ItemMeta meta = this.itemStack.getItemMeta();
      if (meta == null) {
         meta = Bukkit.getItemFactory().getItemMeta(this.itemStack.getType());
      }

      if (meta == null) {
         return this;
      } else {
         block.accept(meta);
         this.itemStack.setItemMeta(meta);
         return this;
      }
   }

   public ItemComponentBukkitItemStack setName(String name) {
      return this.name(Text.parse(name));
   }

   public ItemComponentBukkitItemStack name(Component component) {
      return ServerVersion.isPaper()
         ? this.editMeta(meta -> meta.displayName(component))
         : this.editMeta(meta -> meta.setDisplayName(Text.toLegacy(component)));
   }

   public ItemComponentBukkitItemStack itemName(Component component) {
      return ServerVersion.isPaper() ? this.editMeta(meta -> meta.itemName(component)) : this.editMeta(meta -> meta.setItemName(Text.toLegacy(component)));
   }

   public ItemComponentBukkitItemStack setLore(List<String> lore) {
      return this.lore(lore.stream().map(Text::parse).toList());
   }

   public ItemComponentBukkitItemStack lore(List<Component> lines) {
      if (ServerVersion.isPaper()) {
         return this.editMeta(meta -> meta.lore(lines));
      } else {
         List<String> lore = Lists.newArrayList();
         lines.forEach(line -> lore.add(Text.toLegacy(line)));
         return this.editMeta(meta -> meta.setLore(lore));
      }
   }

   public ItemComponentBukkitItemStack amount(int amount) {
      this.itemStack.setAmount(amount);
      return this;
   }

   @Deprecated
   public ItemComponentBukkitItemStack data(int data) {
      this.itemStack.setDurability((short)data);
      return this;
   }

   @Override
   public boolean isBreakable() {
      return !Optional.ofNullable(this.getItemMeta()).<Boolean>map(ItemMeta::isUnbreakable).orElse(true);
   }

   public ItemComponentBukkitItemStack breakable(boolean breakable) {
      return this.editMeta(meta -> meta.setUnbreakable(!breakable));
   }

   public ItemComponentBukkitItemStack hideAll() {
      return this.flags(Arrays.asList(AbstractItemFlag.values()));
   }

   public ItemComponentBukkitItemStack flags(Collection<AbstractItemFlag> flags) {
      this.editMeta(meta -> meta.addItemFlags(flags.stream().map(BukkitAdapter::adapt).toArray(ItemFlag[]::new)));
      return this;
   }

   public ItemComponentBukkitItemStack hideTooltip() {
      return this.editMeta(meta -> meta.setHideTooltip(true));
   }

   @Override
   public AbstractItemStack hideTooltipComponents(Collection<String> componentIds) {
      MythicLogger.error("Hiding tooltip components is only available on Paper 1.21.5+");
      return this;
   }

   @Override
   public int getRepairCost() {
      return Optional.ofNullable(this.getItemMeta()).map(meta -> meta instanceof Repairable repairable ? repairable.getRepairCost() : 0).orElse(0);
   }

   public ItemComponentBukkitItemStack repairCost(int repairCost) {
      if (this.itemStack.getItemMeta() instanceof Repairable repairable) {
         repairable.setRepairCost(repairCost);
         this.itemStack.setItemMeta(repairable);
      }

      return this;
   }

   public ItemComponentBukkitItemStack maxStackSize(int maxStackSize) {
      return this.editMeta(meta -> meta.setMaxStackSize(maxStackSize));
   }

   public ItemComponentBukkitItemStack modelData(int modelData) {
      return this.editMeta(meta -> meta.setCustomModelData(modelData));
   }

   public ItemComponentBukkitItemStack addAttributeModifier(AbstractAttributeModifier modifier) {
      Attribute attribute = Registry.ATTRIBUTE.get(modifier.key());
      if (attribute == null) {
         throw new IllegalArgumentException("Unknown attribute '" + modifier.key() + "'");
      } else {
         UUID uniqueId = modifier.uniqueId();
         EquipSlot abstractSlot = modifier.slot();
         if (abstractSlot != null && abstractSlot != EquipSlot.NONE) {
            EquipmentSlot slot = modifier.slot().getBukkitSlot();
            if (slot == null) {
               throw new IllegalStateException("Unexpected value: " + modifier.slot());
            } else {
               return this.editMeta(
                  meta -> meta.addAttributeModifier(
                     attribute,
                     new AttributeModifier(uniqueId, uniqueId.toString(), modifier.value().get(), Operation.values()[modifier.operation().getOperation()], slot)
                  )
               );
            }
         } else {
            return this.editMeta(
               meta -> meta.addAttributeModifier(
                  attribute,
                  new AttributeModifier(uniqueId, uniqueId.toString(), modifier.value().get(), Operation.values()[modifier.operation().getOperation()])
               )
            );
         }
      }
   }

   public ItemComponentBukkitItemStack enchant(Enchantment enchantment, int level) {
      if (this.itemStack.getType() != Material.ENCHANTED_BOOK) {
         this.itemStack.addUnsafeEnchantment(enchantment, level);
      } else {
         this.editMeta(m -> ((EnchantmentStorageMeta)m).addStoredEnchant(enchantment, level, true));
      }

      return this;
   }

   public ItemComponentBukkitItemStack removeEnchant(Enchantment enchantment) {
      if (this.itemStack.getType() != Material.ENCHANTED_BOOK) {
         this.itemStack.removeEnchantment(enchantment);
      } else {
         this.editMeta(m -> ((EnchantmentStorageMeta)m).removeStoredEnchant(enchantment));
      }

      return this;
   }

   public ItemComponentBukkitItemStack enchantmentGlow(boolean glow) {
      return this.editMeta(meta -> meta.setEnchantmentGlintOverride(glow));
   }

   @Override
   public int getDurability() {
      return Optional.ofNullable(this.getItemMeta()).map(meta -> meta instanceof Damageable damageable ? damageable.getDamage() : 0).orElse(0);
   }

   public ItemComponentBukkitItemStack durability(int durability) {
      ItemMeta meta = this.itemStack.getItemMeta();
      if (meta instanceof Damageable damageable) {
         damageable.setDamage(durability);
         this.itemStack.setItemMeta(meta);
      }

      return this;
   }

   @Override
   public int getMaxDurability() {
      return Optional.ofNullable(this.getItemMeta())
         .map(meta -> meta instanceof Damageable damageable && damageable.hasMaxDamage() ? damageable.getMaxDamage() : 0)
         .orElse(0);
   }

   public ItemComponentBukkitItemStack maxDurability(int durability) {
      ItemMeta meta = this.itemStack.getItemMeta();
      if (meta instanceof Damageable damageable) {
         damageable.setMaxDamage(durability);
         this.itemStack.setItemMeta(meta);
      }

      return this;
   }

   public ItemComponentBukkitItemStack canPlaceOn(Collection<Material> materials) {
      this.itemStack = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setCanPlaceOn(this.itemStack, materials);
      return this;
   }

   public ItemComponentBukkitItemStack canBreak(Collection<Material> materials) {
      this.itemStack = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setCanBreak(this.itemStack, materials);
      return this;
   }

   public ItemComponentBukkitItemStack potionEffect(PotionEffectType type, int duration, int amplifier) {
      if (this.getItemMeta() instanceof PotionMeta meta) {
         meta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
         this.itemStack.setItemMeta(meta);
      }

      return this;
   }

   public ItemComponentBukkitItemStack color(int r, int g, int b) {
      ItemMeta meta = this.itemStack.getItemMeta();
      Color color = Color.fromRGB(r, g, b);
      if (meta instanceof PotionMeta potionMeta) {
         potionMeta.setColor(color);
      } else if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
         leatherArmorMeta.setColor(color);
      }

      this.itemStack.setItemMeta(meta);
      return this;
   }

   public ItemComponentBukkitItemStack applyFoodComponent(AbstractItemFoodComponent foodData) {
      return this.editMeta(meta -> {
         if (foodData == null) {
            meta.setFood(null);
         } else {
            FoodComponent foodComponent = meta.getFood();
            if (foodData.getCanAlwaysEat() != null) {
               foodComponent.setCanAlwaysEat(foodData.getCanAlwaysEat());
            }

            if (foodData.getNutrition() != null) {
               foodComponent.setNutrition(foodData.getNutrition());
            }

            if (foodData.getSaturation() != null) {
               foodComponent.setSaturation(foodData.getSaturation());
            }

            meta.setFood(foodComponent);
         }
      });
   }

   @Override
   public AbstractItemStack applyConsumableComponent(AbstractItemConsumableComponent consumableData) {
      return this;
   }

   public ItemComponentBukkitItemStack applyPotionComponent(DropMetadata dropMetadata, AbstractItemPotionComponent potionData) {
      if (potionData.getDurationScale() != null) {
         MythicLogger.error("Potion.DurationScale requires Paper 1.21.5 or newer");
      }

      return this.editMeta(meta -> {
         if (meta instanceof PotionMeta potionMeta) {
            if (potionData.getBasePotionType() != null) {
               potionMeta.setBasePotionType(potionData.getBasePotionType());
            }

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

   public ItemComponentBukkitItemStack applyEquippableComponent(AbstractItemEquippableComponent equippableData) {
      if (ServerVersion.isBefore(MinecraftVersions.v1_21_3)) {
         return this;
      } else {
         this.editMeta(meta -> {
            if (equippableData == null) {
               meta.setEquippable(null);
            } else {
               EquippableComponent equippableComponent = meta.getEquippable();
               if (equippableComponent == null) {
               }

               if (equippableData.getModel() != null) {
                  equippableComponent.setModel(equippableData.getModel());
               }

               if (equippableData.getSlot() != null) {
                  equippableComponent.setSlot(equippableData.getSlot());
               }

               if (equippableData.getCameraOverlay() != null) {
                  equippableComponent.setCameraOverlay(equippableData.getCameraOverlay());
               }

               if (equippableData.getDispensable() != null) {
                  equippableComponent.setDispensable(equippableData.getDispensable());
               }

               if (equippableData.getSwappable() != null) {
                  equippableComponent.setSwappable(equippableData.getSwappable());
               }

               if (equippableData.getDamageOnHurt() != null) {
                  equippableComponent.setDamageOnHurt(equippableData.getDamageOnHurt());
               }

               if (equippableData.getEquipSound() != null) {
                  String soundKey = equippableData.getEquipSound().toLowerCase(Locale.ROOT);
                  if (!soundKey.contains(":")) {
                     soundKey = "minecraft:" + soundKey;
                  }

                  Sound sound = Registry.SOUNDS.get(NamespacedKey.fromString(soundKey));
                  equippableComponent.setEquipSound(sound);
               }

               if (equippableData.getEntityTypes() != null && !equippableData.getEntityTypes().isEmpty()) {
                  equippableComponent.setAllowedEntities(equippableData.getEntityTypes().stream().map(EntityType::valueOf).toList());
               }

               if (equippableData.getEquipOnInteract() != null) {
                  if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
                     equippableComponent.setEquipOnInteract(equippableData.getEquipOnInteract());
                  } else {
                     MythicLogger.error("Equippable.EquipOnInteract requires 1.21.5 or newer");
                  }
               }

               meta.setEquippable(equippableComponent);
            }
         });
         if (equippableData != null) {
            this.applyEquippableShearing(equippableData);
         }

         return this;
      }
   }

   protected void applyEquippableShearing(AbstractItemEquippableComponent equippableData) {
      if (equippableData.getCanBeSheared() != null || equippableData.getShearSound() != null) {
         MythicLogger.error("Equippable.CanBeSheared and Equippable.ShearSound are only available on Paper");
      }
   }

   @Override
   public AbstractItemStack applyUseCooldownComponent(AbstractItemUseCooldownComponent cooldownData) {
      return ServerVersion.isBefore(MinecraftVersions.v1_21_3) ? this : this.editMeta(meta -> {
         if (cooldownData == null) {
            meta.setUseCooldown(null);
         } else {
            UseCooldownComponent useCooldownComponent = meta.getUseCooldown();
            if (cooldownData.getCooldownGroup() != null) {
               useCooldownComponent.setCooldownGroup(cooldownData.getCooldownGroup());
            }

            if (cooldownData.getCooldownSeconds() != null) {
               useCooldownComponent.setCooldownSeconds(cooldownData.getCooldownSeconds());
            }

            meta.setUseCooldown(useCooldownComponent);
         }
      });
   }

   @Override
   public AbstractItemStack applyToolComponent(AbstractItemToolComponent toolData) {
      return this.editMeta(meta -> {
         if (toolData == null) {
            meta.setTool(null);
         } else {
            ToolComponent toolComponent = meta.getTool();
            if (toolData.getDamagePerBlock() != null) {
               toolComponent.setDamagePerBlock(toolData.getDamagePerBlock());
            }

            if (toolData.getDefaultMiningSpeed() != null) {
               toolComponent.setDefaultMiningSpeed(toolData.getDefaultMiningSpeed());
            }

            if (!toolData.getRules().isEmpty()) {
               for (AbstractItemToolComponent.AbstractToolRule rule : toolData.getRules()) {
                  if (rule.getMaterials() == null) {
                     toolComponent.addRule(rule.getTag(), rule.getSpeed(), rule.getIsCorrectForBlock());
                  } else {
                     toolComponent.addRule(rule.getMaterials(), rule.getSpeed(), rule.getIsCorrectForBlock());
                  }
               }
            }

            meta.setTool(toolComponent);
         }
      });
   }

   @Override
   public CompoundTag getCustomData() {
      return MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(this.itemStack);
   }

   public ItemComponentBukkitItemStack customData(String key, io.lumine.mythic.core.utils.jnbt.Tag value) {
      this.itemStack = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().addNBTData(this.itemStack, key, value);
      return this;
   }

   public ItemComponentBukkitItemStack customData(CompoundTag tag) {
      tag.getValue().forEach(this::customData);
      return this;
   }

   public ItemComponentBukkitItemStack itemModel(NamespacedKey itemModel) {
      return this.editMeta(meta -> meta.setItemModel(itemModel));
   }

   @Override
   public NamespacedKey getItemModel() {
      return this.getItemMeta().getItemModel();
   }

   public ItemComponentBukkitItemStack copy() {
      return new ItemComponentBukkitItemStack(this.itemStack.clone());
   }

   @Override
   public AbstractItemStack applyBlockStateComponent(DropMetadata dropMetadata, AbstractItemBlockStateComponent component) {
      MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setItemBlockStateComponent(this.itemStack, component.getStates());
      return this;
   }

   @Override
   public AbstractItemStack applyAttackRangeComponent(DropMetadata dropMetadata, AbstractItemAttackRangeComponent abstractItemAttackRangeComponent) {
      MythicLogger.error("AttackRange Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyEnchantableComponent(DropMetadata dropMetadata, AbstractItemEnchantableComponent componentConfig) {
      MythicLogger.error("Enchantable Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyWeaponComponent(DropMetadata dropMetadata, AbstractItemWeaponComponent componentConfig) {
      MythicLogger.error("Weapon Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyKineticWeaponComponent(DropMetadata dropMetadata, AbstractItemKineticWeaponComponent componentConfig) {
      MythicLogger.error("KineticWeapon Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyUseEffectsComponent(DropMetadata dropMetadata, AbstractItemUseEffectsComponent componentConfig) {
      MythicLogger.error("UseEffects Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applySwingAnimationComponent(DropMetadata dropMetadata, AbstractItemSwingAnimationComponent componentConfig) {
      MythicLogger.error("{0} Component is only available on Paper", componentConfig.getKind().getSection());
      return this;
   }

   @Override
   public AbstractItemStack applyMinimumAttackChargeComponent(DropMetadata dropMetadata, AbstractItemMinimumAttackChargeComponent componentConfig) {
      MythicLogger.error("MinimumAttackCharge Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyUseRemainderComponent(DropMetadata dropMetadata, AbstractItemUseRemainderComponent componentConfig) {
      MythicLogger.error("UseRemainder Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyJukeboxPlayableComponent(DropMetadata dropMetadata, AbstractItemJukeboxPlayableComponent componentConfig) {
      MythicLogger.error("JukeboxPlayable Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyLodestoneTrackerComponent(DropMetadata dropMetadata, AbstractItemLodestoneTrackerComponent componentConfig) {
      return this.editMeta(meta -> {
         if (meta instanceof CompassMeta compassMeta) {
            String worldName = componentConfig.getWorld();
            Double x = componentConfig.getX();
            Double y = componentConfig.getY();
            Double z = componentConfig.getZ();
            boolean hasAnyPositionPart = worldName != null || x != null || y != null || z != null;
            if (hasAnyPositionPart) {
               if (worldName == null || x == null || y == null || z == null) {
                  MythicLogger.error("LodestoneTracker component requires a complete location (x,y,z,world)");
                  return;
               }

               World world = Bukkit.getWorld(worldName);
               if (world == null) {
                  MythicLogger.error("LodestoneTracker component has an invalid world: {0}", worldName);
                  return;
               }

               compassMeta.setLodestone(new Location(world, x, y, z));
            }

            if (componentConfig.getTracked() != null) {
               compassMeta.setLodestoneTracked(componentConfig.getTracked());
            }
         }
      });
   }

   @Override
   public AbstractItemStack applyInstrumentComponent(DropMetadata dropMetadata, AbstractItemInstrumentComponent componentConfig) {
      MythicLogger.error("Instrument Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyBreakSoundComponent(DropMetadata dropMetadata, AbstractItemBreakSoundComponent componentConfig) {
      MythicLogger.error("BreakSound Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyDamageTypeComponent(DropMetadata dropMetadata, AbstractItemDamageTypeComponent componentConfig) {
      MythicLogger.error("DamageType Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyDamageResistantComponent(DropMetadata dropMetadata, AbstractItemDamageResistantComponent componentConfig) {
      if (componentConfig.getTag() == null) {
         return this;
      } else if (ServerVersion.isBefore(MinecraftVersions.v1_21_2)) {
         MythicLogger.error("DamageResistant Component requires 1.21.2 or newer");
         return this;
      } else {
         Tag<DamageType> tag = Bukkit.getTag("damage_types", componentConfig.getTag(), DamageType.class);
         if (tag == null) {
            MythicLogger.error("DamageResistant Component has an unknown damage type tag: {0}", componentConfig.getTag());
            return this;
         } else {
            return this.editMeta(meta -> meta.setDamageResistant(tag));
         }
      }
   }

   @Override
   public AbstractItemStack applyPaintingVariantComponent(DropMetadata dropMetadata, AbstractItemPaintingVariantComponent componentConfig) {
      MythicLogger.error("PaintingVariant Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyWorkstationComponent(DropMetadata dropMetadata, AbstractItemWorkstationComponent componentConfig) {
      if (ServerVersion.isBefore(WORKSTATION_COMPONENTS_VERSION)) {
         MythicLogger.error("{0} Component requires 26.3 or newer", componentConfig.getKind().getSection());
         return this;
      } else {
         VolatileItemHandler handler = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler();
         switch (componentConfig.getKind()) {
            case COMPOSTABLE:
               handler.setCompostableComponent(this.itemStack, componentConfig.getValue());
               break;
            case COOKING_FUEL:
               handler.setCookingFuelComponent(this.itemStack, componentConfig.getValue(), componentConfig.getSpeedMultiplier());
               break;
            case BREWING_FUEL:
               handler.setBrewingFuelComponent(this.itemStack, componentConfig.getValue(), componentConfig.getSpeedMultiplier());
         }

         return this;
      }
   }

   @Override
   public AbstractItemStack unsetComponents(Collection<String> componentIds) {
      MythicLogger.error("UnsetComponents is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyPotDecorationsComponent(DropMetadata dropMetadata, AbstractItemPotDecorationsComponent componentConfig) {
      MythicLogger.error("PotDecorations Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyOminousBottleAmplifierComponent(DropMetadata dropMetadata, AbstractItemOminousBottleAmplifierComponent componentConfig) {
      MythicLogger.error("OminousBottleAmplifier Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyBucketEntityDataComponent(DropMetadata dropMetadata, AbstractItemBucketEntityDataComponent componentConfig) {
      MythicLogger.error("BucketEntityData Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyPiercingWeaponComponent(DropMetadata dropMetadata, AbstractItemPiercingWeaponComponent componentConfig) {
      MythicLogger.error("PiercingWeapon Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyCustomModelDataComponent(DropMetadata dropMetadata, AbstractItemCustomModelDataComponent componentConfig) {
      MythicLogger.error("CustomModelData Component is only available on Paper 1.21.4+");
      return this;
   }

   public ItemComponentBukkitItemStack fireResistant(boolean fireResistant) {
      return this.editMeta(meta -> meta.setFireResistant(fireResistant));
   }

   public ItemComponentBukkitItemStack rarity(AbstractItemStackRarity rarity) {
      return this.editMeta(meta -> {
         meta.setRarity(switch (rarity) {
            case COMMON -> ItemRarity.COMMON;
            case UNCOMMON -> ItemRarity.UNCOMMON;
            case RARE -> ItemRarity.RARE;
            case EPIC -> ItemRarity.EPIC;
         });
      });
   }

   @Override
   public AbstractItemStack applyTooltipComponent(AbstractItemTooltipComponent abstractItemTooltipComponent) {
      MythicLogger.error("ToolTip Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyBlocksAttacksComponent(DropMetadata dropMetadata, AbstractItemBlocksAttacksComponent abstractItemBlocksAttacksComponent) {
      MythicLogger.error("BlocksAttacks Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyGliderComponent(DropMetadata dropMetadata, AbstractItemGliderComponent abstractItemGliderComponent) {
      MythicLogger.error("Glider Component is only available on Paper");
      return this;
   }

   @Override
   public AbstractItemStack applyRepairableComponent(DropMetadata dropMetadata, AbstractItemRepairableComponent abstractItemRepairableComponent) {
      MythicLogger.error("Repairable Component is only available on Paper");
      return this;
   }
}
