package com.nexomc.nexo.nms.v26_R2.packets.transformers;

import com.nexomc.nexo.nms.Transformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
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
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u001a\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00020\nJ\u0010\u0010\t\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0002H\u0016J\u0010\u0010\t\u001a\u00020\f2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u001f\u0010\t\u001a\u0002H\r\"\b\b\u0000\u0010\r*\u00020\u000e2\u0006\u0010\u000b\u001a\u0002H\rH\u0002¢\u0006\u0002\u0010\u000fJ\u0010\u0010\t\u001a\u00020\u00102\u0006\u0010\u000b\u001a\u00020\u0010H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/DialogTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/server/dialog/Dialog;", "componentTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ComponentTransformer;", "itemTransformer", "Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ItemTransformer;", "<init>", "(Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ComponentTransformer;Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ItemTransformer;)V", "transform", "Lnet/minecraft/core/Holder;", "input", "Lnet/minecraft/server/dialog/ActionButton;", "T", "Lnet/minecraft/server/dialog/body/DialogBody;", "(Lnet/minecraft/server/dialog/body/DialogBody;)Lnet/minecraft/server/dialog/body/DialogBody;", "Lnet/minecraft/server/dialog/Input;", "com.nexomc:v26_R2"}
)
public final class DialogTransformer implements Transformer<Dialog> {
   @NotNull
   private final ComponentTransformer componentTransformer;
   @NotNull
   private final ItemTransformer itemTransformer;

   public DialogTransformer(@NotNull ComponentTransformer componentTransformer, @NotNull ItemTransformer itemTransformer) {
      super();
      this.componentTransformer = componentTransformer;
      this.itemTransformer = itemTransformer;
   }

   @NotNull
   public final Holder<Dialog> transform(@NotNull Holder<Dialog> input) {
      Object obj = input.value();
      Holder holder = Holder.direct(this.transform((Dialog)obj));
      return holder;
   }

   @NotNull
   public Dialog transform(@NotNull Dialog input) {
      CommonDialogData commonDialogData = input.common();
      CommonDialogData common = commonDialogData;
      ComponentTransformer componentTransformer2 = this.componentTransformer;
      Component title = common.title();
      Component component = componentTransformer2.transform(title);
      Optional optional = common.externalTitle().map((a0_ -> DialogTransformer.transform$lambda$0((Function1)((p0) -> this.componentTransformer.transform((Component)p0)), a0_)));
      boolean canCloseWithEscape = common.canCloseWithEscape();
      boolean pauseGame = common.pause();
      DialogAction afterAction = common.afterAction();
      List body = common.body();
      Iterable iterable = (Iterable)body;
      DialogAction dialogAction = afterAction;
      boolean flag = pauseGame;
      boolean flag2 = canCloseWithEscape;
      Optional optional2 = optional;
      Component component2 = component;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         DialogBody p0 = (DialogBody)item;
         int value = 0;
         destination.add(this.transform(p0));
      }

      List list = (List)destination;
      List inputs = common.inputs();
      iterable = (Iterable)inputs;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj : iterable) {
         Input p0 = (Input)obj;
         int value2 = 0;
         destination.add(this.transform(p0));
      }

      List list2 = (List)destination;
      CommonDialogData newCommon = new CommonDialogData(component2, optional2, flag2, flag, dialogAction, list, list2);
      Dialog dialog;
      if (input instanceof ConfirmationDialog) {
         ActionButton yesButton = ((ConfirmationDialog)input).yesButton();
         ActionButton actionButton = this.transform(yesButton);
         ActionButton noButton = ((ConfirmationDialog)input).noButton();
         dialog = (Dialog)(new ConfirmationDialog(newCommon, actionButton, this.transform(noButton)));
      } else if (input instanceof NoticeDialog) {
         ActionButton action = ((NoticeDialog)input).action();
         dialog = (Dialog)(new NoticeDialog(newCommon, this.transform(action)));
      } else if (input instanceof ServerLinksDialog) {
         dialog = (Dialog)(new ServerLinksDialog(newCommon, ((ServerLinksDialog)input).exitAction().map((a0_ -> DialogTransformer.transform$lambda$2((Function1)((p0) -> this.transform((ActionButton)p0)), a0_))), ((ServerLinksDialog)input).columns(), ((ServerLinksDialog)input).buttonWidth()));
      } else if (input instanceof MultiActionDialog) {
         List actions = ((MultiActionDialog)input).actions();
         Iterable iterable2 = (Iterable)actions;
         Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object obj2 : iterable2) {
            ActionButton p0 = (ActionButton)obj2;
            int value3 = 0;
            collection.add(this.transform(p0));
         }

         actions = (List)collection;
         Optional optional3 = ((MultiActionDialog)input).exitAction().map((a0_ -> DialogTransformer.transform$lambda$4((Function1)((p0) -> this.transform((ActionButton)p0)), a0_)));
         int columns = ((MultiActionDialog)input).columns();
         Optional optional4 = optional3;
         List list3 = actions;
         dialog = (Dialog)(new MultiActionDialog(newCommon, list3, optional4, columns));
      } else if (input instanceof DialogListDialog) {
         HolderSet dialogs = ((DialogListDialog)input).dialogs();
         Iterable iterable3 = (Iterable)dialogs;
         Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

         for(Object obj3 : iterable3) {
            Holder p0 = (Holder)obj3;
            int value4 = 0;
            collection2.add(this.transform(p0));
         }

         dialogs = (HolderSet)HolderSet.direct((List)collection2);
         Optional optional5 = ((DialogListDialog)input).exitAction().map((a0_ -> DialogTransformer.transform$lambda$6((Function1)((p0) -> this.transform((ActionButton)p0)), a0_)));
         int value5 = ((DialogListDialog)input).columns();
         int buttonWidth = ((DialogListDialog)input).buttonWidth();
         int value6 = value5;
         Optional optional6 = optional5;
         HolderSet holderSet = dialogs;
         dialog = (Dialog)(new DialogListDialog(newCommon, holderSet, optional6, value6, buttonWidth));
      } else {
         dialog = input;
      }

      return dialog;
   }

   private final ActionButton transform(ActionButton input) {
      ComponentTransformer componentTransformer2 = this.componentTransformer;
      Component label = input.button().label();
      CommonButtonData button = new CommonButtonData(componentTransformer2.transform(label), input.button().tooltip().map((a0_ -> DialogTransformer.transform$lambda$7((Function1)((p0) -> this.componentTransformer.transform((Component)p0)), a0_))), input.button().width());
      return new ActionButton(button, input.action());
   }

   private final <T extends DialogBody> T transform(T input) {
      Object obj;
      if (input instanceof PlainMessage) {
         ComponentTransformer componentTransformer2 = this.componentTransformer;
         Component contents = ((PlainMessage)input).contents();
         obj = new PlainMessage(componentTransformer2.transform(contents), ((PlainMessage)input).width());
      } else if (input instanceof ItemBody) {
         ItemTransformer itemTransformer2 = this.itemTransformer;
         ItemStackTemplate item = ((ItemBody)input).item();
         obj = new ItemBody(itemTransformer2.transform(item), ((ItemBody)input).description().map((a0_ -> DialogTransformer.transform$lambda$8((Function1)((p0) -> this.transform((PlainMessage)p0)), a0_))), ((ItemBody)input).showDecorations(), ((ItemBody)input).showTooltip(), ((ItemBody)input).width(), ((ItemBody)input).height());
      } else {
         obj = this;
      }

      Object obj2 = obj;
      return (T)(obj2);
   }

   private final Input transform(Input input) {
      InputControl inputControl = input.control();
      InputControl control = inputControl;
      if (control instanceof BooleanInput) {
         ComponentTransformer componentTransformer2 = this.componentTransformer;
         Component label = ((BooleanInput)control).label();
         inputControl = (InputControl)(new BooleanInput(componentTransformer2.transform(label), ((BooleanInput)control).initial(), ((BooleanInput)control).onTrue(), ((BooleanInput)control).onFalse()));
      } else if (control instanceof NumberRangeInput) {
         int width = ((NumberRangeInput)control).width();
         ComponentTransformer componentTransformer3 = this.componentTransformer;
         Component component = ((NumberRangeInput)control).label();
         inputControl = (InputControl)(new NumberRangeInput(width, componentTransformer3.transform(component), ((NumberRangeInput)control).labelFormat(), ((NumberRangeInput)control).rangeInfo()));
      } else if (control instanceof SingleOptionInput) {
         int value = ((SingleOptionInput)control).width();
         List entries = ((SingleOptionInput)control).entries();
         ComponentTransformer componentTransformer4 = this.componentTransformer;
         Component component2 = ((SingleOptionInput)control).label();
         inputControl = (InputControl)(new SingleOptionInput(value, entries, componentTransformer4.transform(component2), ((SingleOptionInput)control).labelVisible()));
      } else if (control instanceof TextInput) {
         int value2 = ((TextInput)control).width();
         ComponentTransformer componentTransformer5 = this.componentTransformer;
         Component component3 = ((TextInput)control).label();
         inputControl = (InputControl)(new TextInput(value2, componentTransformer5.transform(component3), ((TextInput)control).labelVisible(), ((TextInput)control).initial(), ((TextInput)control).maxLength(), ((TextInput)control).multiline()));
      } else {
         inputControl = control;
      }

      InputControl control_1 = inputControl;
      return new Input(input.key(), control_1);
   }

   private static final Component transform$lambda$0(Function1 tmp0, Object p0) {
      return (Component)tmp0.invoke(p0);
   }

   private static final ActionButton transform$lambda$2(Function1 tmp0, Object p0) {
      return (ActionButton)tmp0.invoke(p0);
   }

   private static final ActionButton transform$lambda$4(Function1 tmp0, Object p0) {
      return (ActionButton)tmp0.invoke(p0);
   }

   private static final ActionButton transform$lambda$6(Function1 tmp0, Object p0) {
      return (ActionButton)tmp0.invoke(p0);
   }

   private static final Component transform$lambda$7(Function1 tmp0, Object p0) {
      return (Component)tmp0.invoke(p0);
   }

   private static final PlainMessage transform$lambda$8(Function1 tmp0, Object p0) {
      return (PlainMessage)tmp0.invoke(p0);
   }

   public static final ActionButton access$transform(DialogTransformer dialogTransformer, ActionButton input) { return dialogTransformer.transform(input); }

   public static final DialogBody access$transform(DialogTransformer dialogTransformer, DialogBody input) { return dialogTransformer.transform(input); }
}
