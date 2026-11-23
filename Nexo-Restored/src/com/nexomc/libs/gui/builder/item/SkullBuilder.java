package com.nexomc.libs.gui.builder.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nexomc.libs.gui.components.exception.GuiException;
import com.nexomc.libs.gui.components.util.SkullUtil;
import com.nexomc.libs.gui.components.util.VersionHelper;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SkullBuilder extends BaseItemBuilder<SkullBuilder> {
   private static final Field PROFILE_FIELD;

   SkullBuilder() {
      super(SkullUtil.skull());
   }

   SkullBuilder(@NotNull ItemStack itemStack) {
      super(itemStack);
      if (!SkullUtil.isPlayerSkull(itemStack)) {
         throw new GuiException("SkullBuilder requires the material to be a PLAYER_HEAD/SKULL_ITEM!");
      }
   }

   @Contract("_, _ -> this")
   public @NotNull SkullBuilder texture(@NotNull String texture, @NotNull UUID profileId) {
      if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
         return this;
      } else if (VersionHelper.IS_PLAYER_PROFILE_API) {
         String textureUrl = SkullUtil.getSkinUrl(texture);
         if (textureUrl == null) {
            return this;
         } else {
            SkullMeta skullMeta = (SkullMeta)this.getMeta();
            PlayerProfile profile = Bukkit.createPlayerProfile(profileId, "");
            PlayerTextures textures = profile.getTextures();

            try {
               textures.setSkin(new URL(textureUrl));
            } catch (MalformedURLException e) {
               e.printStackTrace();
               return this;
            }

            profile.setTextures(textures);
            skullMeta.setOwnerProfile(profile);
            this.setMeta(skullMeta);
            return this;
         }
      } else if (PROFILE_FIELD == null) {
         return this;
      } else {
         SkullMeta skullMeta = (SkullMeta)this.getMeta();
         GameProfile profile = new GameProfile(profileId, "");
         profile.properties().put("textures", new Property("textures", texture));

         try {
            PROFILE_FIELD.set(skullMeta, profile);
         } catch (IllegalAccessException | IllegalArgumentException ex) {
            ((Exception)ex).printStackTrace();
         }

         this.setMeta(skullMeta);
         return this;
      }
   }

   @Contract("_ -> this")
   public @NotNull SkullBuilder texture(@NotNull String texture) {
      return this.texture(texture, UUID.randomUUID());
   }

   @Contract("_ -> this")
   public @NotNull SkullBuilder owner(@NotNull OfflinePlayer player) {
      if (!SkullUtil.isPlayerSkull(this.getItemStack())) {
         return this;
      } else {
         SkullMeta skullMeta = (SkullMeta)this.getMeta();
         if (VersionHelper.IS_SKULL_OWNER_LEGACY) {
            skullMeta.setOwner(player.getName());
         } else {
            skullMeta.setOwningPlayer(player);
         }

         this.setMeta(skullMeta);
         return this;
      }
   }

   static {
      Field field;
      try {
         SkullMeta skullMeta = (SkullMeta)SkullUtil.skull().getItemMeta();
         field = skullMeta.getClass().getDeclaredField("profile");
         field.setAccessible(true);
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
         field = null;
      }

      PROFILE_FIELD = field;
   }
}
