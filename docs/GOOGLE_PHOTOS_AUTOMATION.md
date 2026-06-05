# Automação de exclusão no Google Fotos (experimental)

> **Branch `gphotos-automation`.** Este recurso usa um **AccessibilityService**
> para dirigir o app do Google Fotos. **Não é publicável na Play Store** (política
> de acessibilidade) — por isso fica separado da `master`, que continua a versão
> distribuível. Uso pessoal.

## Por que assim

A API oficial do Google Fotos **não permite apagar** fotos. O único jeito de
"swipe → apaga na nuvem de verdade" automático é simular os toques no app do
Google Fotos. É frágil: depende dos textos/telas do Google Fotos.

## Como funciona

1. Você revisa as fotos no SwipeClean (swipe normal) e vai para **Revisão**.
2. Toca em **"Apagar do Google Fotos (beta)"**.
3. Liga o **serviço de acessibilidade** do SwipeClean (a tela tem o botão que
   abre Configurações ▸ Acessibilidade).
4. Toca **"Apagar do Google Fotos"**. O app então, para cada foto marcada:
   - abre a foto no Google Fotos (`ACTION_VIEW`);
   - o serviço acha o botão **apagar/lixeira** (direto ou via "mais opções") e clica;
   - acha o botão de **confirmação** no diálogo e clica;
   - abre a próxima foto.
5. No fim, volta ao SwipeClean ("Concluído").

Como o Google Fotos apaga uma foto com backup **do aparelho e da nuvem**, isso
resolve o problema de a foto continuar no Google Fotos.

## Arquivos

```
gphotos/
├── GPhotosQueue.kt          # estado compartilhado UI ↔ serviço (fila, fase, log)
├── GPhotosHelpers.kt        # intent de abrir no GPhotos, checagens, abrir settings
└── GPhotosDeleteService.kt  # AccessibilityService que clica apagar/confirmar
ui/screens/GPhotosScreen.kt  # tela: status do serviço, iniciar, progresso
res/xml/gphotos_accessibility_config.xml
AndroidManifest.xml          # <service> de acessibilidade + <queries> do GPhotos
```

## Limitações / avisos

- **Idioma:** as palavras procuradas estão em **PT-BR e EN**
  (`DELETE_WORDS`, `MORE_WORDS`, `CONFIRM_WORDS` em `GPhotosDeleteService.kt`).
  Se o Google Fotos estiver em outro idioma, **adicione as palavras** lá.
- **Mudanças do Google Fotos:** se a tela mudar, o serviço pode não achar o
  botão; ele tenta algumas vezes e **pula** a foto.
- **Não desligue a tela** durante o processo.
- **Velocidade:** há esperas (`STEP_MS`) para a tela assentar; é proposital.
- Não publicar na Play Store com este serviço ativo.

## Como ajustar (debug)

Veja os passos no `logcat` filtrando pela tag **`GPhotosDelete`**. Se um botão
não for encontrado, anote o texto/descrição real do botão no Google Fotos e
acrescente às listas de palavras em `GPhotosDeleteService.kt`.

## Voltar para a versão limpa

```
git checkout master
```
A `master` não tem nada de acessibilidade — é a versão distribuível.
