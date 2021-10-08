<h1> Microsserviços em Java + AWS </h1>
<hr>
<p>Projeto de Microsserviços utilizando Spring Boot em cluster com AWS CDK, ECS, SNS, SQS, RDS, DynamoDB e S3.</p>

<h3>Pré-Requisitos</h3>

```
Docker Desktop;
Docker Hub;
Conta na AWS;
AWS CLI;
AWS CDK;
JDK 15+;
IDE de sua escolha(Utilizo IntelliJ);
MySQL Workbench
Postman
```

<hr>
<h4>Configurações</h4>
<p>Criando o CDK na Linguagem Java:</p>

```
cdk init app --language java
```

<p>Fazendo deployment da configuração da VPC:</p>

```
cdk deploy Vpc
```

<p>Fazendo deployment da configuração do Cluster:</p>

```
cdk deploy Vpc Cluster
```

<p>Fazendo deployment da configuração do Service:</p>

```
cdk deploy Vpc Cluster Service01
```

<p>Destruindo os serviços criados na AWS:</p>

```
cdk destroy Vpc Cluster Service01
```

<hr>
<h4>Inicialização: </h4>

```java
mvn spring-boot:run
```



