# SwipeClean for Google Photos (userscript)

Triagem rápida no **Google Fotos web** (`photos.google.com`), no desktop, com o
teclado — tipo o swipe do app, mas direto na nuvem (apaga de verdade do Google
Fotos).

## Por que web e não Android

A API do Google Fotos não deleta, e no app Android a auto-seleção é inviável.
Já no **site** (desktop) o Google Fotos **permite apagar** e a página é acessível
por script — então dá pra fazer a triagem rápida aqui.

## Instalar

1. Instale a extensão **Tampermonkey** (Chrome/Edge/Firefox/Brave).
2. Abra o Tampermonkey → **Create a new script**.
3. Cole o conteúdo de [`swipeclean-gphotos.user.js`](swipeclean-gphotos.user.js)
   e salve (Ctrl+S). (Ou arraste o arquivo `.user.js` para o navegador.)
4. Abra `https://photos.google.com` e **abra uma foto**.

## Usar

Funciona na **grade** (timeline) do Google Fotos:

1. Use as **setas** para mover o foco entre as fotos (navegação nativa).
2. **S** → seleciona/desmarca a foto focada.
3. Repita S nas fotos que quer apagar.
4. **D** → apaga **todas as selecionadas de uma vez** (lote, com a confirmação do Google).

| Tecla | Ação |
|---|---|
| **S** | selecionar / desmarcar a foto atual |
| **D** | apagar todas as selecionadas (em lote) |
| **H** | esconder / mostrar o HUD |

Um HUD discreto no canto inferior esquerdo mostra as teclas e o status da última
ação. Atrapalhou? Tecle **H** para esconder.

> Apagar no Google Fotos web move para a **Lixeira** (fica ~60 dias lá). Para
> liberar espaço na hora, esvazie a Lixeira depois.

## Idiomas

O script acha o botão lixeira/confirmar por `aria-label`/texto em PT, EN, ES, FR,
DE, IT, PL. Se o seu Google Fotos estiver em outro idioma e o **D** não funcionar,
edite os regex `TRASH_RE` / `CONFIRM_RE` no topo do script e adicione o texto.

## Limites

- Funciona na interface atual do Google Fotos web; se o Google mudar muito a
  página, pode precisar de ajuste.
- Não faz multi-seleção automática — é foto a foto, mas é rápido (1 tecla cada).
