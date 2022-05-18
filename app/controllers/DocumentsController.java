package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import logic.CompareFile;
import logic.unzip.UnzipFileToDirectoryController;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.io.File;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

public class DocumentsController {

    HttpExecutionContext httpExecutionContext;
    UnzipFileToDirectoryController unzip = new UnzipFileToDirectoryController();

    @Inject
    public DocumentsController(HttpExecutionContext httpExecutionContext) {
        this.httpExecutionContext = httpExecutionContext;
    }

    public Result getShit() {
        File file1 = new File("/home/gruhol/Dokumenty/wojtek.docx");
        File file2 = new File("/home/gruhol/Dokumenty/wojtek2.docx");
        CompareFile compareFile = new CompareFile(file1, file2);
        return ok("Comare: " + compareFile.perform().getResultData());
    }

    public Result sayHello(Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest("Expecting Json data");
        } else {
            String name = json.findPath("name").textValue();
            if (name == null) {
                return badRequest("Missing parameter [name]");
            } else {
                return ok("Hello " + name);
            }
        }
    }



}
