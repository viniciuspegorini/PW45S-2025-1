# Configurando o ambiente de produção

Para apresentar uma simulação de ambiente de produção serão utilizadas as tecnologias e serviços:
- React: Aplicação cliente
- Spring Framework: Aplicação servidor
- PostgresSQL: Banco de dados
- Registro.br: Registro do domínio
- Cloudflare: Camada de segurança (ataques DDoS, Firewall, etc...)
- Digital Ocean: Serviço de hospedagem
- Docker: Ambiente de conteiner
- Traefik: Proxy reverso
- Jenkins: Ferramenta de CI/CD


## 1. Configurando a Digital Ocean

Primeiro passo é criar uma conta [https://m.do.co/c/05bf7c48eb76](https://m.do.co/c/05bf7c48eb76).
Preencha com:
Nome
Email
Senha

Confirme o email recebido.

Será necessário adicionar um método de pagamento (cartão de crédito ou PayPal).
Complete o processo de verificação.

Criar um projeto e informar o nome e descrição.
Então podemos criar um droplet:
Escolha o plano de Droplet
Basic → ideal para pequenos projetos.
Modelos:
Regular with SSD → preço mais acessível.
Premium AMD/Intel → mais desempenho.
Premium NVMe → muito rápido.
Selecione o tamanho:
1 GB / 1vCPU → ideal para testes.
2 GB ou mais → para produção, lembrando que as aplicações serão diretamente impactadas de acordo com as configurações do droplet.

No droplet criado iremos receber um IPV4, ele será utilizado na Cloudflare para redirecionamento do domínio.

## 2. Configurando o domínio

Acessar o [registro.br](https://registro.br/busca-dominio/), buscar o domínio, se ele estiver livre, basta seguir o fluxo do site para realizar a compra do mesmo. Após a compra você irá receber um email para pagamento do domínio. Depois de pago o domínio estará disponível na conta para ajuste do servidor DNS.

## 3. Configurando a Cloudflare

Para adicionar uma camada de segurança já pronta, podemos adicionar os serviços fornecido pela Clouflare como proxy. 

### 3.1. Crie uma conta no Cloudflare
Acesse: https://dash.cloudflare.com/sign-up
Informe email e senha.
Confirme a conta via email.

### 3.2. Adicione um domínio
Após logar, clique em "Add a Site".
Digite o seu domínio (ex.: meudominmio.com.br).
Clique em "Add site".

### 3.3. Escolha um plano
Para maioria dos casos, o plano Free já atende.
Selecione o plano e continue.

### 3.4. Cloudflare vai escanear seu DNS atual
Ele listará todos os registros DNS atuais (A, CNAME, MX, etc.).
Revise cuidadosamente.
Se já está usando DigitalOcean, mantenha o registro A apontando para o IP público da sua Droplet, criado no final do passo Configurando a Digital Ocean. 


### 3.5. Alterar os Nameservers no seu provedor de domínio
O Cloudflare vai mostrar dois Nameservers (exemplo: alice.ns.cloudflare.com e bob.ns.cloudflare.com).

Acesse o painel onde comprou o domínio (Registro.br, Namecheap etc.).

Localize a opção de "DNS" ou "Nameservers".

Substitua pelos do Cloudflare.

⚠️ Atenção: A propagação dos Nameservers pode levar de 30 minutos até 48 horas. Geralmente leva entre 1 e 2 horas.

### 3.6. Verificação pelo Cloudflare
Após a troca, volte ao painel Cloudflare.
Clique em "Done, check nameservers".

## 4. Configurando os serviços e aplicações na Digital Ocean

### 4.1 Instalando o docker

Ajustando o firewall para expor as portas 80 e 443

```cmd
sudo ufw allow 80
sudo ufw allow 443
```

Instalando o docker:
```cmd
sudo apt update

sudo apt install apt-transport-https ca-certificates curl software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"

apt-cache policy docker-ce

```

### 4.1 Criando a rede `web`

Será necessário criar uma rede externa no docker para as aplicações.

```cmd
sudo docker network create --driver=bridge --attachable web
```

### 4.2 Criando uma senha para o Traefik

Instalar o apache2-utils no Ubuntu:

```cmd
sudo apt-get install apache2-utils
```

Gerar a senha:
```cmd
htpasswd -nbB <username> <password>
```
Ex.:
```cmd
htpasswd -nbB admin mypassword
```

Substituir a senha no arquivo `docker-compose.yml` ou no arquivo .env.


### 4.3 Executando o docker-compose.yml

Todos os serviços estão configurados no arquivo docker-compose.yml, para executar basta criar o arquivo no servidor e executar:

```cmd
sudo docker compose up -d --build
```

### 4.4 Recuperar a senha inicial para o Jenkins

Para recuperar a senha inicial do Jenkins basta executar o comando abaixo no terminal do servidor:

```cmd
sudo docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Ou listar o log do conteiner do Jenkins:

```cmd
docker logs -f jenkins
```

### 4.5 Ajustando o arquivo com os certificados SSL

Caso ocorra algum problema de permissão quando o Traefik tentar armazenar os certificados SSL gerados pelo Let\`s Encrypt, criar o arquivo `acme.json` e dar permissão:

```cmd
touch acme.json

chmod 600 acme.json
```