package com.neos.simulator.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.google.gson.Gson;
import com.neos.simulator.config.SimulationConfig;

public class TypeHandlerFactory {
    private static final Logger log = LogManager.getLogger(TypeHandlerFactory.class);
    private static final String TYPE_HANDLERS_DEFAULT_PATH = "com.neos.simulator.types";

    private Map<String, Class> typeHandlerNameMap;
    private Map<String, TypeHandler> typeHandlerMap;

    public TypeHandlerFactory() {
        typeHandlerNameMap = new LinkedHashMap<>();
        typeHandlerMap = new LinkedHashMap<>();
        scanForTypeHandlers(TYPE_HANDLERS_DEFAULT_PATH);
    }

    public void configure(SimulationConfig simulationConfig) {
        for (String packageName : simulationConfig.getCustomTypeHandlers()) {
            scanForTypeHandlers(packageName);
        }
    }

    private void scanForTypeHandlers(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends TypeHandler>> subTypes = reflections.getSubTypesOf(TypeHandler.class);
        for (Class type : subTypes) {
            if (Modifier.isAbstract(type.getModifiers())) {
                continue;
            }
            try {
                Object o = type.newInstance();
                Method nameMethod = o.getClass().getMethod("getName");
                nameMethod.setAccessible(true);
                String typeHandlerName = (String) nameMethod.invoke(o);
                typeHandlerNameMap.put(typeHandlerName, type);
                log.debug("Discovered TypeHandler [ " + typeHandlerName + "," + type.getName() + " ]");
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException |
                     IllegalArgumentException | InvocationTargetException ex) {
                log.warn("Error instantiating TypeHandler class [ " + type.getName() + " ]. It will not be available during processing.", ex);
            }
        }
    }

    public TypeHandler getTypeHandler(String name) throws IllegalArgumentException {
        if (name.contains("(")) {
            String typeName = name.substring(0, name.indexOf("("));
            String argsString = name.substring(name.indexOf("(") + 1, name.lastIndexOf(")"));
            List<Object> args = parseArguments(argsString);

            TypeHandler handler = typeHandlerMap.get(typeName);
            if (handler == null) {
                Class handlerClass = typeHandlerNameMap.get(typeName);
                if (handlerClass != null) {
                    try {
                        handler = (TypeHandler) handlerClass.newInstance();
                        handler.setArguments(args);
                        typeHandlerMap.put(typeName, handler);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        log.warn("Error instantiating TypeHandler class [ " + handlerClass.getName() + " ]", ex);
                    }
                }
            } else {
                handler.setArguments(args);
            }
            return handler;
        } else {
            throw new IllegalArgumentException("Not a value string: " + name);
        }
    }


    private static List<Object> parseArguments(String argumentsString) {
        List<Object> arguments = new ArrayList<>();
        if (argumentsString.isEmpty()) {
            return arguments;
        }

        String[] parts = splitArguments(argumentsString);

        for (String part : parts) {
            arguments.add(parseObject(part));
        }

        return arguments;
    }

    private static Object parseObject(String str) {
        try {
            final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            str = str.trim();
            if (str.startsWith("'") && str.endsWith("'")) {
                return str.substring(1, str.length() - 1);
            } else if (str.startsWith("[") && str.endsWith("]")) {
                return parseArray(str.substring(1, str.length() - 1));
            } else if (str.startsWith("{") && str.endsWith("}")) {
                str = str.replace("'", "\"");
                Gson gson = new Gson();
                return gson.fromJson(str, Map.class);
            } else if (str.contains(".")) {
                return Double.parseDouble(str);
            } else if (str.contains("T")) {
                return SDF.parse(str);
            } else {
                return Integer.parseInt(str);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static String[] splitArguments(String argumentsString) {
        List<String> parts = new ArrayList<>();
        int bracketDepth = 0;
        int curlyBracketDepth = 0;
        StringBuilder currentPart = new StringBuilder();
        for (char c : argumentsString.toCharArray()) {
            if (c == ',' && bracketDepth == 0 && curlyBracketDepth == 0) {
                parts.add(currentPart.toString().trim());
                currentPart.setLength(0);
            } else {
                currentPart.append(c);
                if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                } else if (c == '{') {
                    curlyBracketDepth++;
                } else if (c == '}') {
                    curlyBracketDepth--;
                }
            }
        }
        if (currentPart.length() > 0) {
            parts.add(currentPart.toString().trim());
        }
        return parts.toArray(new String[0]);
    }

    private static List<Object> parseArray(String arrayString) {
        List<Object> array = new ArrayList<>();
        String[] elements = arrayString.split(",");
        for (String element : elements) {
            array.add(parseObject(element.trim()));
        }
        return array;
    }


    public static void main(String[] args) {
        TypeHandlerFactory factory = new TypeHandlerFactory();
        TypeHandler random = factory.getTypeHandler("constant(10)");
        if (random == null) {
            log.error("error getting handler");
        } else {
            log.info("success! random value: " + random.getValue());
        }
    }
}
