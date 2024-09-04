package rs.ac.uns.ftn.informatika.jpa.configuration;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class CacheConfiguration {

    @Bean
    public KeyGenerator customKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                // Custom logic to generate a key
                return "CustomKeyFor" + method.getName() + Arrays.deepToString(params);
            }
        };
    }
}
