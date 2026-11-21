package io.lumine.mythic.core.skills.auras;

public class AuraPersistenceData {
   public static final String METADATA_KEY = "aura-persist";
   public String id;
   public String casterUuid;
   public int stacks;
   public int chargesRemaining;
   public int ticksRemaining;
   public int startCharges;
   public int startDuration;

   public AuraPersistenceData() {
   }

   public AuraPersistenceData(String id) {
      this.id = id;
   }
}
