package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.menu.MenuHelper;
import io.lumine.mythic.core.menus.custom.CustomMenu;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Player;

@MythicMechanic(
   name = "SetCustomMenuTitle",
   aliases = {"setMenuTitle"},
   description = "Sets the title of an open custom menu"
)
public class SetCustomMenuTitleMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "title",
      aliases = {"t"},
      description = "The title text to apply to the open custom menu"
   )
   private PlaceholderString title;

   public SetCustomMenuTitleMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.title = mlc.getPlaceholderString(new String[]{"title", "t"}, "");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Player player = BukkitAdapter.adapt(data.getCaster().getEntity().asPlayer());
      MenuHelper.OpenMenu<?> openMenu = MenuHelper.getOpenMenu(player);
      if (openMenu != null && openMenu.getMenu().getHolder() != null && openMenu.getMenu().getHolder() instanceof CustomMenu) {
         String titleStr = this.title.get(data);
         MenuHelper.updateMenuTitle(player, titleStr);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
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
