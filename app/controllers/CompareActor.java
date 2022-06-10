package controllers;

import akka.actor.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.CompareProtocol.*;
import data.ResultData;
import logic.CompareFile;
import play.libs.Json;

public class CompareActor extends AbstractActor {

    public static Props getProps() {
        return Props.create(CompareActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        FilesContainer.class,
                        file -> {
                            CompareFile compareFile = new CompareFile(file.originalFile, file.compareFile);
                            ResultData data = compareFile.perform();
                            ObjectNode result = Json.newObject();
                            result.put("file-difference", data.getResultData());
                            sender().tell( result.toString(), self());
                        })
                .build();
    }
}
