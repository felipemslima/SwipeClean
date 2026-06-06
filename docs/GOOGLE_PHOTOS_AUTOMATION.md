# Apagar do Google Fotos — assistido manual

> **Branch `gphotos-automation`.** Integração do SwipeClean com o Google Fotos
> para remover também da nuvem. Mantida separada da `master` (que é só local).

## Por que assistido manual (e não automático)

- A **API oficial do Google Fotos não permite apagar** fotos.
- **Auto-selecionar** suas fotos na grade do Google Fotos **não é confiável**:
  a grade não expõe a identidade dos arquivos, então um app não consegue marcar
  exatamente as fotos que você escolheu.
- A tentativa anterior (AccessibilityService clicando foto a foto) era **lenta e
  falhava** ("falha ao remover para a lixeira"). Foi removida.

A triagem — a parte chata de decidir o que apagar — já é feita no swipe. A
exclusão na nuvem fica como um passo manual rápido e **sem falhas**.

## Como funciona (tela "Apagar do Google Fotos")

Acessível pelo botão **"Apagar do Google Fotos (manual)"** na tela de Revisão.

1. Mostra as fotos marcadas no lote.
2. **"Abrir Google Fotos"** → abre o app; você segura uma foto para entrar no
   modo seleção, marca as do lote e toca na **lixeira** (apaga do aparelho e da
   nuvem de uma vez, com um confirm só).
3. Alternativa: tocar numa **miniatura** abre aquela foto direto no Google Fotos
   para você mandar pra lixeira individualmente.

## Arquivos

```
gphotos/GPhotosHelpers.kt    # intents: abrir foto / abrir app, checar instalado
ui/screens/GPhotosScreen.kt  # tela assistida (grade + abrir Google Fotos)
AndroidManifest.xml          # <queries> do Google Fotos (visibilidade do pacote)
```

> A automação por acessibilidade foi removida (sem `<service>`, sem
> `AccessibilityService`). Histórico no git desta branch, se quiser revisitar.

## Versão distribuível

A `master` não tem nada de Google Fotos — é a versão limpa/Play-safe:

```
git checkout master
```
