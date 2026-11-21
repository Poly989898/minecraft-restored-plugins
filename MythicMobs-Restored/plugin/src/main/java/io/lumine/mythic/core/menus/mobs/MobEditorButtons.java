package io.lumine.mythic.core.menus.mobs;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.menus.mobs.editor.EditAttrButton;
import io.lumine.mythic.core.menus.mobs.editor.EditDisplayButton;
import io.lumine.mythic.core.menus.mobs.editor.EditTypeButton;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.skills.stats.Stats;
import java.util.List;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class MobEditorButtons {
   public List<MobEditorButton> buttons = Lists.newArrayList();

   public MobEditorButtons() {
      new EditTypeButton(this, 0);
      new EditDisplayButton(this, 1);
      this.stat(2, "Health", null, Material.COOKED_BEEF, Stats.HEALTH);
      this.stat(3, "Damage", null, Material.IRON_SWORD, Stats.ATTACK_DAMAGE);
      this.stat(4, "Armor", null, Material.IRON_CHESTPLATE, Stats.ARMOR);
      this.stat(5, "Movement Speed", "Options.MovementSpeed", Material.GOLDEN_BOOTS, Stats.MOVEMENT_SPEED);
      new EditAttrButton(this, 6, "Faction", Material.ITEM_FRAME, MythicMob::getFaction, (mm, in) -> mm.setFaction(in), mm -> mm.setFaction(null));
      this.stat(7, "Attack Speed", "Options.AttackSpeed", Material.CLOCK, Stats.ATTACK_SPEED);
      this.stat(8, "Flying Speed", "Options.FlyingSpeed", Material.FEATHER, Stats.FLYING_SPEED);
      this.stat(9, "Knockback Resistance", "Options.KnockbackResistance", Material.SHIELD, Stats.KNOCKBACK_RESISTANCE);
      this.stat(10, "Follow Range", "Options.FollowRange", Material.SPYGLASS, Stats.FOLLOW_RANGE);
      this.stat(11, "Step Height", "StepHeight", Material.STONE_SLAB, Stats.STEP_HEIGHT);
      this.stat(12, "Scale", "Options.Scale", Material.SLIME_BLOCK, Stats.SCALE);
   }

   private void stat(int sort, String label, String option, Material icon, StatType type) {
      new EditAttrButton(
         this,
         sort,
         label,
         option,
         icon,
         mm -> mm.getStatValue(type).map(Object::toString).orElse(null),
         (mm, in) -> mm.setStatValue(type, in),
         mm -> mm.getStats().remove(type)
      );
   }
}
