package litebans;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public enum PunishmentType implements CharSequence {
   BAN(null, PunishmentTable.BANS, 1, null),
   MUTE(null, PunishmentTable.MUTES, 1, null),
   WARN(null, PunishmentTable.WARNINGS, 1, null),
   KICK(null, PunishmentTable.KICKS, 1, null),
   UNBAN(BAN, null, 2, null),
   UNMUTE(MUTE, null, 2, null),
   UNWARN(WARN, null, 2, null);

   public static final PunishmentTypeCompanion PUNISHMENT_TYPE_COMPANION = new PunishmentTypeCompanion(null);
   private final PunishmentType punishmentType2;
   private final PunishmentTable punishmentTable;
   private static final List value3;
   private static final KotlinListSub KOTLIN_LIST_SUB = EnumEntries.computeKotlinListSub(values());

   PunishmentType(PunishmentType punishmentType, PunishmentTable punishmentTable2) {
      this.punishmentType2 = punishmentType;
      this.punishmentTable = punishmentTable2;
   }

   PunishmentType(PunishmentType punishmentType, PunishmentTable punishmentTable2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         punishmentType = null;
      }

      if ((value & 2) != 0) {
         PunishmentTable punishmentTable3;
         label16: {
            if (punishmentType != null) {
               punishmentTable3 = punishmentType.punishmentTable;
               if (punishmentType.punishmentTable != null) {
                  break label16;
               }
            }

            punishmentTable3 = (PunishmentTable)(Object)StandardKt.notImplemented();
         }

         punishmentTable2 = punishmentTable3;
      }

      this(punishmentType, punishmentTable2);
   }

   public final PunishmentTable getPunishmentTable() {
      return this.punishmentTable;
   }

   public final PunishmentType obtainPunishmentType() {
      PunishmentType punishmentType = this.punishmentType2;
      if (this.punishmentType2 == null) {
         punishmentType = this;
      }

      return punishmentType;
   }

   public int obtainIntValue() {
      return this.toString().length();
   }

   @Override
   public String toString() {
      String string = super.toString();
      return string.toLowerCase(Locale.ENGLISH);
   }

   @Override
   public IntStream chars() {
      return this.toString().chars();
   }

   @Override
   public IntStream codePoints() {
      return this.toString().codePoints();
   }

   public char computeCharValue(int value) {
      return this.toString().charAt(value);
   }

   @Override
   public CharSequence subSequence(int value, int value2) {
      return this.toString().subSequence(value, value2);
   }

   public static PunishmentType computePunishmentType(String text) {
      return Enum.valueOf(PunishmentType.class, text);
   }

   private static final PunishmentType[] obtainPunishmentTypeArray() {
      return new PunishmentType[]{BAN, MUTE, WARN, KICK, UNBAN, UNMUTE, UNWARN};
   }

   @Override
   public final int length() {
      return this.obtainIntValue();
   }

   @Override
   public final char charAt(int value) {
      return this.computeCharValue(value);
   }

   public static final List obtainList() {
      return value3;
   }

   static {
      PunishmentType[] punishmentTypes = values();
      var arrayList = new ArrayList();
      int value = 0;

      for (int i = punishmentTypes.length; value < i; value++) {
         PunishmentType punishmentType = punishmentTypes[value];
         if (punishmentType == punishmentType.obtainPunishmentType()) {
            arrayList.add(punishmentType);
         }
      }

      value3 = arrayList;
   }
}
