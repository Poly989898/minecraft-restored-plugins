package io.lumine.mythic.lib.comp.mclogs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Util {
   public static String inputStreamToString(InputStream is) {
      return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
   }
}
