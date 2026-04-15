# 📱 Hub App Lab

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![API](https://img.shields.io/badge/API%2025%2B-brightgreen?style=for-the-badge&logo=android&logoColor=white)

Um aplicativo Android nativo que funciona como **hub central** para os projetos desenvolvidos pela turma **AppLab Mobile UFRN 2026.1**. A tela principal exibe cards de acesso direto a cada mini-aplicativo integrado, permitindo navegar entre eles sem sair do app.

---

## 🎬 Demonstração

> *(vídeo será adicionado após a gravação)*

---

## ✨ Aplicativos Integrados

### 🏀 Partida de Basquete
Placar imersivo que simula um painel eletrônico de ginásio real. Controle completo de pontuação, cronômetro regressivo de 10 min (padrão FIBA) acionado pelo toque no display, e detecção automática de vitória ao atingir 100 pontos.

> 🔗 [Repositório original](https://github.com/AppLab-Mobile-UFRN-2026-1/App-PartidaDeBasquete)

---

### 🧮 Calculadora
Calculadora inspirada no design do iOS com dois modos de operação — padrão e científico. Avalia expressões via parser recursivo descendente, exibe expoentes com Unicode superscript, mantém histórico persistente em Bottom Sheet e preserva estado completo na rotação de tela.

> 🔗 [Repositório original](https://github.com/AppLab-Mobile-UFRN-2026-1/App-Calculadora)

---

### 🎯 Matriz de Foco Físico
Implementação gestual da Matriz de Eisenhower: tarefas viram bolhas arrastáveis que o usuário solta nos quadrantes — ao pousar, cada bolha quica com animação de mola. Persistência via SharedPreferences + Gson.

> 🔗 [Repositório original](https://github.com/AppLab-Mobile-UFRN-2026-1/App-MatrizDeFocoFisico)

---

## 📂 Estrutura do Projeto

```text
HubAppLab/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/applab/hub/
│   │   │   │   ├── MainActivity.kt                    # Hub: navega para cada mini-app via cards
│   │   │   │   ├── basquete/
│   │   │   │   │   └── BasqueteActivity.kt            # Lógica do placar, cronômetro e regras
│   │   │   │   ├── calculadora/
│   │   │   │   │   └── CalculadoraActivity.kt         # Motor de cálculo, ExprParser e histórico
│   │   │   │   └── matrizfoco/
│   │   │   │       ├── MatrizFocoActivity.kt          # UI, gestos e animações de mola
│   │   │   │       └── data/
│   │   │   │           ├── Quadrant.kt                # Enum dos 4 quadrantes
│   │   │   │           ├── Task.kt                    # Modelo de dados da tarefa
│   │   │   │           └── TaskRepository.kt          # Leitura/escrita no SharedPreferences
│   │   │   ├── res/
│   │   │   │   ├── drawable/                          # Ícones e shapes customizados (25 arquivos)
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml              # Tela do hub (cards de navegação)
│   │   │   │   │   ├── basquete_activity_main.xml     # Interface do placar
│   │   │   │   │   ├── calc_activity_main.xml         # Layout portrait da calculadora
│   │   │   │   │   ├── calc_bottom_sheet_history.xml  # Painel de histórico da calculadora
│   │   │   │   │   ├── matriz_activity_main.xml       # Grade de quadrantes e overlay de bolhas
│   │   │   │   │   ├── matriz_dialog_editar_tarefa.xml# Dialog customizado de edição
│   │   │   │   │   └── matriz_item_bubble.xml         # Layout de cada bolha da matriz
│   │   │   │   ├── layout-land/                       # Layout landscape (calculadora científica)
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml                     # Paleta de cores global
│   │   │   │       ├── dimens.xml                     # Dimensões reutilizáveis
│   │   │   │       ├── strings.xml                    # Textos e rótulos
│   │   │   │       └── themes.xml                     # Temas por Activity
│   │   │   └── AndroidManifest.xml                    # Atividades e configurações globais
│   └── build.gradle.kts                               # Dependências e configurações do módulo
└── build.gradle.kts                                   # Configurações de build globais
```

---

## 🛠️ Tecnologias Utilizadas

| Categoria | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| SDK | API 36 (compileSdk) · Min: API 25 |
| UI | XML Layouts (ConstraintLayout, LinearLayout, RelativeLayout) |
| Componentes Material | `MaterialCardView` |
| Componentes Nativos | `CountDownTimer`, `AlertDialog`, `Toast`, `BottomSheetDialog`, `onSaveInstanceState` |
| Persistência | `SharedPreferences` + Gson |
| Animações | `androidx.dynamicanimation` (SpringAnimation) |
| Gestos | `setOnTouchListener` (ACTION_DOWN / MOVE / UP) |
| Estilização | Drawables customizados (`<shape>`, `<ripple>`, `<solid>`) |
| IDE | Android Studio |

---

## 💻 Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:
* [Git](https://git-scm.com) para clonar o repositório.
* [Android Studio](https://developer.android.com/studio) para rodar e editar o código.

## 🚀 Como executar o projeto

1. Abra o seu terminal e faça o clone deste repositório:
   ```bash
   git clone https://github.com/AppLab-Mobile-UFRN-2026-1/App-Hub.git
   ```

2. Abra o Android Studio.

3. Na tela inicial, clique em **Open** e selecione a pasta do projeto que você acabou de clonar.

4. Aguarde o Gradle sincronizar todas as dependências.

5. Conecte o seu celular Android via cabo USB ou inicie um Emulador pelo **Device Manager**.

6. Clique no botão verde de **Run** (▶️) na barra superior ou pressione **Shift + F10** para rodar o aplicativo!

---

## 👥 Equipe de Desenvolvimento

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/leonardonadson">
        <img src="https://avatars.githubusercontent.com/leonardonadson" width="100px;" alt="Foto de Leonardo Nadson no GitHub"/>
        <br>
        <sub>
          <b>Leonardo Nadson</b>
        </sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/luan-sampaio">
        <img src="https://avatars.githubusercontent.com/luan-sampaio" width="100px;" alt="Foto de Luan Sampaio no GitHub"/>
        <br>
        <sub>
          <b>Luan Sampaio</b>
        </sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/MarcusAurelius33">
        <img src="https://avatars.githubusercontent.com/MarcusAurelius33" width="100px;" alt="Foto de Marcus Aurelius no GitHub"/>
        <br>
        <sub>
          <b>Marcus Aurelius</b>
        </sub>
      </a>
    </td>
  </tr>
</table>
