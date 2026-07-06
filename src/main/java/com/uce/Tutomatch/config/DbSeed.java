package com.uce.Tutomatch.config;

import com.uce.Tutomatch.model.ConfiguracionSistema;
import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.model.Usuario;
import com.uce.Tutomatch.repository.ConfiguracionSistemaRepository;
import com.uce.Tutomatch.repository.MateriaRepository;
import com.uce.Tutomatch.repository.UsuarioRepository;
import com.uce.Tutomatch.service.ExcelImportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Component
public class DbSeed implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DbSeed.class);

    private static final String ICONO_CODE =
            "<svg viewBox=\"0 0 24 24\"><path fill=\"currentColor\" d=\"m8 18-6-6 6-6 1.425 1.425-4.6 4.6L9.4 16.6Zm8 0-1.425-1.425 4.6-4.6L14.6 7.4 16 6l6 6Z\"/></svg>";

    private static final String ICONO_CALCULATOR =
            "<svg viewBox=\"0 0 24 24\"><path fill=\"currentColor\" d=\"M7 2h10a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2m0 2v4h10V4Zm0 6v2h2v-2Zm4 0v2h2v-2Zm4 0v2h2v-2Zm-8 4v2h2v-2Zm4 0v2h2v-2Zm4 0v2h2v-2Zm-8 4v2h2v-2Zm4 0v2h2v-2Zm4 0v2h2v-2Z\"/></svg>";

    private static final String ICONO_DATABASE =
            "<svg viewBox=\"0 0 24 24\"><path fill=\"currentColor\" d=\"M18.375 9.825Q21 8.65 21 7t-2.625-2.825T12 3t-6.375 1.175T3 7t2.625 2.825T12 11t6.375-1.175m-3.812 3.463q1.537-.213 2.962-.688t2.45-1.237T21 9.5V12q0 1.1-1.025 1.863t-2.45 1.237t-2.962.688T12 16t-2.562-.213t-2.963-.687t-2.45-1.237T3 12V9.5q0 1.1 1.025 1.863t2.45 1.237t2.963.688T12 13.5t2.563-.213m0 5q1.537-.212 2.962-.687t2.45-1.237T21 14.5V17q0 1.1-1.025 1.863t-2.45 1.237t-2.962.688T12 21t-2.562-.213t-2.963-.687t-2.45-1.237T3 17v-2.5q0 1.1 1.025 1.863t2.45 1.237t2.963.688T12 18.5t2.563-.213\"/></svg>";

    private static final String ICONO_PHYSICS =
            "<svg viewBox=\"0 0 24 24\"><g fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\"><path stroke-linecap=\"round\" d=\"M12 5.793a28 28 0 0 1 3.342 2.865A28 28 0 0 1 18.207 12m0 0c2.584-3.57 3.554-6.947 2.147-8.354S15.57 3.209 12 5.793a28 28 0 0 0-3.342 2.865A28 28 0 0 0 5.793 12m12.414 0c2.584 3.57 3.554 6.947 2.147 8.354c-1.043 1.043-3.17.78-5.654-.48M18.207 12a28 28 0 0 1-2.865 3.342A28 28 0 0 1 12 18.207m0 0a28 28 0 0 1-3.342-2.865A28 28 0 0 1 5.793 12M12 18.207c-3.57 2.584-6.947 3.554-8.354 2.147S3.209 15.57 5.793 12m0 0C3.21 8.43 2.24 5.053 3.646 3.646c1.043-1.043 3.17-.78 5.654.48\"/><circle cx=\"12\" cy=\"12\" r=\"2\"/></g></svg>";

    private static final String ICONO_FLASK =
            "<svg viewBox=\"0 0 14 14\"><path fill=\"none\" stroke=\"currentColor\" stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M9 .5v6l3.59 4.57a1.5 1.5 0 0 1-1.18 2.43H2.59a1.5 1.5 0 0 1-1.18-2.43L5 6.5v-6M3.5.5h7\"/></svg>";

    private static final String ICONO_CHART =
            "<svg viewBox=\"0 0 24 24\"><path fill=\"currentColor\" d=\"m16 11.78 4.24-7.33 1.73 1-5.23 9.05-6.51-3.75L5.46 19H22v2H2V3h2v14.54L9.5 8Z\"/></svg>";

    private static final String ICONO_MATRIX =
            "<svg viewBox=\"0 0 24 24\"><path fill=\"currentColor\" d=\"M2 2h4v2H4v16h2v2H2Zm18 2h-2V2h4v20h-4v-2h2ZM9 5h1v5h1v1H8v-1h1V6l-1 .5v-1Zm6 8h1v5h1v1h-3v-1h1v-4l-1 .5v-1Zm-6 0c1.1 0 2 1.34 2 3s-.9 3-2 3-2-1.34-2-3 .9-3 2-3m0 1c-.55 0-1 .9-1 2s.45 2 1 2 1-.9 1-2-.45-2-1-2m6-9c1.1 0 2 1.34 2 3s-.9 3-2 3-2-1.34-2-3 .9-3 2-3m0 1c-.55 0-1 .9-1 2s.45 2 1 2 1-.9 1-2-.45-2-1-2\"/></svg>";

    private static final String ICONO_DEFAULT =
            "<svg viewBox=\"0 0 24 24\"><path fill=\"currentColor\" d=\"M4 6H2v14a2 2 0 0 0 2 2h14v-2H4V6m16-4H8a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2m0 14H8V4h12v12Z\"/></svg>";

    private final MateriaRepository materiaRepository;
    private final ConfiguracionSistemaRepository configRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExcelImportService excelImportService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.nombre}")
    private String adminNombre;

    public DbSeed(MateriaRepository materiaRepository,
                  ConfiguracionSistemaRepository configRepository,
                  UsuarioRepository usuarioRepository,
                  PasswordEncoder passwordEncoder,
                  ExcelImportService excelImportService)
    {
        this.materiaRepository = materiaRepository;
        this.configRepository = configRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.excelImportService = excelImportService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Iniciando verificación del sistema...");

        if (materiaRepository.count() == 0) {
            logger.info("Base de datos vacía. Iniciando lectura del archivo Excel...");
            excelImportService.importarMateriasDesdeExcel();
            logger.info("¡Las 92 materias del Excel han sido importadas con éxito!");
        }

        if (materiaRepository.count() > 0) {
            logger.info("Actualizando facultad y carrera desde Excel...");
            seedFacultadesYCarreras();
            logger.info("Aplicando mapeo inteligente de iconos vectoriales...");
            seedIconosMateriasMasivas();
        }

        if (configRepository.count() == 0) {
            logger.info("Insertando configuración por defecto del sistema...");
            configRepository.saveAll(List.of(
                    new ConfiguracionSistema("duracion_bloque_horas", "1")
            ));
        }

        if (usuarioRepository.findByCorreoInstitucional(adminEmail).isEmpty()) {
            logger.info("Creando usuario administrador maestro...");
            Usuario admin = new Usuario(
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    adminNombre,
                    false,
                    false,
                    true
            );
            usuarioRepository.save(admin);
        }

        logger.info("Seed complementario y mapeo estético finalizado con éxito.");
    }

    private void seedFacultadesYCarreras() {
        try {
            InputStream is = new ClassPathResource("Base_TutoMatch.xlsx").getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            List<Materia> todas = materiaRepository.findAll();
            boolean huboCambios = false;

            for (Materia m : todas) {
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    String nombreExcel = row.getCell(3) != null ? row.getCell(3).getStringCellValue().trim() : "";
                    if (nombreExcel.equalsIgnoreCase(m.getNombre())) {
                        m.setFacultad(row.getCell(0) != null ? row.getCell(0).getStringCellValue().trim() : "");
                        m.setCarrera(row.getCell(1) != null ? row.getCell(1).getStringCellValue().trim() : "");
                        huboCambios = true;
                        break;
                    }
                }
            }

            if (huboCambios) {
                materiaRepository.saveAll(todas);
                logger.info("Facultad y carrera actualizadas desde Excel.");
            }
            workbook.close();
        } catch (Exception e) {
            logger.warn("No se pudo actualizar facultad/carrera: {}", e.getMessage());
        }
    }

    private void seedIconosMateriasMasivas() {
        List<Materia> todas = materiaRepository.findAll();
        boolean huboCambios = false;

        for (Materia m : todas) {
            if (m.getIcono() == null || m.getIcono().isBlank() || m.getIcono().equals(ICONO_DEFAULT)) {
                String nombre = m.getNombre().toLowerCase();
                String facultad = m.getFacultad() != null ? m.getFacultad().toLowerCase() : "";
                String asignado = ICONO_DEFAULT;

                if (nombre.contains("programación") || nombre.contains("sistemas") || nombre.contains("estructura de datos")) {
                    asignado = ICONO_CODE;
                }
                else if (nombre.contains("base de datos") || nombre.contains("sql") || nombre.contains("información")) {
                    asignado = ICONO_DATABASE;
                }
                else if (nombre.contains("cálculo") || nombre.contains("álgebra") || nombre.contains("ecuaciones") || nombre.contains("matemática") || nombre.contains("geometría")) {
                    asignado = ICONO_MATRIX;
                }
                else if (nombre.contains("estadística") || nombre.contains("probabilidad") || nombre.contains("psicometría") || nombre.contains("balance")) {
                    asignado = ICONO_CHART;
                }
                else if (nombre.contains("contabilidad") || nombre.contains("financiera") || nombre.contains("tributaria") || nombre.contains("economía") || nombre.contains("marketing") || nombre.contains("administración")) {
                    asignado = ICONO_CALCULATOR;
                }
                else if (nombre.contains("física") || nombre.contains("mecánica") || nombre.contains("fluidos") || nombre.contains("biomecánica") || nombre.contains("topografía") || nombre.contains("mineralogía")) {
                    asignado = ICONO_PHYSICS;
                }
                else if (nombre.contains("química") || nombre.contains("bioquímica") || nombre.contains("orgánica") || nombre.contains("analítica") || nombre.contains("termodinámica")) {
                    asignado = ICONO_FLASK;
                }

                if (!asignado.equals(ICONO_DEFAULT)) {
                    m.setIcono(asignado);
                    huboCambios = true;
                }
            }
        }

        if (huboCambios) {
            materiaRepository.saveAll(todas);
            logger.info("¡Logrado! Se inyectaron iconos personalizados vectoriales a las materias de Base_TutoMatch.");
        }
    }
}
