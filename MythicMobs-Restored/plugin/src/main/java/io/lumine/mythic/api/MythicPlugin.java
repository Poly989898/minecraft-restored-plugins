package io.lumine.mythic.api;

import io.lumine.mythic.api.adapters.ServerInterface;
import io.lumine.mythic.api.config.ConfigManager;
import io.lumine.mythic.api.drops.DropManager;
import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.packs.PackManager;
import io.lumine.mythic.api.skills.SkillManager;
import io.lumine.mythic.api.skills.pins.PinManager;
import io.lumine.mythic.api.skills.placeholders.PlaceholderManager;
import io.lumine.mythic.api.skills.stats.StatManager;
import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.core.skills.variables.VariableManager;

public interface MythicPlugin {
   ServerInterface getBootstrap();

   ConfigManager getConfiguration();

   PackManager getPackManager();

   PinManager getPinManager();

   MobManager getMobManager();

   SkillManager getSkillManager();

   ItemManager getItemManager();

   DropManager getDropManager();

   StatManager getStatManager();

   VariableManager getVariableManager();

   PlaceholderManager getPlaceholderManager();

   VolatileCodeHandler getVolatileCodeHandler();

   boolean isLoading();

   boolean isShuttingDown();
}
