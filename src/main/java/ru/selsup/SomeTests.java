package ru.selsup;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

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

        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 1);

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        try {
            // Create a list to hold the futures of the API calls
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                Callable<Void> apiCallTask = () -> {
                    System.out.println("API called by thread: " + Thread.currentThread().getName());
                    crptApi.createDocument(document, "csdcsdcsdscsddc");
                    return null;
                };

                // Submit the task to the executor service and store the future
                futures.add(executorService.submit(apiCallTask));
            }

            // Wait for all tasks to complete
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // Shutdown the executor service
            executorService.shutdown();
        }
    }
}