package org.codelanka.speech.SpeechTranscribe;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

@SpringBootApplication
public class SpeechTranscribeApplication {


	public static void sampleLongRunningRecognize() {
		// TODO(developer): Replace these variables before running the sample.
		String storageUri = "gs://speechtranscribe-1/Mark_Zuckerberg.mp4";
		sampleLongRunningRecognize(storageUri);
	}

	/**
	 * Transcribe a short audio file using an enhanced model
	 *
	 * @param  to local audio file, e.g. /path/audio.wav
	 */
	public static void sampleLongRunningRecognize(String storageUri) {

		try (SpeechClient speechClient = SpeechClient.create()) {

			// Sample rate in Hertz of the audio data sent
			int sampleRateHertz = 16000;

			// The language of the supplied audio
			String languageCode = "en-US";

			// Encoding of audio data sent. This sample sets this explicitly.
			// This field is optional for FLAC and WAV audio formats.
			RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;
			RecognitionConfig config =
					RecognitionConfig.newBuilder()
							.setSampleRateHertz(sampleRateHertz)
							.setLanguageCode(languageCode)
							.setEncoding(encoding)
							.build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(storageUri).build();
			LongRunningRecognizeRequest request =
					LongRunningRecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> future =
					speechClient.longRunningRecognizeAsync(request);

			System.out.println("Waiting for operation to complete...");
			LongRunningRecognizeResponse response = future.get();
			for (SpeechRecognitionResult result : response.getResultsList()) {
				// First alternative is the most probable result
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcript: %s\n", alternative.getTranscript());
			}
		} catch (Exception exception) {
			System.err.println("Failed to create the client due to: " + exception);
		}


}
