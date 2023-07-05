package team33.modulecore.domain;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(value = RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = UserAccountFactory.class)
public @interface UserAccount {
    String[] value();
}
