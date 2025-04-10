package br.com.tecsus.sigaubs.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class ValidationUtils {

    public Boolean attrIsNotNull(Object attr) {

        if (attr instanceof String) {
            return attr != null && !((String) attr).isEmpty();
        }
        return attr != null;
    }

}
