package ru.selsup;

import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SomeTests {
    public static void main(String[] args) {
        var document = new CrptApi.Document(
                new CrptApi.Description("123456789"),
                "123",
                "Status",
                CrptApi.Document.DocType.LP_INTRODUCE_GOODS,
                true,
                "OwnerInn",
                "ParticipantInn",
                "ProducerInn",
                LocalDate.now(),
                "ProductionType",
                Collections.singletonList(new CrptApi.Product(
                        "CertDoc",
                        LocalDate.now(),
                        "CertDocNumber",
                        "OwnerInn",
                        "ProducerInn",
                        LocalDate.now(),
                        "TnvedCode",
                        "UitCode",
                        "UituCode"
                )),
                LocalDate.now(),
                "RegNumber"
        );

        CrptApi api = new CrptApi(1L, TimeUnit.SECONDS, 10);
        for (int i = 0; i < 100; i++) {
            api.createDocument(document, "dscdcsddscsdsdsc");
        }

    }

}