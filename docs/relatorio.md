# Googol

---
title: Relatório de Sistemas Distríbuidos
lang: pt-PT
toc: true
author:
  - Luís Góis, nº 2018280716
  - João Gonçalo Reis Lopes, nº 2012170913
date: \today
header-includes: |
    \usepackage[utf8]{inputenc}
    \usepackage{fdsymbol}
    \usepackage{newunicodechar}
    \usepackage[a4paper, margin=1in]{geometry}
    \usepackage{float}
    \makeatletter
    \def\fps@figure{H}
    \makeatother
---

## Resumo

Este projeto tem como objetivo criar um motor de pesquisa de páginas Web.
Pretende-se que tenha funcionalidades semelhantes aos serviços Google.com,
Qwant.com e Ecosia.org, incluindo indexação automática (Web crawler) e busca
(search engine). O sistema deverá apresentar di- versas informações relevantes
sobre cada página, tais como o URL, o título, uma citação de texto e outras que
considere importantes. Ao realizar uma busca, obtém-se a lista de páginas que
contenham as palavras pesquisadas ordenada por relevância. Os utilizadores
podem sugerir URLs para serem indexados pelo sistema. Partindo desses URLs, o
sistema construir o índice recursivamente/iterativamente para todas as ligações
encontradas em cada página.

## Arquitetura de software detalhadamente descrita

A arquitetura do nosso motor de pesquisa é composta por quatro principais componentes:

### IndexStorageBarrel

Esta componente é responsável pelo armazenamento e gestão dos índices das
páginas Web. Utiliza uma estrutura de dados eficiente para suportar a rápida
recuperação de informações durante as consultas.

### Downloader

O Downloader é a parte do sistema encarregada de buscar o conteúdo das páginas
Web. Ele utiliza técnicas de multitasking para que várias páginas possam ser
baixadas ao mesmo tempo, garantindo uma maior eficiência.

### Gateway

O Gateway atua como a interface entre o utilizador e o sistema. Ele recebe as
consultas feitas pelos utilizadores, processa-as e encaminha-as para as
componentes adequadas do sistema. Após obter os resultados, o Gateway os
formata e apresenta-os para os utilizadores.

### Client

O Client é a aplicação que os utilizadores irão interagir. Que permitirá aos
utilizadores realizar buscas, visualizar resultados e sugerir novos URLs para
indexação.

## Detalhes do funcionamento da replicação (reliable multicast)

## Detalhes do funcionamento da componente RMI (interface, javadocs, etc.)

GatewayI.java

```java
!include ./src/main/java/com/googol/GatewayI.java
```

IndexStorageBarrelI.java

```java
!include ./src/main/java/com/googol/IndexStorageBarrelI.java
```

## Distribuição de tarefas pelos elementos do grupo

## Testes de software (tabela com descrição + pass/fail de cada teste)

