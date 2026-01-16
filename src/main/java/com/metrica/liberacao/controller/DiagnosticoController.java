package com.metrica.liberacao.controller;

import com.metrica.liberacao.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/diagnostico")
public class DiagnosticoController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ProjetoRepository projetoRepository;

    @GetMapping("/db")
    public Map<String, Object> testarConexao() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            Connection conn = dataSource.getConnection();
            resultado.put("conexao", "OK");
            resultado.put("database", conn.getMetaData().getDatabaseProductName());
            conn.close();
        } catch (Exception e) {
            resultado.put("conexao", "ERRO");
            resultado.put("mensagem", e.getMessage());
        }

        try {
            long count = projetoRepository.count();
            resultado.put("projetos_count", count);
        } catch (Exception e) {
            resultado.put("projetos_erro", e.getMessage());
        }

        return resultado;
    }

    @GetMapping("/health")
    public String health() {
        return "API funcionando!";
    }
}
