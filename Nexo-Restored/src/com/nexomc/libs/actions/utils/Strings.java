package com.nexomc.libs.actions.utils;

import org.jetbrains.annotations.NotNull;

public class Strings {
   public static String replaceEach(@NotNull String text, @NotNull String[] searchList, @NotNull String[] replacementList) {
      if (text.trim().isEmpty()) {
         return text;
      } else {
         int searchLength = searchList.length;
         int replacementLength = replacementList.length;
         if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
         } else {
            boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
            int textIndex = -1;
            int replaceIndex = -1;
            int tempIndex = -1;

            for(int i = 0; i < searchLength; ++i) {
               if (!noMoreMatchesForReplIndex[i] && !searchList[i].isEmpty() && replacementList[i] != null) {
                  tempIndex = text.indexOf(searchList[i]);
                  if (tempIndex == -1) {
                     noMoreMatchesForReplIndex[i] = true;
                  } else if (textIndex == -1 || tempIndex < textIndex) {
                     textIndex = tempIndex;
                     replaceIndex = i;
                  }
               }
            }

            if (textIndex == -1) {
               return text;
            } else {
               int start = 0;
               int increase = 0;

               for(int i = 0; i < searchList.length; ++i) {
                  if (searchList[i] != null && replacementList[i] != null) {
                     int greater = replacementList[i].length() - searchList[i].length();
                     if (greater > 0) {
                        increase += 3 * greater;
                     }
                  }
               }

               increase = Math.min(increase, text.length() / 5);
               StringBuilder buf = new StringBuilder(text.length() + increase);

               while(textIndex != -1) {
                  for(int i = start; i < textIndex; ++i) {
                     buf.append(text.charAt(i));
                  }

                  buf.append(replacementList[replaceIndex]);
                  start = textIndex + searchList[replaceIndex].length();
                  textIndex = -1;
                  replaceIndex = -1;

                  for(int i = 0; i < searchLength; ++i) {
                     if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && !searchList[i].isEmpty() && replacementList[i] != null) {
                        tempIndex = text.indexOf(searchList[i], start);
                        if (tempIndex == -1) {
                           noMoreMatchesForReplIndex[i] = true;
                        } else if (textIndex == -1 || tempIndex < textIndex) {
                           textIndex = tempIndex;
                           replaceIndex = i;
                        }
                     }
                  }
               }

               int textLength = text.length();

               for(int i = start; i < textLength; ++i) {
                  buf.append(text.charAt(i));
               }

               return buf.toString();
            }
         }
      }
   }
}
