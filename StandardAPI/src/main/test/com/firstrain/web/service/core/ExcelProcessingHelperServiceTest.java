package com.firstrain.web.service.core;

import com.firstrain.utils.FR_Loader;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.poi.ss.usermodel.Cell; 
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ExcelProcessingHelperService.class,
        FR_Loader.class,
        File.class,
        GetMethod.class,
        XSSFWorkbook.class,
        XSSFCell.class
})
public class ExcelProcessingHelperServiceTest {

    private static final String HELPER_FILE_NAME = "fileName";
    private static final AtomicLong lastModifiedTime  = new AtomicLong(2425);
    private static final String RESOURCE_PATH = "path";
    private static final String FILE_URL = "C://file/file.txt";
    private static final int PHYSICAL_NUMBER_OF_ROWS = 2;
    private static final String STR_CELL_VALUE = "cellValue";
    private static final int SECTOR_ID = 44;
    private static final String TITLE_JOB = "job";
    private static final String SOURCE_KEY = "key";
    @InjectMocks
    private final ExcelProcessingHelperService helperService = new ExcelProcessingHelperService();
    @Mock
    private HttpClient httpClient;
    @Mock
    private URL url;
    @Mock
    private File file;
    @Mock
    private GetMethod getMethod;
    @Mock
    private Header header;
    @Mock
    private XSSFWorkbook xssfWorkbook;
    @Mock
    private InputStream inputStream;
    @Mock
    private XSSFSheet sheet;
    @Mock
    private XSSFRow row;
    @Mock
    private XSSFCell xssfCell;
    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        mockStatic(FR_Loader.class, XSSFWorkbook.class, GetMethod.class, XSSFCell.class);
        when(sheet.getPhysicalNumberOfRows()).thenReturn(PHYSICAL_NUMBER_OF_ROWS);
        when(xssfCell.getStringCellValue()).thenReturn(STR_CELL_VALUE);
        when(xssfCell.getCellType()).thenReturn(Cell.CELL_TYPE_STRING);
        when(row.getCell(anyInt())).thenReturn(xssfCell);
        when(sheet.getRow(anyInt())).thenReturn(row);
        when(xssfWorkbook.getSheetAt(anyInt())).thenReturn(sheet);
        whenNew(XSSFWorkbook.class).withAnyArguments().thenReturn(xssfWorkbook);
        whenNew(GetMethod.class).withAnyArguments().thenReturn(getMethod);
    }

    @Test
    public void init() throws Exception {
        //Arrange
        when(url.getPath()).thenReturn(RESOURCE_PATH);
        whenNew(File.class).withAnyArguments().thenReturn(file);
        when(url.openStream()).thenReturn(inputStream);
        when(FR_Loader.getResource(anyString())).thenReturn(url);
        //Act
        helperService.init();
        //Assert
        verify(row, atLeastOnce()).getCell(integerArgumentCaptor.capture());
        assertEquals(Arrays.asList(0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 0,
                0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1), integerArgumentCaptor.getAllValues());
    }

    @Test
    public void givenGetFileFromPathWhenResourceIsNullThenResponse() throws Exception {
        //Arrange
        when(FR_Loader.getResource(anyString())).thenReturn(null);
        //Act
        InputStream stream = helperService.getFileFromPath(HELPER_FILE_NAME, lastModifiedTime );
        //Assert
        assertNull(stream);
    }

    @Test
    public void givenGetFileFromPathWhenResourceIsNotNullThenResponse() throws Exception {
        //Arrange
        when(url.getPath()).thenReturn(RESOURCE_PATH);
        whenNew(File.class).withAnyArguments().thenReturn(file);
        when(FR_Loader.getResource(anyString())).thenReturn(url);
        //Act
        InputStream stream = helperService.getFileFromPath(HELPER_FILE_NAME, lastModifiedTime );
        //Assert
        assertNull(stream);
    }

    @Test
    public void givenGetFileFromPathWhenLastModifiedDateNotEqualsToFileModifiedDateThenResponse() throws Exception {
        //Arrange
        when(url.getPath()).thenReturn(RESOURCE_PATH);
        when(file.lastModified()).thenReturn(lastModifiedTime .longValue());
        whenNew(File.class).withAnyArguments().thenReturn(file);
        when(FR_Loader.getResource(anyString())).thenReturn(url);
        //Act
        InputStream stream = helperService.getFileFromPath(HELPER_FILE_NAME, lastModifiedTime );
        //Assert
        assertNull(stream);
    }

    @Test
    public void loadFileFromHTTP() throws Exception {
        //Arrange
        when(getMethod.getResponseBodyAsStream()).thenReturn(inputStream);
        when(header.getValue()).thenReturn("Thu, 15 May 2014 07:15:03 -0700");
        when(getMethod.getResponseHeader("Last-Modified"))
                .thenReturn(header);
        //Act
        helperService.loadFileFromHTTP(FILE_URL, lastModifiedTime );
        //Assert
        verify(row, atLeastOnce()).getCell(integerArgumentCaptor.capture());
        assertEquals(Arrays.asList(0, 0, 0, 1, 1, 1), integerArgumentCaptor.getAllValues());
    }

    @Test
    public void loadSourceMetaDataMap() {
        //Act
        helperService.loadSourceMetaDataMap(inputStream);
        //Assert
        verify(row, atLeastOnce()).getCell(integerArgumentCaptor.capture());
        assertEquals(Arrays.asList(0, 0, 0, 1, 1, 1), integerArgumentCaptor.getAllValues());
    }

    @Test
    public void getSectorVsCXO() {
        //Act&Assert
        Map<Integer, String> sector = helperService.getSectorVsCXO();
        //Assert
        assertEquals(0, sector.size());
    }

    @Test
    public void setSectorVsCXO() {
        //Arrange
        Map<Integer, String> sector = Collections.singletonMap(123, "abc");
        //Act
        helperService.setSectorVsCXO(sector);
        //Assert
        assertNotNull(helperService.getSectorVsCXO());
    }

    @Test
    public void getCXOForSector() {
        //Act
        String cxoForSector = helperService.getCXOForSector(SECTOR_ID);
        //Assert
        assertNull(cxoForSector);
    }

    @Test
    public void getJobVsTitles() {
        //Act
        Map<String, String> jobVsTitles = helperService.getJobVsTitles();
        //Assert
        assertEquals(0, jobVsTitles.size());
    }

    @Test
    public void setJobVsTitles() {
        //Arrange
        Map<String, String> jobVsTitles = Collections.singletonMap("234", "abc");
        //Act
        helperService.setJobVsTitles(jobVsTitles);
        //Assert
        assertNotNull(helperService.getJobVsTitles());
    }

    @Test
    public void getTitlesForJob() {
        //Act
        String titles = helperService.getTitlesForJob(TITLE_JOB);
        //Assert
        assertNull(titles);
    }

    @Test
    public void getSourceMetaData() {
        //Arrange
        helperService.loadSourceMetaDataMap(inputStream);
        //Act
        String metaData = helperService.getSourceMetaData(SOURCE_KEY);
        //Assert
        assertEquals("English", metaData);
    }
}
