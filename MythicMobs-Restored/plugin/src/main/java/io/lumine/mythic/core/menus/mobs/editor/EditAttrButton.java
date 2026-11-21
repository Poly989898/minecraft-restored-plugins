package io.lumine.mythic.core.menus.mobs.editor;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.core.menus.mobs.MobEditorButton;
import io.lumine.mythic.core.menus.mobs.MobEditorButtons;
import io.lumine.mythic.core.menus.mobs.MobEditorMenuContext;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.Material;

public class EditAttrButton extends MobEditorButton {
   private String attribute;
   private Material icon;
   private Function<MythicMob, String> currentValue;
   private BiConsumer<MythicMob, String> saveValue;
   private Consumer<MythicMob> resetValue;
   private String option;

   public EditAttrButton(
      MobEditorButtons parent,
      int sort,
      String attribute,
      Material icon,
      Function<MythicMob, String> currentValue,
      BiConsumer<MythicMob, String> saveValue,
      Consumer<MythicMob> resetValue
   ) {
      this(parent, sort, attribute, null, icon, currentValue, saveValue, resetValue);
   }

   public EditAttrButton(
      MobEditorButtons parent,
      int sort,
      String attribute,
      String option,
      Material icon,
      Function<MythicMob, String> currentValue,
      BiConsumer<MythicMob, String> saveValue,
      Consumer<MythicMob> resetValue
   ) {
      super(parent, sort);
      this.attribute = attribute;
      this.icon = icon;
      this.currentValue = currentValue;
      this.saveValue = saveValue;
      this.resetValue = resetValue;
      this.option = option;
   }

   @Override
   public String getLabel() {
      return this.attribute;
   }

   private String getConfigKey() {
      return this.option == null ? this.attribute : this.option;
   }

   @Override
   public Icon<MobEditorMenuContext> getIcon() {
      return IconBuilder.<MobEditorMenuContext>create()
         .material(this.icon)
         .name("<green>Set " + this.attribute)
         .hideFlags()
         .lore(
            context -> {
               String currentVal;
               try {
                  currentVal = this.currentValue.apply(context.getMob());
               } catch (Error | Exception e) {
                  currentVal = null;
               }

               return Lists.newArrayList(
                  new String[]{
                     "&c",
                     "<yellow>Current: <white>" + (currentVal == null ? "None" : currentVal),
                     "<red> ",
                     "<gray>\u22b3 <yellow>Left-Click to Edit Attribute",
                     "<gray>\u22b3 <yellow>Right-Click to Reset Attribute"
                  }
               );
            }
         )
         .click(
            (context, player) -> {
               CommandHelper.sendEditorMessage(
                  player,
                  "Type in the " + this.attribute + " Value you'd like to use.",
                  "<white>You can use math and placeholders, such as <aqua><mob.level>",
                  "Type cancel to abort editing."
               );
               player.closeInventory();
               ChatPrompt.listen(player, input -> {
                  if (input.equalsIgnoreCase("cancel")) {
                     return ChatPrompt.Response.ACCEPTED;
                  } else {
                     MythicMob editing = context.getMob();
                     this.saveValue.accept(editing, input);
                     editing.getConfig().setSave(this.getConfigKey(), input);
                     editing.buildCache();
                     return ChatPrompt.Response.ACCEPTED;
                  }
               }).thenAcceptSync(in -> context.openMenu(player));
            }
         )
         .rightClick((context, player) -> {
            MythicMob editing = context.getMob();
            this.resetValue.accept(editing);
            editing.getConfig().unsetSave(this.getConfigKey());
            editing.buildCache();
            context.openMenu(player);
         })
         .build();
   }
}
