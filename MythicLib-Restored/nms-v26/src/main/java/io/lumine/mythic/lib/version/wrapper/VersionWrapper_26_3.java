package io.lumine.mythic.lib.version.wrapper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTCompound;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.NBTTypeHelper;
import io.lumine.mythic.lib.version.OreDrops;
import io.lumine.mythic.lib.version.VInventoryView;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

public class VersionWrapper_26_3 implements VersionWrapper {
   private final Set<Material> generatorOutputs = new HashSet<>();
   private static final String URL_PREFIX = "\"url\":\"";
   private static final String URL_SUFFIX = "\"";
   private static final OreDrops IRON_ORE = new OreDrops(Material.IRON_INGOT);
   private static final OreDrops GOLD_ORE = new OreDrops(Material.GOLD_INGOT);
   private static final OreDrops COPPER_ORE = new OreDrops(Material.COPPER_INGOT, 2, 5);
   private static final OreDrops ANCIENT_DEBRIS = new OreDrops(Material.NETHERITE_SCRAP);

   public VersionWrapper_26_3() {
      this.generatorOutputs.add(Material.COBBLESTONE);
      this.generatorOutputs.add(Material.OBSIDIAN);
      this.generatorOutputs.add(Material.BASALT);
   }

   public PlayerProfile getProfile(SkullMeta meta) {
      return meta.getOwnerProfile();
   }

   @Override
   public void setProfile(SkullMeta meta, Object object) {
      meta.setOwnerProfile(object == null ? null : (PlayerProfile)object);
   }

   public PlayerProfile newProfile(UUID uniqueId, String textureValue) {
      PlayerProfile profile = Bukkit.getServer().createPlayerProfile(uniqueId, "SkullTexture");
      String stringUrl = textureValue.startsWith("http") ? textureValue : this.extractUrl(new String(Base64.getDecoder().decode(textureValue)));

      URL url;
      try {
         url = new URL(stringUrl);
      } catch (MalformedURLException exception) {
         throw new RuntimeException("Could not create new player profile: " + exception.getMessage());
      }

      profile.getTextures().setSkin(url);
      return profile;
   }

   private String extractUrl(String str) {
      int start = str.indexOf("\"url\":\"");
      Validate.isTrue(start >= 0, "Could not find prefix in decoded skull value", new Object[0]);
      start += "\"url\":\"".length();
      int end = str.indexOf("\"", start);
      return str.substring(start, end);
   }

   @Override
   public boolean isGeneratorOutput(Material material) {
      return this.generatorOutputs.contains(material);
   }

   @Override
   public boolean isHelmet(Material material) {
      return material.getEquipmentSlot() == EquipmentSlot.HEAD;
   }

   @Override
   public OreDrops getOreDrops(Material material) {
      switch (material) {
         case IRON_ORE:
         case DEEPSLATE_IRON_ORE:
            return IRON_ORE;
         case GOLD_ORE:
         case DEEPSLATE_GOLD_ORE:
            return GOLD_ORE;
         case COPPER_ORE:
         case DEEPSLATE_COPPER_ORE:
            return COPPER_ORE;
         case ANCIENT_DEBRIS:
            return ANCIENT_DEBRIS;
         default:
            return null;
      }
   }

   @Override
   public float getAttackCooldown(Player player) {
      return player.getAttackCooldown();
   }

   @Override
   public int getFoodRestored(ItemStack item) {
      FoodProperties food = CraftItemStack.asNMSCopy(item).get(DataComponents.FOOD);
      return food == null ? 0 : food.nutrition();
   }

   @Override
   public float getSaturationRestored(ItemStack item) {
      FoodProperties food = CraftItemStack.asNMSCopy(item).get(DataComponents.FOOD);
      return food == null ? 0.0F : food.saturation();
   }

   @Override
   public void sendJson(Player player, String message) {
      try {
         player.spigot().sendMessage(ChatMessageType.CHAT, ComponentSerializer.parse(message));
      } catch (RuntimeException exception) {
         MythicLib.plugin.getLogger().log(Level.WARNING, "Could not parse raw message sent to player. Make sure it has the right syntax");
         exception.printStackTrace();
      }
   }

   @Override
   public void sendActionBar(Player player, String message) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(message));
   }

   @Override
   public void sendActionBarRaw(Player player, String message) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ComponentSerializer.parse(message));
   }

   @Override
   public int getNextContainerId(Player player) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public void handleInventoryCloseEvent(Player player) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public void sendPacketOpenWindow(Player player, int containerId) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public void sendPacketCloseWindow(Player player, int containerId) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public void setActiveContainerDefault(Player player) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public void setActiveContainer(Player player, Object container) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public void setActiveContainerId(Object container, int containerId) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public void addActiveContainerSlotListener(Object container, Player player) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public Inventory toBukkitInventory(Object container) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public Object newContainerAnvil(Player player) {
      throw new RuntimeException("Not implemented");
   }

   @Override
   public NBTItem getNBTItem(ItemStack item) {
      return new VersionWrapper_26_3.CraftNBTItem(item);
   }

   @Override
   public void playArmAnimation(Player player) {
      ServerPlayer handle = ((CraftPlayer)player).getHandle();
      ServerGamePacketListenerImpl connection = handle.connection;
      connection.send(new ClientboundAnimatePacket(handle, 0));
      // The original also fed a serverbound swing packet to the connection (server-side swing handling).
      new org.bukkit.event.player.PlayerAnimationEvent(player).callEvent();
   }

   @Override
   public Sound getBlockPlaceSound(Block block) {
      ServerLevel level = ((CraftWorld)block.getWorld()).getHandle();
      BlockState state = level.getBlockState(new BlockPos(block.getX(), block.getY(), block.getZ()));
      NamespacedKey key = NamespacedKey.fromString(state.getSoundType().getPlaceSound().location().toString());
      return Registry.SOUNDS.get(key);
   }

   @Override
   public String getSkullValue(Block block) {
      SkullBlockEntity skull = (SkullBlockEntity)((CraftWorld)block.getWorld()).getHandle().getBlockEntity(new BlockPos(block.getX(), block.getY(), block.getZ()));
      if (skull == null || skull.getOwnerProfile() == null) {
         return "";
      }
      Collection<Property> textures = skull.getOwnerProfile().partialProfile().properties().get("textures");
      return textures.isEmpty() ? "" : textures.iterator().next().value();
   }

   @Override
   public void setSkullValue(Block block, String value) {
      SkullBlockEntity skull = (SkullBlockEntity)((CraftWorld)block.getWorld()).getHandle().getBlockEntity(new BlockPos(block.getX(), block.getY(), block.getZ()));
      com.google.common.collect.Multimap<String, Property> props = com.google.common.collect.HashMultimap.create();
      props.put("textures", new Property("textures", value));
      GameProfile profile = new GameProfile(UUID.randomUUID(), "SkullTexture", new com.mojang.authlib.properties.PropertyMap(props));
      skull.owner = ResolvableProfile.createResolved(profile);
      skull.setChanged();
   }

   @Override
   public FurnaceRecipe getFurnaceRecipe(String path, ItemStack item, Material material, float exp, int cook) {
      return new FurnaceRecipe(new NamespacedKey(MythicLib.inst(), "mmoitems_furnace_" + path), item, material, exp, cook);
   }

   @Override
   public Enchantment getEnchantmentFromString(String s) {
      return Enchantment.getByKey(NamespacedKey.minecraft(s));
   }

   @Override
   public FurnaceRecipe getFurnaceRecipe(NamespacedKey key, ItemStack item, Material material, float exp, int cook) {
      return new FurnaceRecipe(key, item, material, exp, cook);
   }

   @Override
   public boolean isCropFullyGrown(Block block) {
      if (block.getBlockData() instanceof Ageable) {
         Ageable ageable = (Ageable)block.getBlockData();
         return ageable.getAge() == ageable.getMaximumAge();
      } else {
         return false;
      }
   }

   @Override
   public boolean isUndead(Entity entity) {
      EntityType type = entity.getType();
      return type == EntityType.SKELETON
         || type == EntityType.STRAY
         || type == EntityType.WITHER_SKELETON
         || type == EntityType.ZOMBIE
         || type == EntityType.DROWNED
         || type == EntityType.HUSK
         || type.name().equals("PIG_ZOMBIE")
         || type == EntityType.ZOMBIE_VILLAGER
         || type == EntityType.PHANTOM
         || type == EntityType.WITHER
         || type == EntityType.SKELETON_HORSE
         || type == EntityType.ZOMBIE_HORSE;
   }

   @Override
   public void setUUID(Player player, UUID uniqueId) {
      if (!player.getUniqueId().equals(uniqueId)) {
         ServerPlayer handle = ((CraftPlayer)player).getHandle();
         GameProfile gameProfile = handle.getGameProfile();

         try {
            // GameProfile is a record in recent authlib: its id cannot be changed by reflection.
            // The profile field of the player is replaced instead.
            Field profileField = ServerPlayer.class.getDeclaredField("gameProfile");
            profileField.setAccessible(true);
            profileField.set(handle, new GameProfile(uniqueId, gameProfile.name(), gameProfile.properties()));
         } catch (Exception exception) {
            throw new RuntimeException("Could not update player UUID", exception);
         }

         handle.setUUID(uniqueId);
      }
   }

   @Override
   public GameProfile getGameProfile(Player player) {
      return ((CraftPlayer)player).getHandle().getGameProfile();
   }

   @Override
   public AttributeModifier newAttributeModifier(NamespacedKey key, double amount, Operation operation) {
      return new AttributeModifier(key, amount, operation, EquipmentSlotGroup.ANY);
   }

   @Override
   public boolean matches(AttributeModifier modifier, NamespacedKey key) {
      return modifier.getKey().equals(key);
   }

   @Override
   public VInventoryView getView(InventoryEvent event) {
      return new VersionWrapper_26_3.InventoryViewImpl(event.getView());
   }

   @Override
   public VInventoryView getOpenInventory(Player player) {
      return new VersionWrapper_26_3.InventoryViewImpl(player.getOpenInventory());
   }

   @Override
   public InventoryClickEvent newInventoryClickEvent(VInventoryView view, SlotType type, int slot, ClickType click, InventoryAction action) {
      return new InventoryClickEvent(((VersionWrapper_26_3.InventoryViewImpl)view).view, type, slot, click, action);
   }

   private static class CraftNBTCompound extends NBTCompound {
      private final CompoundTag compound;

      public CraftNBTCompound(VersionWrapper_26_3.CraftNBTItem item, String path) {
         this.compound = item.hasTag(path) && NBTTypeHelper.COMPOUND.is(item.getTypeId(path)) ? item.compound.getCompoundOrEmpty(path) : new CompoundTag();
      }

      public CraftNBTCompound(VersionWrapper_26_3.CraftNBTCompound comp, String path) {
         this.compound = comp.hasTag(path) && NBTTypeHelper.COMPOUND.is(comp.getTypeId(path)) ? comp.compound.getCompoundOrEmpty(path) : new CompoundTag();
      }

      @Override
      public boolean hasTag(String path) {
         return this.compound.contains(path);
      }

      @Override
      public Object get(String path) {
         return this.compound.get(path);
      }

      @Override
      public NBTCompound getNBTCompound(String path) {
         return new VersionWrapper_26_3.CraftNBTCompound(this, path);
      }

      @Override
      public String getString(String path) {
         return this.compound.getStringOr(path, "");
      }

      @Override
      public boolean getBoolean(String path) {
         return this.compound.getBooleanOr(path, false);
      }

      @Override
      public double getDouble(String path) {
         return this.compound.getDoubleOr(path, 0.0);
      }

      @Override
      public int getInteger(String path) {
         return this.compound.getIntOr(path, 0);
      }

      @Override
      public Set<String> getTags() {
         return this.compound.keySet();
      }

      @Override
      public int getTypeId(String path) {
         return this.compound.get(path).getId();
      }
   }

   public static class CraftNBTItem extends NBTItem {
      private final net.minecraft.world.item.ItemStack nms;
      private final CompoundTag compound;
      private Collection<Material> canMine;

      public CraftNBTItem(ItemStack item) {
         super(item);
         this.nms = CraftItemStack.asNMSCopy(item);
         CustomData customData = this.nms.get(DataComponents.CUSTOM_DATA);
         this.compound = customData == null ? new CompoundTag() : customData.copyTag();
      }

      @Override
      public Object get(String path) {
         return this.compound.get(path);
      }

      @Override
      public String getString(String path) {
         return this.compound.getStringOr(path, "");
      }

      @Override
      public boolean hasTag(String path) {
         return this.compound.contains(path);
      }

      @Override
      public boolean getBoolean(String path) {
         return this.compound.getBooleanOr(path, false);
      }

      @Override
      public double getDouble(String path) {
         return this.compound.getDoubleOr(path, 0.0);
      }

      @Override
      public int getInteger(String path) {
         return this.compound.getIntOr(path, 0);
      }

      @Override
      public NBTCompound getNBTCompound(String path) {
         return new VersionWrapper_26_3.CraftNBTCompound(this, path);
      }

      @Override
      public NBTItem addTag(List<ItemTag> tags) {
         tags.forEach(tag -> {
            if (tag.getValue() instanceof Boolean) {
               this.compound.putBoolean(tag.getPath(), (Boolean)tag.getValue());
            } else if (tag.getValue() instanceof Double) {
               this.compound.putDouble(tag.getPath(), (Double)tag.getValue());
            } else if (tag.getValue() instanceof String) {
               this.compound.putString(tag.getPath(), (String)tag.getValue());
            } else if (tag.getValue() instanceof Integer) {
               this.compound.putInt(tag.getPath(), (Integer)tag.getValue());
            } else if (tag.getValue() instanceof List) {
               ListTag tagList = new ListTag();

               for (Object s : (List<?>)tag.getValue()) {
                  if (s instanceof String) {
                     tagList.add(StringTag.valueOf((String)s));
                  }
               }

               this.compound.put(tag.getPath(), tagList);
            }
         });
         return this;
      }

      @Override
      public void setCanMine(Collection<Material> blocks) {
         // Applied through the Bukkit item meta in toItem() (the NMS predicate classes changed between versions).
         this.canMine = blocks;
      }

      @Override
      public NBTItem removeTag(String... paths) {
         for (String path : paths) {
            this.compound.remove(path);
         }

         return this;
      }

      @Override
      public Set<String> getTags() {
         return this.compound.keySet();
      }

      @Override
      public ItemStack toItem() {
         this.nms.set(DataComponents.CUSTOM_DATA, CustomData.of(this.compound));
         ItemStack result = CraftItemStack.asBukkitCopy(this.nms);
         if (this.canMine != null) {
            org.bukkit.inventory.meta.ItemMeta meta = result.getItemMeta();
            if (meta != null) {
               meta.setDestroyableKeys(this.canMine.stream().filter(Material::isBlock).map(Material::getKey).collect(java.util.stream.Collectors.toList()));
               result.setItemMeta(meta);
            }
         }
         return result;
      }

      @Override
      public int getTypeId(String path) {
         return this.compound.get(path).getId();
      }
   }

   private static class InventoryViewImpl implements VInventoryView {
      private final InventoryView view;

      InventoryViewImpl(InventoryView view) {
         this.view = view;
      }

      @Override
      public String getTitle() {
         return this.view.getTitle();
      }

      @Override
      public InventoryType getType() {
         return this.view.getType();
      }

      @Override
      public Inventory getTopInventory() {
         return this.view.getTopInventory();
      }

      @Override
      public Inventory getBottomInventory() {
         return this.view.getBottomInventory();
      }

      @Override
      public void setCursor(ItemStack actualCursor) {
         this.view.setCursor(actualCursor);
      }

      @Override
      public HumanEntity getPlayer() {
         return this.view.getPlayer();
      }

      @Override
      public void close() {
         this.view.close();
      }
   }
}
