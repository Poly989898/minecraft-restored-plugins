package com.nexomc.nexo.dialog;

import com.nexomc.nexo.decomp.ResultShim;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.ItemDialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\b\u001a\u00020\tJ\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u000f\u001a\u00020\u0010HÖ\u0081\u0004J\n\u0010\u0011\u001a\u00020\u0012HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/dialog/NexoDialogBody;", "", "bodySection", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getBodySection", "()Lorg/bukkit/configuration/ConfigurationSection;", "createDialogBody", "Lio/papermc/paper/registry/data/dialog/body/DialogBody;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
)
public final class NexoDialogBody {
   @NotNull
   private final ConfigurationSection bodySection;

   public NexoDialogBody(@NotNull ConfigurationSection bodySection) {
      super();
      this.bodySection = bodySection;
   }

   @NotNull
   public final ConfigurationSection getBodySection() {
      return this.bodySection;
   }

   @NotNull
   public final DialogBody createDialogBody() {
      NexoDialogBody nexoDialogBody = this;

      Object obj2;
      try {
         NexoDialogBody nexoDialogBody2 = nexoDialogBody;
         int value = 0;
         String string = nexoDialogBody2.bodySection.getString("type");
         obj2 = ResultShim.ok(DialogBodyTypes.valueOf(string));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      DialogBodyTypes dialogBodyTypes = DialogBodyTypes.MESSAGE;
      DialogBodyTypes type = (DialogBodyTypes)(ResultShim.isFailure(obj) ? dialogBodyTypes : obj);
      DialogBody dialogBody;
      switch (NexoDialogBody.WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
         case 1:
            Component richMessage = this.bodySection.getRichMessage("message");
            if (richMessage == null) {
               TextComponent textComponent = Component.empty();
               richMessage = (Component)textComponent;
            }

            Component message = richMessage;
            PlainMessageDialogBody plainMessageDialogBody = DialogBody.plainMessage(message, RangesKt.coerceIn(this.bodySection.getInt("width", 200), 1, 1024));
            dialogBody = (DialogBody)plainMessageDialogBody;
            break;
         case 2:
            ItemStack item = (new NexoDialogItem(this.bodySection)).buildItem();
            String description = this.bodySection.getString("description");
            PlainMessageDialogBody plainMessageDialogBody2 = new PlainMessageDialogBody() {
               public Component contents() {
                  Component component = NexoDialogBody.this.getBodySection().getRichMessage("description.contents");
                  if (component == null) {
                     component = NexoDialogBody.this.getBodySection().getRichMessage("description");
                     if (component == null) {
                        TextComponent textComponent2 = Component.empty();
                        component = (Component)textComponent2;
                     }
                  }

                  return component;
               }

               public int width() {
                  return RangesKt.coerceIn(NexoDialogBody.this.getBodySection().getInt("description.width", 200), 1, 1024);
               }
            };
            int value2 = 0;
            PlainMessageDialogBody messageBody = description != null ? plainMessageDialogBody2 : null;
            boolean showDecorations = this.bodySection.getBoolean("showDecorations", true);
            boolean showTooltip = this.bodySection.getBoolean("showTooltip", true);
            value2 = RangesKt.coerceIn(this.bodySection.getInt("width", 16), 1, 256);
            int height = RangesKt.coerceIn(this.bodySection.getInt("height", 16), 1, 256);
            ItemDialogBody itemDialogBody = DialogBody.item(item, messageBody, showDecorations, showTooltip, value2, height);
            dialogBody = (DialogBody)itemDialogBody;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return dialogBody;
   }

   @NotNull
   public final ConfigurationSection component1() {
      return this.bodySection;
   }

   @NotNull
   public final NexoDialogBody copy(@NotNull ConfigurationSection bodySection) {
      return new NexoDialogBody(bodySection);
   }

   @NotNull
   public String toString() {
      return "NexoDialogBody(bodySection=" + this.bodySection + ")";
   }

   public int hashCode() {
      return this.bodySection.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof NexoDialogBody)) {
         return false;
      } else {
         NexoDialogBody nexoDialogBody = (NexoDialogBody)other;
         return Intrinsics.areEqual(this.bodySection, nexoDialogBody.bodySection);
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.dialog.DialogBodyTypes.values().length];
         try { a_0[com.nexomc.nexo.dialog.DialogBodyTypes.MESSAGE.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogBodyTypes.ITEM.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
