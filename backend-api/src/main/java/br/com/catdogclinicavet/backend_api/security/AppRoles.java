package br.com.catdogclinicavet.backend_api.security;

public final class AppRoles {

    private AppRoles() {}

    public static final String CLIENTE = "CLIENTE";
    public static final String FUNCIONARIO = "FUNCIONARIO";
    public static final String VETERINARIO = "MEDICO_VETERINARIO";
    public static final String ADMIN = "ADMINISTRADOR";

    public static final String ACESSO_ANIMAIS = "hasAnyRole('" + ADMIN + "','" + CLIENTE + "', '" + FUNCIONARIO + "', '" + VETERINARIO + "')";

    public static final String ACESSO_INTERNO = "hasAnyRole('" + ADMIN + "','" + FUNCIONARIO + "', '" + VETERINARIO + "')";

    public static final String ACESSO_CLIENT = "hasAnyRole('" + CLIENTE + "')";
}