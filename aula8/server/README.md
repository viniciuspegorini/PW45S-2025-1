# Spring Framework (back-end) - Upload de arquivos utilizando MINIO

## Introdução
Utilizando o serviço de armazenamento de objetos **Minio** será realizado o *upload* de arquivos para a API. Os arquivos armazenados serão as imagens dos produtos, sendo que para cada produto será possível realizar o *upload* de uma imagem.

## Criando uma instância do Minio no Docker
Para poder fazer o upload do arquivo para o Minio será necessário criar um contêiner **docker** para isso, do contrário seria necessário contratar um serviço de nuvem para o armazenamento. Na raiz da pasta do projeto **server** existe um arquivo chamado  **docker-compose.yml**, o qual possui as configurações necessárias para criar um contêiner **docker** com uma instância do **Minio**, com usuário e senha: "*minio@admin*", conforme pode ser observado no código abaixo:

```yml
version: "3.9"  
########################### SERVICES  
services:  
  ########################### MINIO  
  minio:  
    image: minio/minio:latest  
    container_name: minio  
    environment:  
	    MINIO_ROOT_USER: "minio@admin"  
		MINIO_ROOT_PASSWORD: "minio@admin"  
	volumes:  
      - ./data:/data  
	ports:  
		- 9000:9000  
		- 9001:9001  
	command: server /data --console-address :9001
```
Com o **Docker** instalado no computador juntamente com o Docker compose, basta executar o comando:
Para executar o  comando:
```cmd
docker compose up -d
```  
Com isso um contêiner do Minio será criado e iniciado, o próximo passo é testar esse contêiner, bastando acessar a URL: [http://127.0.0.1:9001/](http://127.0.0.1:9001/) e utilizar "*minio@admin*" como usuário e senha. Com isso a etapa inicial de configuração do Minio está concluída.

## Alterações na estrutura do projeto
Abaixo serão listados apenas os arquivos criados e modificados em relação ao projeto **server** da **aula7**:
- Arquivo **pom.xml**.
- Arquivo **application.yml**.
- Classe **ProductController** no pacote *controller*.
- Classe **ProductDto** no pacote *dto*.
- No pacote *minio*:
  - Classe **MinioConfig** no pacote *config*.
  - Classe **FileResponse** no pacote *payload*.
  - Classes **MinioService** e **MinioServiceImpl** nos pacotes *service* e *service.imp*, respectivamente.
  - Classe **FileTypeUtils** no pacote *util*.
  - Classe **MinioUtil** no pacote *util*.
- Classe **Product** no pacote *model*.
- Classes **ProductService** e **ProductServiceImpl** nos pacotes *service* e *service.impl*, respectivamente.

### Modificações nos arquivos pom.xml e application.yml

No arquivo **pom.xml** foram adicionadas as dependências do Minio para facilitar o *upload* e *download* de arquivos para o serviço instalado. A dependência do **Apache commons** para copia do *stream* de dados no *download* e a dependência do **hutool-all** utilizada para otimizar o trabalho com os tipos de arquivo.

```xml
<!-- ... -->
	<dependencies>
	<!-- ... -->
	<!-- File Storage com MINIO -->
		<dependency>
			<groupId>io.minio</groupId>
			<artifactId>minio</artifactId>
			<version>8.5.13</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
		</dependency>
		<dependency>
			<groupId>cn.hutool</groupId>
			<artifactId>hutool-all</artifactId>
			<version>5.8.22</version>
		</dependency>
	</dependencies>
	<!-- ... -->
```
No arquivo **application.yml** foram adicionadas as configurações para acesso ao serviço do **Minio**.

```yml
#...
minio:
	endpoint: http://127.0.0.1:9000
	port: 9000
	accessKey: minio@admin  #Login Account
	secretKey: minio@admin  # Login Password
	secure: false
	bucket-name: commons  # Bucket Name
	image-size: 10485760  # Maximum size of picture file
	file-size: 104857600  # Maximum file size
#...
```


### Modificações na classe ProductController
Nessa classe foi criado um novo método para tratar o objeto produto que está vindo do lado cliente, juntamente com o arquivo da imagem, o *saveProduct()*. E, um método para *download* do arquivo armazenado no Minio, o *downloadFile()*.

```java
@RestController
@RequestMapping("products")
@Slf4j
public  class ProductController extends CrudController<Product, ProductDto, Long> {
	private  final  ProductService productService;
	private  final  ModelMapper modelMapper;
	public ProductController(ProductService productService, ModelMapper modelMapper) {
		super(Product.class, ProductDto.class);
		this.productService = productService;
		this.modelMapper = modelMapper;
	}
	@Override
	protected  CrudService<Product, Long> getService() {
		return  this.productService;
	}
	@Override
	protected  ModelMapper getModelMapper() {
		return  this.modelMapper;
	}

	/*
		Será realizada uma requisição a partir do client enviando um FormData.
		O corpo da requisição será um JSON com {"product": {...}, "image": ...}
		Que será tratado na classe Service.
	*/
	@PostMapping(value = "upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
	public  Product saveProduct(@RequestPart("product") @Valid  Product entity, @RequestPart("image") @Valid  MultipartFile file) {
		return productService.save(entity, file);
	}
	//A requisição HTTP GET irá vir com o código do produto e irá retornar a imagem no corpo da resposta.
	@GetMapping(value = "download/{id}")
	public  void downloadFile(@PathVariable("id") Long id, HttpServletResponse response) {
		productService.downloadFile(id, response);
	}
}
```

### Modificações nas classes ProductDto e Product

As únicas modificações nas classes **ProductDto** e **Product** foram a adição dos atributos **imageName** e **contentType**, para armazenar o nome da imagem gerado pelo **Minio** e o seu **content-type**.
```java
public  class ProductDto {
	//...
	private  String imageName;
	private  String contentType;
}
```

### Modificações nas classes ProductService e ProductServiceImpl

Na classe **ProductService** estão apenas as assinaturas dos métodos para salvar um novo produto e armazenar a imagem e para *download* da imagem.  No **ProductServiceImpl** está a implementação desses métodos. Essa classe precisa de uma instância de um objeto da classe **MinioService** que é onde estão as implementações de *download*, *upload* e demais serviços disponibilizados pela biblioteca do **Minio**.

```java
@Service
@Slf4j
public  class ProductServiceImpl extends CrudServiceImpl<Product, Long> implements ProductService {
	private  final  ProductRepository productRepository;
	private  final  MinioService minioService;
	
	public ProductServiceImpl(ProductRepository productRepository, MinioService minioService) {
		this.productRepository = productRepository;
		this.minioService = minioService;
	}
	@Override
	protected  JpaRepository<Product, Long> getRepository() {
		return  this.productRepository;
	}
	
	public  Product save(Product entity, MultipartFile file) {
		String fileType = FileTypeUtils.getFileType(file);
		if (fileType != null) {
			FileResponse fileResponse = minioService.putObject(file, "commons", fileType);
			entity.setImageName(fileResponse.getFilename());
			entity.setContentType(fileResponse.getContentType());
		}
		return  super.save(entity);
	}
	@Override
	public  void downloadFile(Long id, HttpServletResponse response) {
		InputStream in = null;
		try {
			Product product = this.findOne(id);
			in = minioService.downloadObject("commons", product.getImageName());
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(product.getImageName(), "UTF-8"));
			response.setCharacterEncoding("UTF-8");
			// Remove bytes from InputStream Copied to the OutputStream .
			IOUtils.copy(in, response.getOutputStream());
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}
	}
}
```
### Classes do pacote *`minio`*

#### MinioConfig
A classe **MinioConfig** que está dentro do pacote *config* instância uma classe **MinioClient** com as configurações diponibilizadas no arquivo **application.yml**:
```java
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public  class MinioConfig {
	/** * It's a URL, domain name ,IPv4 perhaps IPv6 Address ") */
	private  String endpoint;
	/** * //"TCP/IP Port number " */
	private  Integer port;
	/** * //"accessKey Similar to user ID, Used to uniquely identify your account " */
	private  String accessKey;
	/** * //"secretKey It's the password for your account " */
	private  String secretKey;
	/** * //" If it is true, It uses https instead of http, The default value is true" */
	private  boolean secure;
	/** * //" Default bucket " */
	private  String bucketName;
	/** * The maximum size of the picture */
	private  long imageSize;
	/** * Maximum size of other files */
	private  long fileSize;
	@Bean
	public  MinioClient minioClient() {
		MinioClient minioClient =
		MinioClient.builder()
			.credentials(accessKey, secretKey)
			.endpoint(endpoint,port,secure)
			.build();
		return minioClient;
	}
}
```

#### FileResponse

A classe **FileResponse** será utilizada para o retorno do objeto adicionado no serviço **Minio** contendo os campos que serão utilizados para armazenar no banco de dados da aplicação.

```java
package br.edu.utfpr.pb.pw45s.server.minio.payload;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public  class FileResponse {
	String filename;
	String contentType;
	Long fileSize;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
	private  LocalDateTime createdTime;
}
```

#### MinioService e MinioServiceImpl

As classes **MinioService** e **MinioServiceImp** contém a assinatura e implementação dos principais métodos utilizados para acesso aos serviços disponibilizados pelo **Minio**.

```java
public  interface MinioService {
	// Upload files in the bucket
	FileResponse putObject(MultipartFile multipartFile, String bucketName, String fileType);
	// Download file from bucket
	InputStream downloadObject(String bucketName, String objectName);
	//Check whether bucket already exists
	boolean bucketExists(String bucketName);
	// Create a bucket
	void makeBucket(String bucketName);
	// List all bucket names
	List<String> listBucketName();
	//List all buckets
	List<Bucket> listBuckets();
	// Delete Bucket by Name
	boolean removeBucket(String bucketName);
	// List all object names in the bucket
	List<String> listObjectNames(String bucketName);
	// Delete file in bucket
	boolean removeObject(String bucketName, String objectName);
	// Delete files in bucket
	boolean removeListObject(String bucketName, List<String> objectNameList);
	// Get file path from bucket
	String getObjectUrl(String bucketName,String objectName);
}
```
O principal método utilizado neste projeto é o ***putObject()***, que recebe o arquivo, o nome do *bucket* em que o arquivo será armazenado e o tipo do arquivo. E, utilizando os métodos da classe **MinioUtil** verifica se o *bucket* existe, caso não exista é criado e então o arquivo é enviado para o serviço do **Minio**.
```java
package br.edu.utfpr.pb.pw45s.server.minio.service.impl;
import br.edu.utfpr.pb.pw45s.server.minio.config.MinioConfig;
import br.edu.utfpr.pb.pw45s.server.minio.payload.FileResponse;
import br.edu.utfpr.pb.pw45s.server.minio.service.MinioService;
import br.edu.utfpr.pb.pw45s.server.minio.util.MinioUtil;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public  class MinioServiceImpl implements MinioService {
	private  final  MinioUtil minioUtil;
	private  final  MinioConfig minioProperties;
	
	public MinioServiceImpl(MinioUtil minioUtil, MinioConfig minioProperties) {
		this.minioUtil = minioUtil;
		this.minioProperties = minioProperties;
	}

	@SneakyThrows
	@Override
	public  FileResponse putObject(MultipartFile multipartFile, String bucketName, String fileType) {
		try {
			bucketName = StringUtils.isNotBlank(bucketName) ? bucketName : minioProperties.getBucketName();
			if (!this.bucketExists(bucketName)) {
				this.makeBucket(bucketName);
			}
			String fileName = multipartFile.getOriginalFilename();
			Long fileSize = multipartFile.getSize();
			String objectName = UUID.randomUUID().toString().replaceAll("-", "") + fileName.substring(fileName.lastIndexOf("."));
			LocalDateTime createdTime = LocalDateTime.now();
			minioUtil.putObject(bucketName, multipartFile, objectName,fileType);
			return FileResponse.builder().filename(objectName).fileSize(fileSize)
						.contentType(fileType).createdTime(createdTime)
						.build();
		} catch (Exception e) {
			return  null;
		}
	}
	
	@Override
	public  InputStream downloadObject(String bucketName, String objectName) {
		return minioUtil.getObject(bucketName,objectName);
	}
	
	@Override
	public  boolean bucketExists(String bucketName) {
		return minioUtil.bucketExists(bucketName);
	}
	@Override
	public  void makeBucket(String bucketName) {
		minioUtil.makeBucket(bucketName);
	}
	@Override
	public  List<String> listBucketName() {
		return minioUtil.listBucketNames();
	}
	@Override
	public  List<Bucket> listBuckets() {
		return minioUtil.listBuckets();
	}
	@Override
	public  boolean removeBucket(String bucketName) {
		return minioUtil.removeBucket(bucketName);
	}
	@Override
	public  List<String> listObjectNames(String bucketName) {
		return minioUtil.listObjectNames(bucketName);
	}
	@Override
	public  boolean removeObject(String bucketName, String objectName) {
		return minioUtil.removeObject(bucketName, objectName);
	}
	@Override
	public  boolean removeListObject(String bucketName, List<String> objectNameList) {
		return minioUtil.removeObject(bucketName,objectNameList);
	}
	@Override
	public  String getObjectUrl(String bucketName, String objectName) {
		return minioUtil.getObjectUrl(bucketName, objectName);
	}
}
```
#### FileTypeUtils e MinioUtil

A classe **FileTypeUtils** possui apenas um métrodo o ***getFileType()*** que recebe o arquivo e retorna o tipo de arquivo do mesmo.
```java
package br.edu.utfpr.pb.pw45s.server.minio.util;
import cn.hutool.core.io.FileTypeUtil;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

public  class FileTypeUtils {
	private  final  static  String IMAGE_TYPE = "image/";
	private  final  static  String AUDIO_TYPE = "audio/";
	private  final  static  String VIDEO_TYPE = "video/";
	private  final  static  String APPLICATION_TYPE = "application/";
	private  final  static  String TXT_TYPE = "text/";

	public  static  String getFileType(MultipartFile multipartFile) {
		InputStream inputStream = null;
		String type = null;
		try {
			inputStream = multipartFile.getInputStream();
			type = FileTypeUtil.getType(inputStream);
			if (type.equalsIgnoreCase("JPG") || type.equalsIgnoreCase("JPEG")
				|| type.equalsIgnoreCase("GIF") || type.equalsIgnoreCase("PNG")
				|| type.equalsIgnoreCase("BMP") || type.equalsIgnoreCase("PCX")
				|| type.equalsIgnoreCase("TGA") || type.equalsIgnoreCase("PSD")
				|| type.equalsIgnoreCase("TIFF")) {
				return IMAGE_TYPE+type;
			}
			if (type.equalsIgnoreCase("mp3") || type.equalsIgnoreCase("OGG")
				|| type.equalsIgnoreCase("WAV") || type.equalsIgnoreCase("REAL")
				|| type.equalsIgnoreCase("APE") || type.equalsIgnoreCase("MODULE")
				|| type.equalsIgnoreCase("MIDI") || type.equalsIgnoreCase("VQF")
				|| type.equalsIgnoreCase("CD")) {
				return AUDIO_TYPE+type;
			}
			if (type.equalsIgnoreCase("mp4") || type.equalsIgnoreCase("avi")
				|| type.equalsIgnoreCase("MPEG-1") || type.equalsIgnoreCase("RM")
				|| type.equalsIgnoreCase("ASF") || type.equalsIgnoreCase("WMV")
				|| type.equalsIgnoreCase("qlv") || type.equalsIgnoreCase("MPEG-2")
				|| type.equalsIgnoreCase("MPEG4") || type.equalsIgnoreCase("mov")
				|| type.equalsIgnoreCase("3gp")) {
				return VIDEO_TYPE+type;
			}
			if (type.equalsIgnoreCase("doc") || type.equalsIgnoreCase("docx")
				|| type.equalsIgnoreCase("ppt") || type.equalsIgnoreCase("pptx")
				|| type.equalsIgnoreCase("xls") || type.equalsIgnoreCase("xlsx")
				|| type.equalsIgnoreCase("zip")||type.equalsIgnoreCase("jar")) {
				return APPLICATION_TYPE+type;
			}
			if (type.equalsIgnoreCase("txt")) {
				return TXT_TYPE+type;
			}
		}catch (IOException e){
		}
		return  null;
	}
}
```

Já a classe **MinioUtil** é onde estão implementados os métodos de comunicação direto com a API do serviço **Minio**. Sendo o principal método o ***putObject()*** , responsável pelo upload do arquivo para o serviço de armazenamento.

```java
package br.edu.utfpr.pb.pw45s.server.minio.util;
import br.edu.utfpr.pb.pw45s.server.minio.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MinioUtil {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    // Upload Files
    @SneakyThrows
    public void putObject(String bucketName, MultipartFile multipartFile, String filename, String fileType) {
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                        inputStream, -1, minioConfig.getFileSize())
                        .contentType(fileType)
                        .build());
    }

    // Get a file object as a stream from the specified bucket
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build());
                return stream;
            }
        }
        return null;
    }

    // Check if bucket name exists
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        boolean found =
                minioClient.bucketExists(
                        BucketExistsArgs.builder().
                                bucket(bucketName).
                                build());

        return found;
    }

    // Create bucket name
    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
            return true;
        } else {
            return false;
        }
    }

    // List all buckets
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    // List all bucket names
    @SneakyThrows
    public List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    // List all objects from the specified bucket
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    // Delete Bucket by its name from the specified bucket
    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                //  Delete failed when There are object files in bucket
                if (item.size() > 0) {
                    return false;
                }
            }
            //  Delete bucket when bucket is empty
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);

            if (!flag) {
                return true;
            }
        }
        return false;
    }

    // List all object names from the specified bucket
    @SneakyThrows
    public List<String> listObjectNames(String bucketName) {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        } else {
            listObjectNames.add("Bucket does not exist");
        }
        return listObjectNames;
    }

    // Delete object from the specified bucket
    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        }
        return false;
    }

    // Get file path from the specified bucket
    @SneakyThrows
    public String getObjectUrl(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(2, TimeUnit.MINUTES)
                            .build());
        }
        return url;
    }

    // Get metadata of the object from the specified bucket
    @SneakyThrows
    public StatObjectResponse statObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return stat;
        }
        return null;
    }

    // Get a file object as a stream from the specified bucket （ Breakpoint download )
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .offset(offset)
                                        .length(length)
                                        .build());
                return stream;
            }
        }
        return null;
    }

    // Delete multiple file objects from the specified bucket
    @SneakyThrows
    public boolean removeObject(String bucketName, List<String> objectNames) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            List<DeleteObject> objects = new LinkedList<>();
            for (int i = 0; i < objectNames.size(); i++) {
                objects.add(new DeleteObject(objectNames.get(i)));
            }
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                return false;
            }
        }
        return true;
    }

    // Upload InputStream object to the specified bucket
    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, InputStream inputStream, String contentType) {
        boolean flag = bucketExists(bucketName);

        if (flag) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                            inputStream, -1, minioConfig.getFileSize())
                            .contentType(contentType)
                            .build());
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return true;
            }
        }
        return false;
    }
}
```
Com as implementações acima é possível realizar o *upload* e gerenciamento do serviço **Minio**, na aplicação **server** será possível realizar o *download* do arquivo também. Porém para utilizar o arquivo publicamente, é necessário alterar a configuração de visibilidade do *bucket* criado. Para isso basta acessar o console do Minio por meio da URL [http://127.0.0.1:9001/](http://127.0.0.1:9001/) navegar até o menu **buckets** > selecionar o bucket criado para aplicação, que foi o **commons**, então ir no sub-menu **Anonymous** adicionar uma nova regra, com os valores: **Prefix=*** e **Access=readonly**, assim qualquer portador da URL do arquivo carregado no Minio vai poder visualizar o arquivo, com permissão de leitura.

# Referências

[1] Minio, https://min.io/. Acesso em: 19/11/2024.