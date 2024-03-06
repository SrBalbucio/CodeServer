package org.codeserver.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

@RequiredArgsConstructor
public class User {

    @NonNull
    @Getter
    private String user;
    @NonNull
    @Getter
    private String password;
    @Getter
    private Point cursorLocation; // localidade do cursor na tela
    @Getter
    @Setter
    private Dimension screenSize; // tamanho da tela
    @Getter
    @Setter
    private Dimension appSize; // tamanho do window do app
}
