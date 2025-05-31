# Spring Framework (back-end) Specification

Nesta aula será adicionada a funcionalidade de pesquisa/filtro na API REST usando **Spring Data JPA** e **Specification**.

Por que uma linguagem de consulta em uma API REST? Porque pesquisar/filtrar os recursos por campos muito simples não é suficiente para APIs muito complexas. Uma linguagem de consulta é mais flexível e nos permite filtrar exatamente os recursos solicitados.

O **Spring Data JPA** é um projeto poderoso e popular dentro do ecossistema Spring, que oferece suporte para facilitar o acesso e a manipulação de dados em bancos de dados relacionais usando a especificação JPA (Java Persistence API). Uma das funcionalidades-chave do Spring Data JPA é a capacidade de usar **Specifications** para definir critérios de consulta de maneira flexível e dinâmica.

As **Specifications** no **Spring Data JPA** são uma abordagem baseada em critérios que permitem criar consultas complexas de forma programática. Com as Specifications, você pode definir critérios de consulta de forma declarativa, usando uma combinação de cláusulas e operadores lógicos, como igualdade, desigualdade, maior que, menor que, etc. Essa abordagem permite criar consultas dinâmicas, onde os critérios podem ser adicionados ou removidos em tempo de execução, tornando a busca de dados mais flexível e adaptável a diferentes requisitos.

Para utilizar Specifications com o Spring Data JPA, primeiro você precisa definir uma *interface* que estenda a *interface* **Specification** fornecida pelo Spring Data JPA. Essa *interface* deve implementar o método **toPredicate**, que recebe um objeto **Root** (representando a entidade a ser consultada), um objeto **CriteriaQuery** (que define a consulta) e um objeto **CriteriaBuilder** (que permite a construção de critérios de consulta). Dentro desse método, você pode criar critérios de consulta usando os métodos disponíveis no CriteriaBuilder para definir cláusulas, operadores lógicos e comparações.

Após criar suas Specifications, você pode usá-las junto com os repositórios do Spring Data JPA. Os repositórios já possuem métodos de consulta básicos, como **findAll** e **findById**, mas você também pode criar métodos personalizados que utilizam Specifications. Para isso, basta definir um método na interface do repositório e anotá-lo com **@Query**, passando a Specification como parâmetro. O Spring Data JPA se encarregará de traduzir a Specification em uma consulta SQL adequada e executá-la no banco de dados.  
Além disso, o Spring Data JPA também fornece uma série de métodos auxiliares para combinar Specifications e criar consultas ainda mais complexas. Você pode usar métodos como and, or e not para combinar Specifications de maneira lógica e criar consultas mais avançadas.

Em resumo, o Spring Data JPA com Specifications oferece uma forma poderosa e flexível de criar consultas dinâmicas em bancos de dados relacionais. Com essa abordagem, você pode definir critérios de consulta de forma programática, permitindo que sua aplicação se adapte facilmente a diferentes requisitos de busca de dados. O Spring Data JPA cuida da tradução dessas Specifications em consultas SQL eficientes, tornando a tarefa de acesso a dados mais fácil e produtiva.

## Alterações no projeto:

Foi adicionado o pacote **specification** (e, **specification.core**) com as seguintes classes:
- **CriteriaParcer**: Utilizado para criar a string de consulta e ajustar a ordem de AND/OR.
- **GenericSpecification**: Classe abstrata utilizada para gerar o predicado da consulta.
- **GenericSpecificationsBuilder**: Classe que permite combinar Specifications.
- **SearchCriteria**: Classe base com os dados da chave a ser buscada, operação e valor a ser consultado.
- **SearchOperation**: ENUM com as operações utilizadas durante as consultas.
- **SpecSerarchCriteria**: Mesma função da classe **SerarchCriteria**, entretanto permitindo a consulta a estruturas complexas.
- **ProductSpecification**: Implementação de **Specification** para classe **Product**.

E foram ajustadas as classes:
- **ProductRepository**: Ajustado a herança para utilizar **Specification**.
- **ProductService**: Criada a assinatura da consulta com **Specification**.
- **ProductServiceImpl**: Criada a implementação da consulta com **Specification**.
- **ProductControlle**: Adicionado o método **search()** que utiliza **Specification** para fazer a consulta.

## Como realizar uma consulta:

Como é possível visualizar na classe **ProductController** a consulta deverá ser realizada na url **/produtcs/search**:
```  
http://localhost:8080/products/search?search=description:*Gamer*,'price>2000  
```  
Classe **ProductController**:
```java
//... imports
public class ProductController extends CrudController<Product, ProductDto, Long> {
    //... 
    @GetMapping("search")  
    public List<ProductDto> search(@RequestParam(value = "search") String search) {  
        Specification<Product> spec = resolveSpecification(search);  
        return productService.findAll(spec).stream()  
                .map(this::convertToDto)  
                .collect(Collectors.toList());  
    }  
    //Método responsável por gerar a Specification.  
    protected Specification<Product> resolveSpecification(String searchParameters) {
        // Cria um SpecificationBuilder de Product  
        GenericSpecificationsBuilder<Product> specBuilder = new GenericSpecificationsBuilder<>();  
        // De acordo com a lista de parâmetros passada na URL extraí a expressão regular sendo que cada critério de consulta será separado por ,
        Pattern pattern = Pattern.compile("(\\p{Punct}?)(\\w+?)(" + Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET) + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");  
        Matcher matcher = pattern.matcher(searchParameters + ",");
        //Para cada , (virgula) encontrada cria um parâmetro de consulta.
        while (matcher.find()) {  
            specBuilder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(5), matcher.group(4), matcher.group(6));  
        }  
        // retorna a specification para ser utilizada no service/repository.
        return specBuilder.build(ProductSpecification::new);  
    }
}
```

## Outros Exemplos:
1. https://www.baeldung.com/rest-api-search-language-spring-data-specifications
2. https://github.com/RafaelLeoni/generic-specifications