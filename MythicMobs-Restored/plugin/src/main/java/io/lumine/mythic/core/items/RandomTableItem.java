package io.lumine.mythic.core.items;

import io.lumine.mythic.api.adapters.AbstractAttributeModifier;
import io.lumine.mythic.api.adapters.AbstractItemFlag;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.items.components.AbstractItemAttackRangeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBannerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlockStateComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlocksAttacksComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemDeathProtectionComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemEquippableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemFoodComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemGliderComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemPotionComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemRepairableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemSpawnerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTooltipComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTrialSpawnerComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTrimComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseCooldownComponent;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.adapters.item.ItemComponentBukkitItemStack;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.drops.DropTable;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

@Getter
@ToString
@AllArgsConstructor
public class RandomTableItem implements AbstractItemStack {
   private final DropTable dropTable;

   @Nullable
   @Override
   public String getName() {
      return "";
   }

   @Override
   public Component name() {
      return null;
   }

   @Override
   public AbstractItemStack setName(@Nullable String name) {
      return null;
   }

   @Override
   public AbstractItemStack name(Component component) {
      return null;
   }

   @Nullable
   @Override
   public Collection<String> getLore() {
      return List.of();
   }

   @Override
   public Collection<Component> lore() {
      return List.of();
   }

   @Override
   public AbstractItemStack setLore(List<String> lore) {
      return this;
   }

   @Override
   public AbstractItemStack lore(List<Component> lore) {
      return this;
   }

   @Override
   public AbstractItemStack loreText(List<String> lore) {
      return this;
   }

   @Override
   public AbstractItemStack clearLore() {
      return this;
   }

   @Override
   public int getAmount() {
      return 0;
   }

   @Override
   public AbstractItemStack amount(int amount) {
      return this;
   }

   @Override
   public int getData() {
      return 0;
   }

   @Override
   public AbstractItemStack data(int data) {
      return this;
   }

   @Override
   public boolean isBreakable() {
      return false;
   }

   @Override
   public AbstractItemStack breakable(boolean breakable) {
      return this;
   }

   @Override
   public AbstractItemStack hideAll() {
      return this;
   }

   @Override
   public AbstractItemStack flags(Collection<AbstractItemFlag> flags) {
      return this;
   }

   @Override
   public int getRepairCost() {
      return 0;
   }

   @Override
   public AbstractItemStack repairCost(int repairCost) {
      return this;
   }

   @Override
   public int getMaxStackSize() {
      return 0;
   }

   @Override
   public AbstractItemStack maxStackSize(int maxStackSize) {
      return this;
   }

   @Override
   public int getModelData() {
      return 0;
   }

   @Override
   public AbstractItemStack modelData(int modelData) {
      return this;
   }

   @Override
   public AbstractItemStack addAttributeModifier(AbstractAttributeModifier modifier) {
      return this;
   }

   @Override
   public AbstractItemStack skullOwner(String username) {
      return this;
   }

   @Override
   public AbstractItemStack skullOwner(String username, UUID uniqueId) {
      return this;
   }

   @Override
   public AbstractItemStack skullTexture(String texture) {
      return this;
   }

   @Override
   public AbstractItemStack skullTexture(URI url) {
      return null;
   }

   public BukkitItemStack skullTextureId(String id) {
      return null;
   }

   @Override
   public AbstractItemStack enchant(Enchantment enchantment, int level) {
      return this;
   }

   @Override
   public AbstractItemStack removeEnchant(Enchantment enchantment) {
      return this;
   }

   @Override
   public AbstractItemStack enchantmentGlow(boolean glow) {
      return this;
   }

   @Override
   public int getDurability() {
      return 0;
   }

   @Override
   public AbstractItemStack durability(int durability) {
      return this;
   }

   @Override
   public int getMaxDurability() {
      return 0;
   }

   @Override
   public AbstractItemStack maxDurability(int maxDurability) {
      return this;
   }

   @Override
   public AbstractItemStack canPlaceOn(Collection<Material> materials) {
      return this;
   }

   @Override
   public AbstractItemStack canBreak(Collection<Material> materials) {
      return this;
   }

   @Override
   public AbstractItemStack potionEffect(PotionEffectType type, int duration, int amplifier) {
      return this;
   }

   @Override
   public AbstractItemStack color(int r, int g, int b) {
      return this;
   }

   @Override
   public AbstractItemStack color(Chroma color) {
      return this;
   }

   @Override
   public AbstractItemStack color(String color) {
      return this;
   }

   @Override
   public CompoundTag getCustomData() {
      return null;
   }

   @Override
   public AbstractItemStack applyBannerComponent(DropMetadata dropMetadata, AbstractItemBannerComponent abstractItemBannerComponent) {
      return null;
   }

   @Override
   public AbstractItemStack applyFoodComponent(AbstractItemFoodComponent foodData) {
      return null;
   }

   @Override
   public AbstractItemStack applyConsumableComponent(AbstractItemConsumableComponent consumableData) {
      return null;
   }

   @Override
   public AbstractItemStack applyPotionComponent(DropMetadata dropMetadata, AbstractItemPotionComponent foodData) {
      return null;
   }

   public ItemComponentBukkitItemStack applyEquippableComponent(AbstractItemEquippableComponent equippableData) {
      return null;
   }

   @Override
   public AbstractItemStack applyUseCooldownComponent(AbstractItemUseCooldownComponent cooldownData) {
      return null;
   }

   @Override
   public AbstractItemStack applyToolComponent(AbstractItemToolComponent toolData) {
      return null;
   }

   @Override
   public AbstractItemStack applyTooltipComponent(AbstractItemTooltipComponent abstractItemTooltipComponent) {
      return null;
   }

   @Override
   public AbstractItemStack applyMobSpawnerComponent(AbstractItemSpawnerComponent spawnerData) {
      return null;
   }

   @Override
   public AbstractItemStack applyTrialSpawnerComponent(AbstractItemTrialSpawnerComponent spawnerData) {
      return null;
   }

   @Override
   public AbstractItemStack applyTrimComponent(DropMetadata dropMetadata, AbstractItemTrimComponent abstractItemTrimComponent) {
      return null;
   }

   @Override
   public AbstractItemStack applyBlocksAttacksComponent(DropMetadata dropMetadata, AbstractItemBlocksAttacksComponent abstractItemBlocksAttacksComponent) {
      return null;
   }

   @Override
   public AbstractItemStack applyGliderComponent(DropMetadata dropMetadata, AbstractItemGliderComponent abstractItemGliderComponent) {
      return null;
   }

   @Override
   public AbstractItemStack applyRepairableComponent(DropMetadata dropMetadata, AbstractItemRepairableComponent abstractItemRepairableComponent) {
      return null;
   }

   @Override
   public AbstractItemStack applyDeathProtectionComponent(DropMetadata dropMetadata, AbstractItemDeathProtectionComponent abstractItemDeathProtectionComponent) {
      return null;
   }

   @Override
   public AbstractItemStack customData(String key, Tag tag) {
      return this;
   }

   @Override
   public AbstractItemStack customData(CompoundTag tag) {
      return this;
   }

   @Override
   public AbstractItemStack mythicItemType(String itemType) {
      return this;
   }

   @Override
   public Optional<String> getMythicItemType() {
      return Optional.empty();
   }

   @Override
   public AbstractItemStack mythicItemVersion(int itemVersion) {
      return this;
   }

   @Override
   public Optional<Integer> getMythicItemVersion() {
      return Optional.empty();
   }

   @Override
   public AbstractItemStack preventEnchanting() {
      return this;
   }

   @Override
   public AbstractItemStack preventRepairingWith() {
      return this;
   }

   @Override
   public AbstractItemStack preventCraftingWith() {
      return this;
   }

   @Override
   public AbstractItemStack preventSmithingWith() {
      return this;
   }

   @Override
   public AbstractItemStack UUID(UUID uuid) {
      return this;
   }

   @Nullable
   @Override
   public UUID getUUID() {
      return null;
   }

   @Override
   public AbstractItemStack timestamp(long time) {
      return this;
   }

   @Override
   public long getTimestamp() {
      return 0L;
   }

   @Nullable
   public RandomTableItem itemModel(NamespacedKey itemModel) {
      return this;
   }

   @Override
   public NamespacedKey getItemModel() {
      return null;
   }

   @Override
   public boolean isSimilar(AbstractItemStack other) {
      return false;
   }

   @Override
   public AbstractItemStack copy() {
      return new RandomTableItem(this.dropTable);
   }

   @Override
   public AbstractItemStack applyBlockStateComponent(DropMetadata dropMetadata, AbstractItemBlockStateComponent component) {
      return null;
   }

   @Override
   public AbstractItemStack applyAttackRangeComponent(DropMetadata dropMetadata, AbstractItemAttackRangeComponent abstractItemAttackRangeComponent) {
      return null;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof RandomTableItem other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$dropTable = this.getDropTable();
         Object other$dropTable = other.getDropTable();
         return this$dropTable == null ? other$dropTable == null : this$dropTable.equals(other$dropTable);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof RandomTableItem;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $dropTable = this.getDropTable();
      return result * 59 + ($dropTable == null ? 43 : $dropTable.hashCode());
   }
}
