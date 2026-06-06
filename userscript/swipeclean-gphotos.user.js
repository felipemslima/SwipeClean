// ==UserScript==
// @name         SwipeClean for Google Photos
// @namespace    felipemslima.swipeclean
// @version      0.1.3
// @description  Triagem rápida no Google Fotos (web): D apaga (lixeira), seta direita mantém. HUD com contador.
// @author       Felipe + Claude
// @match        https://photos.google.com/*
// @run-at       document-idle
// @grant        none
// ==/UserScript==

(function () {
  'use strict';

  console.log('[SwipeClean] userscript carregado em', location.href);

  // Garante que o body existe antes de inserir o HUD (SPA pode demorar).
  function whenReady(fn) {
    if (document.body) return fn();
    const t = setInterval(() => {
      if (document.body) { clearInterval(t); fn(); }
    }, 100);
  }

  // Rótulos do botão "lixeira" e do botão de confirmação, em vários idiomas.
  // Se o seu Google Fotos estiver em outro idioma e não funcionar, adicione o
  // texto do botão aqui.
  // Verbos de AÇÃO do botão apagar (NÃO o item "Lixeira" do menu lateral).
  const TRASH_RE = /mover para a lixeira|move to trash|move to bin|enviar para a reciclagem|excluir|apagar|delete|supprimer|l[öo]schen|eliminar/i;
  const CONFIRM_RE = /mover para a lixeira|move to trash|move to bin|excluir|delete|apagar|remove|mover a la papelera|eliminar/i;

  let deleted = 0;
  let busy = false;

  // ── HUD ────────────────────────────────────────────────────────────────
  const hud = document.createElement('div');
  hud.style.cssText = [
    'position:fixed', 'top:14px', 'right:14px', 'z-index:99999',
    'background:rgba(20,20,31,.92)', 'color:#ECECF6', 'font:13px/1.4 system-ui,sans-serif',
    'padding:10px 12px', 'border-radius:12px', 'box-shadow:0 4px 16px rgba(0,0,0,.4)',
    'pointer-events:none', 'max-width:230px',
  ].join(';');
  whenReady(() => document.body.appendChild(hud));

  // Sem innerHTML: o Google Fotos usa Trusted Types (CSP) e bloqueia innerHTML.
  // Montamos o HUD com createElement/textContent.
  function span(text, color, bold) {
    const s = document.createElement('span');
    s.textContent = text;
    if (color) s.style.color = color;
    if (bold) s.style.fontWeight = 'bold';
    return s;
  }
  function renderHud(msg) {
    const inPhoto = location.pathname.includes('/photo/');
    hud.textContent = '';
    const br = () => document.createElement('br');
    hud.append(
      span('SwipeClean', '#FFB871', true), span(' · Google Fotos'), br(),
      span('D', '#FF90A1', true), span(' = apagar   '),
      span('→', '#7CB1FF', true), span(' = manter'), br(),
      span('apagadas: '), span(String(deleted), null, true),
    );
    if (!inPhoto) hud.append(br(), span('abra uma foto para começar', '#A9A9C2'));
    if (msg) hud.append(br(), span(msg, '#A9A9C2'));
  }
  renderHud();
  setInterval(renderHud, 1000); // some/aparece conforme você entra/sai da foto

  // ── helpers de DOM ──────────────────────────────────────────────────────
  function visible(el) {
    return el && el.offsetParent !== null &&
      el.getClientRects().length > 0 && !el.disabled;
  }

  // Acha o BOTÃO de apagar (não o link "Lixeira" do menu lateral).
  function findByLabel(re) {
    const nodes = document.querySelectorAll('[aria-label],[data-tooltip]');
    for (const el of nodes) {
      const label = (el.getAttribute('aria-label') || el.getAttribute('data-tooltip') || '');
      if (!re.test(label)) continue;
      if (el.closest('nav,[role="navigation"]')) continue;   // ignora menu lateral
      const btn = el.closest('[role="button"],button');
      if (!btn) continue;                                     // só botão, não link
      if (btn.closest('a[href]')) continue;                   // não é link de navegação
      if (!visible(btn)) continue;
      return btn;
    }
    return null;
  }

  function findConfirmButton(re) {
    const btns = document.querySelectorAll('[role="button"],button');
    for (const b of btns) {
      if (b.closest('nav,[role="navigation"]')) continue;
      const txt = (b.innerText || b.textContent || '').trim();
      if (txt && re.test(txt) && visible(b)) return b;
    }
    return null;
  }

  // ── ações ────────────────────────────────────────────────────────────────
  function trashCurrent() {
    if (busy) return;
    if (!location.pathname.includes('/photo/')) { renderHud('abra uma foto'); return; }
    const btn = findByLabel(TRASH_RE);
    if (!btn) { renderHud('não achei a lixeira nesta tela'); return; }
    busy = true;
    btn.click();

    // Alguns idiomas/versões mostram um diálogo de confirmação; clica nele.
    let tries = 0;
    const t = setInterval(() => {
      tries++;
      const ok = findConfirmButton(CONFIRM_RE);
      if (ok) { ok.click(); clearInterval(t); finishDelete(); }
      else if (tries >= 6) { clearInterval(t); finishDelete(); } // sem diálogo (só toast)
    }, 200);
  }

  function finishDelete() {
    deleted++;
    renderHud('apagada ✓');
    // O Google Fotos costuma avançar sozinho; se não, vai para a próxima.
    setTimeout(() => { busy = false; }, 400);
  }

  function next() {
    // ArrowRight nativo do visualizador navega para a próxima foto.
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight', bubbles: true }));
  }

  // ── teclado ───────────────────────────────────────────────────────────────
  window.addEventListener('keydown', (e) => {
    const tag = (e.target && e.target.tagName) || '';
    if (tag === 'INPUT' || tag === 'TEXTAREA' || e.target.isContentEditable) return;

    const k = e.key.toLowerCase();
    if (k === 'd' || e.key === 'Delete') {
      e.preventDefault();
      e.stopPropagation();
      trashCurrent();
    } else if (k === 'k') {
      e.preventDefault();
      next();
    }
    // a seta direita (→) já é nativa do Google Fotos para "próxima"
  }, true);
})();
