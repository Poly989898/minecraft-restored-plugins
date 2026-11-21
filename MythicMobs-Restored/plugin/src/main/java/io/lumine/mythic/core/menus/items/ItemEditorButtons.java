package io.lumine.mythic.core.menus.items;

import com.google.common.collect.Lists;
import io.lumine.mythic.core.menus.items.editor.EditAttackRangeComponentsButton;
import io.lumine.mythic.core.menus.items.editor.EditBreakSoundButton;
import io.lumine.mythic.core.menus.items.editor.EditColorButton;
import io.lumine.mythic.core.menus.items.editor.EditConsumableComponentsButton;
import io.lumine.mythic.core.menus.items.editor.EditCustomModelDataButton;
import io.lumine.mythic.core.menus.items.editor.EditDisplayButton;
import io.lumine.mythic.core.menus.items.editor.EditDurabilityButton;
import io.lumine.mythic.core.menus.items.editor.EditDurabilityMaxButton;
import io.lumine.mythic.core.menus.items.editor.EditEnchantableButton;
import io.lumine.mythic.core.menus.items.editor.EditEnchantsButton;
import io.lumine.mythic.core.menus.items.editor.EditEquippableComponentsButton;
import io.lumine.mythic.core.menus.items.editor.EditFoodComponentsButton;
import io.lumine.mythic.core.menus.items.editor.EditGliderButton;
import io.lumine.mythic.core.menus.items.editor.EditGroupButton;
import io.lumine.mythic.core.menus.items.editor.EditLoreButton;
import io.lumine.mythic.core.menus.items.editor.EditMaterialButton;
import io.lumine.mythic.core.menus.items.editor.EditPiercingWeaponComponentsButton;
import io.lumine.mythic.core.menus.items.editor.EditRepairableButton;
import io.lumine.mythic.core.menus.items.editor.EditStatsButton;
import io.lumine.mythic.core.menus.items.editor.EditTooltipStyleButton;
import io.lumine.mythic.core.menus.items.editor.EditTrimButton;
import io.lumine.mythic.core.menus.items.editor.EditUnbreakableButton;
import io.lumine.mythic.core.menus.items.editor.EditUseCooldownComponentsButton;
import io.lumine.mythic.core.menus.items.editor.EditWeaponComponentsButton;
import java.util.List;
import lombok.Getter;

@Getter
public class ItemEditorButtons {
   public List<ItemEditorButton> buttons = Lists.newArrayList();

   public ItemEditorButtons() {
      new EditMaterialButton(this, 0);
      new EditCustomModelDataButton(this, 1);
      new EditGroupButton(this, 2);
      new EditDisplayButton(this, 3);
      new EditLoreButton(this, 4);
      new EditColorButton(this, 5);
      new EditEnchantsButton(this, 20);
      new EditEnchantableButton(this, 21);
      new EditUnbreakableButton(this, 29);
      new EditDurabilityButton(this, 30);
      new EditDurabilityMaxButton(this, 31);
      new EditRepairableButton(this, 32);
      new EditStatsButton(this, 40);
      new EditTooltipStyleButton(this, 45);
      new EditBreakSoundButton(this, 46);
      new EditGliderButton(this, 47);
      new EditTrimButton(this, 48);
      new EditFoodComponentsButton(this, 90);
      new EditUseCooldownComponentsButton(this, 90);
      new EditEquippableComponentsButton(this, 90);
      new EditWeaponComponentsButton(this, 90);
      new EditAttackRangeComponentsButton(this, 100);
      new EditConsumableComponentsButton(this, 100);
      new EditPiercingWeaponComponentsButton(this, 100);
   }
}
