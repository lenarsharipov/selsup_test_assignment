package ru.selsup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private static final String URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private static final String SIGNATURE = "Signature";
    private final long time;
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private static final Logger LOG = LoggerFactory.getLogger(CrptApi.class.getName());

    public CrptApi(final long time, final TimeUnit timeUnit, final int requestLimit) {
        this.time = time;
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
    }

    public int createDocument(Document document, String signature) {
        LOG.info("Starting creating document");
        var jsonDocument = DocumentUtils.documentToJson(document);
        return executePostRequest(jsonDocument, signature);
    }

    private int executePostRequest(String jsonDocument, String signature) {
        int statusCode = -1;
        LOG.info("Starting post request");
        try (var entity = new StringEntity(jsonDocument, ContentType.APPLICATION_JSON);
            var client = HttpClients.createDefault()
        ) {
            var request = new HttpPost(URL);
            request.addHeader(SIGNATURE, signature);
            request.setEntity(entity);
            var response = client.execute(request);
            statusCode = response.getCode();
            LOG.info("Post request finished with status code {}", statusCode);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return statusCode;
    }

    /**
     * Утлитный класс для работы с Document.
     */
    static class DocumentUtils {
        private static final Logger LOG = LoggerFactory.getLogger(DocumentUtils.class.getName());

        public static String documentToJson(Document document) {
            LOG.info("Starting parsing document to JSON");
            String jsonString = null;
            try {
                var objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                jsonString = objectMapper.writeValueAsString(document);
                LOG.info("Document successfully parsed");
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
            return jsonString;
        }
    }

    public record Description(String participantInn) {

    }

    public record Product(
            @JsonProperty("certificate_document") String certificateDocument,
            @JsonProperty("certificate_document_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate certificateDocumentDate,
            @JsonProperty("certificate_document_number") String certificateDocumentNumber,
            @JsonProperty("owner_inn") String ownerInn,
            @JsonProperty("producer_inn") String producerInn,
            @JsonProperty("production_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate productionDate,
            @JsonProperty("tnved_code") String tnvedCode,
            @JsonProperty("uit_code") String uitCode,
            @JsonProperty("uitu_code") String uituCode
    ) {

    }

    public record Document(
            Description description,
            @JsonProperty("doc_id") String docId,
            @JsonProperty("doc_status") String docStatus,
            @JsonProperty("doc_type") DocType docType,
            boolean importRequest,
            @JsonProperty("owner_inn") String ownerInn,
            @JsonProperty("participant_inn") String participantInn,
            @JsonProperty("producer_inn") String producerInn,
            @JsonProperty("production_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate productionDate,
            @JsonProperty("production_typ") String productionType,
            List<Product> products,
            @JsonProperty("reg_date")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate regDate,
            @JsonProperty("reg_number") String regNumber
    ) {
        public enum DocType {
            LP_INTRODUCE_GOODS
        }
    }

}