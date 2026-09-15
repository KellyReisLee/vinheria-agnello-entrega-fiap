document.querySelectorAll('.account-nav-menu .nav-item').forEach(item => {
  item.addEventListener('click', function (e) {
    // Evita recarregar a página se for link vazio
    if (this.getAttribute('href') === '#') {
      e.preventDefault();
    }

    // Remove a classe active de todos os itens do menu
    document.querySelectorAll('.account-nav-menu .nav-item').forEach(nav => {
      nav.classList.remove('active');
    });

    // Adiciona apenas no item que acabou de ser clicado
    this.classList.add('active');
  });
});