package br.com.vrbeneficios.miniautorizador.repository;

import br.com.vrbeneficios.miniautorizador.model.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, String> {}
