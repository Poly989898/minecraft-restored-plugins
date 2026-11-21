package com.nexomc.nexo.dialog;

import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import kotlin.Metadata;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/dialog/NexoActionButton;", "", "<init>", "()V", "createButton", "Lio/papermc/paper/registry/data/dialog/ActionButton;", "dialogConfig", "Lorg/bukkit/configuration/ConfigurationSection;", "com.nexomc:core"}
)
public final class NexoActionButton {
   @NotNull
   public static final NexoActionButton INSTANCE = new NexoActionButton();

   private NexoActionButton() {
   }

   @NotNull
   public final ActionButton createButton(@Nullable ConfigurationSection dialogConfig) {
      io.papermc.paper.registry.data.dialog.ActionButton.Builder builder;
      Component component;
      block: {
         if (dialogConfig != null) {
            component = dialogConfig.getRichMessage("label");
            if (component != null) {
               break block;
            }
         }

         TextComponent textComponent = Component.empty();
         component = (Component)textComponent;
      }

      DialogAction dialogAction;
      block: {
         builder = ActionButton.builder(component).tooltip(dialogConfig != null ? dialogConfig.getRichMessage("tooltip") : null).width(dialogConfig != null ? RangesKt.coerceIn(dialogConfig.getInt("width"), 1, 1024) : 150);
         if (dialogConfig != null) {
            ConfigurationSection configurationSection = NexoYamlKt.section(dialogConfig, "action");
            if (configurationSection != null) {
               ConfigurationSection p0 = configurationSection;
               ActionButton.Builder builder2 = builder;
               int value = 0;
               NexoDialogAction nexoDialogAction = new NexoDialogAction(p0);
               builder = builder2;
               dialogAction = nexoDialogAction.createAction();
               break block;
            }
         }

         dialogAction = null;
      }

      ActionButton actionButton = builder.action(dialogAction).build();
      return actionButton;
   }
}
