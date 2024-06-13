package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.informatika.jpa.service.QRCodeService;

@Hidden // Vasilije: mislim da ne treba da se vidi u dokumentaciji
@RestController
public class QRCodeController {

	@Autowired
	private QRCodeService qrCodeService;

	@GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getQRCode(@RequestParam String text, @RequestParam(defaultValue = "200") int width,
							@RequestParam(defaultValue = "200") int height) {
		return qrCodeService.generateQRCodeImage(text, width, height);
	}
}
