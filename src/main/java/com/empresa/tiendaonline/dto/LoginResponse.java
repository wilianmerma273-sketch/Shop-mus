package com.empresa.tiendaonline.dto;

public record LoginResponse(String token, String tipo, String email, String rol, long expiraEn) {
}
