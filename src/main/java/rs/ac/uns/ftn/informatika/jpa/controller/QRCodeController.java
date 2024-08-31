package rs.ac.uns.ftn.informatika.jpa.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.informatika.jpa.service.QRCodeService;
import rs.ac.uns.ftn.informatika.jpa.service.ReservationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Hidden // Vasilije: mislim da ne treba da se vidi u dokumentaciji
@RestController
public class QRCodeController {

	@Autowired
	private QRCodeService qrCodeService;

	@Autowired
	private ReservationService reservationService;

	@GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getQRCode(@RequestParam String text, @RequestParam(defaultValue = "200") int width,
							@RequestParam(defaultValue = "200") int height) {
		return qrCodeService.generateQRCodeImage(text, width, height);
	}

	@PreAuthorize("hasRole('REGISTERED_USER')")
	@GetMapping(value = "/qrcode/reservations")
	public ResponseEntity<List<String>> getQRCodesReservations(
			@RequestParam(defaultValue = "200") int width,
			@RequestParam(defaultValue = "200") int height,
			@RequestParam(required = false) String status){


		try{
			List<String> qrCodes = reservationService.getQRcodes(status);
			return ResponseEntity.ok(qrCodes);
		}catch (IllegalArgumentException e){
			return ResponseEntity.badRequest().body(Collections.singletonList("Invalid status parameter"));
		}
	}

}
