package nl.bryansuk.foundationapi.handlers;

import nl.bryansuk.foundationapi.converter.Converter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ConfigurationHandler {
    protected final FileHandler<Map<String,?>> configFileHandler;

    /**
     * Default constructor for Config.
     */
    public ConfigurationHandler(String path, Converter<Map<String,?>> converter, boolean defaultResource, boolean isAutoReloading) {
        configFileHandler = new FileHandler<>(path, converter, defaultResource, isAutoReloading);
        configFileHandler.read();

        checkIfAutoReloading();
    }

    public ConfigurationHandler(String path, Converter<Map<String,?>> converter, boolean defaultResource) {
        this(path, converter, defaultResource, false);
    }

    /**
     * Default constructor for Config.
     */
    public ConfigurationHandler(String path, Converter<Map<String,?>> converter) {
        this(path, converter, false, false);
    }

    public String getPath(){
        return configFileHandler.getPath();
    }

    /**
     * Retrieves the value associated with the specified key from the configuration.
     *
     * @param key The key to look up in the configuration.
     * @return The value associated with the specified key.
     */
    public @Nullable Object get(String key) {
        Map<String, ?> config = getConfiguration();
        if (config == null) return null;
        return config.get(key);
    }

    /**
     * Retrieves the value associated with the specified key as a String.
     *
     * @param key The key to look up in the configuration.
     * @return The String value associated with the specified key.
     */
    public @Nullable String getText(String key){
        return (get(key) instanceof String string) ? string : null;
    }

    /**
     * Retrieves the value associated with the specified key as a Number.
     *
     * @param key The key to look up in the configuration.
     * @return The Number value associated with the specified key.
     */
    public @Nullable Number getNumber(String key){
        return (get(key) instanceof Number number) ? number : null;
    }

    /**
     * Retrieves the value associated with the specified key as a boolean.
     *
     * @param key The key to look up in the configuration.
     * @return The boolean value associated with the specified key.
     */
    public @Nullable Boolean getBoolean(String key) {
        return (get(key) instanceof Boolean bool) ? bool : null;
    }

    public @Nullable List<?> getList(String key){
        return (get(key) instanceof List<?> list) ? list : null;
    }

    public @Nullable List<String> getStringList(String key){
        List<?> list = getList(key);
        if (list == null) return null;
        return list.stream()
                .map(String::valueOf)
                .toList();
    }

    public @Nullable List<Number> getNumberList(String key){
        List<?> list = getList(key);
        if (list == null) return null;
        return list.stream()
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .toList();
    }

    public @Nullable Map<?, ?> getMap(String key){
        return (get(key) instanceof Map<?,?> map) ? map : null;
    }

    public @Nullable Map<String, ?> getSection(String key){
        Map<?, ?> map = getMap(key);
        if (map == null) return null;
        return map.entrySet().stream()
                .collect(Collectors.toMap(entry ->
                        String.valueOf(entry.getKey()),
                        Map.Entry::getValue));
    }


    public @Nullable Map<String, ?> getConfiguration(){
        return configFileHandler.getObject();
    }

    public @Nullable Map<String, String> getConfigurationAsStringMap(){
        Map<String, ?> map = getConfiguration();
        if (map == null) return null;
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry-> String.valueOf(entry.getValue())));
    }

    private void checkIfAutoReloading(){
        Boolean autoReload = getBoolean("autoReload");
        if (autoReload == null) return;
        configFileHandler.setAutoReloading(autoReload);
    }

    public FileHandler<Map<String, ?>> getConfigFileHandler() {
        return configFileHandler;
    }
}
