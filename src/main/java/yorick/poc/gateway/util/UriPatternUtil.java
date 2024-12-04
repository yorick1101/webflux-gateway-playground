package yorick.poc.gateway.util;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class UriPatternUtil {
    public static Function<String, String> createUriTagFunction(String[] uriTemplates){
        if(uriTemplates == null || uriTemplates.length==0)
            return Function.identity();

        List<PathPattern> patterns = Arrays.stream(uriTemplates)
                .map(PathPatternParser.defaultInstance::parse)
                .toList();

        return uri-> {
            for(PathPattern pattern : patterns){
                if(pattern.matches(PathContainer.parsePath(uri)))
                    return pattern.getPatternString();
            }
            return uri;
        };
    }
}
