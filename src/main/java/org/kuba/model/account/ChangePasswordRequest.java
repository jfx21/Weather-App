package org.kuba.model.account;

public record ChangePasswordRequest(String username, String currentPassword, String newPassword) {}
