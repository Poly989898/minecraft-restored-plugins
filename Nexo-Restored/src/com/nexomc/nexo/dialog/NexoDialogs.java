package com.nexomc.nexo.dialog;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.EnumUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryBuilder;
import io.papermc.paper.registry.RegistryBuilderFactory;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.ConfirmationType;
import io.papermc.paper.registry.data.dialog.type.DialogListType;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import io.papermc.paper.registry.data.dialog.type.NoticeType;
import io.papermc.paper.registry.data.dialog.type.ServerLinksType;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.set.RegistryValueSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0011J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\r2\b\u0010\u0014\u001a\u0004\u0018\u00010\fJ\u0006\u0010\u0015\u001a\u00020\u0011J \u0010\u001d\u001a\u001a\u0012\u0004\u0012\u00020\f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u001f0\u001e0\u000bH\u0002J\u0014\u0010 \u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u0005H\u0007b\u0002\b!J\u0010\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%H\u0002J\u0010\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020%H\u0002R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R#\u0010\u0016\u001a\n \u0018*\u0004\u0018\u00010\u00170\u00178BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001b\u0010\u001c\u001a\u0004\b\u0019\u0010\u001aR&\u0010)\u001a\r\u0012\t\u0012\u00070\r¢\u0006\u0002\b+0*8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b.\u0010\u001c\u001a\u0004\b,\u0010-¨\u0006/"},
   d2 = {"Lcom/nexomc/nexo/dialog/NexoDialogs;", "", "<init>", "()V", "context", "Lio/papermc/paper/plugin/bootstrap/BootstrapContext;", "getContext", "()Lio/papermc/paper/plugin/bootstrap/BootstrapContext;", "setContext", "(Lio/papermc/paper/plugin/bootstrap/BootstrapContext;)V", "reloadedDialogs", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lnet/kyori/adventure/key/Key;", "Lio/papermc/paper/dialog/Dialog;", "needsReload", "", "markNeedsReload", "", "reloadDialogsIfNeeded", "reloadedDialog", "key", "reloadDialogs", "dialogDirectory", "Ljava/io/File;", "kotlin.jvm.PlatformType", "getDialogDirectory", "()Ljava/io/File;", "dialogDirectory$delegate", "Lkotlin/Lazy;", "parseDialogFiles", "Lkotlin/Function1;", "Lio/papermc/paper/registry/data/dialog/DialogRegistryEntry$Builder;", "registerDialogs", "Lkotlin/jvm/JvmStatic;", "dialogType", "Lio/papermc/paper/registry/data/dialog/type/DialogType;", "dialogConfig", "Lorg/bukkit/configuration/ConfigurationSection;", "dialogBase", "Lio/papermc/paper/registry/data/dialog/DialogBase;", "baseConfig", "dialogRegistry", "Lorg/bukkit/Registry;", "Lkotlin/jvm/internal/EnhancedNullability;", "getDialogRegistry", "()Lorg/bukkit/Registry;", "dialogRegistry$delegate", "com.nexomc:core"}
)
public final class NexoDialogs {
   @NotNull
   public static final NexoDialogs INSTANCE = new NexoDialogs();
   public static BootstrapContext context;
   @NotNull
   private static final Object2ObjectOpenHashMap<Key, Dialog> reloadedDialogs = new Object2ObjectOpenHashMap();
   private static boolean needsReload;
   @NotNull
   private static final Lazy dialogDirectory$delegate = LazyKt.lazy(NexoDialogs::dialogDirectory_delegate$lambda$0);
   @NotNull
   private static final Lazy dialogRegistry$delegate = LazyKt.lazy(NexoDialogs::dialogRegistry_delegate$lambda$0);

   private NexoDialogs() {
   }

   @NotNull
   public final BootstrapContext getContext() {
      BootstrapContext bootstrapContext = context;
      if (bootstrapContext != null) {
         return bootstrapContext;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("context");
         return null;
      }
   }

   public final void setContext(@NotNull BootstrapContext bootstrapContext) {
      context = bootstrapContext;
   }

   public final void markNeedsReload() {
      needsReload = true;
   }

   public final void reloadDialogsIfNeeded() {
      if (needsReload) {
         this.reloadDialogs();
      }

   }

   @Nullable
   public final Dialog reloadedDialog(@Nullable Key key) {
      return (Dialog)reloadedDialogs.get(key);
   }

   public final void reloadDialogs() {
      NexoDialogs nexoDialogs = this;

      Object $this$reloadDialogs_u24lambda_u240;
      try {
         NexoDialogs $this$reloadDialogs_u24lambda_u240_11 = nexoDialogs;
         int value = 0;
         needsReload = false;
         reloadedDialogs.clear();
         Map $this$forEach$iv = (Map)$this$reloadDialogs_u24lambda_u240_11.parseDialogFiles();

         for(Map.Entry element$iv : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)$this$forEach$iv.entrySet()) {
            int value2 = 0;
            Key dialogKey = (Key)element$iv.getKey();
            Function1 dialogBuilder = (Function1)element$iv.getValue();
            Dialog existingDialog = (Dialog)$this$reloadDialogs_u24lambda_u240_11.getDialogRegistry().get(dialogKey);
            Dialog dialog = Dialog.create((a0_ -> NexoDialogs.reloadDialogs$lambda$0$0$0(dialogBuilder, (RegistryBuilderFactory)a0_)));
            Dialog reloadedDialog = dialog;
            if (existingDialog == null) {
               Logs.logWarn("Skipped reloading of dialog " + dialogKey + " as it does not exist in the registry!", false);
               Logs.logWarn("If this was added after startup, you must restart the server once to use it.", false);
            } else {
               ((Map)reloadedDialogs).put(dialogKey, reloadedDialog);
            }
         }

         $this$reloadDialogs_u24lambda_u240 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         $this$reloadDialogs_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull($this$reloadDialogs_u24lambda_u240);
      if (throwable != null) {
         Throwable it = throwable;
         int value3 = 0;
         INSTANCE.getContext().getLogger().error("Error while loading Custom Nexo Dialogs", it);
      }

   }

   private final File getDialogDirectory() {
      Lazy lazy = dialogDirectory$delegate;
      return (File)lazy.getValue();
   }

   private final Object2ObjectOpenHashMap<Key, Function1<DialogRegistryEntry.Builder, DialogRegistryEntry.Builder>> parseDialogFiles() {
      NexoDialogs nexoDialogs = this;

      Object $this$parseDialogFiles_u24lambda_u240;
      try {
         NexoDialogs $this$parseDialogFiles_u24lambda_u240_10 = nexoDialogs;
         int value = 0;
         File dialogDirectory = $this$parseDialogFiles_u24lambda_u240_10.getDialogDirectory();
         Iterable $this$associateFastNotNull$iv = (Iterable)FileUtilsKt.listYamlFiles(dialogDirectory, true);
         Object2ObjectOpenHashMap result$iv = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(($this$associateFastNotNull$iv instanceof Collection ? (Collection)$this$associateFastNotNull$iv : null) != null ? ($this$associateFastNotNull$iv instanceof Collection ? (Collection)$this$associateFastNotNull$iv : null).size() : 10), 16));

         for(Object element$iv : $this$associateFastNotNull$iv) {
            File file = (File)element$iv;
            int value2 = 0;
            String str = FilesKt.getNameWithoutExtension(file).toLowerCase(Locale.ROOT);
            Key key = Key.key("nexo", str);
            Key dialogKey = key;

            Object $this$parseDialogFiles_u24lambda_u240_u240_u240;
            try {
               int value3 = 0;
               $this$parseDialogFiles_u24lambda_u240_u240_u240 = ResultShim.ok(YamlConfiguration.loadConfiguration(file));
            } catch (Throwable e) {
               $this$parseDialogFiles_u24lambda_u240_u240_u240 = ResultShim.ok(ResultKt.createFailure(e));
            }

            YamlConfiguration yamlConfiguration = (YamlConfiguration)(ResultShim.isFailure($this$parseDialogFiles_u24lambda_u240_u240_u240) ? null : $this$parseDialogFiles_u24lambda_u240_u240_u240);
            Pair pair;
            if (yamlConfiguration == null) {
               pair = null;
            } else {
               block: {
                  YamlConfiguration dialogConfig = yamlConfiguration;
                  ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)dialogConfig, "base");
                  if (configurationSection != null) {
                     ConfigurationSection p0 = configurationSection;
                     int value4 = 0;
                     DialogBase dialogBase2 = $this$parseDialogFiles_u24lambda_u240_10.dialogBase(p0);
                     if (dialogBase2 != null) {
                        DialogBase dialogBase = dialogBase2;
                        pair = TuplesKt.to(dialogKey, (Function1<DialogRegistryEntry.Builder, DialogRegistryEntry.Builder>)(b -> parseDialogFiles$lambda$0$0$1($this$parseDialogFiles_u24lambda_u240_10, dialogConfig, dialogBase, b)));
                        break block;
                     }
                  }

                  pair = null;
               }
            }

            if (pair != null) {
               Pair it$iv = pair;
               int value5 = 0;
               Object first = it$iv.getFirst();
               if (first != null) {
                  Object second = it$iv.getSecond();
                  if (second != null) {
                     Pair pair2 = TuplesKt.to(first, second);
                     if (pair2 != null) {
                        Pair pair3 = pair2;
                        Object key$iv = pair3.component1();
                        Object value$iv = pair3.component2();
                        ((Map)result$iv).put(key$iv, value$iv);
                     }
                  }
               }
            }
         }

         $this$parseDialogFiles_u24lambda_u240 = ResultShim.ok(result$iv);
      } catch (Throwable ex) {
         $this$parseDialogFiles_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = $this$parseDialogFiles_u24lambda_u240;
      Throwable throwable = ResultShim.exceptionOrNull($this$parseDialogFiles_u24lambda_u240);
      if (throwable != null) {
         Throwable it = throwable;
         int value6 = 0;
         INSTANCE.getContext().getLogger().error("Error while loading Custom Nexo Dialogs", it);
      }

      Object2ObjectOpenHashMap it = new Object2ObjectOpenHashMap();
      return (Object2ObjectOpenHashMap)(ResultShim.isFailure(obj) ? it : obj);
   }

   @JvmStatic
   public static final void registerDialogs(@NotNull final BootstrapContext context) {
      INSTANCE.setContext(context);
      final NexoDialogs $this$registerDialogs_u24lambda_u240 = INSTANCE;

      Object $this$registerDialogs_u24lambda_u240_9;
      try {
         int value = 0;
         context.getLifecycleManager().registerEventHandler(RegistryEvents.DIALOG.compose().newHandler(new LifecycleEventHandler<RegistryComposeEvent<Dialog, DialogRegistryEntry.Builder>>() {
            public final void run(RegistryComposeEvent<Dialog, DialogRegistryEntry.Builder> handler) {
               Map $this$forEach$iv = (Map)$this$registerDialogs_u24lambda_u240.parseDialogFiles();
               BootstrapContext bootstrapContext = context;

               for(Map.Entry element$iv : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)$this$forEach$iv.entrySet()) {
                  int value2 = 0;
                  Key dialogKey = (Key)element$iv.getKey();
                  Function1 dialogBuilder = (Function1)element$iv.getValue();

                  Object $this$run_u24lambda_u240_u240;
                  try {
                     int value3 = 0;
                     handler.registry().register(TypedKey.create(RegistryKey.DIALOG, dialogKey), b -> run$lambda$0$0$0(dialogBuilder, b));
                     $this$run_u24lambda_u240_u240 = ResultShim.ok(Unit.INSTANCE);
                  } catch (Throwable e) {
                     $this$run_u24lambda_u240_u240 = ResultShim.ok(ResultKt.createFailure(e));
                  }

                  Throwable throwable = ResultShim.exceptionOrNull($this$run_u24lambda_u240_u240);
                  if (throwable != null) {
                     Throwable it = throwable;
                     int value4 = 0;
                     bootstrapContext.getLogger().warn("Failed to register dialog " + dialogKey, it);
                  }
               }

            }

            private static final Unit run$lambda$0$0$0(Function1 $dialogBuilder, DialogRegistryEntry.Builder it) {
               $dialogBuilder.invoke(it);
               return Unit.INSTANCE;
            }

            private static final void run$lambda$0$0$1(Function1 $tmp0, Object p0) {
               $tmp0.invoke(p0);
            }
         }));
         $this$registerDialogs_u24lambda_u240_9 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex) {
         $this$registerDialogs_u24lambda_u240_9 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Throwable throwable2 = ResultShim.exceptionOrNull($this$registerDialogs_u24lambda_u240_9);
      if (throwable2 != null) {
         Throwable it = throwable2;
         int value5 = 0;
         context.getLogger().error("Error while loading Custom Nexo Dialogs", it);
      }

   }

   private final DialogType dialogType(ConfigurationSection dialogConfig) {
      String str = NexoYamlKt.stringOrNull(dialogConfig, "type");
      if (str != null) {
         DialogTypes dialogTypes = (DialogTypes)EnumUtils.INSTANCE.toEnumOrElse(str, DialogTypes.class, NexoDialogs::dialogType$lambda$0);
         if (dialogTypes != null) {
            DialogTypes type = dialogTypes;
            DialogType dialogType;
            switch (NexoDialogs.WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
               case 1:
                  ActionButton action = NexoActionButton.INSTANCE.createButton(NexoYamlKt.section(dialogConfig, "action"));
                  NoticeType noticeType = DialogType.notice(action);
                  dialogType = (DialogType)noticeType;
                  break;
               case 2:
                  ActionButton yesButton = NexoActionButton.INSTANCE.createButton(NexoYamlKt.section(dialogConfig, "yesButton"));
                  ActionButton noButton = NexoActionButton.INSTANCE.createButton(NexoYamlKt.section(dialogConfig, "noButton"));
                  ConfirmationType confirmationType = DialogType.confirmation(yesButton, noButton);
                  dialogType = (DialogType)confirmationType;
                  break;
               case 3:
                  ActionButton exitAction = NexoActionButton.INSTANCE.createButton(NexoYamlKt.section(dialogConfig, "exitAction"));
                  int columns = RangesKt.coerceAtLeast(dialogConfig.getInt("columns"), 1);
                  int buttonWidth = RangesKt.coerceIn(dialogConfig.getInt("buttonWidth", 150), 1, 1024);
                  ServerLinksType serverLinksType = DialogType.serverLinks(exitAction, columns, buttonWidth);
                  dialogType = (DialogType)serverLinksType;
                  break;
               case 4:
                  Iterable $this$map$iv = (Iterable)NexoYamlKt.sectionList(dialogConfig, "actions");
                  NexoActionButton exitAction_8 = NexoActionButton.INSTANCE;
                  Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

                  for(Object item$iv$iv : $this$map$iv) {
                     ConfigurationSection configurationSection = (ConfigurationSection)item$iv$iv;
                     int element$iv$iv$iv = 0;
                     destination$iv$iv.add(exitAction_8.createButton(configurationSection));
                  }

                  List actions = (List)destination$iv$iv;
                  ActionButton exitAction_7 = NexoActionButton.INSTANCE.createButton(NexoYamlKt.section(dialogConfig, "exitAction"));
                  int columns_6 = RangesKt.coerceAtLeast(dialogConfig.getInt("columns"), 2);
                  MultiActionType multiActionType = DialogType.multiAction(actions, exitAction_7, columns_6);
                  dialogType = (DialogType)multiActionType;
                  break;
               case 5:
                  Iterable noButton_5 = (Iterable)NexoYamlKt.keyList(dialogConfig, "dialogs");
                  Registry buttonWidth_4 = this.getDialogRegistry();
                  int $i$f$mapNotNull = 0;
                  Collection destination$iv$iv_3 = (Collection)(new ArrayList());

                  for(Object element$iv$iv$iv : noButton_5) {
                     int value = 0;
                     Key p0 = (Key)element$iv$iv$iv;
                     int value2 = 0;
                     Dialog dialog = (Dialog)buttonWidth_4.get(p0);
                     if (dialog != null) {
                        Object it$iv$iv = dialog;
                        int value3 = 0;
                        destination$iv$iv_3.add(it$iv$iv);
                     }
                  }

                  List dialogs = (List)destination$iv$iv_3;
                  RegistryValueSet registryValueSet = RegistrySet.valueSet(RegistryKey.DIALOG, (Iterable)dialogs);
                  RegistryValueSet registrySet = registryValueSet;
                  ActionButton exitAction_2 = NexoActionButton.INSTANCE.createButton(NexoYamlKt.section(dialogConfig, "exitAction"));
                  $i$f$mapNotNull = RangesKt.coerceAtLeast(dialogConfig.getInt("columns"), 2);
                  int buttonWidth_1 = RangesKt.coerceIn(dialogConfig.getInt("buttonWidth", 150), 1, 1024);
                  DialogListType dialogListType = DialogType.dialogList((RegistrySet)registrySet, exitAction_2, $i$f$mapNotNull, buttonWidth_1);
                  dialogType = (DialogType)dialogListType;
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }

            return dialogType;
         }
      }

      NoticeType notice = DialogType.notice();
      return (DialogType)notice;
   }

   private final DialogBase dialogBase(ConfigurationSection baseConfig) {
      java.util.List list;
      java.util.List list2;
      Component richMessage = baseConfig.getRichMessage("title");
      if (richMessage == null) {
         TextComponent textComponent = Component.empty();
         richMessage = (Component)textComponent;
      }

      Component title = richMessage;
      Component externalTitle = baseConfig.getRichMessage("externalTitle");
      boolean canCloseWithEscape = baseConfig.getBoolean("canCloseWithEscape");
      boolean pause = baseConfig.getBoolean("pause");
      String key$iv = "afterAction";
      DialogBase.DialogAfterAction dialogAfterAction = (DialogBase.DialogAfterAction)org.apache.commons.lang3.EnumUtils.getEnum(DialogBase.DialogAfterAction.class, NexoYamlKt.stringOrNull(baseConfig, key$iv));
      if (dialogAfterAction == null) {
         dialogAfterAction = DialogAfterAction.CLOSE;
      }

      DialogBase.DialogAfterAction afterAction;
      block: {
         afterAction = dialogAfterAction;
         ConfigurationSection configurationSection = NexoYamlKt.section(baseConfig, "bodies");
         if (configurationSection != null) {
            Map map = NexoYamlKt.childSections(configurationSection);
            if (map != null) {
               Collection collection = map.values();
               if (collection != null) {
                  Iterable $this$map$iv = (Iterable)collection;
                  Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

                  for(Object item$iv$iv : $this$map$iv) {
                     ConfigurationSection it = (ConfigurationSection)item$iv$iv;
                     int element$iv$iv = 0;
                     destination$iv$iv.add((new NexoDialogBody(it)).createDialogBody());
                  }

                  list2 = (List)destination$iv$iv;
                  break block;
               }
            }
         }

         list2 = CollectionsKt.emptyList();
      }

      List body;
      block: {
         body = list2;
         ConfigurationSection configurationSection2 = NexoYamlKt.section(baseConfig, "inputs");
         if (configurationSection2 != null) {
            Map map2 = NexoYamlKt.childSections(configurationSection2);
            if (map2 != null) {
               Map $this$mapNotNull$iv = map2;
               int $i$f$mapNotNull = 0;
               Collection destination$iv$iv = (Collection)(new ArrayList());

               for(Map.Entry element$iv$iv$iv : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)$this$mapNotNull$iv.entrySet()) {
                  int value = 0;
                  int value2 = 0;
                  DialogInput createDialogInput = (new NexoDialogInput((String)element$iv$iv$iv.getKey(), (ConfigurationSection)element$iv$iv$iv.getValue())).createDialogInput();
                  if (createDialogInput != null) {
                     Object it$iv$iv = createDialogInput;
                     int value3 = 0;
                     destination$iv$iv.add(it$iv$iv);
                  }
               }

               list = (List)destination$iv$iv;
               break block;
            }
         }

         list = CollectionsKt.emptyList();
      }

      List inputs = list;
      DialogBase dialogBase = DialogBase.create(title, externalTitle, canCloseWithEscape, pause, afterAction, body, inputs);
      return dialogBase;
   }

   @NotNull
   public final Registry<Dialog> getDialogRegistry() {
      Lazy lazy = dialogRegistry$delegate;
      return (Registry)lazy.getValue();
   }

   private static final void reloadDialogs$lambda$0$0$0(Function1 $dialogBuilder, RegistryBuilderFactory it) {
      RegistryBuilder registryBuilder = it.empty();
      $dialogBuilder.invoke(registryBuilder);
   }

   private static final File dialogDirectory_delegate$lambda$0() {
      File $this$dialogDirectory_delegate_u24lambda_u240_u240 = INSTANCE.getContext().getDataDirectory().resolve("dialogs").toFile();
      int value = 0;
      $this$dialogDirectory_delegate_u24lambda_u240_u240.mkdirs();
      return $this$dialogDirectory_delegate_u24lambda_u240_u240;
   }

   private static final DialogRegistryEntry.Builder parseDialogFiles$lambda$0$0$1(NexoDialogs $this_runCatching, YamlConfiguration $dialogConfig, DialogBase $dialogBase, DialogRegistryEntry.Builder builder) {
      return builder.type($this_runCatching.dialogType((ConfigurationSection)$dialogConfig)).base($dialogBase);
   }

   private static final DialogTypes dialogType$lambda$0(String it) {
      return DialogTypes.NOTICE;
   }

   private static final Registry dialogRegistry_delegate$lambda$0() {
      return RegistryAccess.registryAccess().getRegistry(RegistryKey.DIALOG);
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.dialog.DialogTypes.values().length];
         try { a_0[com.nexomc.nexo.dialog.DialogTypes.NOTICE.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogTypes.CONFIRM.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogTypes.LINK.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogTypes.MULTI.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.dialog.DialogTypes.LIST.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }

   public static final Object2ObjectOpenHashMap<Key, Function1<DialogRegistryEntry.Builder, DialogRegistryEntry.Builder>> access$parseDialogFiles(NexoDialogs $this) { return $this.parseDialogFiles(); }
}
