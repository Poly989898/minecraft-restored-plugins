package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PunishmentRequest {
   private PunishmentType type;
   private boolean ipBan;
   private boolean silent;
   private boolean extraSilent;
   private boolean temporary;
   private boolean modify;
   private boolean delete;
   private boolean override;
   private boolean noQueue;
   private boolean customSender;
   private boolean confirmed;
   private boolean skip;
   private boolean hidden;
   private boolean flag15;
   private final long longValue;
   private long until;
   private long duration;
   private byte templateId;
   private Template template;
   private TemplateGroup templateGroup;
   private boolean flag16;
   private TemplateInfo templateInfo;
   private int intValue;
   private CharSequence durationText;
   private LoginRecord targetRecord;
   private Punishment punishment;
   private Punishment existingPunishment;
   private final LiteSender sender;
   private String executorName;
   private String executorUuid;
   private String serverScope;
   private String serverOrigin;
   private String reason;
   private final StringBuilder reasonBuilder;
   private final String[] args;
   private final ArrayList arrayList;
   private final List list3;
   private PunishmentStage punishmentStage;
   private final LiteHelper2 liteHelper;

   public PunishmentRequest(
      @NotNull PunishmentType punishmentType,
   boolean flag,
   boolean flag2,
   boolean flag3,
   boolean flag4,
   boolean flag5,
   boolean flag6,
   boolean flag7,
   boolean flag8,
   boolean flag9,
   boolean flag10,
   boolean flag11,
   boolean flag12,
   boolean flag13,
   long value,
   long value2,
   long value3,
   byte b,
   @NotNull Template template,
   @Nullable TemplateGroup templateGroup,
   boolean flag14,
   @NotNull TemplateInfo templateInfo,
   int value4,
   @NotNull CharSequence message,
   @Nullable LoginRecord loginRecord,
   @Nullable Punishment punishment,
   @Nullable Punishment punishment2,
   @NotNull LiteSender liteSender,
   @NotNull String text,
   @NotNull String text2,
   @NotNull String text3,
   @Nullable String text4,
   @NotNull String text5,
   @NotNull StringBuilder builder,
   @NotNull String[] texts,
   @NotNull ArrayList list,
   @NotNull List list2
   ) {
      this.type = punishmentType;
      this.ipBan = flag;
      this.silent = flag2;
      this.extraSilent = flag3;
      this.temporary = flag4;
      this.modify = flag5;
      this.delete = flag6;
      this.override = flag7;
      this.noQueue = flag8;
      this.customSender = flag9;
      this.confirmed = flag10;
      this.skip = flag11;
      this.hidden = flag12;
      this.flag15 = flag13;
      this.longValue = value;
      this.until = value2;
      this.duration = value3;
      this.templateId = b;
      this.template = template;
      this.templateGroup = templateGroup;
      this.flag16 = flag14;
      this.templateInfo = templateInfo;
      this.intValue = value4;
      this.durationText = message;
      this.targetRecord = loginRecord;
      this.punishment = punishment;
      this.existingPunishment = punishment2;
      this.sender = liteSender;
      this.executorName = text;
      this.executorUuid = text2;
      this.serverScope = text3;
      this.serverOrigin = text4;
      this.reason = text5;
      this.reasonBuilder = builder;
      this.args = texts;
      this.arrayList = list;
      this.list3 = list2;
      this.punishmentStage = PunishmentStage.INIT;
      this.liteHelper = new LiteHelper2(this);
      LiteHelper2.handleLiteHelper(this.liteHelper, null, false, false, 7, null);
   }

   public PunishmentRequest(
      PunishmentType punishmentType,
      boolean flag,
      boolean flag2,
      boolean flag3,
      boolean flag4,
      boolean flag5,
      boolean flag6,
      boolean flag7,
      boolean flag8,
      boolean flag9,
      boolean flag10,
      boolean flag11,
      boolean flag12,
      boolean flag13,
      long value,
      long value2,
      long value3,
      byte b,
      Template template,
      TemplateGroup templateGroup,
      boolean flag14,
      TemplateInfo templateInfo,
      int value4,
      CharSequence message,
      LoginRecord loginRecord,
      Punishment punishment,
      Punishment punishment2,
      LiteSender liteSender,
      String text,
      String text2,
      String text3,
      String text4,
      String text5,
      StringBuilder builder,
      String[] texts,
      ArrayList list,
      List list2,
      int value5,
      int value6,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value5 & 2) != 0) {
         flag = false;
      }

      if ((value5 & 4) != 0) {
         flag2 = false;
      }

      if ((value5 & 8) != 0) {
         flag3 = false;
      }

      if ((value5 & 16) != 0) {
         flag4 = false;
      }

      if ((value5 & 32) != 0) {
         flag5 = false;
      }

      if ((value5 & 64) != 0) {
         flag6 = false;
      }

      if ((value5 & 128) != 0) {
         flag7 = false;
      }

      if ((value5 & 256) != 0) {
         flag8 = false;
      }

      if ((value5 & 512) != 0) {
         flag9 = false;
      }

      if ((value5 & 1024) != 0) {
         flag10 = false;
      }

      if ((value5 & 2048) != 0) {
         flag11 = false;
      }

      if ((value5 & 4096) != 0) {
         flag12 = false;
      }

      if ((value5 & 8192) != 0) {
         flag13 = false;
      }

      if ((value5 & 32768) != 0) {
         value2 = -1L;
      }

      if ((value5 & 65536) != 0) {
         value3 = 0L;
      }

      if ((value5 & 131072) != 0) {
         b = 0;
      }

      if ((value5 & 262144) != 0) {
         template = Template.TEMPLATE_COMPANION.obtainTemplate();
      }

      if ((value5 & 524288) != 0) {
         templateGroup = null;
      }

      if ((value5 & 1048576) != 0) {
         flag14 = false;
      }

      if ((value5 & 2097152) != 0) {
         templateInfo = Template.TEMPLATE_COMPANION.obtainTemplateInfo();
      }

      if ((value5 & 4194304) != 0) {
         value4 = -1;
      }

      if ((value5 & 8388608) != 0) {
         message = Message.FOREVER;
      }

      if ((value5 & 16777216) != 0) {
         loginRecord = null;
      }

      if ((value5 & 33554432) != 0) {
         punishment = null;
      }

      if ((value5 & 67108864) != 0) {
         punishment2 = null;
      }

      if ((value5 & 536870912) != 0) {
         text2 = liteSender.isPlayer() ? liteSender.getUuidString() : liteSender.getName();
      }

      if ((value6 & 1) != 0) {
         text5 = "";
      }

      if ((value6 & 2) != 0) {
         builder = new StringBuilder();
      }

      if ((value6 & 4) != 0) {
         texts = new String[0];
      }

      if ((value6 & 8) != 0) {
         list = new ArrayList(texts.length);
      }

      if ((value6 & 16) != 0) {
         list2 = new ArrayList();
      }

      this(
         punishmentType,
         flag,
         flag2,
         flag3,
         flag4,
         flag5,
         flag6,
         flag7,
         flag8,
         flag9,
         flag10,
         flag11,
         flag12,
         flag13,
         value,
         value2,
         value3,
         b,
         template,
         templateGroup,
         flag14,
         templateInfo,
         value4,
         message,
         loginRecord,
         punishment,
         punishment2,
         liteSender,
         text,
         text2,
         text3,
         text4,
         text5,
         builder,
         texts,
         list,
         list2
      );
   }

   public final PunishmentType getType() {
      return this.type;
   }

   public final void setType(@NotNull PunishmentType punishmentType) {
      this.type = punishmentType;
   }

   public final boolean isIpBan() {
      return this.ipBan;
   }

   public final void setIpBan(boolean flag) {
      this.ipBan = flag;
   }

   public final boolean isSilent() {
      return this.silent;
   }

   public final void setSilent(boolean flag) {
      this.silent = flag;
   }

   public final boolean isExtraSilent() {
      return this.extraSilent;
   }

   public final void setExtraSilent(boolean flag) {
      this.extraSilent = flag;
   }

   public final boolean isTemporary() {
      return this.temporary;
   }

   public final void setTemporary(boolean flag) {
      this.temporary = flag;
   }

   public final boolean isModify() {
      return this.modify;
   }

   public final void setModify(boolean flag) {
      this.modify = flag;
   }

   public final boolean isDelete() {
      return this.delete;
   }

   public final void setDelete(boolean flag) {
      this.delete = flag;
   }

   public final boolean isOverride() {
      return this.override;
   }

   public final void setOverride(boolean flag) {
      this.override = flag;
   }

   public final boolean isNoQueue() {
      return this.noQueue;
   }

   public final void setNoQueue(boolean flag) {
      this.noQueue = flag;
   }

   public final boolean isCustomSender() {
      return this.customSender;
   }

   public final void setCustomSender(boolean flag) {
      this.customSender = flag;
   }

   public final boolean isConfirmed() {
      return this.confirmed;
   }

   public final void setConfirmed(boolean flag) {
      this.confirmed = flag;
   }

   public final boolean isSkip() {
      return this.skip;
   }

   public final void setSkip(boolean flag) {
      this.skip = flag;
   }

   public final void setHidden(boolean flag) {
      this.hidden = flag;
   }

   public final long getCreationTime() {
      return this.longValue;
   }

   public final long getUntil() {
      return this.until;
   }

   public final void setUntil(long value) {
      this.until = value;
   }

   public final long getDuration() {
      return this.duration;
   }

   public final void setDuration(long value) {
      this.duration = value;
   }

   public final byte getTemplateId() {
      return this.templateId;
   }

   public final void setTemplateId(byte b) {
      this.templateId = b;
   }

   public final Template getTemplate() {
      return this.template;
   }

   public final void setTemplate(@NotNull Template template) {
      this.template = template;
   }

   public final TemplateGroup getTemplateGroup() {
      return this.templateGroup;
   }

   public final void setTemplateGroup(@Nullable TemplateGroup templateGroup) {
      this.templateGroup = templateGroup;
   }

   public final boolean isFlag16() {
      return this.flag16;
   }

   public final void setFlag16(boolean flag) {
      this.flag16 = flag;
   }

   public final TemplateInfo getTemplateInfo() {
      return this.templateInfo;
   }

   public final void setTemplateInfo(@NotNull TemplateInfo templateInfo) {
      this.templateInfo = templateInfo;
   }

   public final int getStackIndex() {
      return this.intValue;
   }

   public final void setStackIndex(int value) {
      this.intValue = value;
   }

   public final CharSequence getDurationText() {
      return this.durationText;
   }

   public final void setDurationText(@NotNull CharSequence message) {
      this.durationText = message;
   }

   public final LoginRecord getTargetRecord() {
      return this.targetRecord;
   }

   public final void setTargetRecord(@Nullable LoginRecord loginRecord) {
      this.targetRecord = loginRecord;
   }

   public final Punishment getPunishment() {
      return this.punishment;
   }

   public final void setPunishment(@Nullable Punishment punishment) {
      this.punishment = punishment;
   }

   public final Punishment getExistingPunishment() {
      return this.existingPunishment;
   }

   public final void setExistingPunishment(@Nullable Punishment punishment) {
      this.existingPunishment = punishment;
   }

   public final LiteSender getSender() {
      return this.sender;
   }

   public final String getExecutorName() {
      return this.executorName;
   }

   public final void setExecutorName(@NotNull String text) {
      this.executorName = text;
   }

   public final String getExecutorUuid() {
      return this.executorUuid;
   }

   public final void setExecutorUuid(@NotNull String text) {
      this.executorUuid = text;
   }

   public final String getServerScope() {
      return this.serverScope;
   }

   public final void setServerScope(@NotNull String text) {
      this.serverScope = text;
   }

   public final String getServerOrigin() {
      return this.serverOrigin;
   }

   public final void setServerOrigin(@Nullable String text) {
      this.serverOrigin = text;
   }

   public final String getReason() {
      return this.reason;
   }

   public final void setReason(@NotNull String text) {
      this.reason = text;
   }

   public final StringBuilder getReasonBuilder() {
      return this.reasonBuilder;
   }

   public final String[] getArgs() {
      return this.args;
   }

   public final ArrayList getExtraArgs() {
      return this.arrayList;
   }

   public final void fireStage(@NotNull PunishmentStage punishmentStage) {
      this.punishmentStage = punishmentStage;
      Iterable iterable = this.list3;

      for (Object obj : iterable) {
         Consumer consumer = (Consumer)obj;
         consumer.accept(this);
      }

      if (this.punishmentStage == PunishmentStage.CANCELLED) {
         CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
         commandOutput.returnResult("");
         throw new CommandAbortException();
      }
   }

   public final boolean hasTemplate() {
      Template template = this.template;
      return Intrinsics.compare(template.getId() & 255, -1 & 0xFF) < 0;
   }

   public final boolean isUndo() {
      boolean flag;
      switch (PunishmentTypeSwitchMap4.value[this.type.ordinal()]) {
         case 1:
         case 2:
         case 3:
            flag = true;
            break;
         default:
            flag = false;
      }

      return flag;
   }

   public final void applyTemplate(@NotNull DatabaseSession databaseSession) {
      List list = this.templateInfo.getFlags();
      if (list.isEmpty()) {
         list = this.template.getDefaultInfo().getFlags();
      }

      if (!list.isEmpty()) {
         Collection collection = list;
         this.liteHelper.handleTexts((String[]) collection.toArray(new String[0]), false, true);
         this.handleDatabaseSession(databaseSession, CollectionsKt.toMutableList(list), false);
      }
   }

   public final void toUndoType() {
      PunishmentType punishmentType;
      switch (PunishmentTypeSwitchMap4.value[this.type.ordinal()]) {
         case 4:
            punishmentType = PunishmentType.UNBAN;
            break;
         case 5:
            punishmentType = PunishmentType.UNMUTE;
            break;
         case 6:
            punishmentType = PunishmentType.UNWARN;
            break;
         default:
            punishmentType = this.type;
      }

      this.type = punishmentType;
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull String text, @NotNull String text2) {
      boolean flag = this.checkDatabaseSession(databaseSession, text, text2);
      if (!this.isUndo() && flag && !this.checkDatabaseSession(databaseSession, text2)) {
         PunishmentType punishmentType = this.type;
         CharSequence message;
         switch (PunishmentTypeSwitchMap32.value[punishmentType.obtainPunishmentType().ordinal()]) {
            case 1:
               message = Message.BAN_EXEMPT;
               break;
            case 2:
               message = Message.EXEMPT;
               break;
            case 3:
               message = Message.EXEMPT_2;
               break;
            case 4:
               message = Message.EXEMPT_3;
               break;
            default:
               StandardKt.notImplemented();
               throw new CommandAbortException();
         }

         this.computeVoid(databaseSession, message);
         throw new CommandAbortException();
      }
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender, @NotNull Punishment punishment, @Nullable LoginRecord loginRecord) {
      boolean flag = false;
      boolean flag2 = false;
      Punishment punishment2 = punishment;
      ConfigModule module = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      long time = punishment2.getTime();
      if (liteSender.isConsole()) {
         long durationsConsoleCooldown = module.getSettings().getDurationsConsoleCooldown();
         if (durationsConsoleCooldown > 0L) {
            AltAccountPunisher.handleDatabaseSession(databaseSession, databaseSession, punishment2, time, durationsConsoleCooldown, 0L, loginRecord, false);
         }
      } else {
         Iterable iterable = module.getSettings().getLimitGroups();
         List sortedWith2 = CollectionsKt.sortedWith((Iterable)iterable, new KotlinComparatorImpl3(punishment));
         if (module.isVerbose()) {
            module.logDebug(sortedWith2);
         }

         Iterable iterable2 = sortedWith2;
         Iterator iterator2 = iterable2.iterator();

         Object obj;
         while (true) {
            if (iterator2.hasNext()) {
               Object obj2 = iterator2.next();
               DurationLimitGroup kotlinData6 = (DurationLimitGroup)obj2;
               if (module.isVerbose()) {
                  StringBuilder builder = new StringBuilder().append("for ").append(kotlinData6.getName()).append(' ');
                  LiteSender liteSender2 = liteSender;
                  String text = kotlinData6.getName();
                  module.logDebug(builder.append(liteSender2.hasPermission(text)).toString());
               }

               label66: {
                  if (!Intrinsics.areEqual(kotlinData6.getName(), "none")) {
                     LiteSender liteSender3 = liteSender;
                     String text2 = kotlinData6.getName();
                     if (!liteSender3.hasPermission(text2)) {
                        flag = false;
                        break label66;
                     }
                  }

                  flag = true;
               }

               if (!flag) {
                  continue;
               }

               obj = obj2;
               break;
            }

            obj = null;
            break;
         }

         DurationLimitGroup kotlinData62 = (DurationLimitGroup)obj;
         if (kotlinData62 != null) {
            long value = kotlinData62.getCooldowns().computeLongValue(punishment);
            byte b = 5;
            if (module.isDebugAtLeast(b)) {
               StringBuilder builder2 = new StringBuilder().append(liteSender.getName()).append("'s ").append(punishment2.getType()).append(" cooldown is ");
               module.logDebug(builder2.append(Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value, false, 2, null)).append('.').toString());
            }

            if (value > 0L) {
               LiteSender liteSender4 = liteSender;
               String[] texts = new String[]{"litebans.cooldown.bypass", "litebans.cooldown.bypass." + punishment2.getType()};
               String[] copyOf2 = Arrays.copyOf(texts, texts.length);
               int value2 = 0;
               int length2 = copyOf2.length;

               while (true) {
                  if (value2 >= length2) {
                     flag2 = false;
                     break;
                  }

                  String text3 = copyOf2[value2];
                  if (liteSender4.hasPermission(text3)) {
                     flag2 = true;
                     break;
                  }

                  value2++;
               }

               if (!flag2) {
                  AltAccountPunisher.handleDatabaseSession(databaseSession, databaseSession, punishment2, time, value, kotlinData62.getCooldowns().getLongValue5(), loginRecord, false, 64, null);
               }
            }
         }
      }
   }

   public final boolean isExempt(@NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender) {
      return this.checkDatabaseSession(databaseSession, liteSender.getName(), liteSender.getUuidString());
   }

   public final boolean checkDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull String text, @NotNull String text2) {
      boolean flag = false;
      BroadcastModule module = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      ConfigModule module2 = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      String text3 = text2;
      String string = this.type.toString();
      String normalize2 = TargetParser.normalize(text3);
      if (module2.getStateFlags().isFlag4() || !module2.getSettings().isExemptEnable()) {
         module2.debug(1, "Exempting is disabled.");
         return false;
      }

      if (this.checkDatabaseSession(databaseSession, text, normalize2, this.type)) {
         return true;
      }

      byte b2 = 1;
      if (module2.isDebugAtLeast(b2)) {
         module2.logDebug(text + " is not in exempt_players.");
      }

      boolean flag2 = false;
      Object luckPerms = module.getLuckPerms();
      byte platformId = databaseSession.getPlatform().getPlatformId();
      if (platformId == 0) {

         UUID uuid;
         try {
            uuid = UUID.fromString(normalize2);
         } catch (IllegalArgumentException e) {
            module2.handleObj(e, 0);
            return false;
         }

         PlatformBridge bridge = databaseSession.getPlatform().createBridge();
         Intrinsics.checkNotNullExpressionValue(bridge, "");
         BukkitBridge bukkitBridge = (BukkitBridge)bridge;
         flag2 = bukkitBridge.getVault().getPermission() != null && bukkitBridge.getVault().checkText(normalize2, uuid, string, "litebans.exempt", module2.getSettings().getExemptPermissionWorld(), module2.getSettings().getExemptExemptGroups());
      } else {
         if (platformId != 3 && platformId != 1 && platformId != 2) {
            StandardKt.notImplemented();
            throw new CommandAbortException();
         }

         LiteSender onlineSender = module.findOnlineSender(normalize2);
         if (onlineSender != null) {
            flag2 = onlineSender.hasPermission("litebans.exempt") || onlineSender.hasPermission("litebans.exempt." + string);
         } else if (databaseSession.getPlatform().getPlatformId() == 1) {
            PlatformBridge bridge2 = databaseSession.getPlatform().createBridge();
            Intrinsics.checkNotNullExpressionValue(bridge2, "");
            BungeeBridge bungeeBridge = (BungeeBridge)bridge2;
            String[] exemptExemptGroups = module2.getSettings().getExemptExemptGroups();
            int value = 0;
            int length2 = exemptExemptGroups.length;

            while (true) {
               if (value >= length2) {
                  flag = false;
                  break;
               }

               String text4 = exemptExemptGroups[value];
               if (bungeeBridge.checkText(text, normalize2, text4)) {
                  flag = true;
                  break;
               }

               value++;
            }

            boolean flag3 = flag;
            flag2 = flag3 || bungeeBridge.checkText2(text, normalize2, "litebans.exempt") || bungeeBridge.checkText2(text, normalize2, "litebans.exempt." + string);
         }
      }

      if (!flag2 && luckPerms != null) {
         Object obj = LiteHelper.LITE_HELPER.loadUser(luckPerms, normalize2);
         if (obj != null) {
            flag2 = LiteHelper.LITE_HELPER.hasPermission(obj, "litebans.exempt") || LiteHelper.LITE_HELPER.hasPermission(obj, "litebans.exempt." + string);
         }
      }

      byte b3 = 1;
      if (module2.isDebugAtLeast(b3)) {
         module2.logDebug(text + " exempt? " + flag2);
      }

      return flag2;
   }

   public final boolean checkDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull String text, @NotNull String text2, @NotNull PunishmentType punishmentType) {
      ConfigModule module = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      String string = punishmentType.toString();
      String[] exemptExemptPlayers = module.getSettings().getExemptExemptPlayers();
      int value = 0;

      for (int i = exemptExemptPlayers.length; value < i; value++) {
         String text3 = exemptExemptPlayers[value];
         CharSequence message = text3;
         char[] chars = new char[]{':'};
         Collection split = StringsKt.splitWithDefaults(message, chars, false, 2, 2, null);
         String[] array = (String[])split.toArray(new String[0]);
         String text4 = array[0];
         if ((Intrinsics.areEqual(text4, text2) || StringsKt.checkText(text4, text, true)) && (array.length == 1 || StringsKt.checkText(array[1], string, true))) {
            byte b = 1;
            if (module.isDebugAtLeast(b)) {
               module.logDebug(text + " exempt? true");
            }

            return true;
         }
      }

      return false;
   }

   public final boolean checkDatabaseSession(@NotNull DatabaseSession databaseSession, @Nullable String text) {
      BroadcastModule module = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      ConfigModule module2 = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      if (this.sender.isConsole()) {
         return true;
      }

      if (this.sender.isPlayer()) {
         if (module2.getSettings().isExemptUseGroupWeights()) {
            Object luckPerms = module.getLuckPerms();
            if (luckPerms != null && text != null) {
               Object obj = LiteHelper.LITE_HELPER.loadUser(luckPerms, this.sender.getUuidString());
               Object obj2 = LiteHelper.LITE_HELPER.loadUser(luckPerms, text);
               if (obj != null && obj2 != null) {
                  int value = LiteHelper.LITE_HELPER.getGroupWeight(luckPerms, obj);
                  int value2 = LiteHelper.LITE_HELPER.getGroupWeight(luckPerms, obj2);
                  if (value >= value2) {
                     byte b = 1;
                     if (module2.isDebugAtLeast(b)) {
                        module2.logDebug("Group weight of " + this.sender.getName() + " (" + value + ") >= target weight of " + value2);
                     }

                     if (value == value2 && !module2.getSettings().isExemptPermitSameWeight()) {
                        return false;
                     }

                     return true;
                  }

                  byte b2 = 1;
                  if (module2.isDebugAtLeast(b2)) {
                     module2.logDebug("Group weight of " + this.sender.getName() + " (" + value + ") does not exceed " + value2);
                  }

                  return false;
               }

               module2.debug(1, "Missing user!");
            }
         }

         if (!module2.getSettings().isAllowExemptBypass()) {
            byte b3 = 1;
            if (module2.isDebugAtLeast(b3)) {
               module2.logDebug("allow_exempt_bypass is false.");
            }

            return false;
         }
      }

      LiteSender liteSender = this.sender;
      String[] texts = new String[]{"litebans.exempt.bypass", "litebans.exempt.bypass." + this.type};
      int value3 = 0;
      int length2 = texts.length;

      boolean flag;
      while (true) {
         if (value3 >= length2) {
            flag = false;
            break;
         }

         String text2 = texts[value3];
         if (liteSender.hasPermission(text2)) {
            flag = true;
            break;
         }

         value3++;
      }

      boolean flag2 = flag;
      if (flag2) {
         byte b4 = 1;
         if (module2.isDebugAtLeast(b4)) {
            module2.logDebug(this.sender.getName() + " bypasses exempt permissions.");
         }
      }

      return flag2;
   }

   public final void sendToSender(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message) {
      LiteSender liteSender = (LiteSender)CommandTask.COMMAND_OUTPUT.getSenderHolder().get();
      if (liteSender != null) {
         CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, message, this.punishment, this.targetRecord, false, 4, null), null, 4, null);
      }
   }

   public final Void computeVoid(@NotNull DatabaseSession databaseSession, @Nullable Object obj) {
      CommandTask.COMMAND_OUTPUT.returnResult(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, String.valueOf(obj), this.punishment, this.targetRecord, false, 4, null));
      throw new CommandAbortException();
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull List list, boolean flag) {
      DatabaseModule module = (DatabaseModule)databaseSession.getPlatform().getModule(DatabaseModule.class);
      ServerScopes scopes = ServerScopes.SERVER_SCOPE.parseScopes(this.sender, list, module.getServerName(), flag);
      CharSequence message = scopes.getScope();
      String text = message == null || message.length() == 0 ? null : scopes.getScope();
      if (text == null) {
         text = this.serverScope;
      }

      this.serverScope = text;
      this.serverScope = ServerScopes.SERVER_SCOPE.resolveScope(this.serverScope, module.getServerName(), this.sender);
   }

   public static void handlePunishmentRequest(PunishmentRequest punishmentRequest, DatabaseSession databaseSession, List list, boolean flag, int value, Object obj) {
      if ((value & 1) != 0) {
         list = punishmentRequest.arrayList;
      }

      if ((value & 2) != 0) {
         flag = true;
      }

      punishmentRequest.handleDatabaseSession(databaseSession, list, flag);
   }

   public final void broadcastPunishment(@NotNull DatabaseSession databaseSession) {
      ConfigModule module = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      Settings settings = module.getSettings();
      PunishmentType punishmentType = this.type.obtainPunishmentType();
      LiteSender liteSender = this.sender;
      String[] texts = new String[]{"litebans.unlimited", "litebans.unlimited." + punishmentType, "litebans.group.unlimited"};
      String[] copyOf2 = Arrays.copyOf(texts, texts.length);
      int value = 0;
      int length2 = copyOf2.length;

      boolean flag;
      while (true) {
         if (value >= length2) {
            flag = false;
            break;
         }

         String text = copyOf2[value];
         if (liteSender.hasPermission(text)) {
            flag = true;
            break;
         }

         value++;
      }

      if (!flag) {
         if (!this.isUndo()) {
            var list = settings.getLimitGroups();
            if (list instanceof Collection && ((Collection)list).isEmpty()) {
               flag = false;
            } else {
               Iterator iterator2 = list.iterator();

               while (true) {
                  if (!iterator2.hasNext()) {
                     flag = false;
                     break;
                  }

                  Object obj = iterator2.next();
                  DurationLimitGroup kotlinData6 = (DurationLimitGroup)obj;
                  if (kotlinData6.getGroupLimitOther().isTemplateOnly()) {
                     flag = true;
                     break;
                  }
               }
            }

            if (flag) {
               CommandOutput commandOutput;
               label92: {
                  Iterable iterable = settings.getLimitGroups();
                  List sortedWith2 = CollectionsKt.sortedWith((Iterable)iterable, new KotlinComparatorImpl());
                  DurationLimitGroup kotlinData = KotlinUtil16.computeKotlinData(sortedWith2, this.sender);
                  commandOutput = CommandTask.COMMAND_OUTPUT;
                  if (kotlinData != null) {
                     GroupLimitOther groupLimitOther = kotlinData.getGroupLimitOther();
                     if (groupLimitOther != null) {
                        flag = groupLimitOther.isTemplateOnly();
                        break label92;
                     }
                  }

                  flag = false;
               }

               label87: {
                  if (flag) {
                     Template template = this.template;
                     if (Intrinsics.compare(template.getId() & 255, -1 & 0xFF) >= 0) {
                        flag = true;
                        break label87;
                     }
                  }

                  flag = false;
               }

               boolean flag2 = flag;
               CharSequence message = Message.ERROR_NO_TEMPLATE_PROVIDED;
               CommandOutput commandOutput2 = commandOutput;
               if (flag2) {
                  commandOutput2.returnResult(message);
                  throw new CommandAbortException();
               }
            }
         }

         if (punishmentType == PunishmentType.BAN || punishmentType == PunishmentType.MUTE) {
            Iterable iterable2 = settings.getLimitGroups();
            List sortedWith3 = CollectionsKt.sortedWith((Iterable)iterable2, new KotlinComparatorImpl6(punishmentType));
            if (module.isVerbose()) {
               module.logDebug(sortedWith3);
            }

            DurationLimitGroup kotlinData2 = KotlinUtil16.computeKotlinData(sortedWith3, this.sender);
            if (kotlinData2 == null) {
               return;
            }

            long value2 = kotlinData2.getDurations().computeLongValue(punishmentType);
            if (value2 > 0L && value2 < Long.MAX_VALUE) {
               long value3 = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null) + value2;
               if (this.until > value3 || this.until <= 0L) {
                  if (!settings.isDurationsReduceToLimit()) {
                     this.computeVoid(databaseSession, Message.DURATION_LIMIT_ERROR.computeText("duration", Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value2, false, 2, null)));
                     throw new CommandAbortException();
                  }

                  this.until = value3;
                  this.durationText = Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value2, false, 2, null);
               }
            }
         }
      }
   }

   public final CharSequence getBroadcastMessage(@NotNull DatabaseSession databaseSession) {
      Settings settings = ((ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class)).getSettings();
      boolean flag = this.type == PunishmentType.KICK ? settings.isRequireKickReason() : (this.type == PunishmentType.WARN ? true : (this.isUndo() ? settings.isRequireUnbanUnmuteReason() : settings.isRequireBanMuteReason()));
      CharSequence message = this.reasonBuilder;
      int value = 0;
      int value2 = message.length() - 1;
      boolean flag2 = false;

      while (value <= value2) {
         int value3 = !flag2 ? value : value2;
         char charAt2 = message.charAt(value3);
         boolean flag3 = Intrinsics.compare(charAt2, 32) <= 0;
         if (!flag2) {
            if (!flag3) {
               flag2 = true;
            } else {
               value++;
            }
         } else {
            if (!flag3) {
               break;
            }

            value2--;
         }
      }

      CharSequence subSequence2 = message.subSequence(value, value2 + 1);
      if (StringsKt.containsWithDefaults(subSequence2, '{', false, 2, null) || StringsKt.containsWithDefaults(subSequence2, '}', false, 2, null)) {
         LiteSender liteSender = this.sender;
         String text = "litebans.admin";
         if (!liteSender.hasPermission(text)) {
            subSequence2 = StringsKt.computeText(
               StringsKt.computeText(StringsKt.computeText(StringsKt.computeText(subSequence2.toString(), "{hoverText:", "", false, 4, null), "{run:", "", false, 4, null), "{url:", "", false, 4, null),
               "}",
               "",
               false,
               4,
               null
            );
         }
      }

      if (StringsKt.containsWithDefaults(subSequence2, '\n', false, 2, null)) {
         LiteSender liteSender2 = this.sender;
         String text2 = "litebans.newline";
         if (!liteSender2.hasPermission(text2)) {
            subSequence2 = StringsKt.computeText(subSequence2.toString(), "\n", " ", false, 4, null);
         }
      }

      if (!this.isUndo()) {
         if (StringsKt.isBlank(subSequence2)) {
            if (this.hasTemplate()) {
               CharSequence message2 = this.templateInfo.getReason();
               if (message2 != null) {
                  message2 = message2;
               } else {
                  message2 = this.template.getDefaultInfo().getReason();
                  if (message2 != null) {
                     message2 = message2;
                  } else {
                     PunishmentType punishmentType = this.type;
                     switch (PunishmentTypeSwitchMap32.value[punishmentType.obtainPunishmentType().ordinal()]) {
                        case 1:
                           message2 = Message.DEFAULT_BAN_REASON;
                           break;
                        case 2:
                           message2 = Message.DEFAULT_MUTE_REASON;
                           break;
                        default:
                           message2 = null;
                     }

                     if (message2 == null) {
                        PunishmentType punishmentType2 = this.type;
                        Message message3 = Message.BAN_USAGE;
                        switch (PunishmentTypeSwitchMap22.value[punishmentType2.ordinal()]) {
                           case 1:
                              message2 = message3 == Message.EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                              break;
                           case 2:
                              message2 = message3 == Message.EXAMPLE ? Message.EXAMPLE_2 : Message.MUTE_USAGE;
                              break;
                           case 3:
                              message2 = Message.KICK_USAGE;
                              break;
                           default:
                              message2 = message3;
                        }

                        CharSequence message4 = message2;
                        CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
                        boolean flag4 = punishmentType2 == PunishmentType.KICK;
                        if (flag4) {
                           commandOutput.returnResult(message4);
                           throw new CommandAbortException();
                        }

                        Message message5 = Message.EXAMPLE;
                        switch (PunishmentTypeSwitchMap22.value[punishmentType2.ordinal()]) {
                           case 1:
                              message2 = message5 == Message.EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                              break;
                           case 2:
                              message2 = message5 == Message.EXAMPLE ? Message.EXAMPLE_2 : Message.MUTE_USAGE;
                              break;
                           case 3:
                              message2 = Message.KICK_USAGE;
                              break;
                           default:
                              message2 = message5;
                        }

                        CharSequence message6 = message2;
                        CommandTask.COMMAND_OUTPUT.returnResult(TextUtil.computeMessage(message4, true, message6));
                        throw new CommandAbortException();
                     }
                  }
               }

               subSequence2 = message2;
            } else {
               CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
               CharSequence message7 = Message.ERROR_NO_REASON_PROVIDED;
               if (flag) {
                  commandOutput2.returnResult(message7);
                  throw new CommandAbortException();
               }

               PunishmentType punishmentType3 = this.type;
               CharSequence message8;
               switch (PunishmentTypeSwitchMap32.value[punishmentType3.obtainPunishmentType().ordinal()]) {
                  case 1:
                     message8 = Message.DEFAULT_BAN_REASON;
                     break;
                  case 2:
                     message8 = Message.DEFAULT_MUTE_REASON;
                     break;
                  default:
                     message8 = null;
               }

               if (message8 == null) {
                  message8 = "";
               }

               subSequence2 = message8;
            }
         }
      } else if (StringsKt.isBlank(subSequence2)) {
         CommandOutput commandOutput3 = CommandTask.COMMAND_OUTPUT;
         CharSequence message9 = Message.ERROR_NO_REASON_REMOVAL;
         if (flag) {
            commandOutput3.returnResult(message9);
            throw new CommandAbortException();
         }

         PunishmentType punishmentType4 = this.type;
         CharSequence message10;
         switch (PunishmentTypeSwitchMap32.value[punishmentType4.obtainPunishmentType().ordinal()]) {
            case 1:
               message10 = Message.DEFAULT_UNBAN_REASON;
               break;
            case 2:
               message10 = Message.DEFAULT_UNMUTE_REASON;
               break;
            case 3:
               message10 = Message.DEFAULT_UNWARN_REASON;
               break;
            default:
               message10 = null;
         }

         if (message10 == null) {
            PunishmentType punishmentType5 = this.type;
            Message message11 = Message.BAN_USAGE;
            switch (PunishmentTypeSwitchMap22.value[punishmentType5.ordinal()]) {
               case 1:
                  message10 = message11 == Message.EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                  break;
               case 2:
                  message10 = message11 == Message.EXAMPLE ? Message.EXAMPLE_2 : Message.MUTE_USAGE;
                  break;
               case 3:
                  message10 = Message.KICK_USAGE;
                  break;
               default:
                  message10 = message11;
            }

            CharSequence message12 = message10;
            CommandOutput commandOutput4 = CommandTask.COMMAND_OUTPUT;
            boolean flag5 = punishmentType5 == PunishmentType.KICK;
            if (flag5) {
               commandOutput4.returnResult(message12);
               throw new CommandAbortException();
            }

            Message message13 = Message.EXAMPLE;
            switch (PunishmentTypeSwitchMap22.value[punishmentType5.ordinal()]) {
               case 1:
                  message10 = message13 == Message.EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                  break;
               case 2:
                  message10 = message13 == Message.EXAMPLE ? Message.EXAMPLE_2 : Message.MUTE_USAGE;
                  break;
               case 3:
                  message10 = Message.KICK_USAGE;
                  break;
               default:
                  message10 = message13;
            }

            CharSequence message14 = message10;
            CommandTask.COMMAND_OUTPUT.returnResult(TextUtil.computeMessage(message12, true, message14));
            throw new CommandAbortException();
         }

         subSequence2 = message10;
      }

      return Placeholders.replaceTemplatePlaceholders(databaseSession, subSequence2, this.template, this.intValue);
   }

   public final CharSequence computeMessage(@NotNull DatabaseSession databaseSession, @NotNull KotlinHelper13 kotlinHelper) {
      Message message = null;
      Message message2 = null;
      Punishment punishment = this.punishment;
      Intrinsics.checkNotNull(this.punishment);
      String text = this.templateInfo.getBroadcast();
      CharSequence message3;
      if (text != null) {
         message3 = text;
      } else {
         switch (PunishmentTypeSwitchMap4.value[punishment.getType().ordinal()]) {
            case 4:
               label84: {
                  if (punishment.isIpBan()) {
                     if (!punishment.isPermanent()) {
                        message2 = Message.BROADCAST_TEMP_IP_BAN;
                        break label84;
                     }
                  }

                  if (punishment.isIpBan() && punishment.isPermanent()) {
                     message2 = Message.BROADCAST_IP_BAN;
                  } else {
                     message2 = !punishment.isPermanent() ? Message.BROADCAST_TEMPBAN : Message.BROADCAST_BAN;
                  }
               }

               message3 = message2;
               break;
            case 5:
               label83: {
                  if (punishment.isIpBan()) {
                     if (!punishment.isPermanent()) {
                        message = Message.BROADCAST_TEMP_IP_MUTE;
                        break label83;
                     }
                  }

                  if (punishment.isIpBan() && punishment.isPermanent()) {
                     message = Message.BROADCAST_IP_MUTE;
                  } else {
                     message = !punishment.isPermanent() ? Message.BROADCAST_TEMPMUTE : Message.BROADCAST;
                  }
               }

               message3 = message;
               break;
            case 6:
               message3 = Message.WARN_BROADCAST;
               break;
            case 7:
               message3 = this.reasonBuilder.length() == 0 ? Message.BROADCAST_NO_REASON : Message.KICK_BROADCAST;
               break;
            default:
               StandardKt.notImplemented();
               throw new CommandAbortException();
         }
      }

      CharSequence message4 = message3;
      return this.computeMessage(databaseSession, message4, kotlinHelper);
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @Nullable LiteSender liteSender, @Nullable KotlinHelper13 kotlinHelper, boolean flag) {
      if (!this.hidden) {
         KotlinHelper13 kotlinHelper2 = kotlinHelper;
         if (kotlinHelper == null) {
            Intrinsics.checkNotNull(liteSender);
            String name = liteSender.getName();
            String uuidString = liteSender.getUuidString();
            String staffName = databaseSession.getStaffName(liteSender);
            String ipAddress = liteSender.getIpAddress();
            kotlinHelper2 = new KotlinHelper13(name, null, staffName, uuidString, ipAddress, liteSender, null, 66, null);
         }

         CharSequence message = this.computeMessage(databaseSession, kotlinHelper2);
         LoginRecord loginRecord = this.targetRecord;
         Platform platform = databaseSession.getPlatform();
         new PunishmentAnnounceTask(this, liteSender, loginRecord, message, platform, null, flag, false, 160, null).run();
      }
   }

   public static void handlePunishmentRequest(PunishmentRequest punishmentRequest, DatabaseSession databaseSession, LiteSender liteSender, KotlinHelper13 kotlinHelper, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         kotlinHelper = null;
      }

      if ((value & 4) != 0) {
         flag = true;
      }

      punishmentRequest.handleDatabaseSession(databaseSession, liteSender, kotlinHelper, flag);
   }

   public final CharSequence computeMessage(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @NotNull KotlinHelper13 kotlinHelper) {
      CharSequence[] messages = new CharSequence[]{"tempDuration", this.durationText};
      CharSequence message2 = TextUtil.replacePairs(message, messages);
      return kotlinHelper.getLiteSender() != null ? databaseSession.replacePlaceholders(message2, kotlinHelper.getLiteSender()) : databaseSession.replacePlaceholders(message2, kotlinHelper);
   }

   public final String computeText(@NotNull CommandTask commandTask) {
      return AltAccountPunisher.resolveName(commandTask, this.reason);
   }

   public final boolean isWildcardIp(@Nullable String text) {
      if (this.ipBan && text != null) {
         if (!StringsKt.containsWithDefaults(text, '*', false, 2, null) && !StringsKt.containsWithDefaults(text, '%', false, 2, null)) {
            if (!StringsKt.containsWithDefaults(text, '_', false, 2, null)) {
               return false;
            }

            String text2 = text;
            if (!TargetParser.TARGET_PARSER.checkText(text2)) {
               return false;
            }
         }

         if (!this.liteHelper.checkText(text)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isWildcardIpWithDefaults(PunishmentRequest punishmentRequest, String text, int value, Object obj) {
      if ((value & 1) != 0) {
         text = punishmentRequest.reason;
      }

      return punishmentRequest.isWildcardIp(text);
   }
}
