package controllers;

import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.*;
import scala.compat.java8.FutureConverters;
import javax.inject.*;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import static akka.pattern.Patterns.ask;

@Singleton
public class DocumentsController extends Controller {

    final ActorRef compareActor;

    @Inject
    public DocumentsController(ActorSystem system) {
        compareActor = system.actorOf(CompareActor.getProps());
    }

    public CompletionStage<Result> compare(Http.Request request) {
        String stringOriginalFile;
        String stringCompareFile;
        JsonNode json = request.body().asJson();
        if (json == null) {
            return CompletableFuture.supplyAsync(() -> {
                ObjectNode result = Json.newObject();
                result.put("json error", "Expecting Json data");
                return badRequest(result);
            });
        } else {
            stringOriginalFile = json.findPath("originalFile").textValue();
            stringCompareFile = json.findPath("compareFile").textValue();
        }
        if (stringOriginalFile == null && stringCompareFile == null) {
            return CompletableFuture.supplyAsync(() -> {
                ObjectNode result = Json.newObject();
                result.put("json error", "Missing parameter [originalFile] or [compareFile]");
                return badRequest(result);
            });
        }
        File originalFile = new File(stringOriginalFile);
        File compareFile = new File(stringCompareFile);
        return FutureConverters.toJava(ask(compareActor, new CompareProtocol.FilesContainer(originalFile, compareFile), 1000))
                .thenApply(response -> ok((String) response));
    }
}