package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import data.FileDiffJsonData;
import data.FileDifference;
import logic.ListFileCreator.ListOfFilesFromPathCreator;
import logic.differenceInFilesReader.DifferenceInXmlFilesReader;
import logic.metaDataDifferenceFinder.MetaDataDifferenceFinder;
import logic.metaDataReader.DocxMetaDataReader;
import logic.metaDataReader.MetaDataReadable;
import logic.metaDataReader.PdfMetaDataReader;
import logic.metaDataReader.XlsxMetaDataReader;
import logic.unzip.UnzipFileToDirectoryController;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static play.mvc.Results.ok;

public class DocumentsController {

    HttpExecutionContext httpExecutionContext;
    UnzipFileToDirectoryController unzip = new UnzipFileToDirectoryController();

    @Inject
    public DocumentsController(HttpExecutionContext httpExecutionContext) {
        this.httpExecutionContext = httpExecutionContext;
    }

    public Result getShit() {
        List<FileDifference> fileDifferences = new ArrayList<>();
        String jsonStringWithDifference = "";
        Map<String, Object> diff;
        try {
            File file1 = new File("/home/gruhol/Dokumenty/wojtek.docx");
            File file2 = new File("/home/gruhol/Dokumenty/wojtek2.docx");
            Path tempDirSource = Files.createTempDirectory("");
            Path tempDirToCompare = Files.createTempDirectory("");
            unzip.unzip(file1.toPath(), tempDirSource);
            unzip.unzip(file2.toPath(), tempDirToCompare);
            List<File> sourceFiles = new ListOfFilesFromPathCreator().getListOfFile(tempDirSource.toString());
            List<File> filesToCompare = new ListOfFilesFromPathCreator().getListOfFile(tempDirToCompare.toString());
            fileDifferences = new DifferenceInXmlFilesReader().getListOfDifferences(sourceFiles, filesToCompare);
            MetaDataReadable metaDataStrategy = getStrategyToReadMetaData(file1);
            MetaDataDifferenceFinder metaDataDifferenceFinder = new MetaDataDifferenceFinder();
            diff = metaDataDifferenceFinder.getMetaDataDifference(metaDataStrategy.getMataData(file1), metaDataStrategy.getMataData(file2));
            jsonStringWithDifference = createJson(fileDifferences, diff);
        } catch (IOException ioException) {}
        return ok("Działa: ");
    }

    private String createJson(List<FileDifference> fileDifferences, Map<String, Object> diff) throws JsonProcessingException {
        FileDiffJsonData fileDiffJsonData = new FileDiffJsonData.Builder()
                .compareDate(new Date())
                .listFileDifference(fileDifferences)
                .mapOfDiffInMetaData(diff)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        return objectMapper.writeValueAsString(fileDiffJsonData);
    }

    private MetaDataReadable getStrategyToReadMetaData(File file) {
        String extension = getExtension(file);
        MetaDataReadable metaDataReadable = null;
        switch (extension) {
            case "pdf":
                metaDataReadable = new PdfMetaDataReader();
                break;
            case "docx":
                metaDataReadable = new DocxMetaDataReader();
                break;
            case "xlsx":
                metaDataReadable = new XlsxMetaDataReader();
                break;
            default:
        }

        return metaDataReadable;
    }

    private String getExtension(File file) {
        String fileName = file.toString();
        int index = fileName.lastIndexOf('.');
        return fileName.substring(index + 1);
    }

}
