package io.lumine.mythic.lib.version.wrapper;

import com.mojang.authlib.GameProfile;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.version.OreDrops;
import io.lumine.mythic.lib.version.VInventoryView;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public interface VersionWrapper {
   String PLAYER_PROFILE_NAME = "SkullTexture";

   Object getProfile(SkullMeta skullMeta);

   void setProfile(SkullMeta skullMeta, Object obj);

   Object newProfile(UUID uuid, String text);

   OreDrops getOreDrops(Material material);

   int getFoodRestored(ItemStack item);

   float getAttackCooldown(Player player);

   boolean isGeneratorOutput(Material material);

   float getSaturationRestored(ItemStack item);

   FurnaceRecipe getFurnaceRecipe(String text, ItemStack item, Material material, float value, int count);

   @Deprecated
   default NBTItem copyTexture(NBTItem item) {
      return this.getNBTItem(new ItemStack(item.getItem().getType())).addTag(new ItemTag("CustomModelData", item.getInteger("CustomModelData")));
   }

   @Deprecated
   default ItemStack textureItem(Material material, int modelData) {
      ItemStack stack = new ItemStack(material);
      ItemMeta meta = stack.getItemMeta();
      meta.setCustomModelData(modelData);
      stack.setItemMeta(meta);
      return stack;
   }

   Enchantment getEnchantmentFromString(String text);

   FurnaceRecipe getFurnaceRecipe(NamespacedKey namespacedKey, ItemStack item, Material material, float value, int count);

   boolean isCropFullyGrown(Block block);

   boolean isUndead(Entity entity);

   NBTItem getNBTItem(ItemStack item);

   boolean isHelmet(Material material);

   default void sendActionBar(Player player, String message) {
      this.sendActionBarRaw(player, "{\"text\": \"" + message + "\"}");
   }

   void sendActionBarRaw(Player player, String text);

   void sendJson(Player player, String text);

   @Deprecated
   int getNextContainerId(Player player);

   @Deprecated
   void handleInventoryCloseEvent(Player player);

   @Deprecated
   void sendPacketOpenWindow(Player player, int count);

   @Deprecated
   void sendPacketCloseWindow(Player player, int count);

   @Deprecated
   void setActiveContainerDefault(Player player);

   @Deprecated
   void setActiveContainer(Player player, Object obj);

   @Deprecated
   void setActiveContainerId(Object obj, int count);

   @Deprecated
   void addActiveContainerSlotListener(Object obj, Player player);

   @Deprecated
   Inventory toBukkitInventory(Object obj);

   @Deprecated
   Object newContainerAnvil(Player player);

   void playArmAnimation(Player player);

   Sound getBlockPlaceSound(Block block);

   String getSkullValue(Block block);

   void setSkullValue(Block block, String text);

   void setUUID(Player player, UUID uuid);

   GameProfile getGameProfile(Player player);

   default AttributeModifier newAttributeModifier(@NotNull NamespacedKey key, double amount, @NotNull Operation operation) {
      String str = key.toString();
      return new AttributeModifier(UUID.nameUUIDFromBytes(str.getBytes(StandardCharsets.UTF_8)), str, amount, operation);
   }

   default boolean matches(AttributeModifier modifier, NamespacedKey key) {
      return modifier.getName().equals(key.toString());
   }

   VInventoryView getView(InventoryEvent event);

   VInventoryView getOpenInventory(@NotNull Player player);

   InventoryClickEvent newInventoryClickEvent(
      @NotNull VInventoryView vInventoryView, @NotNull SlotType slotType, int count, @NotNull ClickType clickType, @NotNull InventoryAction inventoryAction
   );
}
