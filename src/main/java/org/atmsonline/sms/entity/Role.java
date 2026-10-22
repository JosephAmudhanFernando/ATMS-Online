package org.atmsonline.sms.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_SUPER_ADMIN,
    ROLE_ADMIN,
    ROLE_TEACHER,
    ROLE_STUDENT,
    ROLE_PARENT;

    @Override
    public String getAuthority() {
        return name();
    }
}