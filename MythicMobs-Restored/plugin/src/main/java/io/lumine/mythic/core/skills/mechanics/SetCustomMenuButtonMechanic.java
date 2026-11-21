package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.menu.EditableIcon;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MenuHelper;
import io.lumine.mythic.core.menus.custom.CustomMenu;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.entity.Player;

@MythicMechanic(
   name = "SetCustomMenuButton",
   description = "Opens a custom menu"
)
public class SetCustomMenuButtonMechanic extends SkillMechanic implements INoTargetSkill {
   @MythicField(
      name = "slot",
      aliases = {"s"},
      description = "The menu slot index to update",
      defValue = "0"
   )
   private PlaceholderInt slot;
   @MythicField(
      name = "icon",
      aliases = {"i"},
      description = "The icon key to place in the target slot",
      defValue = "default"
   )
   private PlaceholderString icon;

   public SetCustomMenuButtonMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.slot = mlc.getPlaceholderInteger(new String[]{"slot", "s"}, 0);
      this.icon = mlc.getPlaceholderString(new String[]{"icon", "i"}, "default");
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      Player player = BukkitAdapter.adapt(data.getCaster().getEntity().asPlayer());
      MenuHelper.OpenMenu<?> openMenu = MenuHelper.getOpenMenu(player);
      if (openMenu.getMenu().getHolder() != null && openMenu.getMenu().getHolder() instanceof CustomMenu rpgMenu) {
         Optional<EditableIcon<?>> icon = openMenu.getMenu().getBuilder().getIcon(this.icon.get(data));
         if (icon != null && !icon.isEmpty()) {
            this.setIcon(data, openMenu, this.slot.get(data), icon.get());
            return SkillResult.SUCCESS;
         } else {
            return SkillResult.INVALID_CONFIG;
         }
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   private <T> void setIcon(SkillMetadata data, MenuHelper.OpenMenu openMenu, int slot, EditableIcon<T> editableIcon) {
      Icon<? super T> icon = editableIcon.getBuilder().build();
      openMenu.setIcon(this.slot.get(data), icon);
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
