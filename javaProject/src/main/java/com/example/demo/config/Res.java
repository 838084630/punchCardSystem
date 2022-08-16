package com.example.demo.config;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.function.Supplier;

@Data
@SuperBuilder
public class Res<T> {
    private Code code;
    private T data;

    public Res<T> setBody(Supplier<T> supplier) {
        this.data = supplier.get();
        return this;
    }
}
