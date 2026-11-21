package io.lumine.mythic.core;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.serialization.SerializingModule;

public abstract class SerializingComponent extends SerializingModule<MythicBukkit> {
   public SerializingComponent(MythicBukkit plugin) {
      super(plugin, "data", GsonProvider.get());
   }
}
