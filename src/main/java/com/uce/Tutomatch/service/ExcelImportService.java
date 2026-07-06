package com.uce.Tutomatch.service;

import com.uce.Tutomatch.model.Materia;
import com.uce.Tutomatch.repository.MateriaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ExcelImportService {

    private final MateriaRepository materiaRepository;

    public ExcelImportService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    public void importarMateriasDesdeExcel() {
        try {
            InputStream is = new ClassPathResource("Base_TutoMatch.xlsx").getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Materia materia = new Materia();

                materia.setFacultad(getCellValue(row.getCell(0)));
                materia.setCarrera(getCellValue(row.getCell(1)));
                materia.setSemestreReferencial((int) getNumericCellValue(row.getCell(2), 1));
                materia.setNombre(getCellValue(row.getCell(3)));
                materia.setDescripcion(getCellValue(row.getCell(4)));
                materia.setCategoria(getCellValue(row.getCell(5)));
                materia.setNivelDesercion(getCellValue(row.getCell(6)));
                materia.setTransversalidad(getCellValue(row.getCell(7)));

                materia.setIcono("");

                materiaRepository.save(materia);
            }

            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo Excel: " + e.getMessage(), e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue().trim() : cell.toString().trim();
    }

    private int getNumericCellValue(Cell cell, int defaultValue) {
        if (cell == null) return defaultValue;
        return cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : defaultValue;
    }
}
