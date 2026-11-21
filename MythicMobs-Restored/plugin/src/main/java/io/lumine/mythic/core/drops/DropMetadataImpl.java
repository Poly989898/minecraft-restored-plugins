package io.lumine.mythic.core.drops;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

@ToString
public class DropMetadataImpl implements DropMetadata, Cloneable, PlaceholderMeta {
   @Getter
   private final Optional<SkillCaster> dropper;
   @Getter
   private final Optional<AbstractEntity> cause;
   private final Optional<PlaceholderMeta> extraPlaceholderMeta;
   @Getter @Setter
   private Map<String, Object> metadata;
   @Getter
   private Optional<ItemStack> item = Optional.empty();
   @Getter @Setter
   private float amount = 0.0F;
   @Getter @Setter
   private double level = 1.0;
   @Getter @Setter
   private double bias = 100.0;
   @Getter @Setter
   private int generations = 0;

   public DropMetadataImpl(SkillCaster dropper, AbstractEntity killer) {
      this.dropper = Optional.ofNullable(dropper);
      this.cause = Optional.ofNullable(killer);
      this.extraPlaceholderMeta = Optional.empty();
   }

   public DropMetadataImpl(SkillCaster dropper, AbstractEntity killer, double level) {
      this.dropper = Optional.ofNullable(dropper);
      this.cause = Optional.ofNullable(killer);
      this.level = level;
      this.extraPlaceholderMeta = Optional.empty();
   }

   public DropMetadataImpl(SkillCaster dropper, AbstractEntity killer, PlaceholderMeta extraMeta, Integer level) {
      this.dropper = Optional.ofNullable(dropper);
      this.cause = Optional.ofNullable(killer);
      this.extraPlaceholderMeta = Optional.of(extraMeta);
      if (level != null) {
         this.level = level.intValue();
      }
   }

   @Override
   public int tick() {
      return this.generations++;
   }

   @Override
   public SkillCaster getCaster() {
      return this.dropper.orElse(null);
   }

   @Override
   public AbstractEntity getTrigger() {
      return this.cause.orElse(null);
   }

   @Override
   public PlaceholderMeta getExtraPlaceholderMeta() {
      return this.extraPlaceholderMeta.orElse(null);
   }

   @Override
   public void setItem(ItemStack item) {
      this.item = Optional.ofNullable(item);
   }

   public DropMetadataImpl setMetadata(String key, Object value) {
      if (this.metadata == null) {
         this.metadata = Maps.newHashMap();
      }

      this.metadata.put(key, value);
      return this;
   }

   @Override
   public Optional<Object> getMetadata(String key) {
      if (this.metadata != null) {
         Object value = this.metadata.get(key);
         if (value != null) {
            return Optional.of(value);
         }
      }

      return this.extraPlaceholderMeta.isPresent() ? this.extraPlaceholderMeta.get().getMetadata(key) : Optional.empty();
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

   @Override
   public DropMetadata clone() {
      try {
         return (DropMetadata)super.clone();
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public DropMetadataImpl deeperClone() {
      DropMetadataImpl clone = (DropMetadataImpl)this.clone();
      if (this.metadata != null) {
         clone.setMetadata(Maps.newHashMap(this.metadata));
      }

      return clone;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DropMetadataImpl other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Float.compare(this.getAmount(), other.getAmount()) != 0) {
         return false;
      } else if (Double.compare(this.getLevel(), other.getLevel()) != 0) {
         return false;
      } else if (Double.compare(this.getBias(), other.getBias()) != 0) {
         return false;
      } else if (this.getGenerations() != other.getGenerations()) {
         return false;
      } else {
         Object this$dropper = this.getDropper();
         Object other$dropper = other.getDropper();
         if (this$dropper == null ? other$dropper == null : this$dropper.equals(other$dropper)) {
            Object this$cause = this.getCause();
            Object other$cause = other.getCause();
            if (this$cause == null ? other$cause == null : this$cause.equals(other$cause)) {
               Object this$extraPlaceholderMeta = this.getExtraPlaceholderMeta();
               Object other$extraPlaceholderMeta = other.getExtraPlaceholderMeta();
               if (this$extraPlaceholderMeta == null ? other$extraPlaceholderMeta == null : this$extraPlaceholderMeta.equals(other$extraPlaceholderMeta)) {
                  Object this$metadata = this.getMetadata();
                  Object other$metadata = other.getMetadata();
                  if (this$metadata == null ? other$metadata == null : this$metadata.equals(other$metadata)) {
                     Object this$item = this.getItem();
                     Object other$item = other.getItem();
                     return this$item == null ? other$item == null : this$item.equals(other$item);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
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
      return other instanceof DropMetadataImpl;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + Float.floatToIntBits(this.getAmount());
      long $level = Double.doubleToLongBits(this.getLevel());
      result = result * 59 + (int)($level >>> 32 ^ $level);
      long $bias = Double.doubleToLongBits(this.getBias());
      result = result * 59 + (int)($bias >>> 32 ^ $bias);
      result = result * 59 + this.getGenerations();
      Object $dropper = this.getDropper();
      result = result * 59 + ($dropper == null ? 43 : $dropper.hashCode());
      Object $cause = this.getCause();
      result = result * 59 + ($cause == null ? 43 : $cause.hashCode());
      Object $extraPlaceholderMeta = this.getExtraPlaceholderMeta();
      result = result * 59 + ($extraPlaceholderMeta == null ? 43 : $extraPlaceholderMeta.hashCode());
      Object $metadata = this.getMetadata();
      result = result * 59 + ($metadata == null ? 43 : $metadata.hashCode());
      Object $item = this.getItem();
      return result * 59 + ($item == null ? 43 : $item.hashCode());
   }
}
