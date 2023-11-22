package ru.selsup;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class HttpApiCaller {
    public static void main(String[] args) {
        HttpGet request = new HttpGet("https://google.com/");
        try (var client = HttpClients.createDefault();
             var response = client.execute(request)) {
            var entity = response.getEntity();
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity, "UTF-8"));
            }

            System.out.println("Headers");
            for (var header : response.getHeaders()) {
                System.out.println(header.getName() + " : " + header.getValue());
                System.out.println();
            }

            System.out.println(response.getCode());
            System.out.println(response.getReasonPhrase());
            System.out.println(response.getVersion());

        } catch (Throwable cause) {
            cause.printStackTrace();
        }

    }
}
