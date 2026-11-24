package net.Indyuce.mmocore.manager.social;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.experience.Booster;
import net.Indyuce.mmocore.experience.Profession;
import org.jetbrains.annotations.Nullable;

public class BoosterManager {
   private final List<Booster> map = new ArrayList<>();

   public void register(Booster booster) {
      this.flush();

      for (Booster active : this.map) {
         if (active.canStackWith(booster)) {
            active.addLength(booster.getLength());
            return;
         }
      }

      this.map.add(booster);
   }

   public Booster get(int index) {
      this.flush();
      return this.map.get(index);
   }

   private void flush() {
      this.map.removeIf(Booster::isTimedOut);
   }

   public double getMultiplier(@Nullable Profession profession) {
      double d = 1.0;

      for (Booster booster : this.map) {
         if (Objects.equals(profession, booster.getProfession()) && !booster.isTimedOut()) {
            d += booster.getExtra();
         }
      }

      return d;
   }

   public List<Booster> getActive() {
      return this.map.stream().filter(b -> !b.isTimedOut()).collect(Collectors.toList());
   }
}
