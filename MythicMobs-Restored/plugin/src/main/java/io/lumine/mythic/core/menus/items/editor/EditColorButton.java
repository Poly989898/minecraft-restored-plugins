package io.lumine.mythic.core.menus.items.editor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.IconBuilder;
import io.lumine.mythic.bukkit.utils.prompts.chat.ChatPrompt;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorButton;
import io.lumine.mythic.core.menus.items.ItemEditorButtons;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import java.util.Collection;
import org.bukkit.Material;

public class EditColorButton extends ItemEditorButton {
   private static final Collection<Material> COLORABLE = Sets.newHashSet(
      new Material[]{
         Material.LEATHER_HORSE_ARMOR,
         Material.LEATHER_HELMET,
         Material.LEATHER_CHESTPLATE,
         Material.LEATHER_LEGGINGS,
         Material.LEATHER_BOOTS,
         Material.POTION,
         Material.LINGERING_POTION,
         Material.SPLASH_POTION,
         Material.SHIELD
      }
   );

   public EditColorButton(ItemEditorButtons parent, int sort) {
      super(parent, sort);
   }

   @Override
   public boolean isApplicable(MythicItem mythicItem) {
      return COLORABLE.contains(mythicItem.getMaterial());
   }

   @Override
   public Icon<ItemEditorMenuContext> getIcon() {
      return IconBuilder.<ItemEditorMenuContext>create()
         .material(Material.RED_DYE)
         .name("<green>Set Color")
         .lore(
            context -> Lists.newArrayList(
               new String[]{
                  "&c",
                  "<yellow>Current: <" + context.getItem().getColor().get().toHexString() + ">" + context.getItem().getColor().get().toHexString(),
                  "<gray>",
                  "<gray>Left-Click to open Color Picker",
                  "<gray>Right-Click to supply Placeholder"
               }
            )
         )
         .click((context, player) -> {
            Chroma existingColor = context.getItem().getColor() == null ? null : context.getItem().getColor().get();
            MythicBukkit.inst().getMenuManager().getColorPickerMenu().openMenu(player, existingColor).thenAcceptSync(color -> {
               MythicItem item = context.getItem();
               item.getConfig().setSave("Options.Color", color.serializeShortForm());
               item.loadItem();
               item.buildItemCache();
               context.openMenu(player);
            });
         })
         .rightClick((context, player) -> {
            CommandHelper.sendEditorMessage(player, "Type in the Color you'd like to use.", "Type cancel to abort editing.");
            player.closeInventory();
            ChatPrompt.listen(player, input -> {
               if (input.equalsIgnoreCase("cancel")) {
                  return ChatPrompt.Response.ACCEPTED;
               } else {
                  try {
                     Chroma color = Chroma.of(input);
                     if (color == null) {
                        CommandHelper.sendError(player, "Invalid color format supplied");
                        return ChatPrompt.Response.TRY_AGAIN;
                     }

                     MythicItem item = context.getItem();
                     item.getConfig().setSave("Options.Color", color.serializeShortForm());
                     item.loadItem();
                     item.buildItemCache();
                  } catch (Throwable e) {
                     CommandHelper.sendError(player, "Invalid color format supplied");
                     return ChatPrompt.Response.TRY_AGAIN;
                  }

                  return ChatPrompt.Response.ACCEPTED;
               }
            }).thenAcceptSync(in -> context.openMenu(player));
         })
         .build();
   }
}
