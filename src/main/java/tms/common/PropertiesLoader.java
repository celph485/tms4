package tms.common;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static tms.common.Constants.CONFIG_FILE_LOCATION;

public final class PropertiesLoader {
    private final Properties properties;
    private PropertiesLoader(){
        final var configFile = System.getProperty(CONFIG_FILE_LOCATION);
        try(
                FileInputStream stream = new FileInputStream(configFile)
        ){
            properties = new Properties();
            properties.load(stream);
        }catch (Exception e){
            throw new RuntimeException("Unable to load config file");
        }
    }

    public static PropertiesLoader getInstance(){
        return Holder.INSTANCE;
    }

    public String getProperty(final ConfigKey key){
        return properties.getProperty(key.value());
    }

    public String getProperty(final ConfigKey key, final String defaultVal){
        final var val = properties.getProperty(key.value());
        return Objects.isNull(val)?defaultVal:val;
    }

    public List<Long> getListOfLongValues(final ConfigKey key){
        final String val =  properties.getProperty(key.value());
        return Arrays.stream(val.split(","))
                .map(Long::parseLong)
                .toList();
    }

    private static class Holder {
        private Holder(){
            throw new UnsupportedOperationException("PropertiesLoader.Holder should not be instantiated");
        }
        private static final PropertiesLoader INSTANCE = new PropertiesLoader();
    }
}
