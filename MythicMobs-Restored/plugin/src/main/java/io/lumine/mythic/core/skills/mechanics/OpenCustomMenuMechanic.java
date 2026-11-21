package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.menus.custom.CustomMenu;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.entity.Player;

@MythicMechanic(
   name = "openCustomMenu",
   aliases = {"openMenu"},
   description = "Opens a custom menu"
)
public class OpenCustomMenuMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private PlaceholderString menu;

   public OpenCustomMenuMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.menu = mlc.getPlaceholderString(new String[]{"menu", "m"}, "default");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      return this.openMenuFor(data, data.getCaster().getEntity());
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.openMenuFor(data, target);
   }

   private SkillResult openMenuFor(SkillMetadata data, AbstractEntity entity) {
      if (!MythicBukkit.isVolatile() && this.getPlugin().getCompatibility().getRpg().isEmpty()) {
         MythicLogger.errorMechanicConfig(this, this.config, "OpenCustomMenu mechanic requires Mythic Premium or MythicRPG");
         return SkillResult.INVALID_CONFIG;
      } else if (!entity.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         String menuName = this.menu.get(data);
         Optional<CustomMenu> maybeMenu = this.getPlugin().getMenuManager().getMenu(menuName);
         if (maybeMenu.isEmpty()) {
            return SkillResult.INVALID_CONFIG;
         } else {
            Player player = BukkitAdapter.adapt(entity.asPlayer());
            maybeMenu.get().openMenu(player);
            return SkillResult.SUCCESS;
         }
      }
   }

   @Override
   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   @Override
   public boolean getTargetsSpectators() {
      return true;
   }

   @Override
   public boolean getTargetsCreatives() {
      return true;
   }
}
