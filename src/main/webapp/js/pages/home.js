// Renderização dinâmica da vitrine e animações da página principal
document.addEventListener('DOMContentLoaded', () => {
  // O clique em "Selecionar" da vitrine é tratado pelo carrinho.js
  // (ouvinte global para todas as páginas). Antes havia um 2.º ouvinte aqui,
  // o que adicionava o mesmo vinho DUAS vezes ao carrinho.

  // Observer de animação exclusivo da Home (Seção Família e afins)
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('is-visible');
        observer.unobserve(entry.target);
      }
    });
  }, { 
    threshold: 0.05, 
    rootMargin: "0px 0px -30px 0px"
  });

  document.querySelectorAll('.scroll-animate-left, .scroll-animate-right').forEach(el => {
    observer.observe(el);
  });
});