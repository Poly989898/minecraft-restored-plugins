package litebans;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public final class LicenseCommandTask extends CommandTask {
   public LicenseCommandTask(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void run() {
      String text = BuildInfoUtil.obtainText();
      byte b = 0;
      byte b2 = 4;
      String substring2 = text.substring(b, b2);
      AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) ("LiteBans - Copyright © 2016-" + substring2 + " Ruan Wessels. All rights reserved.\n\n"));
      AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) ("LiteBans includes the following third-party software:\n"));
      AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) ("Software Licensed Under Apache License Version 2.0 (https://www.apache.org/licenses/LICENSE-2.0):"));
      CommandTask commandTask = this;
      String[] texts = new String[]{
         "Kotlin - Copyright (C) 2010-2020 JetBrains s.r.o and respective authors and developers",
         "HikariCP - Copyright (C) 2013 Brett Wooldridge",
         "json-simple - Copyright (C) 2012 Yidong Fang",
         "Apache Commons Compress - Copyright 2002-2021 The Apache Software Foundation"
      };
      var arrayList = new ArrayList(texts.length);
      int value = 0;

      for (int i = texts.length; value < i; value++) {
         String text2 = texts[value];
         arrayList.add("  " + text2);
      }

      AltAccountPunisher.sendMessage(commandTask, (CharSequence) (CollectionsKt.joinToStringWithDefaults(arrayList, "\n", null, null, 0, null, null, 62, null)));
      AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) ("=====\nOther Software Licenses:"));
      CommandTask commandTask2 = this;
      String[] texts2 = new String[]{
         "bungeecord-chat - (c) 2012-2025 SpigotMC Pty. Ltd.",
         "bungeecord-config - (c) 2012-2025 SpigotMC Pty. Ltd.",
         "Redistribution and use in source and binary forms, with or without",
         "modification, are permitted provided that the following conditions are met:",
         "Redistributions of source code must retain the above copyright notice, this",
         "list of conditions and the following disclaimer.",
         "Redistributions in binary form must reproduce the above copyright notice,",
         "this list of conditions and the following disclaimer in the documentation",
         "and/or other materials provided with the distribution.",
         "The name of the author may not be used to endorse or promote products derived",
         "from this software without specific prior written permission.",
         "You may not use the software for commercial software hosting services without",
         "written permission from the author.",
         "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"",
         "AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE",
         "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE",
         "ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE",
         "LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR",
         "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF",
         "SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS",
         "INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN",
         "CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)",
         "ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE",
         "POSSIBILITY OF SUCH DAMAGE."
      };
      var arrayList2 = new ArrayList(texts2.length);
      int value2 = 0;

      for (int i2 = texts2.length; value2 < i2; value2++) {
         String text3 = texts2[value2];
         arrayList2.add("  " + text3);
      }

      AltAccountPunisher.sendMessage(commandTask2, (CharSequence) (CollectionsKt.joinToStringWithDefaults(arrayList2, "\n", null, null, 0, null, null, 62, null)));
   }
}
