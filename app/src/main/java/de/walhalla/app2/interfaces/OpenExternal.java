package de.walhalla.app2.interfaces;

import org.jetbrains.annotations.NotNull;

public interface OpenExternal {
    void browser(@NotNull String url);

    void email();
}
