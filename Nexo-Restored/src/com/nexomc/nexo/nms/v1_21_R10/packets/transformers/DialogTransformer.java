package com.nexomc.nexo.nms.v1_21_R10.packets.transformers;

import com.nexomc.nexo.nms.Transformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.ServerLinksDialog;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.dialog.input.BooleanInput;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.dialog.input.SingleOptionInput;
import net.minecraft.server.dialog.input.TextInput;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u001a\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\nJ\u0010\u0010\t\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0002H\u0016J\u0010\u0010\t\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u001f\u0010\t\u001a\u0002H\r\"\b\b\u0000\u0010\r*\u00020\u000e2\u0006\u0010\u000b\u001a\u0002H\rH\u0002¢\u0006\u0002\u0010\u000fJ\u0010\u0010\t\u001a\u00020\u00102\u0006\u0010\u000b\u001a\u00020\u0010H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R10/packets/transformers/DialogTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/server/dialog/Dialog;", "componentTransformer", "Lcom/nexomc/nexo/nms/v1_21_R10/packets/transformers/ComponentTransformer;", "itemTransformer", "Lcom/nexomc/nexo/nms/v1_21_R10/packets/transformers/ItemTransformer;", "<init>", "(Lcom/nexomc/nexo/nms/v1_21_R10/packets/transformers/ComponentTransformer;Lcom/nexomc/nexo/nms/v1_21_R10/packets/transformers/ItemTransformer;)V", "transform", "Lnet/minecraft/core/Holder;", "input", "Lnet/minecraft/server/dialog/ActionButton;", "T", "Lnet/minecraft/server/dialog/body/DialogBody;", "(Lnet/minecraft/server/dialog/body/DialogBody;)Lnet/minecraft/server/dialog/body/DialogBody;", "Lnet/minecraft/server/dialog/Input;", "com.nexomc:v1_21_R10"}
)
public final class DialogTransformer implements Transformer<Dialog> {
   @NotNull
   private final ComponentTransformer componentTransformer;
   @NotNull
   private final ItemTransformer itemTransformer;

   public DialogTransformer(@NotNull ComponentTransformer componentTransformer, @NotNull ItemTransformer itemTransformer) {
      Intrinsics.checkNotNullParameter(componentTransformer, "componentTransformer");
      Intrinsics.checkNotNullParameter(itemTransformer, "itemTransformer");
      super();
      this.componentTransformer = componentTransformer;
      this.itemTransformer = itemTransformer;
   }

   @NotNull
   public final Holder<Dialog> transform(@NotNull Holder<Dialog> input) {
      Intrinsics.checkNotNullParameter(input, "input");
      Object var10001 = input.value();
      Intrinsics.checkNotNullExpressionValue(var10001, "value(...)");
      Holder var10000 = Holder.direct(this.transform((Dialog)var10001));
      Intrinsics.checkNotNullExpressionValue(var10000, "direct(...)");
      return var10000;
   }

   @NotNull
   public Dialog transform(@NotNull Dialog input) {
      Intrinsics.checkNotNullParameter(input, "input");
      CommonDialogData common = input.common();
      ComponentTransformer var10000 = this.componentTransformer;
      Component var10001 = common.title();
      Intrinsics.checkNotNullExpressionValue(var10001, "title(...)");
      Component var59 = var10000.transform(var10001);
      Optional var61 = common.externalTitle().map(DialogTransformer::transform$lambda$0);
      boolean var10002 = common.canCloseWithEscape();
      int var10003 = common.pause();
      DialogAction var10004 = common.afterAction();
      List var10005 = common.body();
      Intrinsics.checkNotNullExpressionValue(var10005, "body(...)");
      Iterable $this$map$iv = (Iterable)var10005;
      DialogAction var18 = var10004;
      boolean var17 = (boolean)var10003;
      boolean var16 = var10002;
      Optional var15 = var61;
      Component var14 = var59;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         DialogBody p0 = (DialogBody)item$iv$iv;
         int var12 = 0;
         destination$iv$iv.add(this.transform(p0));
      }

      List var19 = (List)destination$iv$iv;
      List var10006 = common.inputs();
      Intrinsics.checkNotNullExpressionValue(var10006, "inputs(...)");
      $this$map$iv = (Iterable)var10006;
      destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         Input p0 = (Input)item$iv$iv;
         int var55 = 0;
         destination$iv$iv.add(this.transform(p0));
      }

      List var20 = (List)destination$iv$iv;
      CommonDialogData newCommon = new CommonDialogData(var14, var15, var16, var17, var18, var19, var20);
      Dialog var60;
      if (input instanceof ConfirmationDialog) {
         ActionButton var70 = ((ConfirmationDialog)input).yesButton();
         Intrinsics.checkNotNullExpressionValue(var70, "yesButton(...)");
         ActionButton var68 = this.transform(var70);
         ActionButton var72 = ((ConfirmationDialog)input).noButton();
         Intrinsics.checkNotNullExpressionValue(var72, "noButton(...)");
         var60 = (Dialog)(new ConfirmationDialog(newCommon, var68, this.transform(var72)));
      } else if (input instanceof NoticeDialog) {
         ActionButton var71 = ((NoticeDialog)input).action();
         Intrinsics.checkNotNullExpressionValue(var71, "action(...)");
         var60 = (Dialog)(new NoticeDialog(newCommon, this.transform(var71)));
      } else if (input instanceof ServerLinksDialog) {
         var60 = (Dialog)(new ServerLinksDialog(newCommon, ((ServerLinksDialog)input).exitAction().map(DialogTransformer::transform$lambda$2), ((ServerLinksDialog)input).columns(), ((ServerLinksDialog)input).buttonWidth()));
      } else if (input instanceof MultiActionDialog) {
         List var62 = ((MultiActionDialog)input).actions();
         Intrinsics.checkNotNullExpressionValue(var62, "actions(...)");
         Iterable $this$map$iv = (Iterable)var62;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

         for(Object item$iv$iv : $this$map$iv) {
            ActionButton p0 = (ActionButton)item$iv$iv;
            int var13 = 0;
            destination$iv$iv.add(this.transform(p0));
         }

         var62 = (List)destination$iv$iv;
         Optional var66 = ((MultiActionDialog)input).exitAction().map(DialogTransformer::transform$lambda$4);
         int var28 = ((MultiActionDialog)input).columns();
         Optional var29 = var66;
         List var30 = var62;
         var60 = (Dialog)(new MultiActionDialog(newCommon, var30, var29, var28));
      } else if (input instanceof DialogListDialog) {
         HolderSet var64 = ((DialogListDialog)input).dialogs();
         Intrinsics.checkNotNullExpressionValue(var64, "dialogs(...)");
         Iterable $this$map$iv = (Iterable)var64;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

         for(Object item$iv$iv : $this$map$iv) {
            Holder p0 = (Holder)item$iv$iv;
            int var58 = 0;
            destination$iv$iv.add(this.transform(p0));
         }

         var64 = (HolderSet)HolderSet.direct((List)destination$iv$iv);
         Optional var67 = ((DialogListDialog)input).exitAction().map(DialogTransformer::transform$lambda$6);
         var10003 = ((DialogListDialog)input).columns();
         int var32 = ((DialogListDialog)input).buttonWidth();
         int var33 = var10003;
         Optional var34 = var67;
         HolderSet var35 = var64;
         var60 = (Dialog)(new DialogListDialog(newCommon, var35, var34, var33, var32));
      } else {
         var60 = input;
      }

      return var60;
   }

   private final ActionButton transform(ActionButton input) {
      ComponentTransformer var10002 = this.componentTransformer;
      Component var10003 = input.button().label();
      Intrinsics.checkNotNullExpressionValue(var10003, "label(...)");
      CommonButtonData button = new CommonButtonData(var10002.transform(var10003), input.button().tooltip().map(DialogTransformer::transform$lambda$7), input.button().width());
      return new ActionButton(button, input.action());
   }

   private final <T extends DialogBody> T transform(T input) {
      Object var10000;
      if (input instanceof PlainMessage) {
         ComponentTransformer var10002 = this.componentTransformer;
         Component var10003 = ((PlainMessage)input).contents();
         Intrinsics.checkNotNullExpressionValue(var10003, "contents(...)");
         var10000 = new PlainMessage(var10002.transform(var10003), ((PlainMessage)input).width());
      } else if (input instanceof ItemBody) {
         ItemTransformer var4 = this.itemTransformer;
         ItemStack var5 = ((ItemBody)input).item();
         Intrinsics.checkNotNullExpressionValue(var5, "item(...)");
         var10000 = new ItemBody(var4.transform(var5), ((ItemBody)input).description().map(DialogTransformer::transform$lambda$8), ((ItemBody)input).showDecorations(), ((ItemBody)input).showTooltip(), ((ItemBody)input).width(), ((ItemBody)input).height());
      } else {
         var10000 = this;
      }

      Object var2 = var10000;
      Intrinsics.checkNotNull(var2, "null cannot be cast to non-null type T of com.nexomc.nexo.nms.v1_21_R10.packets.transformers.DialogTransformer.transform");
      return (T)(var2);
   }

   private final Input transform(Input input) {
      InputControl control = input.control();
      InputControl var10000;
      if (control instanceof BooleanInput) {
         ComponentTransformer var10002 = this.componentTransformer;
         Component var10003 = ((BooleanInput)control).label();
         Intrinsics.checkNotNullExpressionValue(var10003, "label(...)");
         var10000 = (InputControl)(new BooleanInput(var10002.transform(var10003), ((BooleanInput)control).initial(), ((BooleanInput)control).onTrue(), ((BooleanInput)control).onFalse()));
      } else if (control instanceof NumberRangeInput) {
         int var4 = ((NumberRangeInput)control).width();
         ComponentTransformer var7 = this.componentTransformer;
         Component var10004 = ((NumberRangeInput)control).label();
         Intrinsics.checkNotNullExpressionValue(var10004, "label(...)");
         var10000 = (InputControl)(new NumberRangeInput(var4, var7.transform(var10004), ((NumberRangeInput)control).labelFormat(), ((NumberRangeInput)control).rangeInfo()));
      } else if (control instanceof SingleOptionInput) {
         int var5 = ((SingleOptionInput)control).width();
         List var8 = ((SingleOptionInput)control).entries();
         ComponentTransformer var10 = this.componentTransformer;
         Component var10005 = ((SingleOptionInput)control).label();
         Intrinsics.checkNotNullExpressionValue(var10005, "label(...)");
         var10000 = (InputControl)(new SingleOptionInput(var5, var8, var10.transform(var10005), ((SingleOptionInput)control).labelVisible()));
      } else if (control instanceof TextInput) {
         int var6 = ((TextInput)control).width();
         ComponentTransformer var9 = this.componentTransformer;
         Component var11 = ((TextInput)control).label();
         Intrinsics.checkNotNullExpressionValue(var11, "label(...)");
         var10000 = (InputControl)(new TextInput(var6, var9.transform(var11), ((TextInput)control).labelVisible(), ((TextInput)control).initial(), ((TextInput)control).maxLength(), ((TextInput)control).multiline()));
      } else {
         var10000 = control;
      }

      InputControl control = var10000;
      return new Input(input.key(), control);
   }

   private static final Component transform$lambda$0(Function1 $tmp0, Object p0) {
      return (Component)$tmp0.invoke(p0);
   }

   private static final ActionButton transform$lambda$2(Function1 $tmp0, Object p0) {
      return (ActionButton)$tmp0.invoke(p0);
   }

   private static final ActionButton transform$lambda$4(Function1 $tmp0, Object p0) {
      return (ActionButton)$tmp0.invoke(p0);
   }

   private static final ActionButton transform$lambda$6(Function1 $tmp0, Object p0) {
      return (ActionButton)$tmp0.invoke(p0);
   }

   private static final Component transform$lambda$7(Function1 $tmp0, Object p0) {
      return (Component)$tmp0.invoke(p0);
   }

   private static final PlainMessage transform$lambda$8(Function1 $tmp0, Object p0) {
      return (PlainMessage)$tmp0.invoke(p0);
   }
}
