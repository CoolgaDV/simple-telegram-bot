package cdv.stb.core;

import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Application entry point
 *
 * @author Dmitry Coolga
 *         14.01.2017 11:00
 */
public class Bootstrap {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(LogicConfiguration.class, ServiceConfiguration.class)
                .run(args);
    }

}
