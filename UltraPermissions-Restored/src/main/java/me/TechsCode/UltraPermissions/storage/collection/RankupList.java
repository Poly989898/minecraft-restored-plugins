package me.TechsCode.UltraPermissions.storage.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.UserRankup;

public class RankupList extends ArrayList<UserRankup> {
   public RankupList(int count) {
      super(count);
   }

   public RankupList() {
   }

   public RankupList(Collection<? extends UserRankup> userRankups) {
      super(userRankups);
   }

   public RankupList bestToWorst() {
      this.sort(Comparator.comparing(userRankup -> userRankup.getGroup().get().map(Group::getPriority).orElse(0)));
      return this;
   }

   public RankupList worstToBest() {
      this.sort(Comparator.comparing(userRankup -> userRankup.getGroup().get().map(Group::getPriority).orElse(0), Comparator.reverseOrder()));
      return this;
   }
}
