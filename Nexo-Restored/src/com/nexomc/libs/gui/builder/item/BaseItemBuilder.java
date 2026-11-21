package com.nexomc.libs.gui.builder.item;

import com.google.common.base.Preconditions;
import com.nexomc.libs.gui.components.GuiAction;
import com.nexomc.libs.gui.components.exception.GuiException;
import com.nexomc.libs.gui.components.util.ItemNbt;
import com.nexomc.libs.gui.components.util.Legacy;
import com.nexomc.libs.gui.components.util.VersionHelper;
import com.nexomc.libs.gui.guis.GuiItem;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>> {
   private static final EnumSet<Material> LEATHER_ARMOR;
   private static final Field DISPLAY_NAME_FIELD;
   private static final Field LORE_FIELD;
   private ItemStack itemStack;
   private ItemMeta meta;

   protected BaseItemBuilder(@NotNull ItemStack itemStack) {
      Preconditions.checkNotNull(itemStack, "Item can't be null!");
      this.itemStack = itemStack;
      this.meta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType());
   }

   protected @NotNull Object serializeComponent(@NotNull Component component) {
      return VersionHelper.IS_ITEM_NAME_COMPONENT ? MinecraftComponentSerializer.get().serialize(component) : GsonComponentSerializer.gson().serialize(component);
   }

   protected @NotNull Component deserializeComponent(@NotNull Object obj) {
      return VersionHelper.IS_ITEM_NAME_COMPONENT ? MinecraftComponentSerializer.get().deserialize(obj) : GsonComponentSerializer.gson().deserialize((String)obj);
   }

   @Contract("_ -> this")
   public @NotNull B name(@NotNull Component name) {
      if (this.meta == null) {
         return (B)this;
      } else if (VersionHelper.IS_COMPONENT_LEGACY) {
         this.meta.setDisplayName(Legacy.SERIALIZER.serialize(name));
         return (B)this;
      } else {
         try {
            DISPLAY_NAME_FIELD.set(this.meta, this.serializeComponent(name));
         } catch (IllegalAccessException exception) {
            exception.printStackTrace();
         }

         return (B)this;
      }
   }

   @Contract("_ -> this")
   public @NotNull B amount(int amount) {
      this.itemStack.setAmount(amount);
      return (B)this;
   }

   @Contract("_ -> this")
   public @NotNull B lore(@NotNull Component... lore) {
      return (B)this.lore(Arrays.asList(lore));
   }

   @Contract("_ -> this")
   public @NotNull B lore(@NotNull List<@Nullable Component> lore) {
      if (this.meta == null) {
         return (B)this;
      } else if (VersionHelper.IS_COMPONENT_LEGACY) {
         ItemMeta var10000 = this.meta;
         Stream var10001 = lore.stream().filter(Objects::nonNull);
         LegacyComponentSerializer var10002 = Legacy.SERIALIZER;
         Objects.requireNonNull(var10002);
         var10000.setLore((List)var10001.map(var10002::serialize).collect(Collectors.toList()));
         return (B)this;
      } else {
         List<Object> jsonLore = (List)lore.stream().filter(Objects::nonNull).map(this::serializeComponent).collect(Collectors.toList());

         try {
            LORE_FIELD.set(this.meta, jsonLore);
         } catch (IllegalAccessException exception) {
            exception.printStackTrace();
         }

         return (B)this;
      }
   }

   @Contract("_ -> this")
   public @NotNull B lore(@NotNull Consumer<List<@Nullable Component>> lore) {
      if (this.meta == null) {
         return (B)this;
      } else {
         List<Component> components;
         if (VersionHelper.IS_COMPONENT_LEGACY) {
            List<String> stringLore = this.meta.getLore();
            Object var10000;
            if (stringLore == null) {
               var10000 = new ArrayList();
            } else {
               Stream var6 = stringLore.stream();
               LegacyComponentSerializer var10001 = Legacy.SERIALIZER;
               Objects.requireNonNull(var10001);
               var10000 = (List)var6.map(var10001::deserialize).collect(Collectors.toList());
            }

            components = (List<Component>)var10000;
         } else {
            try {
               List<Object> jsonLore = (List)LORE_FIELD.get(this.meta);
               components = (List<Component>)(jsonLore == null ? new ArrayList() : (List)jsonLore.stream().map(this::deserializeComponent).collect(Collectors.toList()));
            } catch (IllegalAccessException exception) {
               components = new ArrayList();
               exception.printStackTrace();
            }
         }

         lore.accept(components);
         return (B)this.lore(components);
      }
   }

   @Contract("_, _, _ -> this")
   public @NotNull B enchant(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
      this.meta.addEnchant(enchantment, level, ignoreLevelRestriction);
      return (B)this;
   }

   @Contract("_, _ -> this")
   public @NotNull B enchant(@NotNull Enchantment enchantment, int level) {
      return (B)this.enchant(enchantment, level, true);
   }

   @Contract("_ -> this")
   public @NotNull B enchant(@NotNull Enchantment enchantment) {
      return (B)this.enchant(enchantment, 1, true);
   }

   @Contract("_, _ -> this")
   public @NotNull B enchant(@NotNull Map<Enchantment, Integer> enchantments, boolean ignoreLevelRestriction) {
      enchantments.forEach((enchantment, level) -> this.enchant(enchantment, level, ignoreLevelRestriction));
      return (B)this;
   }

   @Contract("_ -> this")
   public @NotNull B enchant(@NotNull Map<Enchantment, Integer> enchantments) {
      return (B)this.enchant(enchantments, true);
   }

   @Contract("_ -> this")
   public @NotNull B disenchant(@NotNull Enchantment enchantment) {
      this.itemStack.removeEnchantment(enchantment);
      return (B)this;
   }

   @Contract("_ -> this")
   public @NotNull B flags(ItemFlag... flags) {
      this.meta.addItemFlags(flags);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B unbreakable() {
      return (B)this.unbreakable(true);
   }

   @Contract("_ -> this")
   public @NotNull B unbreakable(boolean unbreakable) {
      if (VersionHelper.IS_UNBREAKABLE_LEGACY) {
         return (B)this.setNbt("Unbreakable", unbreakable);
      } else {
         this.meta.setUnbreakable(unbreakable);
         return (B)this;
      }
   }

   @Contract(" -> this")
   public @NotNull B glow() {
      return (B)this.glow(true);
   }

   @Contract("_ -> this")
   public @NotNull B glow(boolean glow) {
      if (glow) {
         this.meta.addEnchant(Enchantment.LURE, 1, false);
         this.meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         return (B)this;
      } else {
         for(Enchantment enchantment : this.meta.getEnchants().keySet()) {
            this.meta.removeEnchant(enchantment);
         }

         return (B)this;
      }
   }

   @Contract("_ -> this")
   public @NotNull B pdc(@NotNull Consumer<PersistentDataContainer> consumer) {
      consumer.accept(this.meta.getPersistentDataContainer());
      return (B)this;
   }

   @Contract("_ -> this")
   public @NotNull B model(int modelData) {
      if (VersionHelper.IS_CUSTOM_MODEL_DATA) {
         this.meta.setCustomModelData(modelData);
      }

      return (B)this;
   }

   @Contract("_ -> this")
   public @NotNull B color(@NotNull Color color) {
      if (LEATHER_ARMOR.contains(this.itemStack.getType())) {
         LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)this.getMeta();
         leatherArmorMeta.setColor(color);
         this.setMeta(leatherArmorMeta);
      }

      return (B)this;
   }

   @Contract("_, _ -> this")
   public @NotNull B setNbt(@NotNull String key, @NotNull String value) {
      this.itemStack.setItemMeta(this.meta);
      this.itemStack = ItemNbt.setString(this.itemStack, key, value);
      this.meta = this.itemStack.getItemMeta();
      return (B)this;
   }

   @Contract("_, _ -> this")
   public @NotNull B setNbt(@NotNull String key, boolean value) {
      this.itemStack.setItemMeta(this.meta);
      this.itemStack = ItemNbt.setBoolean(this.itemStack, key, value);
      this.meta = this.itemStack.getItemMeta();
      return (B)this;
   }

   @Contract("_ -> this")
   public @NotNull B removeNbt(@NotNull String key) {
      this.itemStack.setItemMeta(this.meta);
      this.itemStack = ItemNbt.removeTag(this.itemStack, key);
      this.meta = this.itemStack.getItemMeta();
      return (B)this;
   }

   public @NotNull ItemStack build() {
      this.itemStack.setItemMeta(this.meta);
      return this.itemStack;
   }

   @Contract(" -> new")
   public @NotNull GuiItem asGuiItem() {
      return new GuiItem(this.build());
   }

   @Contract("_ -> new")
   public @NotNull GuiItem asGuiItem(@NotNull GuiAction<InventoryClickEvent> action) {
      return new GuiItem(this.build(), action);
   }

   protected @NotNull ItemStack getItemStack() {
      return this.itemStack;
   }

   protected void setItemStack(@NotNull ItemStack itemStack) {
      this.itemStack = itemStack;
   }

   protected @NotNull ItemMeta getMeta() {
      return this.meta;
   }

   protected void setMeta(@NotNull ItemMeta meta) {
      this.meta = meta;
   }

   /** @deprecated */
   @Deprecated
   public B setName(@NotNull String name) {
      this.getMeta().setDisplayName(name);
      return (B)this;
   }

   /** @deprecated */
   @Deprecated
   public B setAmount(int amount) {
      this.getItemStack().setAmount(amount);
      return (B)this;
   }

   /** @deprecated */
   @Deprecated
   public B addLore(String... lore) {
      return (B)this.addLore(Arrays.asList(lore));
   }

   /** @deprecated */
   @Deprecated
   public B addLore(@NotNull List<String> lore) {
      List<String> newLore = (List<String>)(this.getMeta().hasLore() ? this.getMeta().getLore() : new ArrayList());
      newLore.addAll(lore);
      return (B)this.setLore(newLore);
   }

   /** @deprecated */
   @Deprecated
   public B setLore(String... lore) {
      return (B)this.setLore(Arrays.asList(lore));
   }

   /** @deprecated */
   @Deprecated
   public B setLore(@NotNull List<String> lore) {
      this.getMeta().setLore(lore);
      return (B)this;
   }

   /** @deprecated */
   @Deprecated
   public B addEnchantment(@NotNull Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
      this.getMeta().addEnchant(enchantment, level, ignoreLevelRestriction);
      return (B)this;
   }

   /** @deprecated */
   @Deprecated
   public B addEnchantment(@NotNull Enchantment enchantment, int level) {
      return (B)this.addEnchantment(enchantment, level, true);
   }

   /** @deprecated */
   @Deprecated
   public B addEnchantment(@NotNull Enchantment enchantment) {
      return (B)this.addEnchantment(enchantment, 1, true);
   }

   /** @deprecated */
   @Deprecated
   public B removeEnchantment(@NotNull Enchantment enchantment) {
      this.getItemStack().removeEnchantment(enchantment);
      return (B)this;
   }

   /** @deprecated */
   @Deprecated
   public B addItemFlags(ItemFlag... flags) {
      this.getMeta().addItemFlags(flags);
      return (B)this;
   }

   /** @deprecated */
   @Deprecated
   public B setUnbreakable(boolean unbreakable) {
      return (B)this.unbreakable(unbreakable);
   }

   static {
      LEATHER_ARMOR = EnumSet.of(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

      try {
         Class<?> metaClass = VersionHelper.craftClass("inventory.CraftMetaItem");
         DISPLAY_NAME_FIELD = metaClass.getDeclaredField("displayName");
         DISPLAY_NAME_FIELD.setAccessible(true);
         LORE_FIELD = metaClass.getDeclaredField("lore");
         LORE_FIELD.setAccessible(true);
      } catch (ClassNotFoundException | NoSuchFieldException exception) {
         ((ReflectiveOperationException)exception).printStackTrace();
         throw new GuiException("Could not retrieve displayName nor lore field for ItemBuilder.");
      }
   }
}
