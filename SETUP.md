# Setup do zero — rodar o SwipeClean

Guia para quem **nunca** mexeu com Android. No fim, o app roda no seu celular.

## 1. Instalar o Android Studio

O Android Studio já traz tudo junto: o editor, o JDK (Java) e o SDK do Android.

1. Baixe em <https://developer.android.com/studio>.
2. Instale com as opções padrão (deixe marcado "Android SDK", "SDK Platform" e
   "Android Virtual Device").
3. Abra o Android Studio uma vez e deixe ele terminar de baixar os componentes
   iniciais (a primeira abertura baixa o SDK — pode demorar).

## 2. Abrir o projeto

1. Android Studio → **File ▸ Open**.
2. Selecione a pasta `C:\Repos\SwipeClean` e confirme.
3. Ele vai fazer o **Gradle Sync** (baixa Gradle 8.7 e as dependências). Espere
   terminar — a barra de status para de girar e aparece "Sync successful".
   - Se ele reclamar que falta o `gradle-wrapper.jar`, aceite a sugestão de
     gerar o wrapper (ou rode no terminal embutido: `gradle wrapper`). O Android
     Studio normalmente resolve isso sozinho no Open.
   - Se pedir para instalar uma versão do SDK / Build Tools, clique para aceitar.

## 3. Escolher onde rodar

### Opção A — seu celular (recomendado, testa o cartão SD de verdade)

1. No celular: **Configurações ▸ Sobre o telefone ▸** toque 7× em "Número da
   versão" para liberar o **Modo desenvolvedor**.
2. Em **Opções do desenvolvedor**, ligue **Depuração USB**.
3. Conecte o celular no PC por USB e autorize a depuração quando aparecer o
   aviso no celular.
4. No topo do Android Studio, seu aparelho deve aparecer na lista de devices.

### Opção B — emulador (não tem cartão SD físico)

1. Android Studio → **Device Manager ▸ Create Device**.
2. Escolha um celular (ex. Pixel 6), uma imagem de sistema **API 33 ou 34**,
   finalize e inicie o emulador.

## 4. Rodar

1. Selecione o device no topo.
2. Clique no botão ▶ **Run 'app'** (ou `Shift+F10`).
3. O app instala e abre. Toque em **Permitir acesso às fotos**.
4. Pronto: escolha um álbum e comece a deslizar.

## Erros comuns

| Sintoma | Solução |
|---|---|
| "SDK location not found" | Android Studio cria `local.properties` no Sync. Se não, em **File ▸ Project Structure ▸ SDK Location** aponte o Android SDK. |
| Sync falha baixando dependências | Verifique a internet/proxy e clique em **Sync Project with Gradle Files** (ícone do elefante). |
| App não lista fotos | Confirme que deu a permissão de imagens; no emulador, adicione fotos pela galeria antes. |
| Cartão SD não aparece | Só testa em celular físico com SD; o app já lê todos os volumes. |

## Gerar o APK (instalar sem o PC depois)

Android Studio → **Build ▸ Build Bundle(s) / APK(s) ▸ Build APK(s)**. O `.apk`
sai em `app/build/outputs/apk/debug/`. Copie para o celular e instale
(permita "instalar de fontes desconhecidas").
