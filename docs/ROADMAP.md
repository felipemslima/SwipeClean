# Roadmap — SwipeClean

## MVP (feito)

- [x] Ler fotos de todos os volumes (incl. cartão SD) via MediaStore
- [x] Listar álbuns com capa, contagem e tamanho
- [x] Swipe esquerda/direita + botões apagar/manter
- [x] Undo (desfazer decisão antes de confirmar)
- [x] Revisão em lote com espaço a liberar
- [x] Exclusão confirmada pelo sistema (funciona no SD)
- [x] Sem anúncios, sem paywall

## Próximos

- [x] Lembrar fotos já mantidas entre sessões (`KeepStore` em SharedPreferences); pula no swipe, mostra "N a revisar" por álbum e tem "Rever fotos mantidas" no menu
- [ ] Agrupar também por **mês** (além de álbum) — usar `DATE_ADDED`
- [ ] Animar os botões com o mesmo swipe do gesto
- [ ] Ícone próprio do app (adaptive icon) no lugar do ícone padrão
- [ ] Suporte a vídeos (MediaStore.Video) além de imagens
- [ ] Modo "pilha" mostrando a próxima foto atrás da atual
- [ ] Estatística da sessão (quantas apagadas, espaço liberado total)
- [ ] Testes em aparelhos com SD e com Android 11–14

## Ideias futuras

- [ ] Detecção de duplicatas / fotos parecidas
- [ ] Favoritar em vez de só manter/apagar
- [ ] Backup opcional antes de apagar
