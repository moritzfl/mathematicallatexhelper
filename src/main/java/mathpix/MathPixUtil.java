package mathpix;


import com.google.gson.Gson;

import mathpix.api.response.DetectionResult;


import javax.activation.MimeType;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MathPixUtil {

    private static final Logger LOGGER = Logger.getLogger(MathPixUtil.class.getName());

    public static DetectionResult getLatex(Image image) {
        DetectionResult detectionResult;
        try {
            String url = MathPixSettings.getBaseUrl();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("app_id", MathPixSettings.getAppId());
            connection.setRequestProperty("filename", "picture.jpg");
            connection.setRequestProperty("app_key", MathPixSettings.getAppKey());

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            String body = "{\"src\" : \"data:image/jpeg;base64, "
                    + bitmapToBase64(image) + "\" }";
            wr.write(body);
            wr.flush();
            wr.close();

            try (InputStream responseStream = connection.getInputStream()) {

                //To read response as a string:
                MimeType contentType = new MimeType(connection.getContentType());
                String charset = contentType.getParameter("charset");
                String response =
                        new Scanner(responseStream, charset).useDelimiter("\\Z").next();
                detectionResult = new Gson().fromJson(response, DetectionResult.class);

            }
        } catch (Exception e) {
            //nothing to do here but to deliver back nothing, if we can not get the result, we can not get the result :)
            LOGGER.log(Level.SEVERE, "Could not use MathPix API to decode image", e);
            detectionResult = null;
        }
        return detectionResult;
    }


    private static String bitmapToBase64(Image image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        String result = null;
        try {
            ImageIO.write(toBufferedImage(image), "jpg", os);
            byte[] encoded = Base64.getEncoder().encode(os.toByteArray());
            os.close();
            result = new String(encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),
                img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bufferedImage;
    }
}
