package petro.post_hub2.security.validation;

import petro.post_hub2.model.constants.ApiConstants;

public class PasswordUtils {
    public static boolean isNotValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return true;
        }
        String trimPassword = password.trim();
        if (trimPassword.length() < ApiConstants.REQUIRED_MIN_PASSWORD_LENGTH) {
            return true;
        }
        int characterNumber = ApiConstants.REQUIRED_MIN_CHARACTERS_NUMBER_IN_PASSWORD;
        int lettersUppercase = ApiConstants.REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD;
        int letterLowerCase = ApiConstants.REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD;
        int digitsNumber = ApiConstants.REQUIRED_MIN_DIGITS_NUMBER_IN_PASSWORD;

        for (int i = 0; i < trimPassword.length(); i++) {
            String currentLetter = String.valueOf(trimPassword.charAt(i));
            if (!ApiConstants.PASSWORD_ALLCHARACTERS.contains(currentLetter)) {
                return true;
            }
            if (ApiConstants.PASSWORDCHARACTERS.contains(currentLetter)) {
                characterNumber--;
            }
            if (ApiConstants.PASSWORD_DIGITS.contains(currentLetter)) {
                digitsNumber--;
            }
            if (ApiConstants.PASSWORD_LETTERS_LOWER_CASE.contains(currentLetter)) {
                letterLowerCase--;
            }
            if (ApiConstants.PASSWORD_LETTERS_UPPER_CASE.contains(currentLetter)) {
                lettersUppercase--;
            }
        }
        if (characterNumber > 0 || lettersUppercase > 0 || letterLowerCase > 0 || digitsNumber > 0) {
            return true;
        }
        return false;
    }
}
