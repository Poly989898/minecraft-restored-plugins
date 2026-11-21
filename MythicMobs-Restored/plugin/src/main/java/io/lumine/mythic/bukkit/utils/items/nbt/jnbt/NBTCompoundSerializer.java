package io.lumine.mythic.bukkit.utils.items.nbt.jnbt;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.lumine.mythic.bukkit.utils.version.wrappers.VersionWrapper;
import java.lang.reflect.Type;
import java.util.HashMap;

public class NBTCompoundSerializer implements JsonSerializer<CompoundTag>, JsonDeserializer<CompoundTag> {
   public CompoundTag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      Class<? extends CompoundTag> clazz = (Class<? extends CompoundTag>)VersionWrapper.get().getItemHandler().createCompoundTag(new HashMap<>()).getClass();
      return (CompoundTag)context.deserialize(json, clazz);
   }

   public JsonElement serialize(CompoundTag src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src);
   }
}
