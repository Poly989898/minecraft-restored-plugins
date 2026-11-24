package net.Indyuce.mmocore.player.stats;

import io.lumine.mythic.lib.manager.StatManager;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import net.Indyuce.mmocore.experience.Profession;
import org.jetbrains.annotations.NotNull;

public class StatInfo {
   public final String name;
   public Profession profession;
   public LinearValue defaultInfo;

   public StatInfo(String name) {
      this.name = name;
   }

   @Deprecated
   @NotNull
   public String format(double d) {
      return StatManager.format(this.name, d);
   }

   @NotNull
   public LinearValue getDefaultFormula() {
      return this.defaultInfo == null ? LinearValue.ZERO : this.defaultInfo;
   }

   @NotNull
   public static StatInfo valueOf(String str) {
      StatInfo found = MMOCore.plugin.statManager.getInfo(str);
      return found == null ? new StatInfo(str) : found;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         StatInfo statInfo = (StatInfo)o;
         return this.name.equals(statInfo.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name);
   }
}
