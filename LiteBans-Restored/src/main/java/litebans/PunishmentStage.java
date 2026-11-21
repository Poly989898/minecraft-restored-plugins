package litebans;

public enum PunishmentStage {
   INIT,
   PRE_TARGET,
   POST_TARGET,
   ENTRY_CREATED,
   ENTRY_EMITTED,
   ENTRY_APPLIED,
   FINALIZED,
   CANCELLED;

   public static PunishmentStage computePunishmentStage(String text) {
      return Enum.valueOf(PunishmentStage.class, text);
   }

   private static final PunishmentStage[] obtainPunishmentStageArray() {
      return new PunishmentStage[]{INIT, PRE_TARGET, POST_TARGET, ENTRY_CREATED, ENTRY_EMITTED, ENTRY_APPLIED, FINALIZED, CANCELLED};
   }
}
