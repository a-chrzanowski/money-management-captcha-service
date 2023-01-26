package pl.achrzanowski.moneymanagementcaptchaservice.captcha;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping(value = "/generate")
    public ResponseEntity<Captcha> generateImage() {
        try {
            Captcha captcha = Captcha.generate();
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(captcha);
        } catch (CaptchaGenerationException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @Profile(value = "local")
    @GetMapping(value = "/display")
    public void displayGeneratedImage(HttpServletResponse httpServletResponse) throws CaptchaGenerationException, IOException {
        if(activeProfile.equals("local")){
            Captcha captcha = Captcha.generate();
            httpServletResponse.setContentType("image/png");
            InputStream inputStream = new ByteArrayInputStream(captcha.getImage());
            IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

}
