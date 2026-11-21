package io.lumine.mythic.bukkit.utils.particles.reflection.mappings.files;

import io.lumine.mythic.bukkit.utils.particles.reflection.mappings.Mappings;
import io.lumine.mythic.bukkit.utils.particles.reflection.mappings.RealMappings;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class ProguardMapping implements MappingType {
   private static final Map<String, Class<?>> PRIMITIVES = Map.of(
      "boolean",
      boolean.class,
      "byte",
      byte.class,
      "short",
      short.class,
      "int",
      int.class,
      "long",
      long.class,
      "float",
      float.class,
      "double",
      double.class,
      "char",
      char.class
   );
   private static final Logger LOGGER = Logger.getLogger("ProguardMapping");
   private static final Pattern CLASS_REGEX = Pattern.compile("(?<original>[\\w.$]+) -> (?<obfuscated>[\\w.$]+):");
   private static final Pattern METHOD_REGEX = Pattern.compile(
      "    (?:(?:\\d+:\\d+:)?[\\w.$\\[\\]]+ )?(?<original>[\\w<>$]+)\\((?<parameters>[\\w.$, \\[\\]]*)\\) -> (?<obfuscated>[\\w<>]+)"
   );
   private static final Pattern FIELD_REGEX = Pattern.compile("    (?:[\\w.$\\[\\]]+ )?(?<original>[\\w$]+) -> (?<obfuscated>\\w+)");
   private static final Pattern METHOD_PARAMETERS_REGEX = Pattern.compile("([\\w.$]+)(\\[\\])?,?");
   private final boolean failOnLineParse;

   public ProguardMapping(boolean failOnLineParse) {
      this.failOnLineParse = failOnLineParse;
   }

   @Override
   public Mappings parse(@NotNull List<String> lines) {
      List<ProguardMapping.ObfuscatedClass> parsedClasses = new ArrayList<>();
      String classOriginal = null;
      String classObfuscated = null;
      List<ProguardMapping.ObfuscatedMethod> classMethods = null;
      List<ProguardMapping.ObfuscatedField> classFields = null;

      for (String line : lines) {
         if (!line.startsWith("#") && !line.contains("package-info")) {
            Matcher classMatch;
            if ((classMatch = CLASS_REGEX.matcher(line)).matches()) {
               if (classOriginal != null) {
                  parsedClasses.add(new ProguardMapping.ObfuscatedClass(classOriginal, classObfuscated, classMethods, classFields));
               }

               classOriginal = classMatch.group("original");
               classObfuscated = classMatch.group("obfuscated");
               classMethods = new ArrayList<>();
               classFields = new ArrayList<>();
            } else {
               Matcher methodMatch;
               if ((methodMatch = METHOD_REGEX.matcher(line)).matches()) {
                  classMethods.add(
                     new ProguardMapping.ObfuscatedMethod(methodMatch.group("original"), methodMatch.group("obfuscated"), methodMatch.group("parameters"))
                  );
               } else {
                  Matcher fieldMatch;
                  if ((fieldMatch = FIELD_REGEX.matcher(line)).matches()) {
                     classFields.add(new ProguardMapping.ObfuscatedField(fieldMatch.group("original"), fieldMatch.group("obfuscated")));
                  } else {
                     if (this.failOnLineParse) {
                        throw new IllegalArgumentException("Failed to parse line " + line);
                     }

                     LOGGER.log(Level.WARNING, "Failed to parse line {0}", line);
                  }
               }
            }
         }
      }

      if (classOriginal != null) {
         parsedClasses.add(new ProguardMapping.ObfuscatedClass(classOriginal, classObfuscated, classMethods, classFields));
      }

      LOGGER.log(Level.FINE, "Found {0} classes to remap", parsedClasses.size());
      HashMap<String, Type> fakeTypes = new HashMap<>();
      Map<String, RealMappings.RealClassMapping> classes = parsedClasses.stream()
         .map(clazz -> new RealMappings.RealClassMapping(clazz.original, clazz.obfuscated, new ArrayList<>(), new ArrayList<>()))
         .collect(Collectors.toMap(RealMappings.RealClassMapping::getOriginalName, Function.identity()));

      for (ProguardMapping.ObfuscatedClass parsedClass : parsedClasses) {
         RealMappings.RealClassMapping classMapping = classes.get(parsedClass.original);
         classMapping.fields()
            .addAll(parsedClass.fields.stream().map(field -> new RealMappings.RealClassMapping.RealFieldMapping(field.original, field.obfuscated)).toList());
         classMapping.methods()
            .addAll(
               parsedClass.methods
                  .stream()
                  .map(
                     method -> new RealMappings.RealClassMapping.RealMethodMapping(
                        method.original, method.obfuscated, this.parseParameters(method.parameters, fakeTypes, classes)
                     )
                  )
                  .toList()
            );
      }

      return new RealMappings(classes.values());
   }

   @NotNull
   protected Type[] parseParameters(@NotNull String parameters, Map<String, Type> fakeTypes, Map<String, RealMappings.RealClassMapping> classes) {
      List<Type> types = new ArrayList<>(2);
      Matcher matcher = METHOD_PARAMETERS_REGEX.matcher(parameters);

      while (matcher.find()) {
         String typeName = matcher.group(1);
         boolean isArray = matcher.group(2) != null;
         Class<?> clazz = null;
         Type type = classes.get(typeName);
         if (type == null) {
            clazz = PRIMITIVES.get(typeName);
            if (clazz == null) {
               try {
                  clazz = Class.forName(typeName);
               } catch (ClassNotFoundException e) {
                  if (!fakeTypes.containsKey(typeName)) {
                     LOGGER.log(Level.FINER, "Cannot find class {0}", typeName);
                     fakeTypes.put(typeName, new ProguardMapping.FakeType(typeName));
                  }

                  type = fakeTypes.get(typeName);
               }
            }
         }

         if (clazz != null) {
            type = isArray ? clazz.arrayType() : clazz;
         } else if (isArray) {
            type = new Mappings.ClassMapping.ClassArrayType(type);
         }

         types.add(type);
      }

      return types.toArray(Type[]::new);
   }

   @Override
   public void write(@NotNull BufferedWriter writer, @NotNull Mappings mappings) throws IOException {
      for (Mappings.ClassMapping mappedClass : mappings.getClasses()) {
         writer.append("%s -> %s:".formatted(mappedClass.getOriginalName(), mappedClass.getMappedName()));
         writer.newLine();

         for (Mappings.ClassMapping.FieldMapping mappedField : mappedClass.getFields()) {
            writer.append("    %s -> %s".formatted(mappedField.getOriginalName(), mappedField.getMappedName()));
            writer.newLine();
         }

         for (Mappings.ClassMapping.MethodMapping mappedMethod : mappedClass.getMethods()) {
            String parameters = Stream.of(mappedMethod.getParameterTypes()).map(parameter -> parameter.getTypeName()).collect(Collectors.joining(","));
            writer.append("    %s(%s) -> %s".formatted(mappedMethod.getOriginalName(), parameters, mappedMethod.getMappedName()));
            writer.newLine();
         }
      }
   }

   protected record FakeType(String name) implements Type {
      @Override
      public String getTypeName() {
         return this.name;
      }
   }

   private record ObfuscatedClass(
      String original, String obfuscated, List<ProguardMapping.ObfuscatedMethod> methods, List<ProguardMapping.ObfuscatedField> fields
   ) {
   }

   private record ObfuscatedField(String original, String obfuscated) {
   }

   private record ObfuscatedMethod(String original, String obfuscated, String parameters) {
   }
}
