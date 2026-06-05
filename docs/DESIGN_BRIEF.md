# SwipeClean — Brief de design (UI)

## O que é
App Android para **limpar fotos rapidamente por gestos**: o usuário vê uma foto
por vez e desliza para a **esquerda (apagar)** ou **direita (manter)**, com
botões equivalentes. No fim, confirma a exclusão em lote. Alternativa enxuta ao
"Delete Swipe".

## Público e tom
Usuário comum que quer desafogar a galeria sem fricção. Tom desejado: **rápido,
leve, satisfatório** (sensação de "limpeza"), e **confiável** na hora de apagar.
Sem poluição visual, sem anúncios.

## Diferenciais (devem transparecer na UI)
- **Sem anúncios / sem paywall.**
- **Lê o cartão SD** além do armazenamento interno (mostrar a origem do volume
  pode ser um detalhe útil).
- **Undo de verdade** — nada é apagado até a confirmação final; desfazer deve
  ser óbvio e acessível.
- **Mostra espaço liberado** (MB/GB) — métrica de recompensa.
- **Lembra as fotos já mantidas** — quem tem biblioteca grande não revê as
  mesmas fotos toda sessão. A UI deve comunicar progresso ("já revisadas") e
  oferecer um jeito de "rever tudo de novo".

## Stack (restrições técnicas)
- **Jetpack Compose + Material 3** (Kotlin). O design precisa ser expressável em
  Compose/M3.
- `minSdk 30` (Android 11+). Suporta **light e dark** (já há `SwipeCleanTheme`).
- Imagens via Coil. Cores atuais: verde = manter (`#2E7D32`), vermelho = apagar
  (`#C62828`).

## Telas atuais (e o que melhorar)

**1. Permissão**
Hoje: título + texto + 1 botão centralizado. Cru.
Melhorar: boas-vindas com identidade, ícone/ilustração, benefício em 1 frase
(incl. "lê o cartão SD").

**2. Álbuns**
Hoje: botão "Revisar todas" + grade 2 colunas de cards (capa, nome,
"N fotos · tamanho").
Melhorar: hierarquia dos cards, header com total de fotos/espaço, indicar
quantas faltam revisar por álbum, estados vazio e carregando mais agradáveis.

**3. Swipe (tela principal — coração do app)**
Hoje: top bar com "X de N" + undo + "Apagar (N)"; card grande com a foto; selos
"MANTER"/"APAGAR" ao arrastar; rodapé com 3 botões (Apagar / Desfazer / Manter);
legenda nome+tamanho.
Melhorar: gesto **tátil e satisfatório** (cor/escala/haptic ao arrastar, foto da
próxima atrás numa pilha, animação de saída do card), botões ergonômicos para o
polegar, indicador de progresso claro.

**4. Revisão / confirmar exclusão**
Hoje: "N fotos · libera X" + grade 3 colunas das marcadas + botão vermelho
"Apagar N definitivamente".
Melhorar: destacar a recompensa (espaço liberado), permitir desmarcar item antes
de confirmar, ação destrutiva clara mas não assustadora.

## Fluxo
Permissão → Álbuns → Swipe (esq apaga / dir mantém / undo) → Revisão →
confirmação do sistema → volta.

## O que eu quero do design
1. Identidade visual (paleta, tipografia, ícone/logo, cantos/elevação) coerente
   com Material 3.
2. Redesenho das 4 telas (mockups), foco na tela de Swipe.
3. Microinterações: animação do card, feedback ao arrastar, transição de saída,
   haptics.
4. Estados: carregando, vazio, fim da sessão, "tudo já revisado", dark mode.
5. Componentes reutilizáveis (cards, botões de ação, selos manter/apagar,
   indicador de progresso).

## Fora de escopo (por enquanto)
Vídeos, duplicatas, favoritos, multiplataforma. MVP é só fotos.

## Onde cada tela mora (para referência de implementação)
- `app/src/main/java/com/felipe/swipeclean/ui/screens/PermissionScreen.kt`
- `.../ui/screens/AlbumsScreen.kt`
- `.../ui/screens/SwipeScreen.kt`
- `.../ui/screens/ReviewScreen.kt`
- Tema/cores: `.../ui/theme/Theme.kt`
- Estado/lógica: `.../ui/SwipeViewModel.kt`
