package tms.common;

import java.io.FileInputStream;
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
        return properties.getProperty(key.getValue());
    }

    public String getProperty(final ConfigKey key, final String defaultVal){
        final var val = properties.getProperty(key.getValue());
        return Objects.isNull(val)?defaultVal:val;
    }

    private static class Holder {
        private Holder(){
            throw new UnsupportedOperationException("PropertiesLoader.Holder should not be instantiated");
        }
        private static final PropertiesLoader INSTANCE = new PropertiesLoader();
    }
}
