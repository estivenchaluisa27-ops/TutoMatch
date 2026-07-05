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

                materia.setNombre(getCellValue(row.getCell(3)));
                materia.setCategoria(getCellValue(row.getCell(5)));
                materia.setDescripcion(getCellValue(row.getCell(4)));

                Cell semestreCell = row.getCell(2);
                if (semestreCell != null && semestreCell.getCellType() == CellType.NUMERIC) {
                    materia.setSemestreReferencial((int) semestreCell.getNumericCellValue());
                } else {
                    materia.setSemestreReferencial(1);
                }

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
}
