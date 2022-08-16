package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.SQLException;

@SuppressWarnings("ALL")
@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Code {
    SUCCESS(200, "ok"),
    VALIDERROR_EXCEPTION(400, "パラメータがまちがっている"),
    NOLOGIN_EXCEPTION(401, "未登録"),
    FORBIDDEN_EXCEPTION(403, "アクセスの権利がない"),
    NOTFOUND_EXCEPTION(404, "パスミス"),
    SQLERR_EXCEPTION(405, "DB操作失敗、もう一度確認してください"),
    RUNERROR_EXCEPTION(500, "サーバー側で動作エラーが発生");
    private final int value;
    private final String describe;

    public static RuntimeException getException(Code code) {
        return new RuntimeException(code.value + "-" + code.describe);
    }
}