package io.appbase.streamExample;

import com.ning.http.client.*;
import com.ning.http.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Future;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        AsyncHttpClient c = new AsyncHttpClient();
        Future<String> f = c.prepareGet("http://scalr.api.appbase.io/createnewtestapp01/tweet/1?stream=true")
                .setRequestTimeout(0)
                .addHeader("Authorization", "Basic " + new String(Base64.encode((new String("RIvfxo1u1:dee8ee52-8b75-4b5b-be4f-9df3c364f59f")).getBytes())))
                .execute(new AsyncHandler<String>() {
                    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();

                    @Override
                    public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                        int statusCode = status.getStatusCode();
                        // The Status have been read
                        // If you don't want to read the headers,body or stop processing the response
                        if (statusCode >= 500) {
                            return STATE.ABORT;
                        }
                        return STATE.CONTINUE;
                    }

                    @Override
                    public STATE onHeadersReceived(HttpResponseHeaders h) throws Exception {
                        //Headers headers = h.getHeaders();
                        // The headers have been read
                        // If you don't want to read the body, or stop processing the response
                        return STATE.CONTINUE;
                    }

                    @Override
                    public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                        // Send these bytes to a streaming json parser which should write
                        // to a buffer which is use by a JSONReader
                        ByteArrayOutputStream localBytes = new ByteArrayOutputStream();
                        bytes.write(bodyPart.getBodyPartBytes());
                        localBytes.write(bodyPart.getBodyPartBytes());
                        System.out.print("event received: ");
                        System.out.print(localBytes.toString("UTF-8"));
                        return STATE.CONTINUE;
                    }

                    @Override
                    public String onCompleted() throws Exception {
                        // Will be invoked once the response has been fully read or a ResponseComplete exception
                        // has been thrown.
                        // NOTE: should probably use Content-Encoding from headers
                        return bytes.toString("UTF-8");
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                    }
                });

        String bodyResponse = f.get();
        System.out.println("Response completed: " + bodyResponse);
    }
}
