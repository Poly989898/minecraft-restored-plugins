package litebans;

import java.util.Locale;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;

public enum PunishmentTable implements CharSequence {
   BANS,
   MUTES,
   WARNINGS,
   KICKS,
   HISTORY,
   SYNC,
   SERVERS,
   CACHE8K,
   CONFIG,
   ALLOW;

   public static final PunishmentTableCompanion PUNISHMENT_TABLE_COMPANION = new PunishmentTableCompanion(null);
   private static final KotlinListSub KOTLIN_LIST_SUB = EnumEntries.computeKotlinListSub(values());

   public final boolean obtainFlag() {
      boolean flag;
      switch (PunishmentTableSwitchMap.value[this.ordinal()]) {
         case 1:
         case 2:
         case 3:
         case 4:
            flag = true;
            break;
         default:
            flag = false;
      }

      return flag;
   }

   public final PunishmentType obtainPunishmentType() {
      PunishmentType punishmentType;
      switch (PunishmentTableSwitchMap.value[this.ordinal()]) {
         case 1:
            punishmentType = PunishmentType.BAN;
            break;
         case 2:
            punishmentType = PunishmentType.MUTE;
            break;
         case 3:
            punishmentType = PunishmentType.WARN;
            break;
         case 4:
            punishmentType = PunishmentType.KICK;
            break;
         default:
            throw new UnsupportedOperationException();
      }

      return punishmentType;
   }

   @Override
   public String toString() {
      return computeText(this, null, false, 3, null);
   }

   public int obtainIntValue() {
      return this.toString().length();
   }

   @Override
   public IntStream chars() {
      return this.toString().chars();
   }

   public char computeCharValue(int value) {
      return this.toString().charAt(value);
   }

   @Override
   public CharSequence subSequence(int value, int value2) {
      return this.toString().subSequence(value, value2);
   }

   public final String computeText(@NotNull Locale locale, boolean flag) {
      if (PlatformHolder.PLATFORM_STATE.obtainFlag()) {
         ConfigModule module = (ConfigModule)PlatformHolder.PLATFORM_STATE.obtainPlatform().getModule(ConfigModule.class);
         String lowerCase = this.name().toLowerCase(locale);
         if (module != null) {
            Settings settings = module.getSettings();
            String text = settings.getSqlTablePrefix() + lowerCase;
            if (Intrinsics.areEqual(module.getSettings().getSqlDriver(), "h2") && flag) {
               text = text.toUpperCase(locale);
            }

            return text;
         }
      }

      return this.name();
   }

   public static String computeText(PunishmentTable punishmentTable, Locale locale, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 1) != 0) {
         locale = Locale.ENGLISH;
      }

      if ((value & 2) != 0) {
         flag = true;
      }

      return punishmentTable.computeText(locale, flag);
   }

   public static PunishmentTable computePunishmentTable(String text) {
      return Enum.valueOf(PunishmentTable.class, text);
   }

   private static final PunishmentTable[] obtainPunishmentTableArray() {
      return new PunishmentTable[]{BANS, MUTES, WARNINGS, KICKS, HISTORY, SYNC, SERVERS, CACHE8K, CONFIG, ALLOW};
   }

   @Override
   public final int length() {
      return this.obtainIntValue();
   }

   @Override
   public final char charAt(int value) {
      return this.computeCharValue(value);
   }
}
