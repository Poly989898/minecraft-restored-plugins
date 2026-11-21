package io.lumine.mythic.core.skills.placeholders;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;

public record PlaceholderContext(
   PlaceholderMeta meta,
   AbstractEntity entity,
   boolean hasNativeEntity,
   AbstractLocation location,
   boolean hasNativeLocation,
   MythicSpawner spawner,
   AbstractItemStack item
) {
   private static final PlaceholderContext emptyContext = new PlaceholderContext(null, null, false, null, false, null, null);

   public static PlaceholderContext of() {
      return emptyContext;
   }

   @Deprecated(
      forRemoval = true
   )
   public static PlaceholderContext of(PlaceholderMeta meta, AbstractEntity entity, AbstractLocation location, SkillCaster caster, MythicSpawner spawner) {
      return builder().meta(meta).entity(entity).caster(caster).location(location).spawner(spawner).build();
   }

   @Deprecated(
      forRemoval = true
   )
   public static PlaceholderContext of(PlaceholderMeta meta, AbstractEntity entity, AbstractLocation location) {
      return builder().meta(meta).entity(entity).location(location).build();
   }

   @Deprecated(
      forRemoval = true
   )
   public static PlaceholderContext ofMeta(PlaceholderMeta meta) {
      return builder().meta(meta).build();
   }

   @Deprecated(
      forRemoval = true
   )
   public static PlaceholderContext ofEntity(AbstractEntity entity) {
      return builder().entity(entity).build();
   }

   @Deprecated(
      forRemoval = true
   )
   public static PlaceholderContext ofLocation(AbstractLocation location) {
      return builder().location(location).build();
   }

   @Deprecated(
      forRemoval = true
   )
   public static PlaceholderContext ofCaster(SkillCaster caster) {
      return builder().caster(caster).build();
   }

   @Deprecated(
      forRemoval = true
   )
   public static PlaceholderContext ofSpawner(MythicSpawner spawner) {
      return builder().spawner(spawner).build();
   }

   public SkillMetadata skillMetadata() {
      return (SkillMetadata)this.meta;
   }

   public Optional<SkillMetadata> getSkillMetadata() {
      return this.meta instanceof SkillMetadata skillMetadata ? Optional.of(skillMetadata) : Optional.empty();
   }

   public Optional<ItemStack> getItemStack() {
      if (this.item instanceof BukkitItemStack bukkitItemStack) {
         return Optional.of(bukkitItemStack.getItemStack());
      } else {
         ItemStack metadataItemStack = (ItemStack)this.meta.getMetadata(PlaceholderMetadataKey.ITEM_STACK).orElse(null);
         if (metadataItemStack != null) {
            return Optional.of(metadataItemStack);
         } else if (this.meta instanceof SkillMetadata skillMetadata && skillMetadata.getVariables().has("equip-item")) {
            Variable variable = skillMetadata.getVariables().get("equip-item");
            if (variable == null) {
               return Optional.empty();
            } else {
               ItemStack itemStack = (ItemStack)variable.get();
               return itemStack == null ? Optional.empty() : Optional.of(itemStack);
            }
         } else {
            return this.meta instanceof DropMetadata dropMeta && dropMeta.getItem().isPresent() ? dropMeta.getItem() : Optional.empty();
         }
      }
   }

   @Nullable
   public BukkitItemStack getBukkitItemStack() {
      return this.getItemStack().map(BukkitAdapter::adapt).orElse(null);
   }

   public static PlaceholderContext.Builder builder() {
      return new PlaceholderContext.Builder();
   }

   public PlaceholderContext.Builder toBuilder() {
      return new PlaceholderContext.Builder(this);
   }

   public static class Builder {
      private PlaceholderMeta meta;
      private AbstractEntity entity;
      private Boolean hasNativeEntity = null;
      private AbstractLocation location;
      private Boolean hasNativeLocation = null;
      private SkillCaster caster;
      private MythicSpawner spawner;
      private AbstractItemStack item;

      private Builder(PlaceholderContext context) {
         this.meta = context.meta();
         this.entity = context.entity();
         this.hasNativeEntity = context.hasNativeEntity();
         this.location = context.location();
         this.hasNativeLocation = context.hasNativeLocation();
         this.spawner = context.spawner();
         this.item = context.item();
      }

      public Builder() {
      }

      public PlaceholderContext.Builder meta(PlaceholderMeta meta) {
         this.meta = meta;
         return this;
      }

      public PlaceholderContext.Builder entity(AbstractEntity entity) {
         this.entity = entity;
         return this;
      }

      public PlaceholderContext.Builder location(AbstractLocation location) {
         this.location = location;
         return this;
      }

      public PlaceholderContext.Builder caster(SkillCaster caster) {
         this.caster = caster;
         return this;
      }

      public PlaceholderContext.Builder spawner(MythicSpawner spawner) {
         this.spawner = spawner;
         return this;
      }

      public PlaceholderContext.Builder item(AbstractItemStack item) {
         this.item = item;
         return this;
      }

      public PlaceholderContext build() {
         boolean nativeEntity = Objects.requireNonNullElseGet(this.hasNativeEntity, () -> this.entity != null);
         boolean nativeLocation = Objects.requireNonNullElseGet(this.hasNativeLocation, () -> this.location != null);
         if (this.meta == null && this.caster != null) {
            this.meta = new GenericPlaceholderMeta(this.caster, this.caster.getEntity());
         }

         if (this.entity == null && this.meta != null) {
            this.entity = this.meta.getTrigger();
         }

         if (this.location == null && this.entity != null) {
            this.location = this.entity.getLocation();
         }

         return new PlaceholderContext(this.meta, this.entity, nativeEntity, this.location, nativeLocation, this.spawner, this.item);
      }
   }
}
