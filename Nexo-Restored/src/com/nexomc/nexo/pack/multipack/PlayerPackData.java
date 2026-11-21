package com.nexomc.nexo.pack.multipack;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.CustomDataTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nJ\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\t\u001a\u00020\nJ\"\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\u00102\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\r0\fJ\u001d\u0010\u0012\u001a\u0004\u0018\u00010\r2\u0006\u0010\t\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\b¢\u0006\u0002\u0010\u0014J\u000e\u0010\u0015\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\u0010J\f\u0010\u0016\u001a\u00020\u0017*\u00020\nH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/PlayerPackData;", "", "<init>", "()V", "MULTIPACK_KEY", "Lorg/bukkit/NamespacedKey;", "enabledTemplates", "", "", "player", "Lorg/bukkit/OfflinePlayer;", "getSelections", "", "", "setSelections", "", "Lorg/bukkit/entity/Player;", "selections", "toggleTemplate", "templateId", "(Lorg/bukkit/entity/Player;Ljava/lang/String;)Ljava/lang/Boolean;", "syncSelections", "persistentDataContainer", "Lio/papermc/paper/persistence/PersistentDataContainerView;", "com.nexomc:core"}
)
public final class PlayerPackData {
   @NotNull
   public static final PlayerPackData INSTANCE = new PlayerPackData();
   @NotNull
   private static final NamespacedKey MULTIPACK_KEY;

   private PlayerPackData() {
   }

   @NotNull
   public final Set<String> enabledTemplates(@NotNull OfflinePlayer player) {
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      if (!manager.isEnabled()) {
         return SetsKt.emptySet();
      } else {
         Map selections = this.getSelections(player);
         Iterable templates = (Iterable)manager.getTemplates();
         Collection destination = (Collection)(new ArrayList());

         for(Object element : templates) {
            PackTemplate template = (PackTemplate)element;
            int value = 0;
            if (template.getRequired() || (Boolean)selections.getOrDefault(template.getId(), template.getDefault())) {
               destination.add(element);
            }
         }

         templates = (Iterable)((List)destination);
         destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(templates, 10)));

         for(Object item : templates) {
            PackTemplate it = (PackTemplate)item;
            int value2 = 0;
            destination.add(it.getId());
         }

         return CollectionsKt.toSet((Iterable)((List)destination));
      }
   }

   @NotNull
   public final Map<String, Boolean> getSelections(@NotNull OfflinePlayer player) {
      PersistentDataContainerView pdc = this.persistentDataContainer(player);
      List list = (List)pdc.get(MULTIPACK_KEY, CustomDataTypes.INSTANCE.getSTRING_LIST());
      if (list == null) {
         return MapsKt.emptyMap();
      } else {
         List enabledIds = list;
         MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
         Iterable templates = (Iterable)manager.getTemplates();
         int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(templates, 10)), 16);
         Map destination = (Map)(new LinkedHashMap(capacity));

         for(Object element : templates) {
            PackTemplate template = (PackTemplate)element;
            int value = 0;
            Pair pair = TuplesKt.to(template.getId(), enabledIds.contains(template.getId()));
            destination.put(pair.getFirst(), pair.getSecond());
         }

         return destination;
      }
   }

   public final void setSelections(@NotNull Player player, @NotNull Map<String, Boolean> selections) {
      LinkedHashMap result = new LinkedHashMap();

      for(Map.Entry entry : selections.entrySet()) {
         boolean it = (Boolean)entry.getValue();
         int value = 0;
         if (it) {
            result.put(entry.getKey(), entry.getValue());
         }
      }

      List enabledIds = CollectionsKt.toList((Iterable)((Map)result).keySet());
      player.getPersistentDataContainer().set(MULTIPACK_KEY, CustomDataTypes.INSTANCE.getSTRING_LIST(), enabledIds);
   }

   @Nullable
   public final Boolean toggleTemplate(@NotNull Player player, @NotNull String templateId) {
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      Iterable templates = (Iterable)manager.getTemplates();
      Iterator iterator = templates.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object obj2 = iterator.next();
            PackTemplate it = (PackTemplate)obj2;
            int value = 0;
            if (!Intrinsics.areEqual(it.getId(), templateId)) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      PackTemplate packTemplate = (PackTemplate)obj;
      if (packTemplate == null) {
         return null;
      } else {
         PackTemplate template = packTemplate;
         if (template.getRequired()) {
            return null;
         } else {
            Map selections = MapsKt.toMutableMap(this.getSelections((OfflinePlayer)player));
            boolean currentlyEnabled = (Boolean)selections.getOrDefault(templateId, template.getDefault());
            boolean newState = !currentlyEnabled;
            selections.put(templateId, newState);
            if (newState) {
               for(PackTemplate other : manager.getTemplates()) {
                  if (template.conflictsWith(other)) {
                     selections.put(other.getId(), false);
                  }
               }
            }

            this.setSelections(player, selections);
            return newState;
         }
      }
   }

   public final void syncSelections(@NotNull Player player) {
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      if (manager.isEnabled()) {
         Map selections = MapsKt.toMutableMap(this.getSelections((OfflinePlayer)player));
         Iterable templates = (Iterable)manager.getTemplates();
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(templates, 10)));

         for(Object item : templates) {
            PackTemplate it = (PackTemplate)item;
            int value = 0;
            destination.add(it.getId());
         }

         Set validIds = CollectionsKt.toSet((Iterable)((List)destination));
         CollectionsKt.removeAll((Iterable)selections.keySet(), (a0_ -> PlayerPackData.syncSelections$lambda$1(validIds, (String)a0_)));

         for(PackTemplate template : manager.getTemplates()) {
            selections.putIfAbsent(template.getId(), template.getDefault() || template.getRequired());
         }

         this.setSelections(player, selections);
      }
   }

   private final PersistentDataContainerView persistentDataContainer(OfflinePlayer offlinePlayer) {
      PersistentDataContainerView persistentDataContainerView;
      block: {
         Player player = offlinePlayer instanceof Player ? (Player)offlinePlayer : null;
         if ((offlinePlayer instanceof Player ? (Player)offlinePlayer : null) != null) {
            PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
            if (persistentDataContainer != null) {
               persistentDataContainerView = (PersistentDataContainerView)persistentDataContainer;
               break block;
            }
         }

         persistentDataContainerView = Bukkit.getOfflinePlayer(offlinePlayer.getUniqueId()).getPersistentDataContainer();
      }

      PersistentDataContainerView persistentDataContainerView2 = persistentDataContainerView;
      return persistentDataContainerView2;
   }

   private static final boolean syncSelections$lambda$1(Set validIds, String it) {
      return !validIds.contains(it);
   }

   static {
      MULTIPACK_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "multipack_selections");
   }
}
