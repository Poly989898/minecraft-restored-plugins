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
import net.minecraft.advancements.critereon.CriterionConditionBlock;
import net.minecraft.advancements.critereon.CriterionConditionBlock.a;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.protocol.game.PacketPlayInArmAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.food.FoodInfo;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.block.CraftBlockType;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
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
import org.bukkit.profile.PlayerProfile;

public class VersionWrapper_1_20_R4 implements VersionWrapper {
   private final Set<Material> generatorOutputs = new HashSet<>();
   private static final String URL_PREFIX = "\"url\":\"";
   private static final String URL_SUFFIX = "\"";
   private static final OreDrops IRON_ORE = new OreDrops(Material.IRON_INGOT);
   private static final OreDrops GOLD_ORE = new OreDrops(Material.GOLD_INGOT);
   private static final OreDrops COPPER_ORE = new OreDrops(Material.COPPER_INGOT, 2, 5);
   private static final OreDrops ANCIENT_DEBRIS = new OreDrops(Material.NETHERITE_SCRAP);

   public VersionWrapper_1_20_R4() {
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
      return ((FoodInfo)CraftItemStack.asNMSCopy(item).a(DataComponents.v)).b();
   }

   @Override
   public float getSaturationRestored(ItemStack item) {
      return ((FoodInfo)CraftItemStack.asNMSCopy(item).a(DataComponents.v)).c();
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
      return new VersionWrapper_1_20_R4.CraftNBTItem(item);
   }

   @Override
   public void playArmAnimation(Player player) {
      EntityPlayer p = ((CraftPlayer)player).getHandle();
      PlayerConnection connection = p.c;
      PacketPlayOutAnimation armSwing = new PacketPlayOutAnimation(p, 0);
      connection.b(armSwing);
      connection.a(new PacketPlayInArmAnimation(EnumHand.a));
   }

   @Override
   public Sound getBlockPlaceSound(Block block) {
      WorldServer nmsWorld = ((CraftWorld)block.getWorld()).getHandle();
      IBlockData state = nmsWorld.a_(new BlockPosition(block.getX(), block.getY(), block.getZ()));
      SoundEffect event = state.w().e();
      return Sound.valueOf(event.a().a().replace(".", "_").toUpperCase());
   }

   @Override
   public String getSkullValue(Block block) {
      TileEntitySkull skull = (TileEntitySkull)((CraftWorld)block.getWorld()).getHandle().c_(new BlockPosition(block.getX(), block.getY(), block.getZ()));
      return skull.c() == null ? "" : ((Property)skull.c().f().getProperties().get("textures").iterator().next()).value();
   }

   @Override
   public void setSkullValue(Block block, String value) {
      TileEntitySkull skull = (TileEntitySkull)((CraftWorld)block.getWorld()).getHandle().c_(new BlockPosition(block.getX(), block.getY(), block.getZ()));
      GameProfile profile = new GameProfile(UUID.randomUUID(), "SkullTexture");
      profile.getProperties().put("textures", new Property("textures", value));
      skull.a(new ResolvableProfile(profile));
      skull.e();
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
         GameProfile gameProfile = handle.gb();

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
      return new VersionWrapper_1_20_R4.InventoryViewImpl(event.getView());
   }

   @Override
   public VInventoryView getOpenInventory(Player player) {
      return new VersionWrapper_1_20_R4.InventoryViewImpl(player.getOpenInventory());
   }

   @Override
   public InventoryClickEvent newInventoryClickEvent(VInventoryView view, SlotType type, int slot, ClickType click, InventoryAction action) {
      return new InventoryClickEvent(((VersionWrapper_1_20_R4.InventoryViewImpl)view).view, type, slot, click, action);
   }

   private static class CraftNBTCompound extends NBTCompound {
      private final NBTTagCompound compound;

      public CraftNBTCompound(VersionWrapper_1_20_R4.CraftNBTItem item, String path) {
         this.compound = item.hasTag(path) && NBTTypeHelper.COMPOUND.is(item.getTypeId(path)) ? item.compound.p(path) : new NBTTagCompound();
      }

      public CraftNBTCompound(VersionWrapper_1_20_R4.CraftNBTCompound comp, String path) {
         this.compound = comp.hasTag(path) && NBTTypeHelper.COMPOUND.is(comp.getTypeId(path)) ? comp.compound.p(path) : new NBTTagCompound();
      }

      @Override
      public boolean hasTag(String path) {
         return this.compound.e(path);
      }

      @Override
      public Object get(String path) {
         return this.compound.c(path);
      }

      @Override
      public NBTCompound getNBTCompound(String path) {
         return new VersionWrapper_1_20_R4.CraftNBTCompound(this, path);
      }

      @Override
      public String getString(String path) {
         return this.compound.l(path);
      }

      @Override
      public boolean getBoolean(String path) {
         return this.compound.q(path);
      }

      @Override
      public double getDouble(String path) {
         return this.compound.k(path);
      }

      @Override
      public int getInteger(String path) {
         return this.compound.h(path);
      }

      @Override
      public Set<String> getTags() {
         return this.compound.e();
      }

      @Override
      public int getTypeId(String path) {
         return this.compound.c(path).b();
      }
   }

   public static class CraftNBTItem extends NBTItem {
      private final net.minecraft.world.item.ItemStack nms;
      private final NBTTagCompound compound;

      public CraftNBTItem(ItemStack item) {
         super(item);
         this.nms = CraftItemStack.asNMSCopy(item);
         CustomData customDataTag = (CustomData)this.nms.a(DataComponents.b);
         this.compound = customDataTag == null ? new NBTTagCompound() : customDataTag.d();
      }

      @Override
      public Object get(String path) {
         return this.compound.c(path);
      }

      @Override
      public String getString(String path) {
         return this.compound.l(path);
      }

      @Override
      public boolean hasTag(String path) {
         return this.compound.e(path);
      }

      @Override
      public boolean getBoolean(String path) {
         return this.compound.q(path);
      }

      @Override
      public double getDouble(String path) {
         return this.compound.k(path);
      }

      @Override
      public int getInteger(String path) {
         return this.compound.h(path);
      }

      @Override
      public NBTCompound getNBTCompound(String path) {
         return new VersionWrapper_1_20_R4.CraftNBTCompound(this, path);
      }

      @Override
      public NBTItem addTag(List<ItemTag> tags) {
         tags.forEach(tag -> {
            if (tag.getValue() instanceof Boolean) {
               this.compound.a(tag.getPath(), (Boolean)tag.getValue());
            } else if (tag.getValue() instanceof Double) {
               this.compound.a(tag.getPath(), (Double)tag.getValue());
            } else if (tag.getValue() instanceof String) {
               this.compound.a(tag.getPath(), (String)tag.getValue());
            } else if (tag.getValue() instanceof Integer) {
               this.compound.a(tag.getPath(), (Integer)tag.getValue());
            } else if (tag.getValue() instanceof List) {
               NBTTagList tagList = new NBTTagList();

               for (Object s : (List)tag.getValue()) {
                  if (s instanceof String) {
                     tagList.add(NBTTagString.a((String)s));
                  }
               }

               this.compound.a(tag.getPath(), tagList);
            }
         });
         return this;
      }

      @Override
      public void setCanMine(Collection<Material> blocks) {
         net.minecraft.world.level.block.Block[] arr = blocks.stream()
            .map(CraftBlockType::bukkitToMinecraft)
            .toArray(net.minecraft.world.level.block.Block[]::new);
         List<CriterionConditionBlock> list = new ArrayList<>();
         list.add(a.a().a(arr).b());
         this.nms.b(DataComponents.m, new AdventureModePredicate(list, false));
      }

      @Override
      public NBTItem removeTag(String... paths) {
         for (String path : paths) {
            this.compound.r(path);
         }

         return this;
      }

      @Override
      public Set<String> getTags() {
         return this.compound.e();
      }

      @Override
      public ItemStack toItem() {
         this.nms.b(DataComponents.b, CustomData.a(this.compound));
         return CraftItemStack.asBukkitCopy(this.nms);
      }

      @Override
      public int getTypeId(String path) {
         return this.compound.c(path).b();
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
