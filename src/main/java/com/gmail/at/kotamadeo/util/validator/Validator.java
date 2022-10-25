package com.gmail.at.kotamadeo.util.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validator {

    private static final String NICKNAME_PATTERN = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,10}[a-zA-Z0-9]$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-5])(?=.*[a-z])(?=.*[A-Z]).{4,20}$";

    private static final Pattern nicknamePattern = Pattern.compile(NICKNAME_PATTERN);
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValidNickname(final String nickname) {
        return nicknamePattern.matcher(nickname).matches();
    }

    public static boolean isValidPassword(final String password) {
        return passwordPattern.matcher(password).matches();
    }
}
