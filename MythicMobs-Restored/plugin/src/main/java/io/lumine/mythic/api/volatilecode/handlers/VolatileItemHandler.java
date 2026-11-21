package io.lumine.mythic.api.volatilecode.handlers;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTagBuilder;
import io.lumine.mythic.core.utils.jnbt.ListTag;
import io.lumine.mythic.core.utils.jnbt.ListTagBuilder;
import io.lumine.mythic.core.utils.jnbt.StringTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public interface VolatileItemHandler {
   ItemStack addNBTData(ItemStack itemStack, String str, Tag tag);

   CompoundTag getNBTData(ItemStack itemStack);

   ItemStack setNBTData(ItemStack itemStack, CompoundTag compoundTag);

   void destroyItem(ItemStack itemStack);

   ItemStack setNBTData(ItemStack itemStack, CompoundTag compoundTag, PlaceholderMeta placeholderMeta);

   int spawnFakeItem(Player player, ItemStack itemStack, AbstractLocation abstractLocation);

   void collectFakeItem(Player player, int value);

   void destroyFakeItem(Player player, int value);

   float getItemRecharge(Player player);

   boolean getItemRecharging(Player player);

   void resetItemRecharge(Player player);

   default void setTridentItem(Trident entity, ItemStack item) {
   }

   default String serializeItem(ItemStack itemStack) {
      return "[Unsupported Operation]";
   }

   default Map<String, Object> getComponentData(ItemStack itemStack) {
      return Collections.emptyMap();
   }

   default String dumpComponentData(ItemStack itemStack) {
      return "[Unsupported Operation]";
   }

   default String dumpNBTData(ItemStack itemStack) {
      return "";
   }

   default ItemStack setDisplayName(ItemStack itemStack, Component displayName) {
      CompoundTag nbt = this.getNBTData(itemStack);
      CompoundTagBuilder displayTag = Optional.ofNullable(nbt.getCompound("Display")).map(CompoundTag::createBuilder).orElse(CompoundTagBuilder.create());
      displayTag.putString("Name", GsonComponentSerializer.gson().serialize(displayName));
      return this.setNBTData(itemStack, nbt.builder(builder -> builder.put("Display", displayTag.build())));
   }

   default ItemStack setLore(ItemStack itemStack, Collection<Component> lore) {
      CompoundTag nbt = this.getNBTData(itemStack);
      CompoundTagBuilder displayTag = Optional.ofNullable(nbt.getCompound("Display")).map(CompoundTag::createBuilder).orElse(CompoundTagBuilder.create());
      displayTag.put(
         "Lore",
         ListTagBuilder.create(StringTag.class)
            .addAll(lore.stream().map(line -> GsonComponentSerializer.gson().serialize(line)).map(StringTag::new).toList())
            .build()
      );
      return this.setNBTData(itemStack, nbt.builder(builder -> builder.put("Display", displayTag.build())));
   }

   default ItemStack setSkinData(
      ItemStack itemStack, @Nullable String playerName, @Nullable UUID playerUuid, @Nullable String textureData, @Nullable String textureUrl
   ) {
      if (playerName != null) {
         itemStack = this.addNBTData(itemStack, "SkullOwner", new StringTag(playerName));
      } else {
         if (textureData == null && textureUrl == null) {
            throw new IllegalArgumentException("cannot set skin data with none of player name, texture data, or texture URL non-null.");
         }

         CompoundTag valuesTag = CompoundTag.create(
            texture -> texture.putString(
               "Value", Objects.requireNonNullElseGet(textureData, () -> Base64Coder.encodeString("{textures:{SKIN:{url:\"" + textureUrl + "\"}}}"))
            )
         );
         ListTag valuesList = ListTagBuilder.createWith(valuesTag).build();
         itemStack = this.addNBTData(itemStack, "SkullOwner", CompoundTag.create(skullOwnerTag -> {
            skullOwnerTag.put("Properties", CompoundTag.create(properties -> properties.put("textures", valuesList)));
            skullOwnerTag.putUUID("Id", playerUuid);
         }));
      }

      return itemStack;
   }

   default ItemStack setCanPlaceOn(ItemStack itemStack, Collection<Material> materials) {
      return this.addNBTData(
         itemStack,
         "CanPlaceOn",
         ListTag.create(
            StringTag.class,
            builder -> builder.addAll(materials.stream().map(it -> new StringTag(NamespacedKey.minecraft(it.name().toLowerCase()).toString())).toList())
         )
      );
   }

   default ItemStack setCanBreak(ItemStack itemStack, Collection<Material> materials) {
      return this.addNBTData(
         itemStack,
         "CanBreak",
         ListTag.create(
            StringTag.class,
            builder -> builder.addAll(materials.stream().map(it -> new StringTag(NamespacedKey.minecraft(it.name().toLowerCase()).toString())).toList())
         )
      );
   }

   default Optional<String> getItemBlockStateValue(ItemStack itemStack, String key) {
      return Optional.empty();
   }

   default void setItemBlockStateValue(ItemStack itemStack, String key, String value) {
   }

   default void setItemBlockStateComponent(ItemStack itemStack, Map<String, String> values) {
   }

   default void setCompostableComponent(ItemStack itemStack, String layers) {
   }

   default void setCookingFuelComponent(ItemStack itemStack, String burnTime, String speedMultiplier) {
   }

   default void setBrewingFuelComponent(ItemStack itemStack, String uses, String speedMultiplier) {
   }
}
