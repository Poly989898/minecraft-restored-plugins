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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.game.PacketPlayInArmAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
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
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class VersionWrapper_1_17_R1 implements VersionWrapper {
   private final Set<Material> generatorOutputs = new HashSet<>();
   private static final OreDrops IRON_ORE = new OreDrops(Material.IRON_INGOT);
   private static final OreDrops GOLD_ORE = new OreDrops(Material.GOLD_INGOT);
   private static final OreDrops COPPER_ORE = new OreDrops(Material.COPPER_INGOT, 2, 5);
   private static final OreDrops ANCIENT_DEBRIS = new OreDrops(Material.NETHERITE_SCRAP);

   public VersionWrapper_1_17_R1() {
      this.generatorOutputs.add(Material.COBBLESTONE);
      this.generatorOutputs.add(Material.OBSIDIAN);
      this.generatorOutputs.add(Material.BASALT);
   }

   @Override
   public Object getProfile(SkullMeta meta) {
      try {
         Field profileField = meta.getClass().getDeclaredField("profile");
         profileField.setAccessible(true);
         Object profile = profileField.get(meta);
         profileField.setAccessible(false);
         return profile;
      } catch (NoSuchFieldException | IllegalAccessException exception) {
         throw new IllegalArgumentException("Could not fetch skull profile:" + exception.getMessage());
      }
   }

   @Override
   public void setProfile(SkullMeta meta, Object object) {
      try {
         Field profileField = meta.getClass().getDeclaredField("profile");
         profileField.setAccessible(true);
         profileField.set(meta, object);
         profileField.setAccessible(false);
      } catch (NoSuchFieldException | IllegalAccessException exception) {
         throw new IllegalArgumentException("Could not apply skull profile:" + exception.getMessage());
      }
   }

   @Override
   public Object newProfile(UUID uniqueId, String textureValue) {
      GameProfile profile = new GameProfile(uniqueId, "SkullTexture");
      profile.getProperties().put("textures", new Property("textures", textureValue));
      return profile;
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
      return CraftItemStack.asNMSCopy(item).getItem().getFoodInfo().getNutrition();
   }

   @Override
   public float getSaturationRestored(ItemStack item) {
      return CraftItemStack.asNMSCopy(item).getItem().getFoodInfo().getSaturationModifier();
   }

   @Override
   public void sendJson(Player player, String message) {
      this.clientboundChatPacket(player, message, ChatMessageType.a);
   }

   @Override
   public void sendActionBarRaw(Player player, String message) {
      this.clientboundChatPacket(player, message, ChatMessageType.c);
   }

   private void clientboundChatPacket(Player player, String jsonMessage, ChatMessageType chatType) {
      ((CraftPlayer)player).getHandle().b.sendPacket(new PacketPlayOutChat(ChatSerializer.a(jsonMessage), chatType, UUID.randomUUID()));
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

   private EntityPlayer toNMS(Player player) {
      return ((CraftPlayer)player).getHandle();
   }

   @Override
   public NBTItem getNBTItem(ItemStack item) {
      return new VersionWrapper_1_17_R1.NBTItem_v1_17_R1(item);
   }

   @Override
   public void playArmAnimation(Player player) {
      EntityPlayer p = ((CraftPlayer)player).getHandle();
      PlayerConnection connection = p.b;
      PacketPlayOutAnimation armSwing = new PacketPlayOutAnimation(p, 0);
      connection.sendPacket(armSwing);
      connection.a(new PacketPlayInArmAnimation(EnumHand.a));
   }

   @Override
   public Sound getBlockPlaceSound(Block block) {
      WorldServer nmsWorld = ((CraftWorld)block.getWorld()).getHandle();
      IBlockData state = nmsWorld.getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
      SoundEffect event = state.getBlock().getStepSound(state).getPlaceSound();
      return Sound.valueOf(event.a().getKey().replace(".", "_").toUpperCase());
   }

   @Override
   public String getSkullValue(Block block) {
      TileEntitySkull skull = (TileEntitySkull)((CraftWorld)block.getWorld())
         .getHandle()
         .getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
      return skull.d() == null ? "" : ((Property)skull.d().getProperties().get("textures").iterator().next()).getValue();
   }

   @Override
   public void setSkullValue(Block block, String value) {
      TileEntitySkull skull = (TileEntitySkull)((CraftWorld)block.getWorld())
         .getHandle()
         .getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
      GameProfile profile = new GameProfile(UUID.randomUUID(), null);
      profile.getProperties().put("textures", new Property("textures", value));
      skull.setGameProfile(profile);
      skull.update();
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
         EntityPlayer handle = ((CraftPlayer)player).getHandle();
         GameProfile gameProfile = handle.getProfile();

         try {
            Field _id = gameProfile.getClass().getDeclaredField("id");
            _id.setAccessible(true);
            _id.set(gameProfile, uniqueId);
            _id.setAccessible(false);
         } catch (Exception exception) {
            throw new RuntimeException("Could not update player UUID", exception);
         }

         handle.a_(uniqueId);
      }
   }

   @Override
   public GameProfile getGameProfile(Player player) {
      return ((CraftPlayer)player).getProfile();
   }

   @Override
   public VInventoryView getView(InventoryEvent event) {
      return new VersionWrapper_1_17_R1.InventoryViewImpl(event.getView());
   }

   @Override
   public VInventoryView getOpenInventory(Player player) {
      return new VersionWrapper_1_17_R1.InventoryViewImpl(player.getOpenInventory());
   }

   @Override
   public InventoryClickEvent newInventoryClickEvent(VInventoryView view, SlotType type, int slot, ClickType click, InventoryAction action) {
      return new InventoryClickEvent(((VersionWrapper_1_17_R1.InventoryViewImpl)view).view, type, slot, click, action);
   }

   private class AnvilContainer extends ContainerAnvil {
      public AnvilContainer(Player player) {
         super(
            VersionWrapper_1_17_R1.this.getNextContainerId(player),
            ((CraftPlayer)player).getHandle().getInventory(),
            ContainerAccess.at(((CraftWorld)player.getWorld()).getHandle(), new BlockPosition(0, 0, 0))
         );
         this.checkReachable = false;
         this.setTitle(new ChatComponentText("Repair & Name"));
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

   private static class NBTCompound_v1_17_R1 extends NBTCompound {
      private final NBTTagCompound compound;

      public NBTCompound_v1_17_R1(VersionWrapper_1_17_R1.NBTItem_v1_17_R1 item, String path) {
         this.compound = item.hasTag(path) && NBTTypeHelper.COMPOUND.is(item.getTypeId(path)) ? item.compound.getCompound(path) : new NBTTagCompound();
      }

      public NBTCompound_v1_17_R1(VersionWrapper_1_17_R1.NBTCompound_v1_17_R1 comp, String path) {
         this.compound = comp.hasTag(path) && NBTTypeHelper.COMPOUND.is(comp.getTypeId(path)) ? comp.compound.getCompound(path) : new NBTTagCompound();
      }

      @Override
      public boolean hasTag(String path) {
         return this.compound.hasKey(path);
      }

      @Override
      public Object get(String path) {
         return this.compound.get(path);
      }

      @Override
      public NBTCompound getNBTCompound(String path) {
         return new VersionWrapper_1_17_R1.NBTCompound_v1_17_R1(this, path);
      }

      @Override
      public String getString(String path) {
         return this.compound.getString(path);
      }

      @Override
      public boolean getBoolean(String path) {
         return this.compound.getBoolean(path);
      }

      @Override
      public double getDouble(String path) {
         return this.compound.getDouble(path);
      }

      @Override
      public int getInteger(String path) {
         return this.compound.getInt(path);
      }

      @Override
      public Set<String> getTags() {
         return this.compound.getKeys();
      }

      @Override
      public int getTypeId(String path) {
         return this.compound.get(path).getTypeId();
      }
   }

   public static class NBTItem_v1_17_R1 extends NBTItem {
      private final net.minecraft.world.item.ItemStack nms;
      private final NBTTagCompound compound;

      public NBTItem_v1_17_R1(ItemStack item) {
         super(item);
         this.nms = CraftItemStack.asNMSCopy(item);
         this.compound = this.nms.hasTag() ? this.nms.getTag() : new NBTTagCompound();
      }

      @Override
      public Object get(String path) {
         return this.compound.get(path);
      }

      @Override
      public String getString(String path) {
         return this.compound.getString(path);
      }

      @Override
      public boolean hasTag(String path) {
         return this.compound.hasKey(path);
      }

      @Override
      public boolean getBoolean(String path) {
         return this.compound.getBoolean(path);
      }

      @Override
      public double getDouble(String path) {
         return this.compound.getDouble(path);
      }

      @Override
      public int getInteger(String path) {
         return this.compound.getInt(path);
      }

      @Override
      public NBTCompound getNBTCompound(String path) {
         return new VersionWrapper_1_17_R1.NBTCompound_v1_17_R1(this, path);
      }

      @Override
      public NBTItem addTag(List<ItemTag> tags) {
         tags.forEach(tag -> {
            if (tag.getValue() instanceof Boolean) {
               this.compound.setBoolean(tag.getPath(), (Boolean)tag.getValue());
            } else if (tag.getValue() instanceof Double) {
               this.compound.setDouble(tag.getPath(), (Double)tag.getValue());
            } else if (tag.getValue() instanceof String) {
               this.compound.setString(tag.getPath(), (String)tag.getValue());
            } else if (tag.getValue() instanceof Integer) {
               this.compound.setInt(tag.getPath(), (Integer)tag.getValue());
            } else if (tag.getValue() instanceof List) {
               NBTTagList tagList = new NBTTagList();

               for (Object s : (List)tag.getValue()) {
                  if (s instanceof String) {
                     tagList.add(NBTTagString.a((String)s));
                  }
               }

               this.compound.set(tag.getPath(), tagList);
            }
         });
         return this;
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
         return this.compound.getKeys();
      }

      @Override
      public ItemStack toItem() {
         this.nms.setTag(this.compound);
         return CraftItemStack.asBukkitCopy(this.nms);
      }

      @Override
      public int getTypeId(String path) {
         return this.compound.get(path).getTypeId();
      }
   }
}
