package com.github.julman99.gsonfire;

import com.google.gson.JsonElement;

/**
 * @autor: julio
 */
public interface TypeSelector<T> {

    public Class<? extends T> getClassForElement(JsonElement readElement);

}
