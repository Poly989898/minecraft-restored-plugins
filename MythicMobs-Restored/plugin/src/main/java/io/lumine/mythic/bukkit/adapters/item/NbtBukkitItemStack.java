package io.lumine.mythic.bukkit.adapters.item;

import com.pg85.otg.shaded.org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import io.lumine.mythic.api.adapters.AbstractAttributeModifier;
import io.lumine.mythic.api.adapters.AbstractItemFlag;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.items.components.AbstractItemAttackRangeComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlockStateComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemBlocksAttacksComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemConsumableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemEquippableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemFoodComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemGliderComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemRepairableComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemToolComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemTooltipComponent;
import io.lumine.mythic.api.adapters.items.components.AbstractItemUseCooldownComponent;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.uuid.DashedUUIDs;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.drops.EquipSlot;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.utils.jnbt.ByteTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTagBuilder;
import io.lumine.mythic.core.utils.jnbt.IntArrayTag;
import io.lumine.mythic.core.utils.jnbt.IntTag;
import io.lumine.mythic.core.utils.jnbt.ListTag;
import io.lumine.mythic.core.utils.jnbt.ListTagBuilder;
import io.lumine.mythic.core.utils.jnbt.StringTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NbtBukkitItemStack extends BukkitItemStack {
   public NbtBukkitItemStack(ItemStack itemStack) {
      super(itemStack);
   }

   public NbtBukkitItemStack editMeta(Consumer<ItemMeta> block) {
      ItemMeta meta = this.itemStack.getItemMeta();
      block.accept(meta);
      this.itemStack.setItemMeta(meta);
      return this;
   }

   private NbtBukkitItemStack editTag(String key, Consumer<CompoundTagBuilder> builder) {
      CompoundTag nbt = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(this.itemStack);
      CompoundTag tag = nbt.containsKey(key) ? nbt.getCompound(key) : CompoundTagBuilder.create().build();
      CompoundTagBuilder tagBuilder = tag.createBuilder();
      builder.accept(tagBuilder);
      return this.customData(key, tagBuilder.build());
   }

   public NbtBukkitItemStack setName(String name) {
      return this.name(Text.parse(name));
   }

   public NbtBukkitItemStack name(Component component) {
      return this.editTag("display", builder -> builder.putString("Name", GsonComponentSerializer.gson().serialize(component)));
   }

   public NbtBukkitItemStack setLore(List<String> lines) {
      return this.lore(lines.stream().map(Text::parse).toList());
   }

   public NbtBukkitItemStack lore(List<Component> lines) {
      return this.editTag(
         "display",
         builder -> builder.put(
            "Lore",
            ListTagBuilder.createWith(lines.stream().<String>map(GsonComponentSerializer.gson()::serialize).map(StringTag::new).toArray(StringTag[]::new))
               .build()
         )
      );
   }

   public NbtBukkitItemStack amount(int amount) {
      this.itemStack.setAmount(amount);
      return this;
   }

   @Deprecated
   public NbtBukkitItemStack data(int data) {
      this.itemStack.setDurability((short)data);
      return this;
   }

   public NbtBukkitItemStack breakable(boolean breakable) {
      return !breakable ? this.customData("Unbreakable", new ByteTag((byte)1)) : this;
   }

   public NbtBukkitItemStack hideAll() {
      return ServerVersion.isAfterOrEq(MinecraftVersions.v1_20) ? this.customData("HideFlags", new IntTag(255)) : this.customData("HideFlags", new IntTag(127));
   }

   public NbtBukkitItemStack flags(Collection<AbstractItemFlag> flags) {
      this.editMeta(meta -> meta.addItemFlags(flags.stream().map(BukkitAdapter::adapt).toArray(ItemFlag[]::new)));
      return this;
   }

   public NbtBukkitItemStack repairCost(int repairCost) {
      return this.customData("RepairCost", new IntTag(repairCost));
   }

   public NbtBukkitItemStack maxStackSize(int maxStackSize) {
      return maxStackSize == 1 ? this.customData("UUID", new IntArrayTag(DashedUUIDs.uuidToIntArray(UUID.randomUUID()))) : this;
   }

   public NbtBukkitItemStack modelData(int modelData) {
      return this.customData("CustomModelData", new IntTag(modelData));
   }

   public NbtBukkitItemStack addAttributeModifier(AbstractAttributeModifier modifier) {
      Attribute attribute = Registry.ATTRIBUTE.get(modifier.key());
      if (attribute == null) {
         throw new IllegalArgumentException("Unknown attribute '" + modifier.key() + "'");
      } else {
         UUID uniqueId = UUID.randomUUID();
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

   public NbtBukkitItemStack enchant(Enchantment enchantment, int level) {
      if (this.itemStack.getType() != Material.ENCHANTED_BOOK) {
         this.itemStack.addUnsafeEnchantment(enchantment, level);
      } else {
         this.editMeta(m -> ((EnchantmentStorageMeta)m).addStoredEnchant(enchantment, level, true));
      }

      return this;
   }

   @Override
   public BukkitItemStack removeEnchant(Enchantment enchantment) {
      if (this.itemStack.getType() != Material.ENCHANTED_BOOK) {
         this.itemStack.removeEnchantment(enchantment);
      } else {
         this.editMeta(m -> ((EnchantmentStorageMeta)m).removeStoredEnchant(enchantment));
      }

      return this;
   }

   public NbtBukkitItemStack enchantmentGlow(boolean glow) {
      return glow ? this.enchant(Enchantment.VANISHING_CURSE, 1) : this;
   }

   public NbtBukkitItemStack durability(int durability) {
      return this.customData("MYTHIC_CURRENT_CUSTOM_DURABILITY", new IntTag(durability));
   }

   public NbtBukkitItemStack maxDurability(int durability) {
      return this.customData("MYTHIC_MAX_CUSTOM_DURABILITY", new IntTag(durability));
   }

   public NbtBukkitItemStack canPlaceOn(Collection<Material> materials) {
      this.itemStack = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setCanPlaceOn(this.itemStack, materials);
      return this;
   }

   public NbtBukkitItemStack canBreak(Collection<Material> materials) {
      this.itemStack = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setCanBreak(this.itemStack, materials);
      return this;
   }

   public NbtBukkitItemStack potionEffect(PotionEffectType type, int duration, int amplifier) {
      if (this.getItemMeta() instanceof PotionMeta meta) {
         meta.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
         this.itemStack.setItemMeta(meta);
      }

      return this;
   }

   public NbtBukkitItemStack color(int r, int g, int b) {
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

   public NbtBukkitItemStack applyFoodComponent(AbstractItemFoodComponent foodData) {
      return this;
   }

   @Override
   public AbstractItemStack applyConsumableComponent(AbstractItemConsumableComponent consumableData) {
      return this;
   }

   public NbtBukkitItemStack applyEquippableComponent(AbstractItemEquippableComponent equippableData) {
      return this;
   }

   @Override
   public AbstractItemStack applyUseCooldownComponent(AbstractItemUseCooldownComponent cooldownData) {
      return this;
   }

   public NbtBukkitItemStack applyToolComponent(AbstractItemToolComponent toolData) {
      return this;
   }

   public NbtBukkitItemStack applyTooltipComponent(AbstractItemTooltipComponent abstractItemTooltipComponent) {
      return this;
   }

   public NbtBukkitItemStack applyBlocksAttacksComponent(DropMetadata dropMetadata, AbstractItemBlocksAttacksComponent abstractItemBlocksAttacksComponent) {
      return this;
   }

   public NbtBukkitItemStack applyGliderComponent(DropMetadata dropMetadata, AbstractItemGliderComponent abstractItemGliderComponent) {
      return this;
   }

   public NbtBukkitItemStack applyRepairableComponent(DropMetadata dropMetadata, AbstractItemRepairableComponent abstractItemRepairableComponent) {
      return this;
   }

   public NbtBukkitItemStack customData(String key, Tag value) {
      this.itemStack = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().addNBTData(this.itemStack, key, value);
      return this;
   }

   public NbtBukkitItemStack customData(CompoundTag tag) {
      tag.getValue().forEach(this::customData);
      return this;
   }

   public NbtBukkitItemStack itemModel(NamespacedKey itemModel) {
      return this;
   }

   @Override
   public NamespacedKey getItemModel() {
      return null;
   }

   @Override
   public boolean isBreakable() {
      return this.getCustomData().getBoolean("Unbreakable");
   }

   @Override
   public int getRepairCost() {
      return this.getCustomData().getInt("RepairCost");
   }

   @Override
   public int getDurability() {
      int durability = this.getCustomData().getInt("MYTHIC_CURRENT_CUSTOM_DURABILITY");
      return durability < 1
         ? Optional.ofNullable(this.getItemMeta()).map(meta -> meta instanceof Damageable damageable ? damageable.getDamage() : 0).orElse(0)
         : durability;
   }

   @Override
   public int getMaxDurability() {
      int maxDurability = this.getCustomData().getInt("MYTHIC_MAX_CUSTOM_DURABILITY");
      if (maxDurability < 1) {
         maxDurability = this.getType().getMaxDurability();
      }

      return maxDurability;
   }

   @Override
   public CompoundTag getCustomData() {
      return MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(this.itemStack);
   }

   public NbtBukkitItemStack copy() {
      return new NbtBukkitItemStack(this.itemStack.clone());
   }

   @Override
   public AbstractItemStack applyBlockStateComponent(DropMetadata dropMetadata, AbstractItemBlockStateComponent component) {
      return this;
   }

   @Override
   public AbstractItemStack applyAttackRangeComponent(DropMetadata dropMetadata, AbstractItemAttackRangeComponent abstractItemAttackRangeComponent) {
      MythicLogger.error("AttackRange Component is only available on Paper 1.21.11+");
      return this;
   }

   private NbtBukkitItemStack skullData(String username, UUID uniqueId, String texture, String url) {
      if (username != null) {
         return this.customData("SkullOwner", new StringTag(username));
      } else if (texture == null && url == null) {
         return this;
      } else {
         CompoundTag valuesTag = CompoundTag.create(
            data -> data.putString("Value", Objects.requireNonNullElseGet(texture, () -> Base64Coder.encodeString("{textures:{SKIN:{url:\"" + url + "\"}}}")))
         );
         ListTag valuesList = ListTagBuilder.createWith(valuesTag).build();
         return this.customData("SkullOwner", CompoundTag.create(skullOwnerTag -> {
            skullOwnerTag.put("Properties", CompoundTag.create(properties -> properties.put("textures", valuesList)));
            skullOwnerTag.putUUID("Id", uniqueId);
         }));
      }
   }
}
