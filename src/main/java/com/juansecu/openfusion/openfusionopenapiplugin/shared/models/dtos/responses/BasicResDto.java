package com.juansecu.openfusion.openfusionopenapiplugin.shared.models.dtos.responses;

public record BasicResDto(
    boolean success,
    Enum<?> error,
    String message,
    Object data
) {}
