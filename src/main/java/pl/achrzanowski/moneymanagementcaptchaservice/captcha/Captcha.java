package pl.achrzanowski.moneymanagementcaptchaservice.captcha;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Captcha {

    private final String verificationCode;
    private final byte[] image;

    private Captcha(String verificationCode, byte[] image){
        this.verificationCode = verificationCode;
        this.image = image;
    }

    public static Captcha generate() throws CaptchaGenerationException {
        String verificationCode;
        byte[] image;
        try{
            CharacterImage[] randomCharacterImages = ContentGenerator.getRandomCharacterImages();
            verificationCode = ContentGenerator.generateVerificationCode(randomCharacterImages);
            image = ContentGenerator.generateImage(randomCharacterImages);
        } catch (IOException exception){
            throw new CaptchaGenerationException("Error while generating Captcha - try again later");
        }
        return new Captcha(verificationCode, image);
    }

    private static class ContentGenerator {
        private static final int NUMBER_OF_CHARACTERS_IN_CAPTCHA = 6;
        private static final int SINGLE_IMAGE_HEIGHT = 100;
        private static final String CAPTCHA_IMAGE_FORMAT = "png";

        private static CharacterImage[] getRandomCharacterImages(){
            CharacterImage[] randomCharacterImages = new CharacterImage[NUMBER_OF_CHARACTERS_IN_CAPTCHA];
            for(int i=0; i<randomCharacterImages.length; i++){
                int random = ThreadLocalRandom.current().nextInt(0, CharacterImage.IMAGES.size());
                randomCharacterImages[i] = CharacterImage.IMAGES.get(random);
            }
            return randomCharacterImages;
        }

        private static String generateVerificationCode(CharacterImage[] randomCharacterImages){
            StringBuilder verificationCode = new StringBuilder();
            for(CharacterImage characterImage: randomCharacterImages)
                verificationCode.append(characterImage.getCharacter());
            return verificationCode.toString();
        }

        private static byte[] generateImage(CharacterImage[] randomCharacterImages) throws IOException {
            int outputImageWidth = 0;
            for(CharacterImage characterImage : randomCharacterImages)
                outputImageWidth += ImageIO.read(new File(characterImage.getPath())).getWidth();
            BufferedImage outputBufferedImage = new BufferedImage(outputImageWidth, SINGLE_IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics outputImage = outputBufferedImage.getGraphics();

            int x = 0;
            for(CharacterImage characterImage: randomCharacterImages){
                BufferedImage characterBufferedImage = ImageIO.read(new File(characterImage.getPath()));
                outputImage.drawImage(characterBufferedImage, x, 0, null);
                x += characterBufferedImage.getWidth();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(outputBufferedImage, CAPTCHA_IMAGE_FORMAT, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
