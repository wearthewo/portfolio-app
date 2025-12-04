package com.enterprise.portfolio.validation;

import org.passay.*;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private int minLength;
    private int maxLength;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;

    @Override
    public void initialize(ValidPassword constraint) {
        this.minLength = constraint.minLength();
        this.maxLength = constraint.maxLength();
        this.requireUppercase = constraint.requireUppercase();
        this.requireLowercase = constraint.requireLowercase();
        this.requireDigit = constraint.requireDigit();
        this.requireSpecialChar = constraint.requireSpecialChar();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        List<Rule> rules = new java.util.ArrayList<>();
        
        // Length rules
        rules.add(new LengthRule(minLength, maxLength));
        
        // Character rules
        if (requireUppercase) {
            rules.add(new CharacterRule(EnglishCharacterData.UpperCase, 1));
        }
        
        if (requireLowercase) {
            rules.add(new CharacterRule(EnglishCharacterData.LowerCase, 1));
        }
        
        if (requireDigit) {
            rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
        }
        
        if (requireSpecialChar) {
            rules.add(new CharacterRule(EnglishCharacterData.Special, 1));
        }
        
        // No whitespace
        rules.add(new WhitespaceRule());
        
        // No sequential characters
        rules.add(new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false));
        rules.add(new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false));
        rules.add(new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false));
        
        // No repeated characters
        rules.add(new CharacterRule(EnglishCharacterData.Alphabetical, 1));
        rules.add(new CharacterRule(EnglishCharacterData.Digit, 1));
        
        // Create validator and validate password
        PasswordValidator validator = new PasswordValidator(rules);
        RuleResult result = validator.validate(new PasswordData(password));
        
        if (result.isValid()) {
            return true;
        }
        
        // Customize the error message
        List<String> messages = validator.getMessages(result);
        String messageTemplate = messages.stream().collect(Collectors.joining(", "));
        
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate)
               .addConstraintViolation();
        
        return false;
    }
}
