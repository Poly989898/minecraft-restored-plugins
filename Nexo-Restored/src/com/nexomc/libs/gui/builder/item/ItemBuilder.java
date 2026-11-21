package com.nexomc.libs.gui.builder.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nexomc.libs.gui.components.util.SkullUtil;
import java.lang.reflect.Field;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {
   ItemBuilder(@NotNull ItemStack itemStack) {
      super(itemStack);
   }

   @Contract("_ -> new")
   public static @NotNull ItemBuilder from(@NotNull ItemStack itemStack) {
      return new ItemBuilder(itemStack);
   }

   @Contract("_ -> new")
   public static @NotNull ItemBuilder from(@NotNull Material material) {
      return new ItemBuilder(new ItemStack(material));
   }

   @Contract(" -> new")
   public static @NotNull BannerBuilder banner() {
      return new BannerBuilder();
   }

   @Contract("_ -> new")
   public static @NotNull BannerBuilder banner(@NotNull ItemStack itemStack) {
      return new BannerBuilder(itemStack);
   }

   @Contract("_ -> new")
   public static @NotNull BookBuilder book(@NotNull ItemStack itemStack) {
      return new BookBuilder(itemStack);
   }

   @Contract(" -> new")
   public static @NotNull FireworkBuilder firework() {
      return new FireworkBuilder(new ItemStack(Material.FIREWORK_ROCKET));
   }

   @Contract("_ -> new")
   public static @NotNull FireworkBuilder firework(@NotNull ItemStack itemStack) {
      return new FireworkBuilder(itemStack);
   }

   @Contract(" -> new")
   public static @NotNull MapBuilder map() {
      return new MapBuilder();
   }

   @Contract("_ -> new")
   public static @NotNull MapBuilder map(@NotNull ItemStack itemStack) {
      return new MapBuilder(itemStack);
   }

   @Contract(" -> new")
   public static @NotNull SkullBuilder skull() {
      return new SkullBuilder();
   }

   @Contract("_ -> new")
   public static @NotNull SkullBuilder skull(@NotNull ItemStack itemStack) {
      return new SkullBuilder(itemStack);
   }

   @Contract(" -> new")
   public static @NotNull FireworkBuilder star() {
      return new FireworkBuilder(new ItemStack(Material.FIREWORK_STAR));
   }

   @Contract("_ -> new")
   public static @NotNull FireworkBuilder star(@NotNull ItemStack itemStack) {
      return new FireworkBuilder(itemStack);
   }

   /** @deprecated */
   @Deprecated
   public ItemBuilder setSkullTexture(@NotNull String texture) {
      if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
         return this;
      } else {
         SkullMeta skullMeta = (SkullMeta)this.getMeta();
         GameProfile profile = new GameProfile(UUID.randomUUID(), (String)null);
         profile.getProperties().put("textures", new Property("textures", texture));

         try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
         } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException ex) {
            ((Exception)ex).printStackTrace();
         }

         this.setMeta(skullMeta);
         return this;
      }
   }

   /** @deprecated */
   @Deprecated
   public ItemBuilder setSkullOwner(@NotNull OfflinePlayer player) {
      if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
         return this;
      } else {
         SkullMeta skullMeta = (SkullMeta)this.getMeta();
         skullMeta.setOwningPlayer(player);
         this.setMeta(skullMeta);
         return this;
      }
   }
}
