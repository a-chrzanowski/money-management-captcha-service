package pl.achrzanowski.moneymanagementcaptchaservice.captcha;

import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterImage {

    @Getter
    private final char character;
    @Getter
    private final String path;
    public static final List<CharacterImage> IMAGES = Collections.unmodifiableList(loadImages());

    private static final String imagesDirectory = "characterimages";

    private CharacterImage(char character, String path){
        this.character = character;
        this.path = path;
    }

    private static List<CharacterImage> loadImages(){
        List<CharacterImage> values = new ArrayList<>();

        File imagesDirectory = new File(CharacterImage.imagesDirectory);
        File[] imagesList = imagesDirectory.listFiles();
        if(imagesList != null){
            for (File file : imagesList) {
                if(file.isFile()){
                    String fileName = file.getName();
                    if(fileName.contains("_up"))
                        fileName = fileName.toUpperCase();

                    char character = fileName.charAt(0);
                    CharacterImage characterImage = new CharacterImage(character, file.getPath());
                    values.add(characterImage);
                }
            }
        }
        return values;
    }

}
