package com.nexomc.nexo.dialog;

import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickEvent;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\b\u001a\u0004\u0018\u00010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\n"},
   d2 = {"Lcom/nexomc/nexo/dialog/NexoDialogAction;", "", "actionSection", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getActionSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "createAction", "Lio/papermc/paper/registry/data/dialog/action/DialogAction;", "com.nexomc:core"}
)
public final class NexoDialogAction {
   @NotNull
   private final ConfigurationSection actionSection;

   public NexoDialogAction(@NotNull ConfigurationSection actionSection) {
      super();
      this.actionSection = actionSection;
   }

   @NotNull
   public final ConfigurationSection getActionSection() {
      return this.actionSection;
   }

   @Nullable
   public final DialogAction createAction() {
      ConfigurationSection configurationSection = this.actionSection;
      String str4 = "type";
      DialogActionTypes dialogActionTypes = (DialogActionTypes)EnumUtils.getEnum(DialogActionTypes.class, NexoYamlKt.stringOrNull(configurationSection, str4));
      if (dialogActionTypes == null) {
         NexoDialogAction nexoDialogAction = this;
         NexoDialogs.INSTANCE.getContext().getLogger().warn("Invalid ActionType in " + nexoDialogAction.actionSection.getCurrentPath() + ", ignoring action...");
         return null;
      } else {
         block: {
            block2: {
               DialogActionTypes type = dialogActionTypes;
               DialogAction dialogAction;
               switch (NexoDialogAction.WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
                  case 1:
                     dialogAction = (DialogAction)DialogAction.staticAction(ClickEvent.openUrl(NexoYamlKt.stringOrEmpty(this.actionSection, "url")));
                     break;
                  case 2:
                     dialogAction = (DialogAction)DialogAction.staticAction(ClickEvent.runCommand(NexoYamlKt.stringOrEmpty(this.actionSection, "command")));
                     break;
                  case 3:
                     String string = this.actionSection.getString("command");
                     dialogAction = (DialogAction)DialogAction.staticAction(ClickEvent.suggestCommand(string));
                     break;
                  case 4:
                     dialogAction = (DialogAction)DialogAction.staticAction(ClickEvent.changePage(this.actionSection.getInt("page")));
                     break;
                  case 5:
                     String str = this.actionSection.getString("clipboard");
                     dialogAction = (DialogAction)DialogAction.staticAction(ClickEvent.copyToClipboard(str));
                     break;
                  case 6:
                     Key key = NexoYamlKt.key(this.actionSection, "dialog");
                     if (key == null) {
                        break block;
                     }

                     Key p0 = key;
                     Registry dialogRegistry = NexoDialogs.INSTANCE.getDialogRegistry();
                     int value = 0;
                     Dialog dialog2 = (Dialog)dialogRegistry.get(p0);
                     if (dialog2 == null) {
                        break block;
                     }

                     Dialog dialog = dialog2;
                     dialogAction = (DialogAction)DialogAction.staticAction(ClickEvent.showDialog((DialogLike)dialog));
                     break;
                  case 7:
                     Key key2 = NexoYamlKt.key(this.actionSection, "id");
                     if (key2 == null) {
                        NexoDialogAction nexoDialogAction2 = this;
                        int value2 = 0;
                        NexoDialogs.INSTANCE.getContext().getLogger().warn("Invalid id in " + nexoDialogAction2.actionSection.getCurrentPath() + ", ignoring action...");
                        return null;
                     }

                     Key id = key2;
                     String str2 = NexoYamlKt.stringOrNull(this.actionSection, "payload");
                     if (str2 == null) {
                        break block2;
                     }

                     String p0_4 = str2;
                     int value3 = 0;
                     BinaryTagHolder binaryTagHolder = BinaryTagHolder.binaryTagHolder(p0_4);
                     if (binaryTagHolder == null) {
                        break block2;
                     }

                     BinaryTagHolder payload = binaryTagHolder;
                     dialogAction = (DialogAction)DialogAction.staticAction(ClickEvent.custom(id, payload));
                     break;
                  case 8:
                     dialogAction = (DialogAction)DialogAction.commandTemplate(NexoYamlKt.stringOrEmpty(this.actionSection, "template"));
                     break;
                  case 9:
                     Key key3 = NexoYamlKt.key(this.actionSection, "id");
                     if (key3 == null) {
                        NexoDialogAction nexoDialogAction3 = this;
                        int value4 = 0;
                        NexoDialogs.INSTANCE.getContext().getLogger().warn("Invalid id in " + nexoDialogAction3.actionSection.getCurrentPath() + ", ignoring action...");
                        return null;
                     }

                     Key id_3 = key3;
                     String str3 = this.actionSection.getString("additions");
                     BinaryTagHolder binaryTagHolder2;
                     if (str3 != null) {
                        String p0_2 = str3;
                        int p0_1 = 0;
                        binaryTagHolder2 = BinaryTagHolder.binaryTagHolder(p0_2);
                     } else {
                        binaryTagHolder2 = null;
                     }

                     BinaryTagHolder additions = binaryTagHolder2;
                     dialogAction = (DialogAction)DialogAction.customClick(id_3, additions);
                     break;
                  default:
                     throw new NoWhenBranchMatchedException();
               }

               return dialogAction;
            }

            NexoDialogAction nexoDialogAction4 = this;
            int value5 = 0;
            NexoDialogs.INSTANCE.getContext().getLogger().warn("Invalid payload in " + nexoDialogAction4.actionSection.getCurrentPath() + ", ignoring action...");
            return null;
         }

         NexoDialogAction nexoDialogAction5 = this;
         int value6 = 0;
         NexoDialogs.INSTANCE.getContext().getLogger().warn("Invalid Dialog specified in " + nexoDialogAction5.actionSection.getCurrentPath() + ", ignoring action...");
         return null;
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.dialog.DialogActionTypes.values().length];
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.OPEN_URL.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.RUN_COMMAND.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.SUGGEST_COMMAND.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.CHANGE_PAGE.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.CLIPBOARD.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.SHOW_DIALOG.ordinal()] = 6; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.CUSTOM.ordinal()] = 7; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.DYNAMIC_RUN_COMMAND.ordinal()] = 8; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogActionTypes.DYNAMIC_CUSTOM.ordinal()] = 9; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
