document.addEventListener("DOMContentLoaded", () => {
  
  // Garantir que a variável exista para evitar erros (fallback)
  const basePath = window.CONTEXT_PATH || "";

  const headerHTML = `
    <header class="navbar">
      <a href="${basePath}/home" class="agnello-logo-link" style="text-decoration: none; display: inline-flex; align-items: center;">
        <div class="logo">
          <img class="logo-img" src="${basePath}/assets/images/logo-no-bg.png" alt="Ícone Vinheria Agnello">
          <div>
            <span class="logo-title">VINHERIA AGNELLO</span>
            <span class="logo-subtitle">DESDE 1978</span>
          </div>
        </div>
      </a>
      
      <nav class="nav-links">
        <a href="${basePath}/home">Nossa História</a>
        <a href="${basePath}/catalogo">Nossos Rótulos</a>
        <a href="${basePath}/quiz" id="open-quiz">✨ Sommelier Virtual</a>
      </nav>
      
      <div class="nav-icons">
        <button class="icon-btn"><img src="${basePath}/assets/icons/search.svg" alt="lupa"></button>

        <a href="${basePath}/login" class="icon-btn" style="display: inline-flex; align-items: center; justify-content: center; text-decoration: none;">
          <img src="${basePath}/assets/icons/user.svg" alt="perfil">
        </a>
      
        <button class="icon-btn cart-btn">
          <img src="${basePath}/assets/icons/shopping-cart.svg" alt="carrinho">
          <span class="cart-badge" style="display:none;">0</span>
        </button>
      </div>
    </header>
  `;

  const headerPlaceholder = document.getElementById("site-header");
  if (headerPlaceholder) headerPlaceholder.innerHTML = headerHTML;


  // Injeta o Footer Simples
  const footerHTML = `
  <footer class="quiz-footer-container">
  <div class="quiz-footer-content-wrapper">
    <div class="quiz-footer-col quiz-footer-brand-col">
      <div class="quiz-footer-logo-box">
        <img src="${basePath}/assets/images/logo-white-sem-fundo.png" alt="Logo Vinheria Agnello" class="quiz-footer-logo-img">
        Vinheria Agnello
      </div>
    </div>

    <div class="quiz-footer-col quiz-footer-socials-col">
      <div class="quiz-footer-socials-group">
        <a href="#" aria-label="Instagram" class="quiz-social-icon">
          <svg class="quiz-footer-svg" viewBox="0 0 24 24">
            <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
            <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
            <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line>
          </svg>
        </a>
        <a href="#" aria-label="WhatsApp" class="quiz-social-icon">
          <svg class="quiz-footer-svg" viewBox="0 0 24 24">
            <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
          </svg>
        </a>
        <a href="#" aria-label="LinkedIn" class="quiz-social-icon">
          <svg class="quiz-footer-svg" viewBox="0 0 24 24">
            <path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"></path>
            <rect x="2" y="9" width="4" height="12"></rect>
            <circle cx="4" cy="4" r="2"></circle>
          </svg>
        </a>
      </div>
    </div>
  </div>

  <div class="quiz-footer-bottom-bar">
    <div class="quiz-footer-bottom-inner">
      <p class="quiz-footer-copy">&copy; 2026 Vinheria Agnello. Todos os direitos reservados.</p>
      <p class="quiz-footer-legal">Proibida a venda de bebidas alcoólicas para menores de 18 anos. Beba com moderação.</p>
    </div>
  </div>
</footer>
  `;

  const footerPlaceholder = document.getElementById("site-footer");
  if (footerPlaceholder) footerPlaceholder.innerHTML = footerHTML;


  // Injeta o Footer Completo
  const footerCompletoHTML = `
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-col">
        <div class="footer-logo">
          <img src="${basePath}/assets/images/logo-white-sem-fundo.png" alt="Logo Vinheria Agnello" class="footer-logo-img">
          Vinheria Agnello
        </div>
        <p>O autêntico sabor da curadoria familiar, entregue na sua porta com segurança e temperatura ideal.</p>
        <div class="footer-socials">
          <a href="#" aria-label="Instagram" class="social-icon">
            <svg class="footer-svg-icon" viewBox="0 0 24 24">
              <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
              <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
              <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line>
            </svg>
          </a>
          <a href="#" aria-label="WhatsApp" class="social-icon">
            <svg class="footer-svg-icon" viewBox="0 0 24 24">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
            </svg>
          </a>
          <a href="#" aria-label="LinkedIn" class="social-icon">
            <svg class="footer-svg-icon" viewBox="0 0 24 24">
              <path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"></path>
              <rect x="2" y="9" width="4" height="12"></rect>
              <circle cx="4" cy="4" r="2"></circle>
            </svg>
          </a>
        </div>
      </div>

      <div class="footer-col">
        <h4>ATENDIMENTO AO CLIENTE</h4>
        <p>Segunda a Sábado: 10h às 20h</p>
        <p>Rua das Videiras, 1978 — São Paulo, SP</p>
        <p><a href="mailto:contato@vinheriaagnello.com.br">contato@vinheriaagnello.com.br</a></p>
        <p><a href="tel:+5511999999999">(11) 99999-9999</a></p>
      </div>

      <div class="footer-col">
        <h4>GARANTIA E SEGURANÇA</h4>
        <p class="footer-warranty">
          <span class="box-package">
            <img src="${basePath}/assets/icons/package.svg" alt="box" srcset="">
            Embalagem térmica e antichoque patenteada. Sua garrafa chega intacta ou enviamos outra imediatamente.
          </span>
        </p>
        <div class="footer-security-badges">
          <span class="badge-secure">
            <svg viewBox="0 0 24 24" width="14" height="14" stroke="currentColor" stroke-width="2" fill="none">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            Site 100% Seguro
          </span>
          <span class="badge-secure">
            <svg viewBox="0 0 24 24" width="14" height="14" stroke="currentColor" stroke-width="2" fill="none">
              <rect x="1" y="4" width="22" height="16" rx="2" ry="2"></rect>
              <line x1="1" y1="10" x2="23" y2="10"></line>
            </svg>
            Pagamento Criptografado
          </span>
        </div>
      </div>

      <div class="footer-col">
        <h4>LINKS RÁPIDOS</h4>
        <ul class="footer-links">
          <li><a href="${basePath}/catalogo">Nossa Adega</a></li>
          <li><a href="${basePath}/home">Nossa História</a></li>
          <li><a href="${basePath}/quiz">Calculadora de Eventos</a></li>
          <li><a href="#">Política de Privacidade</a></li>
        </ul>
      </div>
    </div>

    <div class="footer-bottom">
      <div class="footer-bottom-content">
        <p>&copy; 2026 Vinheria Agnello. Todos os direitos reservados.</p>
        <p class="footer-legal-warning">Proibida a venda de bebidas alcoólicas para menores de 18 anos. Beba com moderação.</p>
      </div>
    </div>
  </footer>
  `;

  const footerCompletoPlaceholder = document.getElementById("site-footer-completo");
  if (footerCompletoPlaceholder) footerCompletoPlaceholder.innerHTML = footerCompletoHTML;

});