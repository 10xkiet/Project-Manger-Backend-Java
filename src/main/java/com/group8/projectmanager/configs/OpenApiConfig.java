package com.group8.projectmanager.configs;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@SecuritySchemes({
    @SecurityScheme(

        scheme = "basic",
        name = "basicAuth",

        type = SecuritySchemeType.HTTP
    ),
    @SecurityScheme(

        scheme = "bearer",
        name = "bearerAuth",
        bearerFormat = "JWT",

        type = SecuritySchemeType.HTTP
    )
})
public class OpenApiConfig {}
