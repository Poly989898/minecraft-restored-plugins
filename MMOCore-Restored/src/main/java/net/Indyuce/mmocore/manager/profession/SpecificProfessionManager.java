package net.Indyuce.mmocore.manager.profession;

import java.util.Objects;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.manager.MMOCoreManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public abstract class SpecificProfessionManager implements MMOCoreManager {
   private final String key;
   private Profession linkedProfession;

   public SpecificProfessionManager(String key) {
      this.key = key;
   }

   public void setLinkedProfession(Profession linkedProfession) {
      this.linkedProfession = linkedProfession;
   }

   @NotNull
   public Profession getLinkedProfession() {
      return Objects.requireNonNull(this.linkedProfession, "No linked profession found");
   }

   public boolean hasLinkedProfession() {
      return this.linkedProfession != null;
   }

   public String getStringKey() {
      return this.key;
   }

   public abstract void loadProfessionConfiguration(ConfigurationSection section);
}
