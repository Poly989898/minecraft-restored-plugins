package io.lumine.mythic.api.adapters;

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
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface AbstractItemStack {
   @Deprecated
   @Nullable
   String getName();

   Component name();

   @Deprecated
   AbstractItemStack setName(@Nullable String str);

   AbstractItemStack name(Component component);

   @Deprecated
   @Nullable
   Collection<String> getLore();

   Collection<Component> lore();

   @Deprecated
   AbstractItemStack setLore(List<String> strs);

   AbstractItemStack lore(List<Component> components);

   AbstractItemStack loreText(List<String> strs);

   AbstractItemStack clearLore();

   int getAmount();

   AbstractItemStack amount(int value);

   @Deprecated
   @Internal
   int getData();

   @Deprecated
   @Internal
   AbstractItemStack data(int value);

   boolean isBreakable();

   AbstractItemStack breakable(boolean flag);

   AbstractItemStack hideAll();

   default AbstractItemStack flags(AbstractItemFlag... flags) {
      return this.flags(Arrays.stream(flags).toList());
   }

   AbstractItemStack flags(Collection<AbstractItemFlag> abstractItemFlags);

   int getRepairCost();

   AbstractItemStack repairCost(int value);

   int getMaxStackSize();

   AbstractItemStack maxStackSize(int value);

   default AbstractItemStack unstackable() {
      return this.maxStackSize(1);
   }

   int getModelData();

   AbstractItemStack modelData(int value);

   AbstractItemStack addAttributeModifier(AbstractAttributeModifier abstractAttributeModifier);

   AbstractItemStack skullOwner(String str);

   AbstractItemStack skullOwner(String str, UUID uuid);

   AbstractItemStack skullTexture(String str);

   AbstractItemStack skullTexture(URI uri);

   AbstractItemStack skullTextureId(String str);

   AbstractItemStack enchant(Enchantment enchantment, int value);

   AbstractItemStack removeEnchant(Enchantment enchantment);

   AbstractItemStack enchantmentGlow(boolean flag);

   int getDurability();

   AbstractItemStack durability(int value);

   int getMaxDurability();

   AbstractItemStack maxDurability(int value);

   AbstractItemStack canPlaceOn(Collection<Material> materials);

   AbstractItemStack canBreak(Collection<Material> materials);

   AbstractItemStack potionEffect(PotionEffectType potionEffectType, int value, int value2);

   AbstractItemStack color(int value, int value2, int value3);

   AbstractItemStack color(Chroma chroma);

   AbstractItemStack color(String str);

   CompoundTag getCustomData();

   AbstractItemStack applyBannerComponent(DropMetadata dropMetadata, AbstractItemBannerComponent abstractItemBannerComponent);

   AbstractItemStack applyFoodComponent(AbstractItemFoodComponent abstractItemFoodComponent);

   AbstractItemStack applyConsumableComponent(AbstractItemConsumableComponent abstractItemConsumableComponent);

   AbstractItemStack applyPotionComponent(DropMetadata dropMetadata, AbstractItemPotionComponent abstractItemPotionComponent);

   AbstractItemStack applyEquippableComponent(AbstractItemEquippableComponent abstractItemEquippableComponent);

   AbstractItemStack applyUseCooldownComponent(AbstractItemUseCooldownComponent abstractItemUseCooldownComponent);

   AbstractItemStack applyToolComponent(AbstractItemToolComponent abstractItemToolComponent);

   AbstractItemStack applyTooltipComponent(AbstractItemTooltipComponent abstractItemTooltipComponent);

   AbstractItemStack applyMobSpawnerComponent(AbstractItemSpawnerComponent abstractItemSpawnerComponent);

   AbstractItemStack applyTrialSpawnerComponent(AbstractItemTrialSpawnerComponent abstractItemTrialSpawnerComponent);

   AbstractItemStack applyTrimComponent(DropMetadata dropMetadata, AbstractItemTrimComponent abstractItemTrimComponent);

   AbstractItemStack applyBlocksAttacksComponent(DropMetadata dropMetadata, AbstractItemBlocksAttacksComponent abstractItemBlocksAttacksComponent);

   AbstractItemStack applyGliderComponent(DropMetadata dropMetadata, AbstractItemGliderComponent abstractItemGliderComponent);

   AbstractItemStack applyRepairableComponent(DropMetadata dropMetadata, AbstractItemRepairableComponent abstractItemRepairableComponent);

   default AbstractItemStack applyDeathProtectionComponent(DropMetadata dropMetadata, AbstractItemDeathProtectionComponent abstractItemDeathProtectionComponent) {
      return this;
   }

   default AbstractItemStack applyCustomModelDataComponent(DropMetadata dropMetadata, AbstractItemCustomModelDataComponent componentConfig) {
      return this;
   }

   AbstractItemStack customData(String str, Tag tag);

   AbstractItemStack customData(CompoundTag compoundTag);

   AbstractItemStack mythicItemType(String str);

   Optional<String> getMythicItemType();

   AbstractItemStack mythicItemVersion(int value);

   Optional<Integer> getMythicItemVersion();

   AbstractItemStack preventEnchanting();

   AbstractItemStack preventRepairingWith();

   AbstractItemStack preventCraftingWith();

   AbstractItemStack preventSmithingWith();

   AbstractItemStack UUID(UUID uuid);

   UUID getUUID();

   AbstractItemStack timestamp(long value);

   long getTimestamp();

   AbstractItemStack itemModel(NamespacedKey namespacedKey);

   NamespacedKey getItemModel();

   boolean isSimilar(AbstractItemStack abstractItemStack);

   AbstractItemStack copy();

   AbstractItemStack applyBlockStateComponent(DropMetadata dropMetadata, AbstractItemBlockStateComponent abstractItemBlockStateComponent);

   AbstractItemStack applyAttackRangeComponent(DropMetadata dropMetadata, AbstractItemAttackRangeComponent abstractItemAttackRangeComponent);

   default AbstractItemStack applyEnchantableComponent(DropMetadata dropMetadata, AbstractItemEnchantableComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyWeaponComponent(DropMetadata dropMetadata, AbstractItemWeaponComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyKineticWeaponComponent(DropMetadata dropMetadata, AbstractItemKineticWeaponComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyUseEffectsComponent(DropMetadata dropMetadata, AbstractItemUseEffectsComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applySwingAnimationComponent(DropMetadata dropMetadata, AbstractItemSwingAnimationComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyMinimumAttackChargeComponent(DropMetadata dropMetadata, AbstractItemMinimumAttackChargeComponent componentConfig) {
      return this;
   }

   default AbstractItemStack itemName(Component component) {
      return this;
   }

   default AbstractItemStack hideTooltip() {
      return this;
   }

   default AbstractItemStack hideTooltipComponents(Collection<String> componentIds) {
      return this;
   }

   default AbstractItemStack applyDamageTypeComponent(DropMetadata dropMetadata, AbstractItemDamageTypeComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyDamageResistantComponent(DropMetadata dropMetadata, AbstractItemDamageResistantComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyPaintingVariantComponent(DropMetadata dropMetadata, AbstractItemPaintingVariantComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyWorkstationComponent(DropMetadata dropMetadata, AbstractItemWorkstationComponent componentConfig) {
      return this;
   }

   default AbstractItemStack unsetComponents(Collection<String> componentIds) {
      return this;
   }

   default AbstractItemStack applyUseRemainderComponent(DropMetadata dropMetadata, AbstractItemUseRemainderComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyJukeboxPlayableComponent(DropMetadata dropMetadata, AbstractItemJukeboxPlayableComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyNoteBlockSoundComponent(DropMetadata dropMetadata, AbstractItemNoteBlockSoundComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyInstrumentComponent(DropMetadata dropMetadata, AbstractItemInstrumentComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyLodestoneTrackerComponent(DropMetadata dropMetadata, AbstractItemLodestoneTrackerComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyBreakSoundComponent(DropMetadata dropMetadata, AbstractItemBreakSoundComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyPotDecorationsComponent(DropMetadata dropMetadata, AbstractItemPotDecorationsComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyOminousBottleAmplifierComponent(DropMetadata dropMetadata, AbstractItemOminousBottleAmplifierComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyBucketEntityDataComponent(DropMetadata dropMetadata, AbstractItemBucketEntityDataComponent componentConfig) {
      return this;
   }

   default AbstractItemStack applyPiercingWeaponComponent(DropMetadata dropMetadata, AbstractItemPiercingWeaponComponent componentConfig) {
      return this;
   }
}
