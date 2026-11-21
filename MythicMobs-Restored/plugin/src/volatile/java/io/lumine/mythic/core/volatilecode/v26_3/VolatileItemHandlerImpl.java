package io.lumine.mythic.core.volatilecode.v26_3;

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
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.reflection.Reflector;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.component.DataComponentPatch.SplitResult;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BrewingFuel;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt.Constant;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftTrident;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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
            realStack.setCount(0);
         }
      }
   }

   @Override
   public org.bukkit.inventory.ItemStack addNBTData(org.bukkit.inventory.ItemStack itemStack, String key, Tag value) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      Map<String, Tag> nbt = this.getNBTData(itemStack).builder(builder -> builder.put(key, value)).getValue();
      CompoundTag volatileNbt = new VolatileCompoundTag(nbt).toNMSTag();
      nmsItemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(volatileNbt));
      return itemStack instanceof CraftItemStack ? itemStack : CraftItemStack.asBukkitMirror(nmsItemStack.copy());
   }

   @Override
   public io.lumine.mythic.core.utils.jnbt.CompoundTag getNBTData(org.bukkit.inventory.ItemStack itemStack) {
      ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
      if (nmsItemStack != null) {
         CustomData customDataComponent = (CustomData)nmsItemStack.get(DataComponents.CUSTOM_DATA);
         if (customDataComponent != null && !customDataComponent.isEmpty()) {
            return VolatileCompoundTag.fromNMSTag(customDataComponent.copyTag());
         }
      }

      return new VolatileCompoundTag(new HashMap<>());
   }

   @Override
   public org.bukkit.inventory.ItemStack setNBTData(org.bukkit.inventory.ItemStack itemStack, io.lumine.mythic.core.utils.jnbt.CompoundTag compoundTag) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      nmsItemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(((VolatileCompoundTag)compoundTag).toNMSTag()));
      return itemStack instanceof CraftItemStack ? itemStack : CraftItemStack.asBukkitMirror(nmsItemStack.copy());
   }

   @Override
   public org.bukkit.inventory.ItemStack setNBTData(
      org.bukkit.inventory.ItemStack itemStack, io.lumine.mythic.core.utils.jnbt.CompoundTag compoundTag, PlaceholderMeta meta
   ) {
      ItemStack nmsItemStack = this.getNMSItemStack(itemStack);
      nmsItemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(((VolatileCompoundTag)compoundTag).toNMSTag(meta)));
      return itemStack instanceof CraftItemStack ? itemStack : CraftItemStack.asBukkitMirror(nmsItemStack.copy());
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
      SplitResult patch = itemStack.getComponentsPatch().split();
      referencedComponentTypes.addAll(patch.added().keySet());
      referencedComponentTypes.addAll(patch.removed());
      DataComponentMap prototype = itemStack.getItem().components();
      referencedComponentTypes.addAll(prototype.keySet());
      Frozen access = MinecraftServer.getServer().registryAccess();
      RegistryOps<JsonElement> ops = access.createSerializationContext(JsonOps.INSTANCE);
      Registry<DataComponentType<?>> registry = access.lookupOrThrow(Registries.DATA_COMPONENT_TYPE);
      new ArrayList();
      new ArrayList();
      Map<DataComponentType<?>, Optional<?>> patchEntries = new IdentityHashMap<>();
      patch.added().forEach(entry -> patchEntries.put(entry.type(), Optional.of(entry.value())));
      patch.removed().forEach(typex -> patchEntries.put(typex, Optional.empty()));
      Map<String, Object> ret = Maps.newConcurrentMap();

      for (DataComponentType<?> type : referencedComponentTypes) {
         String path = ((ResourceKey)registry.getResourceKey(type).orElseThrow()).identifier().getPath();
         Optional<?> patchedValue = patchEntries.get(type);
         TypedDataComponent<?> prototypeValue = prototype.getTyped(type);
         if (patchedValue != null) {
            if (!patchedValue.isEmpty()) {
               Codec aaa = type.codecOrThrow();
               DataResult bbb = aaa.encodeStart(ops, patchedValue.get());
               Object ccc = bbb.getOrThrow();
               ret.put(path, ccc);
            }
         } else if (prototypeValue != null) {
            DataResult<?> aaa = prototypeValue.encodeValue(ops);
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
         CustomData customDataComponent = (CustomData)nmsItemStack.get(DataComponents.CUSTOM_DATA);
         if (customDataComponent != null && !customDataComponent.isEmpty()) {
            return customDataComponent.toString();
         }
      }

      return "";
   }

   @Override
   public int spawnFakeItem(Player player, org.bukkit.inventory.ItemStack itemStack, AbstractLocation location) {
      ItemEntity item = new ItemEntity(
         ((CraftWorld)BukkitAdapter.adapt(location.getWorld())).getHandle(),
         location.getX(),
         location.getY(),
         location.getZ(),
         CraftItemStack.asNMSCopy(itemStack)
      );
      item.absSnapTo(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
      ClientboundAddEntityPacket spawnItem = new ClientboundAddEntityPacket(
         item.getId(), item.getUUID(), location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F, EntityTypes.ITEM, 0, Vec3.ZERO, 0.0
      );
      ClientboundSetEntityDataPacket data = new ClientboundSetEntityDataPacket(item.getId(), item.getEntityData().getNonDefaultValues());
      Schedulers.async().run(() -> {
         ((CraftPlayer)player).getHandle().connection.send(spawnItem);
         ((CraftPlayer)player).getHandle().connection.send(data);
      });
      return item.getId();
   }

   @Override
   public void collectFakeItem(Player player, int id) {
      int playerID = ((CraftPlayer)player).getHandle().getId();
      ClientboundTakeItemEntityPacket spawnItem = new ClientboundTakeItemEntityPacket(id, playerID, 1);
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().connection.send(spawnItem));
   }

   public void updateFakeItem(Player player, int id, org.bukkit.inventory.ItemStack itemStack, AbstractLocation location) {
      ItemEntity item = new ItemEntity(
         ((CraftWorld)location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack)
      );
      item.absSnapTo(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
      ClientboundSetEntityDataPacket data = new ClientboundSetEntityDataPacket(item.getId(), item.getEntityData().getNonDefaultValues());
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().connection.send(data));
   }

   @Override
   public void destroyFakeItem(Player player, int id) {
      ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(new int[]{id});
      Schedulers.async().run(() -> ((CraftPlayer)player).getHandle().connection.send(destroyPacket));
   }

   public void playTotemEffect(AbstractPlayer player, int modelId) {
   }

   @Override
   public float getItemRecharge(Player player) {
      ServerPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.getAttackStrengthScale(0.0F);
   }

   @Override
   public boolean getItemRecharging(Player player) {
      ServerPlayer ep = ((CraftPlayer)player).getHandle();
      return ep.getAttackStrengthScale(0.0F) < 1.0F;
   }

   @Override
   public void resetItemRecharge(Player player) {
      ServerPlayer ep = ((CraftPlayer)player).getHandle();
      ep.resetAttackStrengthTicker();
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
      return nms.has(DataComponents.BLOCK_STATE)
         ? Optional.ofNullable((String)((BlockItemStateProperties)nms.get(DataComponents.BLOCK_STATE)).properties().get(key))
         : Optional.empty();
   }

   @Override
   public void setItemBlockStateValue(org.bukkit.inventory.ItemStack itemStack, String key, String value) {
      ItemStack nms = this.getNMSItemStack(itemStack);
      if (nms.has(DataComponents.BLOCK_STATE)) {
         ((BlockItemStateProperties)nms.get(DataComponents.BLOCK_STATE)).properties().put(key, value);
      } else {
         nms.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of(key, value)));
      }
   }

   @Override
   public void setItemBlockStateComponent(org.bukkit.inventory.ItemStack itemStack, Map<String, String> values) {
      ItemStack nms = this.getNMSItemStack(itemStack);
      nms.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(values));
   }

   @Override
   public void setCompostableComponent(org.bukkit.inventory.ItemStack itemStack, String layers) {
      ItemStack nms = CraftItemStack.unwrap(itemStack);
      Compostable prototype = (Compostable)nms.getPrototype().get(DataComponents.COMPOSTABLE);
      ResolvableInt resolvedLayers = resolveInt("Compostable.Layers", layers, prototype == null ? null : prototype.layers());
      if (resolvedLayers != null) {
         nms.set(DataComponents.COMPOSTABLE, new Compostable(resolvedLayers));
      }
   }

   @Override
   public void setCookingFuelComponent(org.bukkit.inventory.ItemStack itemStack, String burnTime, String speedMultiplier) {
      ItemStack nms = CraftItemStack.unwrap(itemStack);
      CookingFuel prototype = (CookingFuel)nms.getPrototype().get(DataComponents.COOKING_FUEL);
      ResolvableInt resolvedBurnTime = resolveInt("CookingFuel.BurnTime", burnTime, prototype == null ? null : prototype.burnTime());
      ResolvableFloat resolvedSpeed = resolveFloat(
         "CookingFuel.SpeedMultiplier",
         speedMultiplier,
         prototype == null ? ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER) : prototype.speedMultiplier()
      );
      if (resolvedBurnTime != null && resolvedSpeed != null) {
         nms.set(DataComponents.COOKING_FUEL, new CookingFuel(resolvedBurnTime, resolvedSpeed));
      }
   }

   @Override
   public void setBrewingFuelComponent(org.bukkit.inventory.ItemStack itemStack, String uses, String speedMultiplier) {
      ItemStack nms = CraftItemStack.unwrap(itemStack);
      BrewingFuel prototype = (BrewingFuel)nms.getPrototype().get(DataComponents.BREWING_FUEL);
      ResolvableInt resolvedUses = resolveInt("BrewingFuel.Uses", uses, prototype == null ? null : prototype.uses());
      ResolvableFloat resolvedSpeed = resolveFloat(
         "BrewingFuel.SpeedMultiplier",
         speedMultiplier,
         prototype == null ? ResolvableFloat.fromKey(ContextFloatProviders.BREWING_DEFAULT_SPEED_MULTIPLIER) : prototype.speedMultiplier()
      );
      if (resolvedUses != null && resolvedSpeed != null) {
         nms.set(DataComponents.BREWING_FUEL, new BrewingFuel(resolvedUses, resolvedSpeed));
      }
   }

   private static ResolvableInt resolveInt(String field, String value, ResolvableInt fallback) {
      if (value == null) {
         if (fallback == null) {
            MythicLogger.error("{0} is required when the base material has no such component", field);
         }

         return fallback;
      } else {
         Integer constant = Numbers.parseIntegerNullable(value);
         if (constant != null) {
            return new Constant(constant);
         } else {
            ResourceKey<ContextIntProvider> key = contextProviderKey(Registries.CONTEXT_INT_PROVIDER, field, value);
            return key == null ? null : ResolvableInt.fromKey(key);
         }
      }
   }

   private static ResolvableFloat resolveFloat(String field, String value, ResolvableFloat fallback) {
      if (value == null) {
         return fallback;
      } else {
         Float constant = Numbers.parseFloatNullable(value);
         if (constant != null) {
            return new net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat.Constant(constant);
         } else {
            ResourceKey<ContextFloatProvider> key = contextProviderKey(Registries.CONTEXT_FLOAT_PROVIDER, field, value);
            return key == null ? null : ResolvableFloat.fromKey(key);
         }
      }
   }

   private static <T> ResourceKey<T> contextProviderKey(ResourceKey<? extends Registry<T>> registry, String field, String value) {
      Identifier id = Identifier.tryParse(value);
      ResourceKey<T> key = id == null ? null : ResourceKey.create(registry, id);
      if (key != null && !MinecraftServer.getServer().reloadableRegistries().lookup().lookupOrThrow(registry).get(key).isEmpty()) {
         return key;
      } else {
         MythicLogger.error("{0} is neither a number nor a known provider id: {1}", field, value);
         return null;
      }
   }
}
