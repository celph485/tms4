package tms.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.commons.lang3.StringUtils;

public class UpperCasePropertyNamingStrategy extends PropertyNamingStrategy {
    @Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return StringUtils.upperCase(defaultName);
    }
}