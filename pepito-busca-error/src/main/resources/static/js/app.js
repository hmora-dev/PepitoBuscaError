/**
 * PepitoBuscaError — Interactive UI layer
 * Handles: sidebar toggle, animated counters, copy-to-clipboard,
 * form loading states, toast notifications, Chart.js dark theme,
 * live table search, keyboard shortcuts, page transitions.
 */
(function () {
  'use strict';

  /* ------------------------------------------------------------------ */
  /*  BOOT                                                                */
  /* ------------------------------------------------------------------ */
  document.addEventListener('DOMContentLoaded', function () {
    initSidebar();
    initCounters();
    initCopyButtons();
    initFormLoading();
    initToastFromUrl();
    initChartTheme();
    initTableSearch();
    initKeyboardShortcuts();
    initPageTransitions();
    initLiveTimestamps();
  });

  /* ------------------------------------------------------------------ */
  /*  SIDEBAR — mobile slide-in / overlay                                */
  /* ------------------------------------------------------------------ */
  function initSidebar() {
    var btn     = document.getElementById('mobile-menu-btn');
    var sidebar = document.querySelector('.app-sidebar');

    var overlay = document.getElementById('sidebar-overlay');
    if (!overlay) {
      overlay = document.createElement('div');
      overlay.id = 'sidebar-overlay';
      document.body.appendChild(overlay);
    }

    if (!btn || !sidebar) return;

    btn.addEventListener('click', openSidebar);
    overlay.addEventListener('click', closeSidebar);

    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') closeSidebar();
    });

    function openSidebar() {
      sidebar.classList.add('mobile-open');
      overlay.classList.add('active');
      document.body.style.overflow = 'hidden';
    }

    function closeSidebar() {
      sidebar.classList.remove('mobile-open');
      overlay.classList.remove('active');
      document.body.style.overflow = '';
    }
  }

  /* ------------------------------------------------------------------ */
  /*  ANIMATED COUNTERS                                                   */
  /* ------------------------------------------------------------------ */
  function initCounters() {
    var selectors = [
      '.metric-card strong',
      '.stat-card strong',
      '.dark-metric-panel strong',
      '.dark-metrics strong',
      '.osint-category-stat strong',
      '.severity-bar-card strong'
    ];

    var targets = document.querySelectorAll(selectors.join(','));
    if (!targets.length || !('IntersectionObserver' in window)) return;

    var observer = new IntersectionObserver(function (entries) {
      entries.forEach(function (entry) {
        if (!entry.isIntersecting) return;
        observer.unobserve(entry.target);

        var el   = entry.target;
        var raw  = el.textContent.trim();
        var num  = parseInt(raw.replace(/[^0-9]/g, ''), 10);

        if (isNaN(num) || num <= 0 || num > 99999) return;

        var suffix = raw.replace(/[0-9]/g, '').trim();
        animateCount(el, num, suffix);
      });
    }, { threshold: 0.4 });

    targets.forEach(function (el) { observer.observe(el); });
  }

  function animateCount(el, target, suffix) {
    var start    = performance.now();
    var duration = Math.min(900, 200 + target * 6);

    function step(now) {
      var progress = Math.min((now - start) / duration, 1);
      var value    = Math.round(target * easeOutCubic(progress));
      el.textContent = value + (suffix ? ' ' + suffix : '');
      if (progress < 1) {
        requestAnimationFrame(step);
      } else {
        el.textContent = target + (suffix ? ' ' + suffix : '');
      }
    }

    requestAnimationFrame(step);
  }

  function easeOutCubic(t) {
    return 1 - Math.pow(1 - t, 3);
  }

  /* ------------------------------------------------------------------ */
  /*  COPY TO CLIPBOARD                                                   */
  /* ------------------------------------------------------------------ */
  function initCopyButtons() {
    /* Auto-enhance tracking link boxes */
    document.querySelectorAll('.tracking-link-box').forEach(function (box) {
      var input = box.querySelector('input[type="text"]');
      if (!input || box.querySelector('.copy-btn')) return;

      var btn = makeCopyBtn(function () { return input.value; });
      /* Insert before the last child button/link */
      var last = box.querySelector('a:last-child, button:last-child');
      last ? box.insertBefore(btn, last) : box.appendChild(btn);
    });

    /* Handle [data-copy="#selector"] buttons */
    document.querySelectorAll('[data-copy]').forEach(function (btn) {
      btn.addEventListener('click', function () {
        var target = document.querySelector(btn.dataset.copy);
        if (!target) return;
        writeClipboard(target.value || target.textContent.trim(), btn);
      });
    });
  }

  function makeCopyBtn(getText) {
    var btn = document.createElement('button');
    btn.type      = 'button';
    btn.className = 'copy-btn btn btn-ghost btn-small';
    btn.innerHTML = '<i class="bi bi-clipboard"></i> Copy';

    btn.addEventListener('click', function () {
      writeClipboard(getText(), btn);
    });

    return btn;
  }

  function writeClipboard(text, btn) {
    if (!text) return;

    if (!navigator.clipboard) {
      /* Fallback for non-HTTPS */
      try {
        var ta = document.createElement('textarea');
        ta.value = text;
        ta.style.position = 'fixed';
        ta.style.opacity  = '0';
        document.body.appendChild(ta);
        ta.select();
        document.execCommand('copy');
        document.body.removeChild(ta);
        flashCopied(btn);
        showToast('Copied to clipboard', 'success');
      } catch (e) {
        showToast('Copy not supported in this browser', 'error');
      }
      return;
    }

    navigator.clipboard.writeText(text).then(function () {
      flashCopied(btn);
      showToast('Copied to clipboard', 'success');
    }).catch(function () {
      showToast('Could not access clipboard', 'error');
    });
  }

  function flashCopied(btn) {
    if (!btn) return;
    var orig = btn.innerHTML;
    btn.innerHTML = '<i class="bi bi-check-lg"></i> Copied!';
    btn.classList.add('copied');
    setTimeout(function () {
      btn.innerHTML = orig;
      btn.classList.remove('copied');
    }, 2200);
  }

  /* ------------------------------------------------------------------ */
  /*  FORM LOADING STATES                                                 */
  /* ------------------------------------------------------------------ */
  function initFormLoading() {
    document.querySelectorAll('form').forEach(function (form) {
      form.addEventListener('submit', function () {
        var btn = form.querySelector('[type="submit"]');
        if (!btn) return;

        btn.classList.add('btn-loading');
        btn.disabled = true;

        /* Safety reset after 12 s */
        setTimeout(function () {
          btn.classList.remove('btn-loading');
          btn.disabled = false;
        }, 12000);
      });
    });
  }

  /* ------------------------------------------------------------------ */
  /*  TOAST FROM URL PARAMS  (?success=... &error=... &info=...)          */
  /* ------------------------------------------------------------------ */
  function initToastFromUrl() {
    var params = new URLSearchParams(window.location.search);

    [['success', 'success'], ['error', 'error'], ['info', 'info']].forEach(function (pair) {
      var val = params.get(pair[0]);
      if (val) showToast(decodeURIComponent(val) || pair[0], pair[1]);
    });
  }

  /* ------------------------------------------------------------------ */
  /*  TOAST SYSTEM                                                        */
  /* ------------------------------------------------------------------ */
  function showToast(message, type) {
    var container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }

    var icons = {
      success: 'bi-check-circle-fill',
      error:   'bi-exclamation-circle-fill',
      info:    'bi-info-circle-fill'
    };

    var toast = document.createElement('div');
    toast.className = 'toast ' + (type || 'info');
    toast.innerHTML =
      '<i class="bi ' + (icons[type] || icons.info) + '"></i>' +
      '<span>' + escHtml(message) + '</span>';

    container.appendChild(toast);

    /* Auto-dismiss */
    setTimeout(function () {
      toast.style.transition = 'opacity 0.28s ease, transform 0.28s ease';
      toast.style.opacity    = '0';
      toast.style.transform  = 'translateX(14px)';
      setTimeout(function () { toast.remove(); }, 300);
    }, 3800);
  }

  function escHtml(s) {
    var d = document.createElement('div');
    d.textContent = String(s);
    return d.innerHTML;
  }

  /* ------------------------------------------------------------------ */
  /*  CHART.JS DARK THEME DEFAULTS                                        */
  /* ------------------------------------------------------------------ */
  function initChartTheme() {
    if (!window.Chart) return;

    Chart.defaults.color                          = '#94a3b8';
    Chart.defaults.borderColor                    = 'rgba(148,163,184,0.09)';
    Chart.defaults.font.family                    = 'Inter, ui-sans-serif, system-ui, sans-serif';
    Chart.defaults.font.weight                    = '700';

    Chart.defaults.plugins.legend.labels.color   = '#94a3b8';
    Chart.defaults.plugins.legend.labels.boxWidth = 9;

    Chart.defaults.plugins.tooltip.backgroundColor = '#181818';
    Chart.defaults.plugins.tooltip.titleColor      = '#f1f5f9';
    Chart.defaults.plugins.tooltip.bodyColor       = '#94a3b8';
    Chart.defaults.plugins.tooltip.borderColor     = 'rgba(148,163,184,0.10)';
    Chart.defaults.plugins.tooltip.borderWidth     = 1;
    Chart.defaults.plugins.tooltip.cornerRadius    = 0;
    Chart.defaults.plugins.tooltip.padding         = 10;

    /* Override the inline chartTextColor / chartGridColor variables
       that dashboard.html uses, so scales adopt the dark palette */
    if (typeof window.chartTextColor !== 'undefined') {
      window.chartTextColor = '#94a3b8';
    }
    if (typeof window.chartGridColor !== 'undefined') {
      window.chartGridColor = 'rgba(148,163,184,0.09)';
    }
  }

  /* ------------------------------------------------------------------ */
  /*  LIVE TABLE SEARCH                                                   */
  /* ------------------------------------------------------------------ */
  function initTableSearch() {
    document.querySelectorAll('[data-search-table]').forEach(function (input) {
      var panel = input.closest('.content-panel, .table-panel');
      var table = panel && panel.querySelector('.data-table');
      if (!table) return;

      input.addEventListener('input', function () {
        var q    = input.value.toLowerCase().trim();
        var rows = table.querySelectorAll('tbody tr');
        rows.forEach(function (row) {
          row.style.display = (!q || row.textContent.toLowerCase().includes(q)) ? '' : 'none';
        });
      });
    });

    /* Compact-list live search */
    document.querySelectorAll('[data-search-list]').forEach(function (input) {
      var list = document.querySelector(input.dataset.searchList);
      if (!list) return;

      input.addEventListener('input', function () {
        var q    = input.value.toLowerCase().trim();
        var rows = list.querySelectorAll('.compact-row, .timeline-item');
        rows.forEach(function (row) {
          row.style.display = (!q || row.textContent.toLowerCase().includes(q)) ? '' : 'none';
        });
      });
    });
  }

  /* ------------------------------------------------------------------ */
  /*  KEYBOARD SHORTCUTS                                                  */
  /* ------------------------------------------------------------------ */
  function initKeyboardShortcuts() {
    document.addEventListener('keydown', function (e) {
      var active = document.activeElement;
      var inInput = active && (active.tagName === 'INPUT' || active.tagName === 'TEXTAREA' || active.tagName === 'SELECT');

      /* Ctrl / ⌘ + K  →  focus search */
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        var search = document.querySelector(
          'input[type="search"], .search-pill input, [data-search-table]'
        );
        if (search) {
          search.focus();
          search.select && search.select();
          showToast('Search focused (Ctrl+K)', 'info');
        }
        return;
      }

      /* Never hijack browser shortcuts (Ctrl+C, Ctrl+V, etc.) */
      if (e.ctrlKey || e.metaKey || e.altKey) return;

      if (inInput) return;

      /* Single-key navigation (no modifiers) */
      if (e.key === 'd') { navigate('/dashboard'); }
      if (e.key === 'c') { navigate('/companies'); }
      if (e.key === 'a') { navigate('/analyses'); }
      if (e.key === 'o') { navigate('/osint'); }
    });
  }

  function navigate(path) {
    var origin = window.location.origin;
    fadeOut(function () { window.location.href = origin + path; });
  }

  /* ------------------------------------------------------------------ */
  /*  PAGE TRANSITIONS                                                    */
  /* ------------------------------------------------------------------ */
  function initPageTransitions() {
    /* Fade page in on load */
    var content = document.querySelector('.page-content');
    if (content) {
      content.style.animation = 'fadeUp 0.28s ease both';
    }

    /* Fade out before navigating away */
    document.querySelectorAll('a[href]').forEach(function (link) {
      var href = link.href;
      if (
        !href ||
        href.indexOf('#') !== -1 ||
        href.indexOf('javascript:') === 0 ||
        link.target ||
        link.download ||
        !href.startsWith(window.location.origin)
      ) return;

      link.addEventListener('click', function (e) {
        if (e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) return;
        e.preventDefault();
        fadeOut(function () { window.location.href = href; });
      });
    });
  }

  function fadeOut(cb) {
    var content = document.querySelector('.page-content');
    if (!content) { cb(); return; }
    content.style.transition = 'opacity 0.16s ease';
    content.style.opacity    = '0';
    setTimeout(cb, 165);
  }

  /* ------------------------------------------------------------------ */
  /*  LIVE TIMESTAMPS  (relative "X minutes ago")                        */
  /* ------------------------------------------------------------------ */
  function initLiveTimestamps() {
    document.querySelectorAll('[data-timestamp]').forEach(function (el) {
      var ms = parseInt(el.dataset.timestamp, 10);
      if (isNaN(ms)) return;
      el.title = new Date(ms).toLocaleString();
      el.textContent = relativeTime(ms);
    });
  }

  function relativeTime(ms) {
    var diff = Math.round((Date.now() - ms) / 1000);
    if (diff < 60)   return diff + 's ago';
    if (diff < 3600) return Math.floor(diff / 60) + 'm ago';
    if (diff < 86400)return Math.floor(diff / 3600) + 'h ago';
    return Math.floor(diff / 86400) + 'd ago';
  }

  /* ------------------------------------------------------------------ */
  /*  PUBLIC API                                                          */
  /* ------------------------------------------------------------------ */
  window.PepitoBE = {
    showToast:      showToast,
    writeClipboard: writeClipboard,
    navigate:       navigate
  };

}());
