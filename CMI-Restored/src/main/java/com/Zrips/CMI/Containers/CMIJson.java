package com.Zrips.CMI.Containers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMIJson {
  public static <K, V> String serialize(HashMap<K, V> hashMap) {
    return serializeMap(hashMap);
  }

  private static String serializeMap(Map<?, ?> map) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{");

    for (var entry : map.entrySet()) {
      String string = entry.getKey() == null ? "null" : escapeJson(entry.getKey().toString());
      stringBuilder
          .append("\"")
          .append(string)
          .append("\":")
          .append(serializeObject(entry.getValue()))
          .append(",");
    }

    if (stringBuilder.length() > 1) {
      stringBuilder.setLength(stringBuilder.length() - 1);
    }

    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  private static String serializeObject(Object object) {
    if (object == null) {
      return "null";
    }

    if (object instanceof String) {
      return "\"" + escapeJson(object.toString()) + "\"";
    }

    if (object instanceof Boolean) {
      return (Boolean) object ? "true" : "false";
    }

    if (!(object instanceof Number)) {
      if (object instanceof List) {
        return serializeList((List<?>) object);
      } else {
        return object instanceof Map
            ? serializeMap((Map<?, ?>) object)
            : "\"" + escapeJson(object.toString()) + "\"";
      }
    } else {
      if (object instanceof Double || object instanceof Float) {
        double number = ((Number) object).doubleValue();
        if (Double.isNaN(number) || Double.isInfinite(number)) {
          return "null";
        }
      }

      if (object instanceof BigDecimal) {
        String string = object.toString();
        return string.replace("E+", "E");
      } else {
        return object.toString();
      }
    }
  }

  public static String serializeList(List<?> list) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[");

    for (Object object : list) {
      stringBuilder.append(serializeObject(object)).append(",");
    }

    if (stringBuilder.length() > 1) {
      stringBuilder.setLength(stringBuilder.length() - 1);
    }

    stringBuilder.append("]");
    return stringBuilder.toString();
  }

  private static String escapeJson(String string) {
    if (string == null) {
      return "";
    }

    StringBuilder stringBuilder = new StringBuilder(string.length());

    for (int value = 0; value < string.length(); value++) {
      char ch = string.charAt(value);
      switch (ch) {
        case '\b':
          stringBuilder.append("\\b");
          break;
        case '\t':
          stringBuilder.append("\\t");
          break;
        case '\n':
          stringBuilder.append("\\n");
          break;
        case '\f':
          stringBuilder.append("\\f");
          break;
        case '\r':
          stringBuilder.append("\\r");
          break;
        case '"':
          stringBuilder.append("\\\"");
          break;
        case '/':
          stringBuilder.append("\\/");
          break;
        case '\\':
          stringBuilder.append("\\\\");
          break;
        default:
          if (ch < ' ') {
            stringBuilder.append(String.format("\\u%04X", Integer.valueOf(ch)));
          } else {
            stringBuilder.append(ch);
          }
      }
    }

    return stringBuilder.toString();
  }

  private static String unescapeJson(String string) {
    if (string != null && !string.isEmpty()) {
      StringBuilder stringBuilder = new StringBuilder(string.length());

      for (int value = 0; value < string.length(); value++) {
        char ch = string.charAt(value);
        if (ch == '\\' && value + 1 < string.length()) {
          char nextChar = string.charAt(++value);
          switch (nextChar) {
            case '"':
              stringBuilder.append('"');
              break;
            case '/':
              stringBuilder.append('/');
              break;
            case '\\':
              stringBuilder.append('\\');
              break;
            case 'b':
              stringBuilder.append('\b');
              break;
            case 'f':
              stringBuilder.append('\f');
              break;
            case 'n':
              stringBuilder.append('\n');
              break;
            case 'r':
              stringBuilder.append('\r');
              break;
            case 't':
              stringBuilder.append('\t');
              break;
            case 'u':
              if (value + 4 < string.length()) {
                String hex = string.substring(value + 1, value + 5);

                try {
                  stringBuilder.append((char) Integer.parseInt(hex, 16));
                  value += 4;
                } catch (NumberFormatException numberFormatException) {
                  stringBuilder.append("\\u");
                }
              } else {
                stringBuilder.append("\\u");
              }
              break;
            default:
              stringBuilder.append(nextChar);
          }
        } else {
          stringBuilder.append(ch);
        }
      }

      return stringBuilder.toString();
    } else {
      return "";
    }
  }

  public static HashMap<String, Object> deserialize(String string) {
    HashMap<String, Object> hashMap = new HashMap();
    if (string == null) {
      return hashMap;
    }

    string = string.trim();
    if (string.length() >= 2 && string.startsWith("{") && string.endsWith("}")) {
      String body = string.substring(1, string.length() - 1).trim();
      if (body.isEmpty()) {
        return hashMap;
      }

      String[] strings = splitJsonEntries(body);
      String[] entries = strings;
      int value = strings.length;

      for (int entryIndex = 0; entryIndex < value; entryIndex++) {
        String entry = entries[entryIndex];
        int separator = findKeyValueSeparator(entry);
        if (separator != -1) {
          String key = entry.substring(0, separator).trim();
          String valueText = entry.substring(separator + 1).trim();
          Object object = deserializeObject(key);
          String valueString = object == null ? null : object.toString();
          Object parsedValue = deserializeObject(valueText);
          hashMap.put(valueString, parsedValue);
        }
      }

      return hashMap;
    } else {
      return hashMap;
    }
  }

  private static Object deserializeObject(String string) {
    if (string == null) {
      return null;
    }

    string = string.trim();
    if (string.isEmpty()) {
      return "";
    }

    if (string.startsWith("\"") && string.length() >= 2 && isProperlyClosedQuote(string)) {
      return unescapeJson(string.substring(1, string.length() - 1));
    }

    if ("null".equals(string)) {
      return null;
    }

    if ("true".equals(string)) {
      return Boolean.TRUE;
    }

    if ("false".equals(string)) {
      return Boolean.FALSE;
    }

    if (string.startsWith("{") && string.endsWith("}")) {
      return deserialize(string);
    }

    if (string.startsWith("[") && string.endsWith("]")) {
      return deserializeList(string);
    }

    try {
      if (string.indexOf(46) < 0 && !string.toLowerCase().contains("e")) {
        try {
          return Long.parseLong(string);
        } catch (NumberFormatException numberFormatException) {
          return new BigInteger(string);
        }
      } else {
        return new BigDecimal(string);
      }
    } catch (NumberFormatException numberFormatException2) {
      return unescapeJson(string);
    }
  }

  public static List<Object> deserializeList(String string) {
    ArrayList<Object> arrayList = new ArrayList();
    if (string == null) {
      return arrayList;
    }

    string = string.trim();
    if (string.startsWith("[") && string.endsWith("]")) {
      String body = string.substring(1, string.length() - 1).trim();
      if (body.isEmpty()) {
        return arrayList;
      }

      String[] strings = splitJsonEntries(body);
      String[] items = strings;
      int value = strings.length;

      for (int itemIndex = 0; itemIndex < value; itemIndex++) {
        String item = items[itemIndex];
        arrayList.add(deserializeObject(item));
      }

      return arrayList;
    } else {
      return arrayList;
    }
  }

  private static String[] splitJsonEntries(String string) {
    ArrayList<String> arrayList = new ArrayList();
    int value = 0;
    int start = 0;
    boolean flag = false;
    int end = 0;

    for (int cursor = 0; cursor < string.length(); cursor++) {
      char ch = string.charAt(cursor);
      if (ch == '"') {
        int slashCount = 0;

        for (int back = cursor - 1; back >= 0 && string.charAt(back) == '\\'; back--) {
          slashCount++;
        }

        if (slashCount % 2 == 0) {
          flag = !flag;
        }
      }

      if (!flag) {
        if (ch == '{') {
          value++;
        } else if (ch == '}') {
          value--;
        } else if (ch == '[') {
          start++;
        } else if (ch == ']') {
          start--;
        } else if (ch == ',' && value == 0 && start == 0) {
          arrayList.add(string.substring(end, cursor).trim());
          end = cursor + 1;
        }
      }
    }

    if (end < string.length()) {
      arrayList.add(string.substring(end).trim());
    }

    return arrayList.toArray(new String[0]);
  }

  private static int findKeyValueSeparator(String string) {
    boolean flag = false;
    int value = 0;
    int start = 0;

    for (int cursor = 0; cursor < string.length(); cursor++) {
      char ch = string.charAt(cursor);
      if (ch == '"') {
        int slashes = 0;

        for (int escapes = cursor - 1; escapes >= 0 && string.charAt(escapes) == '\\'; escapes--) {
          slashes++;
        }

        if (slashes % 2 == 0) {
          flag = !flag;
        }
      }

      if (!flag) {
        if (ch == '{') {
          value++;
        } else if (ch == '}') {
          value--;
        } else if (ch == '[') {
          start++;
        } else if (ch == ']') {
          start--;
        } else if (ch == ':' && value == 0 && start == 0) {
          return cursor;
        }
      }
    }

    return -1;
  }

  private static boolean isProperlyClosedQuote(String string) {
    if (!string.endsWith("\"")) {
      return false;
    }

    int value = string.length() - 2;
    int count = 0;

    while (value >= 0 && string.charAt(value) == '\\') {
      count++;
      value--;
    }

    return count % 2 == 0;
  }

  public static <T> T getFromObject(Object object, T value) {
    if (object == null) {
      return (T) (Object) (value);
    }

    var valueClass = value.getClass();

    try {
      if (valueClass == String.class) {
        return (T) (Object) (object.toString());
      }

      if (Number.class.isAssignableFrom(valueClass)) {
        if (object instanceof Number) {
          Number number = (Number) object;
          if (valueClass == Integer.class) {
            return (T) (Object) (number.intValue());
          }

          if (valueClass == Long.class) {
            return (T) (Object) (number.longValue());
          }

          if (valueClass == Double.class) {
            return (T) (Object) (number.doubleValue());
          }

          if (valueClass == Float.class) {
            return (T) (Object) (number.floatValue());
          }

          if (valueClass == Short.class) {
            return (T) (Object) (number.shortValue());
          }

          if (valueClass == Byte.class) {
            return (T) (Object) (number.byteValue());
          }
        } else {
          String string = object.toString().trim();
          if (valueClass == Integer.class) {
            return (T) (Object) (Integer.parseInt(string));
          }

          if (valueClass == Long.class) {
            return (T) (Object) (Long.parseLong(string));
          }

          if (valueClass == Double.class) {
            return (T) (Object) (Double.parseDouble(string));
          }

          if (valueClass == Float.class) {
            return (T) (Object) (Float.parseFloat(string));
          }

          if (valueClass == Short.class) {
            return (T) (Object) (Short.parseShort(string));
          }

          if (valueClass == Byte.class) {
            return (T) (Object) (Byte.parseByte(string));
          }
        }
      }

      if (valueClass == Boolean.class) {
        if (object instanceof Boolean) {
          return (T) (Object) (object);
        }

        String text = object.toString().trim().toLowerCase();
        if (text.equals("true") || text.equals("1") || text.equals("yes")) {
          return (T) (Object) (Boolean.TRUE);
        }

        if (text.equals("false") || text.equals("0") || text.equals("no")) {
          return (T) (Object) (Boolean.FALSE);
        }
      }

      if (value instanceof ArrayList && object instanceof List) {
        return (T) (Object) ((new ArrayList((List) object)));
      }

      if (valueClass == List.class && object instanceof List) {
        return (T) (Object) (object);
      }

      if (valueClass == Map.class && object instanceof Map) {
        return (T) (Object) (object);
      }
    } catch (Exception exception) {
    }

    return (T) (Object) (value);
  }
}
