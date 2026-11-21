package io.lumine.mythic.core.skills.triggers;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import io.lumine.mythic.core.skills.triggers.meta.SimpleEventSkillMetadata;
import io.lumine.mythic.core.skills.triggers.meta.SimpleSkillMetadata;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.event.Event;

@Getter
public abstract class SkillTriggerMetadata {
   private final SkillMetadata meta;
   private final Map<String, Object> data;
   private final Collection<String> tags;

   public static SimpleSkillMetadata simple(Consumer<SkillMetadata> eventMetadataConsumer) {
      return new SimpleSkillMetadata(eventMetadataConsumer);
   }

   public static <E extends Event> SimpleEventSkillMetadata<E> simpleEvent(BiConsumer<E, SkillMetadata> eventMetadataConsumer) {
      return new SimpleEventSkillMetadata<>(eventMetadataConsumer);
   }

   public SkillTriggerMetadata() {
      this.meta = null;
      this.data = new ConcurrentHashMap<>();
      this.tags = null;
   }

   public SkillTriggerMetadata(DamageMetadata damageMetadata) {
      if (damageMetadata == null) {
         this.meta = null;
         this.data = Maps.newHashMap();
         this.tags = null;
      } else {
         this.meta = damageMetadata.getMetadata();
         this.data = damageMetadata.getData();
         this.tags = damageMetadata.getTags();
      }
   }

   public abstract void applyToSkillMetadata(SkillMetadata skillMetadata);

   public void putObject(String key, Object value) {
      this.data.put(key, value);
   }

   public Object getValue(String key) {
      return this.data.get(key);
   }

   public Object getOrDefault(String key, Object def) {
      return this.data.getOrDefault(key, def);
   }

   public void putAll(Map<String, Object> data) {
      this.data.putAll(data);
   }

   public void putString(String key, String value) {
      this.data.put(key, value);
   }

   public void putBoolean(String key, boolean value) {
      this.data.put(key, value);
   }

   public void putFloat(String key, float value) {
      this.data.put(key, value);
   }

   public void putDouble(String key, double value) {
      this.data.put(key, value);
   }

   public String getString(String key) {
      return this.data.get(key).toString();
   }

   public boolean getBoolean(String key) {
      return this.getValue(key) instanceof Boolean bool ? bool : false;
   }

   public float getFloat(String key) {
      if (this.getValue(key) instanceof Float f) {
         return f;
      } else {
         throw new NumberFormatException(this.getString(key) + " is not a decimal value");
      }
   }

   public double getDouble(String key) {
      return this.getValue(key) instanceof Double f ? f : this.getFloat(key);
   }
}
