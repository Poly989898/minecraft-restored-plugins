package io.lumine.mythic.core.menus.spawners;

import com.google.common.collect.Lists;
import io.lumine.mythic.core.menus.spawners.editor.EditSpawnerAttrButton;
import io.lumine.mythic.core.menus.spawners.editor.ToggleSpawnerAttrButton;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class SpawnerEditorButtons {
   private static final String NUMBER_HINT = "<white>You can use math and placeholders, such as <aqua><random.1to5>";
   private static final String PLAIN_HINT = "<white>Values are saved to the spawner file immediately.";
   public List<SpawnerEditorButton> buttons = Lists.newArrayList();

   public SpawnerEditorButtons() {
      this.attr(
         0,
         "Mob Type",
         "mobtype",
         Material.ZOMBIE_SPAWN_EGG,
         MythicSpawner::getTypeName,
         "<white>Accepts a mob name, or a weighted list like <aqua>SkeletalKnight 5,Zombie 1"
      );
      this.attr(
         1,
         "Mob Level",
         "moblevel",
         Material.EXPERIENCE_BOTTLE,
         s -> s.getMobLevel().serialize(),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         2, "Max Mobs", "maxmobs", Material.CHEST, s -> s.getMaxMobs().serialize(), "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         3,
         "Mobs Per Spawn",
         "mobsperspawn",
         Material.BONE_MEAL,
         s -> String.valueOf(s.getMobsPerSpawn()),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         4, "Cooldown", "cooldown", Material.CLOCK, s -> s.getCooldownSeconds() + "s", "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         5, "Warmup", "warmup", Material.COMPASS, s -> s.getWarmupSeconds() + "s", "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         6,
         "Spawn Radius",
         "radius",
         Material.REPEATER,
         s -> String.valueOf(s.getSpawnRadius()),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         7,
         "Spawn Radius Y",
         "radiusy",
         Material.LADDER,
         s -> String.valueOf(s.getSpawnRadiusY()),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         8,
         "Activation Range",
         "activationrange",
         Material.SPYGLASS,
         s -> String.valueOf(s.getActivationRange()),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         9,
         "Scaling Range",
         "scalingrange",
         Material.TARGET,
         s -> String.valueOf(s.getScalingRange()),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         10,
         "Leash Range",
         "leashrange",
         Material.LEAD,
         s -> String.valueOf(s.getLeashRange()),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(
         11,
         "Chance",
         "chance",
         Material.RABBIT_FOOT,
         s -> String.valueOf(s.getChance()),
         "<white>You can use math and placeholders, such as <aqua><random.1to5>"
      );
      this.attr(12, "Group", "group", Material.ITEM_FRAME, MythicSpawner::getGroup, "<white>Values are saved to the spawner file immediately.");
      this.toggle(20, "Use Timer", "usetimer", MythicSpawner::getUseTimer);
      this.toggle(21, "Check For Players", "checkforplayers", MythicSpawner::isCheckForPlayers);
      this.toggle(22, "Show Flames", "showflames", MythicSpawner::isShowFlames);
      this.toggle(23, "Breakable", "breakable", MythicSpawner::isBreakable);
      this.toggle(24, "Heal On Leash", "healonleash", MythicSpawner::isHealOnLeash);
      this.toggle(25, "Reset Threat On Leash", "resetthreatonleash", MythicSpawner::isLeashResetsThreat);
      this.toggle(26, "Random Rotation", "randomrotation", MythicSpawner::isRandomRotation);
      this.toggle(27, "Use World Scaling", "useworldscaling", MythicSpawner::isUseWorldScaling);
   }

   private void attr(int sort, String label, String attribute, Material icon, Function<MythicSpawner, String> reader, String hint) {
      new EditSpawnerAttrButton(this, sort, label, attribute, icon, reader, hint);
   }

   private void toggle(int sort, String label, String attribute, Predicate<MythicSpawner> reader) {
      new ToggleSpawnerAttrButton(this, sort, label, attribute, reader);
   }
}
