package net.Indyuce.mmoitems.api.item.template.explorer;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;

public class TemplateExplorer {
   private static final Random RANDOM = new Random();
   private final Collection<MMOItemTemplate> all = MMOItems.plugin.getTemplates().collectTemplates();

   public int count() {
      return this.all.size();
   }

   public TemplateExplorer applyFilter(Predicate<MMOItemTemplate> var1) {
      this.all.removeIf(this.not(var1));
      return this;
   }

   public Optional<MMOItemTemplate> rollLoot() {
      switch (this.count()) {
         case 0:
            return Optional.empty();
         case 1:
            return this.all.stream().findFirst();
         default:
            return this.all.stream().skip(RANDOM.nextInt(this.count())).findFirst();
      }
   }

   public Optional<MMOItem> rollItem(RPGPlayer var1) {
      return this.rollLoot().map(var1x -> var1x.newBuilder(var1).build());
   }

   private <T> Predicate<T> not(Predicate<T> var1) {
      return var1x -> !var1.test(var1x);
   }
}
