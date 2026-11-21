package io.lumine.mythic.core.skills.placeholders;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillCaster;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenericPlaceholderMeta implements PlaceholderMeta {
   private final SkillCaster caster;
   private final AbstractEntity trigger;
   private Map<String, Object> metadata = Maps.newHashMap();

   public GenericPlaceholderMeta(SkillCaster caster) {
      this.caster = caster;
      this.trigger = null;
   }

   public GenericPlaceholderMeta(AbstractEntity caster) {
      this.caster = new GenericCaster(caster);
      this.trigger = null;
   }

   public GenericPlaceholderMeta(SkillCaster caster, AbstractEntity trigger) {
      this.caster = caster;
      this.trigger = trigger;
   }

   public GenericPlaceholderMeta(AbstractEntity caster, AbstractEntity trigger) {
      this.caster = new GenericCaster(caster);
      this.trigger = trigger;
   }

   @Override
   public PlaceholderMeta setMetadata(String key, Object value) {
      this.metadata.put(key, value);
      return this;
   }

   @Override
   public Optional<Object> getMetadata(String key) {
      return Optional.ofNullable(this.metadata.get(key));
   }

   @Override
   public <T> Optional<T> getMetadata(Class<T> clazz, String key) {
      Objects.requireNonNull(clazz, "clazz");
      Objects.requireNonNull(key, "key");
      Object value = this.metadata.getOrDefault(key, null);
      if (value == null) {
         return Optional.empty();
      } else {
         return !clazz.isInstance(value) ? Optional.empty() : Optional.of(clazz.cast(value));
      }
   }

   @Override
   public Optional<Object> removeMetadata(String key) {
      Objects.requireNonNull(key, "key");
      return Optional.ofNullable(this.metadata.remove(key));
   }

   @Override
   public <T> Optional<T> removeMetadata(Class<T> clazz, String key) {
      Objects.requireNonNull(clazz, "clazz");
      Objects.requireNonNull(key, "key");
      Object value = this.metadata.remove(key);
      if (value == null) {
         return Optional.empty();
      } else {
         return !clazz.isInstance(value) ? Optional.empty() : Optional.of(clazz.cast(value));
      }
   }

   @Override
   public void clearMetadata() {
      this.metadata.clear();
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof GenericPlaceholderMeta other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$caster = this.getCaster();
         Object other$caster = other.getCaster();
         if (this$caster == null ? other$caster == null : this$caster.equals(other$caster)) {
            Object this$trigger = this.getTrigger();
            Object other$trigger = other.getTrigger();
            if (this$trigger == null ? other$trigger == null : this$trigger.equals(other$trigger)) {
               Object this$metadata = this.getMetadata();
               Object other$metadata = other.getMetadata();
               return this$metadata == null ? other$metadata == null : this$metadata.equals(other$metadata);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof GenericPlaceholderMeta;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $caster = this.getCaster();
      result = result * 59 + ($caster == null ? 43 : $caster.hashCode());
      Object $trigger = this.getTrigger();
      result = result * 59 + ($trigger == null ? 43 : $trigger.hashCode());
      Object $metadata = this.getMetadata();
      return result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
   }
}
