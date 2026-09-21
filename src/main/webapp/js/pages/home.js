// Renderização dinâmica da vitrine e animações da página principal
document.addEventListener('DOMContentLoaded', () => {
  // 1. Vitrine "Achados da Semana" (Gerenciada 100% pelo Servidor/JSTL)
  const containerVitrine = document.getElementById('vitrine-achados-container');

  if (containerVitrine) {
    // Ouvinte de clique para os produtos renderizados pelo Servidor (Java/JSTL)
    containerVitrine.addEventListener('click', (event) => {
      const btn = event.target.closest('.agnello-btn-comprar');
      if (!btn) return;

      const produtoId = btn.getAttribute('data-id');
      const nome = btn.getAttribute('data-nome');
      const precoStr = btn.getAttribute('data-preco');
      const origem = btn.getAttribute('data-origem');
      const imagem = btn.getAttribute('data-imagem');

      // Tenta extrair o valor numérico com segurança para formatar certinho
      let precoNumerico = parseFloat(String(precoStr).replace('R$', '').replace(',', '.').trim());
      if (isNaN(precoNumerico)) precoNumerico = 0;

      // Formata o preço corretamente para o padrão brasileiro com 2 casas decimais (ex: R$ 125,00)
      const precoFormatado = 'R$ ' + precoNumerico.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

      if (typeof adicionarAoCarrinho === 'function') {
        adicionarAoCarrinho({
          id: produtoId,
          nome: nome,
          preco: precoFormatado,
          subtitulo: origem, // Garante que o subtítulo/origem vá para o carrinho no lugar do "undefined"
          imagem: imagem
        });
      } else {
        console.error('Função adicionarAoCarrinho não encontrada.');
      }
    });
  }

  // 2. Observer de animação exclusivo da Home (Seção Família e afins)
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