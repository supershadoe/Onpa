package com.supershadoe.onpa;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

class OnpaThread implements Runnable {
	private boolean mTerminate = false;
	private String mServer;
	private int mPort;

	public OnpaThread(String Server, String Port) {
		mServer = Server;
		mPort = Integer.valueOf(Port);
	}

	public void Terminate() {
		mTerminate = true;
	}

	public void run() {
		Socket sock = null;
		BufferedInputStream audioData = null;
		try {
			sock = new Socket(mServer, mPort);
		} catch (UnknownHostException e) {
			// TODO if the host name could not be resolved into an IP address.
			Terminate();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO if an error occurs while creating the socket
			Terminate();
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO if a security manager exists and it denies the permission to
			// connect to the given address and port.
			Terminate();
			e.printStackTrace();
		}

		if (!mTerminate) {
			try {
				audioData = new BufferedInputStream(sock.getInputStream());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				Terminate();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Terminate();
				e.printStackTrace();
			}
		}

		final int sampleRate = 48000;

		int musicLength = AudioTrack.getMinBufferSize(sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT);
		AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT, musicLength,
				AudioTrack.MODE_STREAM);
		audioTrack.play();

		byte[] audioBuffer = new byte[musicLength * 8];

		while (!mTerminate) {
			try {
				int sizeRead = audioData.read(audioBuffer, 0, musicLength * 8);
				int sizeWrite = audioTrack.write(audioBuffer, 0, sizeRead);
				if (sizeWrite == AudioTrack.ERROR_INVALID_OPERATION) {
					sizeWrite = 0;
				}
				if (sizeWrite == AudioTrack.ERROR_BAD_VALUE) {
					sizeWrite = 0;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		audioTrack.stop();
		sock = null;
		audioData = null;
	}
}
