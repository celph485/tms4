package tms.common;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Objects;

import static tms.common.Constants.CONFIG_FILE_LOCATION;
import static tms.common.Constants.LOG_FILE_LOCATION;

public class CliArgProcessor {
    private static final int TOTAL_ARG_LENGTH = 2;
    private static final String PAIR_SEPARATOR = "=";
    private static final String CONFIG_FILE_LOCATION_KEY = "--config-file-location";
    private static final String LOG_FILE_LOCATION_KEY = "--log-file-location";

    private String configFileLocation;
    private String logFileLocation;

    public void process(final String[] args){
        if(Objects.isNull(args) || args.length < TOTAL_ARG_LENGTH){
            throw new IllegalArgumentException("need --config-file-location=<config-file-path> and --log-file-location=<log-file-path> arguments");
        }

        for (final var arg: args){
            if(StringUtils.startsWithIgnoreCase(arg, CONFIG_FILE_LOCATION_KEY)){
                configFileLocation = StringUtils.substringAfter(arg, PAIR_SEPARATOR);
            }
            if(StringUtils.startsWithIgnoreCase(arg, LOG_FILE_LOCATION_KEY)){
                logFileLocation = StringUtils.substringAfter(arg, PAIR_SEPARATOR);
            }
        }

        if(!Files.isReadable(Paths.get(configFileLocation))){
            throw new IllegalArgumentException("config file is not readable");
        }

        if(!Files.isDirectory(Paths.get(logFileLocation))){
            throw new IllegalArgumentException("log file location is not reachable");
        }

        System.setProperty(CONFIG_FILE_LOCATION, configFileLocation);
        System.setProperty(LOG_FILE_LOCATION, logFileLocation);
    }

    public static CliArgProcessor getInstance(){
        return new CliArgProcessor();
    }
}
