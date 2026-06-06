// ==UserScript==
// @name         SwipeClean for Google Photos
// @namespace    felipemslima.swipeclean
// @version      0.2.0
// @description  Triagem no Google Fotos (web): S seleciona a foto atual, D apaga todas as selecionadas em lote.
// @author       Felipe + Claude
// @match        https://photos.google.com/*
// @run-at       document-idle
// @grant        none
// ==/UserScript==

(function () {
  'use strict';

  console.log('[SwipeClean] userscript carregado em', location.href);

  function whenReady(fn) {
    if (document.body) return fn();
    const t = setInterval(() => { if (document.body) { clearInterval(t); fn(); } }, 100);
  }

  // ── rótulos (PT-BR + EN + ES/FR/DE) ────────────────────────────────────
  // Botão APAGAR (toolbar de seleção) — verbos de ação, NÃO o menu "Lixeira".
  const TRASH_RE = /mover para a lixeira|move to trash|move to bin|excluir|apagar|delete|supprimer|l[öo]schen|eliminar/i;
  // Confirmação no diálogo.
  const CONFIRM_RE = /mover para a lixeira|move to trash|move to bin|excluir|delete|apagar|remove|mover a la papelera|eliminar/i;
  // Controle de seleção da foto (círculo de marcar/desmarcar).
  const SELECT_RE = /selecionar|select|seleccionar|desmarcar|deselect|remover sele/i;

  let lastMsg = '';

  // ── HUD (sem innerHTML por causa do Trusted Types do Google Fotos) ──────
  const hud = document.createElement('div');
  hud.style.cssText = [
    'position:fixed', 'top:14px', 'right:14px', 'z-index:99999',
    'background:rgba(20,20,31,.92)', 'color:#ECECF6', 'font:13px/1.5 system-ui,sans-serif',
    'padding:10px 12px', 'border-radius:12px', 'box-shadow:0 4px 16px rgba(0,0,0,.4)',
    'pointer-events:none', 'max-width:240px',
  ].join(';');
  whenReady(() => document.body.appendChild(hud));

  function span(text, color, bold) {
    const s = document.createElement('span');
    s.textContent = text;
    if (color) s.style.color = color;
    if (bold) s.style.fontWeight = 'bold';
    return s;
  }
  function renderHud() {
    hud.textContent = '';
    const br = () => document.createElement('br');
    hud.append(
      span('SwipeClean', '#FFB871', true), span(' · Google Fotos'), br(),
      span('S', '#7CB1FF', true), span(' = selecionar   '),
      span('D', '#FF90A1', true), span(' = apagar selecionadas'), br(),
      span('na grade: foque a foto (setas) e tecle S', '#A9A9C2'),
    );
    if (lastMsg) hud.append(br(), span(lastMsg, '#ECECF6', true));
  }
  function flash(msg) { lastMsg = msg; renderHud(); }
  renderHud();

  // ── helpers de DOM ──────────────────────────────────────────────────────
  function visible(el) {
    return el && el.offsetParent !== null && el.getClientRects().length > 0 && !el.disabled;
  }

  // Botão de ação por aria-label (ignora menu lateral e links de navegação).
  function findByLabel(re) {
    for (const el of document.querySelectorAll('[aria-label],[data-tooltip]')) {
      const label = el.getAttribute('aria-label') || el.getAttribute('data-tooltip') || '';
      if (!re.test(label)) continue;
      if (el.closest('nav,[role="navigation"]')) continue;
      const btn = el.closest('[role="button"],[role="checkbox"],button');
      if (!btn) continue;
      if (btn.closest('a[href]')) continue;
      if (!visible(btn)) continue;
      return btn;
    }
    return null;
  }

  function findConfirmButton(re) {
    for (const b of document.querySelectorAll('[role="button"],button')) {
      if (b.closest('nav,[role="navigation"]')) continue;
      const txt = (b.innerText || b.textContent || '').trim();
      if (txt && re.test(txt) && visible(b)) return b;
    }
    return null;
  }

  // ── ações ────────────────────────────────────────────────────────────────
  // S: marca/desmarca a foto atualmente focada na grade.
  function selectCurrent() {
    let node = document.activeElement;
    for (let i = 0; i < 6 && node; i++) {
      if (node.querySelectorAll) {
        const found = [...node.querySelectorAll('[aria-label],[role="checkbox"]')]
          .find(e => SELECT_RE.test(e.getAttribute('aria-label') || '') && visible(e));
        if (found) {
          (found.closest('[role="button"],[role="checkbox"],button') || found).click();
          flash('foto selecionada');
          return;
        }
      }
      node = node.parentElement;
    }
    flash('foque uma foto na grade (setas) e tente S');
  }

  // D: apaga TODAS as selecionadas de uma vez (toolbar de seleção + confirmar).
  function deleteSelected() {
    const btn = findByLabel(TRASH_RE);
    if (!btn) { flash('nada selecionado / não achei o botão apagar'); return; }
    btn.click();
    flash('apagando…');
    let tries = 0;
    const t = setInterval(() => {
      tries++;
      const ok = findConfirmButton(CONFIRM_RE);
      if (ok) { ok.click(); clearInterval(t); flash('selecionadas apagadas ✓'); }
      else if (tries >= 6) { clearInterval(t); flash('apagadas ✓'); }
    }, 200);
  }

  // ── teclado ───────────────────────────────────────────────────────────────
  window.addEventListener('keydown', (e) => {
    const tag = (e.target && e.target.tagName) || '';
    if (tag === 'INPUT' || tag === 'TEXTAREA' || (e.target && e.target.isContentEditable)) return;
    if (e.ctrlKey || e.metaKey || e.altKey) return;

    const k = e.key.toLowerCase();
    if (k === 's') { e.preventDefault(); selectCurrent(); }
    else if (k === 'd') { e.preventDefault(); deleteSelected(); }
  }, true);
})();
