package de.zwisler.cfvis.util;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Property {
    private static Properties props;

    public static String get(String name) {
        if (Objects.isNull(props)) {
            Property.init();
        }
        String value = props.getProperty(name);
        if (Objects.isNull(value)) {
            System.out.println("Could not find property " + name);
        }
        return resolveEnvVars(value);
    }

    private static void init() {
        try {
            props = new Properties();
            props.load(Property.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String resolveEnvVars(String input)
    {
        if (null == input)
        {
            return null;
        }
        // match ${ENV_VAR_NAME} or $ENV_VAR_NAME
        Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
        Matcher m = p.matcher(input); // get a matcher object
        StringBuilder sb = new StringBuilder();
        while(m.find()){
            String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
            String envVarValue = System.getenv(envVarName);
            m.appendReplacement(sb,
                    null == envVarValue ? "" : Matcher.quoteReplacement(envVarValue));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
