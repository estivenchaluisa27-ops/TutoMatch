package com.uce.Tutomatch.config;

import com.uce.Tutomatch.model.ConfiguracionSistema;
import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.ConfiguracionSistemaRepository;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DbSeed implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DbSeed.class);

    private final MateriaRepository materiaRepository;
    private final ConfiguracionSistemaRepository configRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.nombre}")
    private String adminNombre;

    public DbSeed(MateriaRepository materiaRepository,
                  ConfiguracionSistemaRepository configRepository,
                  UsuarioRepository usuarioRepository,
                  PasswordEncoder passwordEncoder) {
        this.materiaRepository = materiaRepository;
        this.configRepository = configRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Iniciando seed de datos básicos...");

        // 1. Seed Materias
        if (materiaRepository.count() == 0) {
            logger.info("Insertando materias básicas...");
            materiaRepository.saveAll(List.of(
                    materiaConTarifa("Programación I", "Informática", 1, new BigDecimal("20")),
                    materiaConTarifa("Programación II", "Informática", 2, new BigDecimal("22")),
                    materiaConTarifa("Cálculo I", "Matemáticas", 1, new BigDecimal("18")),
                    materiaConTarifa("Cálculo II", "Matemáticas", 2, new BigDecimal("20")),
                    materiaConTarifa("Bases de Datos I", "Informática", 3, new BigDecimal("22")),
                    materiaConTarifa("Bases de Datos II", "Informática", 4, new BigDecimal("25")),
                    materiaConTarifa("Física I", "Ciencias", 1, new BigDecimal("18")),
                    materiaConTarifa("Física II", "Ciencias", 2, new BigDecimal("20")),
                    materiaConTarifa("Química General", "Ciencias", 1, new BigDecimal("18")),
                    materiaConTarifa("Estadística", "Matemáticas", 3, new BigDecimal("18")),
                    materiaConTarifa("Álgebra Lineal", "Matemáticas", 1, new BigDecimal("18")),
                    materiaConTarifa("Contabilidad", "Administración", 1, new BigDecimal("16"))
            ));
        }

        // 2. Seed Configuración del Sistema
        if (configRepository.count() == 0) {
            logger.info("Insertando configuración del sistema...");
            configRepository.saveAll(List.of(
                    new ConfiguracionSistema("duracion_bloque_horas", "1")
            ));
        }

        // 3. Seed Administrador
        if (usuarioRepository.findByCorreoInstitucional(adminEmail).isEmpty()) {
            logger.info("Creando usuario administrador...");
            Usuario admin = new Usuario(
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    adminNombre,
                    false, // rolSolicitante
                    false, // rolTutor
                    true   // rolAdmin
            );
            usuarioRepository.save(admin);
        }

        logger.info("Seed de datos completado.");
    }

    private Materia materiaConTarifa(String nombre, String categoria, Integer semestre, BigDecimal tarifa) {
        Materia m = new Materia(nombre, categoria, semestre);
        m.setTarifaHora(tarifa);
        return m;
    }
}
