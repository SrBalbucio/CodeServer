package org.codeserver.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class User {

    @NonNull
    @Getter
    private String user;
    @NonNull
    @Getter
    private String password;
}
