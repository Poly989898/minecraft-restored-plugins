package io.lumine.mythic.core.volatilecode.v1_21_R7;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileItemHandler;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom.Dimension;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R7.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftTrident;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;

public class VolatileItemHandlerImpl implements VolatileItemHandler {
   private final Reflector<CraftItemStack> refItemStack = new Reflector<>(CraftItemStack.class, "handle");

   public VolatileItemHandlerImpl(VolatileCodeHandler handler) {
   }

   private ItemStack getNMSItemStack(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack;
      if (itemStack instanceof CraftItemStack craftItemStack) {
         nmsItemStack = (ItemStack)this.refItemStack.get(craftItemStack, "handle");
      } else {
         nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      }

      return nmsItemStack;
   }

   @Override
   public void destroyItem(org.bukkit.inventory.ItemStack itemStack) {
      if (itemStack instanceof CraftItemStack craftItemStack) {
         ItemStack realStack = (ItemStack)this.refItemStack.get(craftItemStack, "handle");
         if (realStack != null) {
            realStack.e(0);
         }
      }
   }

   @Override
   public org.bukkit.inventory.ItemStack addNBTData(org.bukkit.inventory.ItemStack itemStack, String key, Tag value) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      Map<String, Tag> nbt = this.getNBTData(itemStack).builder(builder -> builder.put(key, value)).getValue();
      NBTTagCompound volatileNbt = new VolatileCompoundTag(nbt).toNMSTag();
      nmsItemStack.b(DataComponents.b, CustomData.a(volatileNbt));
      return itemStack instanceof CraftItemStack ? itemStack : CraftItemStack.asBukkitCopy(nmsItemStack);
   }

   @Override
   public CompoundTag getNBTData(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      if (nmsItemStack != null) {
         CustomData customDataComponent = (CustomData)nmsItemStack.a(DataComponents.b);
         if (customDataComponent != null && !customDataComponent.a()) {
            return VolatileCompoundTag.fromNMSTag(customDataComponent.b());
         }
      }

      return new VolatileCompoundTag(new HashMap<>());
   }

   @Override
   public org.bukkit.inventory.ItemStack setNBTData(org.bukkit.inventory.ItemStack itemStack, CompoundTag compoundTag) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      nmsItemStack.b(DataComponents.b, CustomData.a(((VolatileCompoundTag)compoundTag).toNMSTag()));
      return itemStack instanceof CraftItemStack ? itemStack : CraftItemStack.asBukkitCopy(nmsItemStack);
   }

   @Override
   public org.bukkit.inventory.ItemStack setNBTData(org.bukkit.inventory.ItemStack itemStack, CompoundTag compoundTag, PlaceholderMeta meta) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      nmsItemStack.b(DataComponents.b, CustomData.a(((VolatileCompoundTag)compoundTag).toNMSTag(meta)));
      return itemStack instanceof CraftItemStack ? itemStack : CraftItemStack.asBukkitCopy(nmsItemStack);
   }

   @Override
   public String serializeItem(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      return "";
   }

   @Override
   public Map<String, Object> getComponentData(org.bukkit.inventory.ItemStack bukkitItemStack) {
      ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitItemStack);
      Builder visualOutput = Component.text();
      new StringBuilder();
      Set<DataComponentType<?>> referencedComponentTypes = Collections.newSetFromMap(new IdentityHashMap<>());
      DataComponentPatch patch = itemStack.d();
      referencedComponentTypes.addAll(patch.b().stream().map(Entry::getKey).toList());
      DataComponentMap prototype = itemStack.h().f();
      referencedComponentTypes.addAll(prototype.b());
      Dimension access = MinecraftServer.getServer().bc();
      RegistryOps<JsonElement> ops = access.a(JsonOps.INSTANCE);
      IRegistry<DataComponentType<?>> registry = access.f(Registries.r);
      new ArrayList();
      new ArrayList();
      Map<String, Object> ret = Maps.newConcurrentMap();

      for (DataComponentType<?> type : referencedComponentTypes) {
         String path = ((ResourceKey)registry.d(type).orElseThrow()).a().a();
         Optional<?> patchedValue = patch.a(type);
         TypedDataComponent<?> prototypeValue = prototype.b(type);
         if (patchedValue != null) {
            if (!patchedValue.isEmpty()) {
               Codec aaa = type.c();
               DataResult bbb = aaa.encodeStart(ops, patchedValue.get());
               Object ccc = bbb.getOrThrow();
               ret.put(path, ccc);
            }
         } else if (prototypeValue != null) {
            DataResult<?> aaa = prototypeValue.a(ops);
            Object bbb = aaa.getOrThrow();
            ret.put(path, bbb);
         }
      }

      return ret;
   }

   @Override
   public String dumpComponentData(org.bukkit.inventory.ItemStack bukkitItemStack) {
      return GsonProvider.standard().toJson(this.getNBTData(bukkitItemStack));
   }

   @Override
   public String dumpNBTData(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      if (nmsItemStack != null) {
         CustomData customDataComponent = (CustomData)nmsItemStack.a(DataComponents.b);
         if (customDataComponent != null && !customDataComponent.a()) {
            return customDataComponent.toString();
         }
      }

      return "";
   }

   @Override
   public int spawnFakeItem(Player player, org.bukkit.inventory.ItemStack itemStack, AbstractLocation location) {
      EntityItem item = new EntityItem(
         ((CraftWorld)BukkitAdapter.adapt(location.getWorld())).getHandle(),
         location.getX(),
         location.getY(),
         location.getZ(),
         CraftItemStack.asNMSCopy(itemStack)
      );
      item.a(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
      PacketPlayOutSpawnEntity spawnItem = new PacketPlayOutSpawnEntity(
         item.aA(), item.cY(), location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F, EntityTypes.av, 0, Vec3D.c, 0.0
      );
      PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.aA(), item.aD().c());
      Schedulers.async().run(() -> {
         ((CraftPlayer)player).getHandle().g.b(spawnItem);
         ((CraftPlayer)player).getHandle().g.b(data);
      });
      return item.aA();
   }

   @Override
   public void collectFakeItem(Player player, int id) {
      int playerID = ((CraftPlayer)player).getHandle().aA();
      PacketPlayOutCollect spawnItem = new PacketPlayOutCollect(id, playerID, 1);
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().g.b(spawnItem));
   }

   public void updateFakeItem(Player player, int id, org.bukkit.inventory.ItemStack itemStack, AbstractLocation location) {
      EntityItem item = new EntityItem(
         ((CraftWorld)location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack)
      );
      item.a(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
      PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.aA(), item.aD().c());
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().g.b(data));
   }

   @Override
   public void destroyFakeItem(Player player, int id) {
      PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(new int[]{id});
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().g.b(destroyPacket));
   }

   public void playTotemEffect(AbstractPlayer player, int modelId) {
   }

   @Override
   public float getItemRecharge(Player player) {
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.I(0.0F);
   }

   @Override
   public boolean getItemRecharging(Player player) {
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.I(0.0F) < 1.0F;
   }

   @Override
   public void resetItemRecharge(Player player) {
      EntityPlayer ep = ((CraftPlayer)player).getHandle();
      ep.hf();
   }

   @Override
   public void setTridentItem(Trident entity, org.bukkit.inventory.ItemStack item) {
      ((CraftTrident)entity).setItem(item);
   }

   @Override
   public org.bukkit.inventory.ItemStack setDisplayName(org.bukkit.inventory.ItemStack itemStack, Component displayName) {
      return null;
   }

   @Override
   public org.bukkit.inventory.ItemStack setLore(org.bukkit.inventory.ItemStack itemStack, Collection<Component> lore) {
      return null;
   }

   @Deprecated
   @Override
   public org.bukkit.inventory.ItemStack setCanPlaceOn(org.bukkit.inventory.ItemStack itemStack, Collection<Material> materials) {
      return itemStack;
   }

   @Override
   public org.bukkit.inventory.ItemStack setCanBreak(org.bukkit.inventory.ItemStack itemStack, Collection<Material> materials) {
      return itemStack;
   }

   @Override
   public Optional<String> getItemBlockStateValue(org.bukkit.inventory.ItemStack itemStack, String key) {
      ItemStack nms = this.getNMSItemStack(itemStack);
      return nms.c(DataComponents.ax) ? Optional.ofNullable((String)((BlockItemStateProperties)nms.a(DataComponents.ax)).b().get(key)) : Optional.empty();
   }

   @Override
   public void setItemBlockStateValue(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
      ItemStack nms = this.getNMSItemStack(itemStack);
      if (nms.c(DataComponents.ax)) {
         ((BlockItemStateProperties)nms.a(DataComponents.ax)).b().put(key, value);
      } else {
         nms.b(DataComponents.ax, new BlockItemStateProperties(Map.of(key, value)));
      }
   }

   @Override
   public void setItemBlockStateComponent(org.bukkit.inventory.ItemStack itemStack, Map<String, String> values) {
      ItemStack nms = this.getNMSItemStack(itemStack);
      nms.b(DataComponents.ax, new BlockItemStateProperties(values));
   }
}
