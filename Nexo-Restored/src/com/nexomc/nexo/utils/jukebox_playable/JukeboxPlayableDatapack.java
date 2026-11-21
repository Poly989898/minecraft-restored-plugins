package com.nexomc.nexo.utils.jukebox_playable;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.NexoDatapack;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.io.FilesKt;
import net.kyori.adventure.key.Key;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005¨\u0006\u0006"},
   d2 = {"Lcom/nexomc/nexo/utils/jukebox_playable/JukeboxPlayableDatapack;", "Lcom/nexomc/nexo/utils/NexoDatapack;", "<init>", "()V", "createDatapack", "", "com.nexomc:core"}
)
public final class JukeboxPlayableDatapack extends NexoDatapack {
   public JukeboxPlayableDatapack() {
      super("nexo_jukebox_playables", "Datapack for Custom Music Discs");
   }

   public final void createDatapack() {
      if (!VersionUtil.atleast("1.21.6")) {
         this.writeMCMeta();
         Collection jukeboxPlayables = (Collection)NexoPlugin.Companion.instance().soundManager().getJukeboxPlayables();
         Collection collection;
         if (jukeboxPlayables.isEmpty()) {
            int value = 0;
            collection = null;
         } else {
            collection = jukeboxPlayables;
         }

         List list = (List)collection;
         if (list != null) {
            Iterable iterable = (Iterable)list;

            for(Object element : iterable) {
               JukeboxPlayable jukeboxPlayable = (JukeboxPlayable)element;
               int value2 = 0;
               Key it = jukeboxPlayable.getSoundId();
               int value4 = 0;
               Pair pair = TuplesKt.to(it.namespace(), it.value());
               Object component1 = pair.component1();
               String namespace = (String)component1;
               component1 = pair.component2();
               String key = (String)component1;
               File file = FilesKt.resolve(this.getDatapackFile(), "data/" + namespace + "/jukebox_song/" + key + ".json");
               int value3 = 0;
               file.getParentFile().mkdirs();
               String str = jukeboxPlayable.getJukeboxJson().toString();
               FilesKt.writeText(file, str, kotlin.text.Charsets.UTF_8);
            }

            if (this.isFirstInstall()) {
               Logs.logError("Nexos's Custom Music Discs datapack could not be found...", false);
               Logs.logWarn("The first time you add a music-disc you need to restart your server so that the DataPack is enabled...", false);
               Logs.logWarn("Custom Music Discs will not work, please restart your server once!", true);
            } else if (!this.getDatapackEnabled()) {
               Logs.logError("Nexos's Custom Music Discs datapack is not enabled...", false);
               Logs.logWarn("Custom Music Discs will not work, please restart your server!", true);
            }

            super.enableDatapack$com_nexomc_core(true, false);
         }
      }
   }
}
