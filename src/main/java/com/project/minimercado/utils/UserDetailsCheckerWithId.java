package com.project.minimercado.utils;

import com.project.minimercado.utils.UserDetailsWithId;

public interface UserDetailsCheckerWithId {

    /**
     * Examines the User
     * @param toCheck the UserDetails instance whose status should be checked.
     */
    void check(UserDetailsWithId toCheck);

}