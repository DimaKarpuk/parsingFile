import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import jsonObject.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class parsingFileTests {
    private final ClassLoader cl = parsingFileTests.class.getClassLoader();

    @DisplayName("Тест файла json")
    @Test
    void jsonFileVerification() throws Exception {
        try (InputStream is = cl.getResourceAsStream("json/simpleJson.json")) {
            ObjectMapper mapper = new ObjectMapper();
            Person person = mapper.readValue(is, Person.class);
            Assertions.assertEquals("John", person.getName());
            Assertions.assertEquals(30, person.getAge());
            Assertions.assertEquals("123 Main St", person.getAddress().getStreet());
            Assertions.assertEquals("Seattle", person.getAddress().getCity());
            Assertions.assertEquals(98101, person.getAddress().getPostcode());
        }
    }
    @DisplayName("Тест файла xls")
    @Test
    void zipXlsFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("zip/zip.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xls")) {
                    XLS xls = new XLS(zis);
                    String actualValue = xls.excel.getSheetAt(0).
                            getRow(3).getCell(4).getStringCellValue();
                    Assertions.assertTrue(actualValue.contains("France"));
                }
            }
        }
    }
    @DisplayName("Тест файла csv")
    @Test
    void zipCsvFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("zip/zip.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    CSVReader csv = new CSVReader(new InputStreamReader(zis));
                    List<String[]> data = csv.readAll();
                    Assertions.assertEquals(4, data.size());
                    Assertions.assertArrayEquals(
                            new String[]{"Имя", "Фамилия"},
                            data.get(0)
                    );
                    Assertions.assertArrayEquals(
                            new String[]{"Иван", "Петров"},
                            data.get(1)
                    );
                }
            }
        }
    }
    @DisplayName("Тест файла pdf")
    @Test
    void zipPdfFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("zip/zip.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".pdf")) {
                    PDF pdf = new PDF(zis);
                    Assertions.assertEquals("Philip Hutchison",pdf.author);
                }
            }
        }
    }
}
