package com.team33.moduleapi.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(value = RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = OauthAccountFactory.class)
public @interface OAuthAccount {
    String[] value();
}