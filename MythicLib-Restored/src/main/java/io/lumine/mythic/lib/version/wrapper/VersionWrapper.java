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

   Object getProfile(SkullMeta var1);

   void setProfile(SkullMeta var1, Object var2);

   Object newProfile(UUID var1, String var2);

   OreDrops getOreDrops(Material var1);

   int getFoodRestored(ItemStack var1);

   float getAttackCooldown(Player var1);

   boolean isGeneratorOutput(Material var1);

   float getSaturationRestored(ItemStack var1);

   FurnaceRecipe getFurnaceRecipe(String var1, ItemStack var2, Material var3, float var4, int var5);

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

   Enchantment getEnchantmentFromString(String var1);

   FurnaceRecipe getFurnaceRecipe(NamespacedKey var1, ItemStack var2, Material var3, float var4, int var5);

   boolean isCropFullyGrown(Block var1);

   boolean isUndead(Entity var1);

   NBTItem getNBTItem(ItemStack var1);

   boolean isHelmet(Material var1);

   default void sendActionBar(Player player, String message) {
      this.sendActionBarRaw(player, "{\"text\": \"" + message + "\"}");
   }

   void sendActionBarRaw(Player var1, String var2);

   void sendJson(Player var1, String var2);

   @Deprecated
   int getNextContainerId(Player var1);

   @Deprecated
   void handleInventoryCloseEvent(Player var1);

   @Deprecated
   void sendPacketOpenWindow(Player var1, int var2);

   @Deprecated
   void sendPacketCloseWindow(Player var1, int var2);

   @Deprecated
   void setActiveContainerDefault(Player var1);

   @Deprecated
   void setActiveContainer(Player var1, Object var2);

   @Deprecated
   void setActiveContainerId(Object var1, int var2);

   @Deprecated
   void addActiveContainerSlotListener(Object var1, Player var2);

   @Deprecated
   Inventory toBukkitInventory(Object var1);

   @Deprecated
   Object newContainerAnvil(Player var1);

   void playArmAnimation(Player var1);

   Sound getBlockPlaceSound(Block var1);

   String getSkullValue(Block var1);

   void setSkullValue(Block var1, String var2);

   void setUUID(Player var1, UUID var2);

   GameProfile getGameProfile(Player var1);

   default AttributeModifier newAttributeModifier(@NotNull NamespacedKey key, double amount, @NotNull Operation operation) {
      String str = key.toString();
      return new AttributeModifier(UUID.nameUUIDFromBytes(str.getBytes(StandardCharsets.UTF_8)), str, amount, operation);
   }

   default boolean matches(AttributeModifier modifier, NamespacedKey key) {
      return modifier.getName().equals(key.toString());
   }

   VInventoryView getView(InventoryEvent var1);

   VInventoryView getOpenInventory(@NotNull Player var1);

   InventoryClickEvent newInventoryClickEvent(
      @NotNull VInventoryView var1, @NotNull SlotType var2, int var3, @NotNull ClickType var4, @NotNull InventoryAction var5
   );
}
