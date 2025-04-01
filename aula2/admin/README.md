# Spring Boot (back-end) - Spring Boot Admin

## Introdução
O [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) [1] é uma aplicação web utilizada para gerenciar e monitorar aplicações Spring Boot. Cada aplicação Spring Boot deve ser registrar em um servidor *Spring Boot Admin* e será considerada um cliente. Todo o processo de administração é realizado por meio dos *endpoints* disponibilizados pelo [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/)[2].

Nessa aula serão descritas as etapas para configurar um servidor Spring Boot Admin e como uma aplicação cliente deve registrar-se nesse servidor.

## Configurando o servidor Spring Boot Admin

Para configurar o servidor é necessário criar um novo projeto Spring Boot Web em [start.spring.io](https://start.spring.io/) e adicionar a [dependência maven](https://search.maven.org/classic/#search%7Cga%7C1%7Cspring-boot-admin-starter-server) do Spring Boot Admin Server:
```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.4.1</version>
</dependency>
```
Agora é possível acessar a anotação _@EnableAdminServer_, que deve ser adicionada na classe principal da aplicação: 

```java
@EnableAdminServer
@SpringBootApplication
public class AdminApplication(exclude = AdminServerHazelcastAutoConfiguration.class) {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminServerApplication.class, args);
    }
}
```
O próximo passo será alterar a porta em que a aplicação será executada para evitar conflitos com a aplicação **server**. Para isso basta editar o arquivo **application.properties** e adicionar:
```properties
server.port=8081
```
Agora basta executar a aplicação e ela estará habilitada para registrar novos clientes na url:

```plaintext
http://localhost:8080
```

## Configurando uma aplicação Cliente

A aplicação que será utilizada como cliente nesse exemplo é o projeto **server**. Então, após configurar o servidor admin, será possível registrar a aplicação Spring Boot como um cliente. Para isso será necessário configurar a [dependência maven](https://search.maven.org/classic/#search%7Cga%7C1%7Cspring-boot-admin-starter-client) no arquivo **pom.xml** do projeto **server**:

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.4.1</version>
</dependency>
```
Depois de adicionada a dependência é necessário configurar no cliente o endereço do servidor **admin**. Para isso , deve ser adicionada nas propriedades do projeto **server** as configurações:

```yml
spring:
	boot:
		admin:
			client:
				url: http://localhost:8081
management:
	endpoints:
		web:
			exposure:
				include: * 	
	endpoint:
		health:
			show-details: always
```
As duas últimas propriedades foram adicionada pois a partir do Spring Boot 2, os _endpoints_ diferentes de  _health_  e  _info_  não são disponibilizados por padrão.

Agora basta executar a aplicação **server** e visualizar na url: http://localhost:8081 a interface gráfica que a aplicação admin disponibiliza para administrar as aplicações. Outras configurações podem ser adicionadas para melhorar o gerenciamento, por exemplo, uma camada de segurança pois os dados disponibilizados pelo Spring Boot Actuator são dados sensíveis da aplicação. Também existe a possibilidade de emitir notificações de acordo com regras definidas de acordo com as ocorrências de eventos nas aplicações clientes.


# Referências

[1] Spring Boot Admin, https://github.com/codecentric/spring-boot-admin

[2] Spring Boot Actuator, https://docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/