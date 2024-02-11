package org.kuba.model.account;

import java.util.Date;

public record AccountDeletionResponse(String email, Date timestamp) {}
