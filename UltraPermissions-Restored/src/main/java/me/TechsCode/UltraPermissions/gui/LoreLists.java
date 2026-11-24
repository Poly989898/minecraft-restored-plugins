package me.TechsCode.UltraPermissions.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.base.visual.LoreScroller;
import me.TechsCode.UltraPermissions.gui.permissionEditor.PermissionCopyList;
import me.TechsCode.UltraPermissions.storage.collection.RankupList;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.UserRankup;

public class LoreLists {
   private static final Phrase LIST_NONE_LINE = Phrase.create("lists.all.noneLine", "- **None**", Colors.GRAY, Colors.RED);
   private static final Phrase RANK_UP_LIST_PERMANENT_LINE = Phrase.create("lists.rankUps.permanentLine", "%index%. **%group%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase RANK_UP_LIST_TEMPORARY_LINE = Phrase.create(
      "lists.rankUps.temporaryLine", "%index%. **%group%** for **%time%**", Colors.GRAY, Colors.YELLOW, Colors.RED
   );
   private static final Phrase PERMISSION_LIST_POSITIVE_INHERITED = Phrase.create(
      "lists.permissions.inheritedPositiveLine", "- **%permission%** from **%group%**", Colors.GRAY, Colors.WHITE, Colors.YELLOW
   );
   private static final Phrase PERMISSION_LIST_NEGATIVE_INHERITED = Phrase.create(
      "lists.permissions.inheritedNegativeLine", "- **-****%permission%** from **%group%**", Colors.GRAY, Colors.RED, Colors.WHITE, Colors.YELLOW
   );
   private static final Phrase PERMISSION_LIST_POSITIVE = Phrase.create("lists.permissions.positiveLine", "- **%permission%**", Colors.GRAY, Colors.WHITE);
   private static final Phrase PERMISSION_LIST_NEGATIVE = Phrase.create(
      "lists.permissions.negativeLine", "- **-****%permission%**", Colors.GRAY, Colors.RED, Colors.WHITE
   );
   private static final Phrase PERMISSION_LIST_POSITIVE_SERVER = Phrase.create(
      "lists.permissions.serverPositiveLine", "- **%permission%** on Server **%server%**", Colors.GRAY, Colors.WHITE, Colors.BLUE
   );
   private static final Phrase PERMISSION_LIST_NEGATIVE_SERVER = Phrase.create(
      "lists.permissions.serverNegativeLine", "- **-****%permission%** on Server **%server%**", Colors.GRAY, Colors.RED, Colors.WHITE, Colors.BLUE
   );
   private static final Phrase PERMISSION_LIST_POSITIVE_WORLD = Phrase.create(
      "lists.permissions.worldPositiveLine", "- **%permission%** on World **%world%**", Colors.GRAY, Colors.WHITE, Colors.YELLOW
   );
   private static final Phrase PERMISSION_LIST_NEGATIVE_WORLD = Phrase.create(
      "lists.permissions.worldNegativeLine", "- **-****%permission%** on World **%world%**", Colors.GRAY, Colors.RED, Colors.WHITE, Colors.YELLOW
   );
   private static final Phrase PERMISSION_LIST_POSITIVE_SERVER_WORLD = Phrase.create(
      "lists.permissions.serverWorldPositiveLine", "- **%permission%** on **%server%** in **%world%**", Colors.GRAY, Colors.WHITE, Colors.BLUE, Colors.YELLOW
   );
   private static final Phrase PERMISSION_LIST_NEGATIVE_SERVER_WORLD = Phrase.create(
      "lists.permissions.serverWorldNegativeLine",
      "- **-****%permission%** on **%server%** in **%world%**",
      Colors.GRAY,
      Colors.WHITE,
      Colors.RED,
      Colors.BLUE,
      Colors.YELLOW
   );

   public LoreLists() {
   }

   public static List<String> printRankUpLoreList(RankupList rankupList) {
      ArrayList list = new ArrayList();
      int value = 1;

      for (UserRankup userRankup : rankupList) {
         Optional optional = userRankup.getGroup().get();
         if (optional.isPresent()) {
            Phrase phrase = userRankup.isPermanent() ? RANK_UP_LIST_PERMANENT_LINE : RANK_UP_LIST_TEMPORARY_LINE;
            String text = phrase.get()
               .replace("%index%", Integer.toString(value))
               .replace("%group%", ((Group)optional.get()).getName())
               .replace("%time%", userRankup.isTemporary() ? Tools.getTimeString(userRankup.getLeftDuration() / 1000L) : "");
            list.add(text);
            value++;
         }
      }

      if (value == 1) {
         list.add(LIST_NONE_LINE.get());
      }

      return list;
   }

   public static List<String> printPermissionCopiesList(PermissionCopyList permissionCopyList, long value) {
      List list = permissionCopyList.stream()
         .map(
            listedPermission -> {
               if (listedPermission.isInherited()) {
                  Phrase phrase;
                  if (listedPermission.getPermission().isPositive()) {
                     phrase = PERMISSION_LIST_POSITIVE_INHERITED;
                  } else {
                     phrase = PERMISSION_LIST_NEGATIVE_INHERITED;
                  }

                  return phrase.get().replace("%permission%", listedPermission.getColoredName()).replace("%group%", listedPermission.getPermission().getHolder().getName());
               } else {
                  Optional<String> optional = listedPermission.getPermission().getServer();
                  Optional<String> worldOptional = listedPermission.getPermission().getWorld();
                  boolean positive = listedPermission.getPermission().isPositive();
                  Phrase worldPhrase;
                  if (optional.isPresent() && worldOptional.isPresent()) {
                     worldPhrase = positive ? PERMISSION_LIST_POSITIVE_SERVER_WORLD : PERMISSION_LIST_NEGATIVE_SERVER_WORLD;
                  } else if (optional.isPresent()) {
                     worldPhrase = positive ? PERMISSION_LIST_POSITIVE_SERVER : PERMISSION_LIST_NEGATIVE_SERVER;
                  } else if (worldOptional.isPresent()) {
                     worldPhrase = positive ? PERMISSION_LIST_POSITIVE_WORLD : PERMISSION_LIST_NEGATIVE_WORLD;
                  } else {
                     worldPhrase = positive ? PERMISSION_LIST_POSITIVE : PERMISSION_LIST_NEGATIVE;
                  }

                  return worldPhrase.get()
                     .replace("%permission%", listedPermission.getColoredName())
                     .replace("%server%", optional.orElse("INVALID"))
                     .replace("%world%", worldOptional.orElse("INVALID"));
               }
            }
         )
         .collect(Collectors.toList());
      if (list.isEmpty()) {
         list.add(LIST_NONE_LINE.get());
      }

      return LoreScroller.scroller(list, 15, value);
   }
}
