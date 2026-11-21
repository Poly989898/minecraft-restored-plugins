package io.lumine.mythic.core.menus.mobs;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.EditableMenuBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.menus.MenuManager;
import io.lumine.mythic.core.menus.MythicMenu;
import io.lumine.mythic.core.mobs.MobType;
import java.util.List;
import java.util.Locale;
import org.bukkit.entity.Player;

public class MobEditorMenu extends MythicMenu<MobEditorMenuContext> {
   private MobEditorButtons buttons = new MobEditorButtons();

   public MobEditorMenu(MythicBukkit plugin, MenuManager manager) {
      super(plugin, manager, "/menus/mob-editor.yml");
   }

   public void open(Player player, MythicMob mythicMob) {
      this.open(player, new MobEditorMenuContext(mythicMob, new MobMenuContext()));
   }

   public void open(Player player, MobEditorMenuContext context) {
      List<MobEditorButton> buttons = Lists.newArrayList(this.buttons.getButtons());
      if (context.hasFilter()) {
         String filter = context.getFilter().toUpperCase(Locale.ROOT);
         buttons.removeIf(button -> !button.getLabel().toUpperCase(Locale.ROOT).contains(filter));
      }

      this.open(player, context, Text.colorize("Editing Mob: " + context.getMob().getInternalName()), buttons);
   }

   @Override
   public EditableMenuBuilder<MobEditorMenuContext> build(EditableMenuBuilder<MobEditorMenuContext> builder) {
      builder = this.addPageButtons(builder);
      builder.getIcon("BACK_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> context.openPreviousMenu(player)));
      builder.getIcon("MOB_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .name("<green>Get Mob Egg")
               .lore(
                  context -> Lists.newArrayList(
                     new String[]{
                        "<red> ",
                        "<yellow>Type<white>: <white>" + context.getMob().getEntityType(),
                        "<yellow>Pack<white>: <white>" + describePack(context),
                        "<red> ",
                        "<gray>\u22b3 <yellow>Left-Click to get Mob Egg",
                        "<gray>\u22b3 <yellow>Right-Click to Spawn Here"
                     }
                  )
               )
               .hideFlags(false)
               .click((context, player) -> {
                  player.performCommand("mythicmobs egg get " + context.getMob().getInternalName());
                  player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1.0F, 1.0F);
               })
               .rightClick((context, player) -> {
                  player.closeInventory();
                  player.performCommand("mythicmobs mobs spawn " + context.getMob().getInternalName() + " 1");
               })
         );
      builder.getIcon("SEARCH_BUTTON")
         .ifPresent(
            icon -> icon.getBuilder()
               .lore(
                  context -> Lists.newArrayList(new String[]{"<red> ", "<yellow>Filter<white>: <white>" + (context.hasFilter() ? context.getFilter() : "None")})
               )
               .click((context, player) -> {
                  player.closeInventory();
                  CommandHelper.sendEditorMessage(player, "Type in the text you'd like to search for:", "<gray>Type cancel to abort filtering.");
                  ChatPrompt.listen(player, input -> {
                     if (input.equalsIgnoreCase("cancel")) {
                        return ChatPrompt.Response.ACCEPTED;
                     } else {
                        context.setFilter(input);
                        return ChatPrompt.Response.ACCEPTED;
                     }
                  }).thenAcceptSync(in -> this.open(player, context));
               })
         );
      builder.getIcon("SEARCH_RESET_BUTTON").ifPresent(icon -> icon.getBuilder().click((context, player) -> {
         context.clearFilter();
         this.open(player, context);
      }));
      builder.getIcon("DELETE_BUTTON").ifPresent(icon -> icon.getBuilder().hideFlags(false).click((context, player) -> {
         if (context.getMob() instanceof MobType mobType) {
            CommandHelper.sendEditorMessage(player, "Type <red>DELETE <white>to confirm deleting", "Type anything else to abort");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (!input.equals("DELETE")) {
                  context.openMenu(player);
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  this.getPlugin().getMobManager().deleteMob(mobType);
                  context.openPreviousMenu(player);
                  player.playSound(player.getLocation(), "entity.zombie.break_wooden_door", 1.0F, 1.0F);
                  return ChatPrompt.Response.ACCEPTED;
               }
            }).thenAcceptSync(in -> {});
         } else {
            CommandHelper.sendError(player, "This mob type cannot be deleted from the editor.");
         }
      }));
      return builder;
   }

   private static String describePack(MobEditorMenuContext context) {
      return context.getMob() instanceof MobType mobType && mobType.getPack() != null ? mobType.getPack().getName() : "None";
   }
}
