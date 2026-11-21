package io.lumine.mythic.api.adapters;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitSkillAdapter;

public class SkillAdapter {
   protected static AbstractSkillAdapter adapter;

   public static void initializeBukkit(MythicBukkit plugin) {
      adapter = new BukkitSkillAdapter(plugin);
   }

   public static AbstractSkillAdapter get() {
      return adapter;
   }
}
