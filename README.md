<h1 align="center" style="font-weight: bold;">Leveling Life 💻</h1>

<p align="center">
  <a href="#functions">Funcionalidades</a> •
 <a href="#tech">Tecnologias Utilizadas</a> • 
 <a href="#started">Configuração e Execução</a> • 
 <a href="#contribute">Contribua</a>
</p>

<p align="center">
    <b>Leveling Life é uma aplicação desenvolvida para gamificar o desenvolvimento pessoal, proporcionando aos usuários um sistema de **quests**, **níveis** e **interação social** com amigos. Inspirada em RPGs e conceitos de gamificação, a aplicação visa tornar o dia a dia mais motivador e divertido.
</b>
</p>

<h2 id="functions">📍 Funcionalidades</h2>

- **Autenticação e Registro** 🔒: Segurança com autenticação JWT para proteger o acesso de usuários.
- **Sistema de Quests** 🎯: Os usuários podem criar e gerenciar quests e acompanhar seu progresso.
- **Níveis e XP** 🆙: Acumule XP e suba de nível com base nas quests concluídas.
- **Sistema de Amizade** 👥: Envie e aceite pedidos de amizade para acompanhar o progresso dos amigos.
- **Perfis Personalizados** 🖼️: Cada usuário tem um perfil com nome, foto e título.
- **Documentação com Swagger** 📖: A API é totalmente documentada com Swagger.
- **Testes Unitários** 🧪: JUnit e Mockito garantem a estabilidade das funcionalidades.


<h2 id="technologies">🛠️ Tecnologias Utilizadas</h2>

- **Spring Boot** 🌱: Estrutura robusta para o desenvolvimento da API RESTful.
- **JWT com Auth0** 🔑: Autenticação segura para gerenciamento de sessões de usuários.
- **Swagger** 📜: Documentação automática para as endpoints.
- **Docker** 🐳: Containers para isolar o ambiente e facilitar o desenvolvimento e a implantação.
- **Insomnia** 🌐: Ferramenta de teste para endpoints durante o desenvolvimento.
- **PostgreSQL** 🗄️: Banco de dados relacional utilizado para persistir os dados da aplicação.
- **JUnit e Mockito** 🧪: Ferramentas para testes unitários e de integração.

---

<h2 id="started">Configuração e Execução ⚙️</h2>

### Instalação e Execução

1. **Pré-requisitos**:
   - Java 11+
   - Docker
   - PostgreSQL
   - Maven

2. **Clonando o repositório**:
   ```bash
   git clone https://github.com/seu-usuario/api-levelinglife.git
   cd levelinglife
   ```

3. **Configuração do Banco de Dados**:
   - Configure o PostgreSQL em seu ambiente e ajuste as configurações no arquivo `application.properties`.

4. **Rodando a aplicação com Docker**:
   - Execute o comando abaixo para iniciar a aplicação em um container Docker:
     ```bash
     docker-compose up --build
     ```

## Testes 🧪

A aplicação conta com testes para validação das principais funcionalidades:

- **JUnit** ✅: Testes de unidade para os serviços.
- **Mockito** 🔄: Mocking de dependências para testar funcionalidades em isolamento.
- **Cobertura** 📊: Relatórios de cobertura para garantir a qualidade do código.

---

<h2 id="contribute"></h2>

## Contribuição 🤝

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

---

## Licença 📄

Este projeto está licenciado sob a MIT License.