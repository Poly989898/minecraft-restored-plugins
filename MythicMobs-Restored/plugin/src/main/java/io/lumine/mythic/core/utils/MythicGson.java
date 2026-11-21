package io.lumine.mythic.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.bukkit.adapters.BukkitWorld;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.gson.adapters.BukkitSerializableAdapterFactory;
import io.lumine.mythic.bukkit.utils.gson.adapters.GsonSerializableAdapterFactory;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableSerializer;
import io.lumine.mythic.core.skills.variables.serializers.InstantTypeAdapter;
import io.lumine.mythic.core.skills.variables.serializers.OptionalTypeAdapter;
import io.lumine.mythic.core.skills.variables.types.ItemVariable;
import io.lumine.mythic.core.skills.variables.types.MetaSkillVariable;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.NBTCompoundSerializer;
import io.lumine.mythic.core.utils.jnbt.NBTSerializer;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nonnull;

public final class MythicGson {
   private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
      .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
      .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
      .registerTypeAdapter(Optional.class, new OptionalTypeAdapter())
      .registerTypeAdapter(AbstractWorld.class, GsonProvider.standard().getAdapter(BukkitWorld.class))
      .registerTypeAdapter(Tag.class, new NBTSerializer())
      .registerTypeAdapter(CompoundTag.class, new NBTCompoundSerializer())
      .registerTypeAdapter(Variable.class, new VariableSerializer())
      .registerTypeAdapter(MetaSkillVariable.class, new MetaSkillVariable.Serializer())
      .registerTypeAdapter(ItemVariable.class, new ItemVariable.Serializer())
      .serializeNulls()
      .disableHtmlEscaping()
      .setPrettyPrinting()
      .create();

   @Nonnull
   public static Gson get() {
      return GSON;
   }
}
