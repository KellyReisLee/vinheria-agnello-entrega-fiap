package com.agnello.service;

import java.util.List;

import com.agnello.dao.EnderecoDAO;
import com.agnello.exception.RegraNegocioException;
import com.agnello.model.Endereco;
import com.agnello.model.Usuario;
import com.agnello.util.Validacao;

/** Endereços guardados na conta (no máximo 5 por cliente). */
public class EnderecoService {

    public static final int MAX_ENDERECOS = 5;

    private final EnderecoDAO dao;

    public EnderecoService() {
        this(new EnderecoDAO());
    }

    /** Construtor para testes. */
    public EnderecoService(EnderecoDAO dao) {
        this.dao = dao;
    }

    public List<Endereco> listar(Usuario cliente) {
        return dao.listar(cliente.getId());
    }

    public void adicionar(Usuario cliente, Endereco endereco) throws RegraNegocioException {
        validar(endereco);
        int quantos = dao.contar(cliente.getId());
        if (quantos >= MAX_ENDERECOS) {
            throw new RegraNegocioException("Você já tem " + MAX_ENDERECOS + " endereços guardados. "
                    + "Exclua um para adicionar outro.");
        }
        nomearSeVazio(endereco, quantos);
        dao.inserir(cliente.getId(), endereco);
    }

    /**
     * Guarda o endereço usado no checkout, se ainda não houver um igual.
     * Erros aqui nunca atrapalham a compra (o pedido já foi gravado).
     */
    public void guardarSeNovo(Usuario cliente, Endereco endereco) {
        try {
            validar(endereco);
            List<Endereco> atuais = dao.listar(cliente.getId());
            if (atuais.size() >= MAX_ENDERECOS) {
                return;
            }
            for (Endereco e : atuais) {
                if (e.getLinha().equalsIgnoreCase(endereco.getLinha())) {
                    return;
                }
            }
            nomearSeVazio(endereco, atuais.size());
            dao.inserir(cliente.getId(), endereco);
        } catch (RegraNegocioException | RuntimeException e) {
            java.util.logging.Logger.getLogger(EnderecoService.class.getName())
                    .log(java.util.logging.Level.WARNING, "Endereço do checkout não foi guardado", e);
        }
    }

    public void excluir(Usuario cliente, int id) throws RegraNegocioException {
        if (!dao.excluir(cliente.getId(), id)) {
            throw new RegraNegocioException("Endereço não encontrado.");
        }
    }

    public void tornarPrincipal(Usuario cliente, int id) throws RegraNegocioException {
        if (!dao.tornarPrincipal(cliente.getId(), id)) {
            throw new RegraNegocioException("Endereço não encontrado.");
        }
    }

    /** Mesmas regras do endereço do checkout. Normaliza os espaços e o CEP. */
    /** Sem apelido, o primeiro chama-se "Principal" e os outros "Endereço 2", "Endereço 3"... */
    static void nomearSeVazio(Endereco endereco, int jaGuardados) {
        if (Validacao.vazio(endereco.getApelido())) {
            endereco.setApelido(jaGuardados == 0 ? "Principal" : "Endereço " + (jaGuardados + 1));
        }
    }

    static void validar(Endereco e) throws RegraNegocioException {
        String cep = Validacao.somenteDigitos(e.getCep());
        if (cep == null || cep.length() != 8) {
            throw new RegraNegocioException("Informe um CEP com 8 dígitos.");
        }
        e.setCep(cep);
        e.setApelido(limpo(e.getApelido(), 40, false, "o apelido"));
        e.setLogradouro(limpo(e.getLogradouro(), 120, true, "a rua"));
        e.setNumero(limpo(e.getNumero(), 20, true, "o número"));
        e.setComplemento(limpo(e.getComplemento(), 60, false, "o complemento"));
        e.setBairro(limpo(e.getBairro(), 80, true, "o bairro"));
        e.setCidade(limpo(e.getCidade(), 80, true, "a cidade"));
    }

    private static String limpo(String valor, int maximo, boolean obrigatorio, String campo)
            throws RegraNegocioException {
        String v = valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) {
            if (obrigatorio) {
                throw new RegraNegocioException("Informe " + campo + ".");
            }
            return null;
        }
        if (v.length() > maximo) {
            throw new RegraNegocioException("Revise " + campo + " (máximo de " + maximo + " caracteres).");
        }
        return v;
    }
}
