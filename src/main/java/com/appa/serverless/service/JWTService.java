package com.appa.serverless.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.appa.serverless.model.Response;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JWTService {

	@Value("${security.JWTKEY}")
	private static final String SECRET_KEY = "";

	// Generate a JWT token
	public static String generateToken(String subject) {
		Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
		return JWT.create().withSubject(subject).withExpiresAt(new Date(System.currentTimeMillis() + 360000))
				.sign(algorithm);
	}

	// Return code:
	// 1 => Token valid
	// -1 => The Token has expired
	// -2 => The Token's Signature resulted invalid
	// -999 => Undefined error occurs
	public static Response validateToken(String token) {

		Response r = new Response();

		// avoid login every time for local testing, skip the control
		String stage = System.getProperty("stage");
		if (stage != null && stage.equalsIgnoreCase("test")) {
			r.setCode(1);
			r.setMessage("Token valid");
			return r;
		}

		try {

			Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
			DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
			System.out.println("Expire at:" + decodedJWT.getExpiresAt());

			r.setCode(1);
			r.setMessage("Token valid");
			r.setData(new Object[] { decodedJWT.getSubject() });

		} catch (TokenExpiredException e) {
			r.setCode(-1);
			r.setMessage(e.getMessage());

		} catch (SignatureVerificationException e) {
			r.setCode(-2);
			r.setMessage(e.getMessage());

		} catch (Exception e) {
			r.setCode(-999);
			r.setMessage(e.getMessage());

		}

		return r;
	}

	public static void main(String[] args) {
		String token = generateToken("Mario");
		System.out.println("Generated token: " + token);
		System.out.println("Token is valid: " + validateToken(
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJtY…5NDh9.XAapbITSxePKoy6_D8OsJPe_uD9XA87KqiimvpdkR8c"));

	}
}
