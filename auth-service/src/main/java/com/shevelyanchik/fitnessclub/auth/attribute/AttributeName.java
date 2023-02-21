package com.shevelyanchik.fitnessclub.auth.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AttributeName {
    public static final String USERNAME = "username";
    public static final String AUTHORITIES = "authorities";
}
