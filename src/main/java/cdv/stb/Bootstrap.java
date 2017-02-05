package cdv.stb;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application entry point
 *
 * @author Dmitry Coolga
 *         14.01.2017 11:00
 */
@EnableAutoConfiguration
@Import(SpringConfiguration.class)
@EnableAsync
@EnableScheduling
public class Bootstrap {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Bootstrap.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

}
