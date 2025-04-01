# Spring Framework (back-end) - Spring Boot Admin

Com o servidor do projeto **admin** ajustado, basta configurar o Spring Boot Admin na aplicação **server**. O primeiro passo é adicionar as dependências no projeto servidor:

```xml
    <!-- Monitoramento dos endpoints para o Admin -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Spring boot Admin Client -->
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-starter-client</artifactId>
        <version>3.3.3</version>
    </dependency>
```
