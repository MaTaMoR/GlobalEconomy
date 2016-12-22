package me.matamor.ge.shared.utils;

public interface Callback<T> {

    void done(T result, Throwable error);

}
