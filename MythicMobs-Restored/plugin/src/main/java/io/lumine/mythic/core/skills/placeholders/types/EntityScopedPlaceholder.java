package io.lumine.mythic.core.skills.placeholders.types;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.apache.commons.lang3.text.WordUtils;
import io.lumine.mythic.bukkit.utils.serialize.Optl;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EntityScopedPlaceholder<T> extends GenericPlaceholder<T> {
   protected final EntityScopedPlaceholder.ScopedEntityGetter getEntity;
   protected final EntityScopedPlaceholder.ScopedLocationGetter getLocation;
   private static final Map<EntityScopedPlaceholder.TargetSource, EntityScopedPlaceholder.ScopedEntityGetter> scopeEntityGetters = new EnumMap<>(
      EntityScopedPlaceholder.TargetSource.class
   );
   private static final Map<EntityScopedPlaceholder.TargetSource, EntityScopedPlaceholder.ScopedLocationGetter> scopedLocationGetters = new EnumMap<>(
      EntityScopedPlaceholder.TargetSource.class
   );

   @Nullable
   protected final ActiveMob getActiveMob(PlaceholderContext context) {
      AbstractEntity entity = this.getEntity.get(context);
      return this.getActiveMob(entity);
   }

   @Nullable
   protected final ActiveMob getActiveMob(@Nullable AbstractEntity entity) {
      return entity == null ? null : this.manager.getPlugin().getMobManager().getMythicMobInstance(entity);
   }

   public EntityScopedPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments arguments) {
      super(arguments);
      this.getEntity = scopeEntityGetters.get(arguments.source);
      this.getLocation = scopedLocationGetters.get(arguments.source);
   }

   @Nullable
   @Override
   public T applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      return this.applyToScope(placeholderContext);
   }

   @Nullable
   public abstract T applyToScope(PlaceholderContext placeholderContext);

   public static String getName(AbstractEntity entity) {
      if (entity == null) {
         return null;
      } else if (entity.isPlayer()) {
         return entity.asPlayer().getName();
      } else {
         return entity.getName() != null ? entity.getName() : WordUtils.capitalize(entity.getBukkitEntity().getType().getName());
      }
   }

   @Nullable
   private static AbstractEntity getParent(@NotNull PlaceholderContext context) {
      return context.meta().getCaster() instanceof ActiveMob am && am.getParent().isPresent() ? am.getParent().get() : null;
   }

   @Nullable
   private static AbstractEntity getOwner(@NotNull PlaceholderContext context) {
      SkillCaster entity = context.meta().getCaster();
      if (entity instanceof ActiveMob activeCaster) {
         Optl<UUID> ownerUUID = activeCaster.getOwnerUUID();
         if (!ownerUUID.isPresent()) {
            return null;
         } else {
            MythicPlugin plugin = MythicBukkit.inst();
            AbstractEntity ownerEntity = plugin.getBootstrap().getEntity(ownerUUID.get());
            return ownerEntity != null ? plugin.getSkillManager().getCaster(ownerEntity).getEntity() : null;
         }
      } else {
         AbstractEntity casterEntity = entity == null ? null : entity.getEntity();
         if (casterEntity != null && casterEntity.getBukkitEntity() instanceof Item item && item.getOwner() != null) {
            AbstractEntity ownerEntity = MythicBukkit.inst().getBootstrap().getEntity(item.getOwner());
            return ownerEntity == null ? null : MythicBukkit.inst().getSkillManager().getCaster(ownerEntity).getEntity();
         } else {
            return null;
         }
      }
   }

   private static AbstractLocation getLocation(@Nullable AbstractEntity entity) {
      return entity == null ? null : entity.getLocation();
   }

   static {
      scopeEntityGetters.put(EntityScopedPlaceholder.TargetSource.CASTER, context -> context.meta().getCaster().getEntity());
      scopeEntityGetters.put(EntityScopedPlaceholder.TargetSource.TARGET, PlaceholderContext::entity);
      scopeEntityGetters.put(EntityScopedPlaceholder.TargetSource.TRIGGER, context -> context.meta().getTrigger());
      scopeEntityGetters.put(EntityScopedPlaceholder.TargetSource.PARENT, EntityScopedPlaceholder::getParent);
      scopeEntityGetters.put(EntityScopedPlaceholder.TargetSource.OWNER, EntityScopedPlaceholder::getOwner);
      scopedLocationGetters.put(EntityScopedPlaceholder.TargetSource.CASTER, context -> context.meta().getCaster().getEntity().getLocation());
      scopedLocationGetters.put(EntityScopedPlaceholder.TargetSource.TARGET, PlaceholderContext::location);
      scopedLocationGetters.put(EntityScopedPlaceholder.TargetSource.TRIGGER, context -> context.meta().getTrigger().getLocation());
      scopedLocationGetters.put(EntityScopedPlaceholder.TargetSource.PARENT, context -> getLocation(getParent(context)));
      scopedLocationGetters.put(EntityScopedPlaceholder.TargetSource.OWNER, context -> getLocation(getOwner(context)));
   }

   public static class EntityScopedPlaceholderArguments extends GenericPlaceholder.GenericPlaceholderArguments {
      public final EntityScopedPlaceholder.TargetSource source;

      @Generated
      protected EntityScopedPlaceholderArguments(EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilder<?, ?> b) {
         super(b);
         this.source = b.source;
      }

      @Generated
      public static EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilder<?, ?> builder() {
         return new EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilderImpl();
      }

      @Generated
      public EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilder<?, ?> toBuilder() {
         return new EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilderImpl().$fillValuesFrom(this);
      }

      @ToString(callSuper = true)
      @Generated
      public abstract static class EntityScopedPlaceholderArgumentsBuilder<C extends EntityScopedPlaceholder.EntityScopedPlaceholderArguments, B extends EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilder<C, B>>
         extends GenericPlaceholder.GenericPlaceholderArguments.GenericPlaceholderArgumentsBuilder<C, B> {
         @Generated
         private EntityScopedPlaceholder.TargetSource source;

         @Generated
         protected B $fillValuesFrom(C instance) {
            super.$fillValuesFrom(instance);
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return this.self();
         }

         @Generated
         private static void $fillValuesFromInstanceIntoBuilder(
            EntityScopedPlaceholder.EntityScopedPlaceholderArguments instance,
            EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilder<?, ?> b
         ) {
            b.source(instance.source);
         }

         @Generated
         public B source(EntityScopedPlaceholder.TargetSource source) {
            this.source = source;
            return this.self();
         }

         @Generated
         protected abstract B self();

         @Generated
         public abstract C build();
      }

      @Generated
      private static final class EntityScopedPlaceholderArgumentsBuilderImpl
         extends EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilder<EntityScopedPlaceholder.EntityScopedPlaceholderArguments, EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilderImpl> {
         @Generated
         protected EntityScopedPlaceholder.EntityScopedPlaceholderArguments.EntityScopedPlaceholderArgumentsBuilderImpl self() {
            return this;
         }

         @Generated
         @Override
         public EntityScopedPlaceholder.EntityScopedPlaceholderArguments build() {
            return new EntityScopedPlaceholder.EntityScopedPlaceholderArguments(this);
         }
      }
   }

   @FunctionalInterface
   protected interface ScopedEntityGetter {
      AbstractEntity get(PlaceholderContext placeholderContext);
   }

   protected interface ScopedLocationGetter {
      AbstractLocation get(PlaceholderContext placeholderContext);
   }

   @Getter
   public static enum TargetSource {
      CASTER(new String[]{"caster", "c"}),
      TARGET(new String[]{"target"}),
      TRIGGER(new String[]{"trigger"}),
      PARENT(new String[]{"parent"}),
      OWNER(new String[]{"owner"});

      private final String[] scopeAliases;

      private TargetSource(String[] scopeAliases) {
         this.scopeAliases = scopeAliases;
      }
   }
}
